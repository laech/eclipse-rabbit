/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.tracking.tests.trackers;

import rabbit.data.store.model.JavaEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.AbstractTracker;
import rabbit.tracking.internal.trackers.JavaTracker;

import static org.eclipse.jdt.internal.ui.actions.SelectionConverter.getElementAtOffset;
import static org.eclipse.jdt.internal.ui.actions.SelectionConverter.getInput;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.actions.OpenJavaPerspectiveAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.joda.time.Interval;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

/**
 * @see JavaTracker
 */
@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class JavaTrackerTest extends AbstractTrackerTest<JavaEvent> {

  private static final SWTWorkbenchBot BOT = new SWTWorkbenchBot();
  private static final String PROJECT_NAME = System.nanoTime() + "";
  private static final String PACKAGE_NAME = "pkg";
  private static final String CLASS_NAME = "Program";

  private static final String JAVA_EDITOR_ID = "org.eclipse.jdt.ui.CompilationUnitEditor";
  private static final IFile FILE = ResourcesPlugin
      .getWorkspace()
      .getRoot()
      .getFile(
          new Path("/" + PROJECT_NAME + "/src/" + PACKAGE_NAME + "/"
              + CLASS_NAME + ".java"));

  @AfterClass
  public static void afterClass() {
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeAllEditors(false);
      }
    });
  }

  @BeforeClass
  public static void beforeClass() {
    // Close the welcome view:
    BOT.viewByTitle("Welcome").close();

    // Open the Java perspective:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        new OpenJavaPerspectiveAction().run();
      }
    });

    // Create a new project:
    BOT.menu("File").menu("New").menu("Java Project").click();
    BOT.shell("New Java Project").activate();
    BOT.textWithLabel("Project name:").setText(PROJECT_NAME);
    BOT.button("Finish").click();

    BOT.waitUntil(Conditions.shellCloses(BOT.shell("New Java Project")));

    // Create a new class:
    BOT.menu("File").menu("New").menu("Class").click();
    BOT.textWithLabel("Package:").setText(PACKAGE_NAME);
    BOT.textWithLabel("Name:").setText(CLASS_NAME);
    BOT.button("Finish").click();

    BOT.waitUntil(Conditions.shellCloses(BOT.shell("New Java Class")));

    // Close all editors:
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .closeAllEditors(false);
      }
    });
  }

  /**
   * Disables the tracker and empties the tracker's data.
   */
  @Before
  public void before() {
    tracker.setEnabled(false);
    tracker.flushData();
  }

  /**
   * Tests when the editor is no longer the active part:
   */
  @Test
  public void testEditorDeactivated() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true); // Start
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);
    // Sets another view to be active to cause the editor to lose focus:

    long preEnd = System.currentTimeMillis();
    BOT.viewByTitle("Outline").show(); // End
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Tests that events are recorded properly with the different states of the
   * editor.
   */
  @Test
  public void testEditorDeactivatedThenActivated() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Now run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    BOT.viewByTitle("Outline").show();
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

    // Now we activate the editor again to see if the tracker will start to
    // track events again:
    tracker.flushData();

    preStart = System.currentTimeMillis();
    BOT.editorByTitle(FILE.getName()).show();
    postStart = System.currentTimeMillis();

    Thread.sleep(20);

    preEnd = System.currentTimeMillis();
    BOT.viewByTitle("Outline").show();
    postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    events = tracker.getData();
    assertEquals(1, events.size());

    event = events.iterator().next();
    start = event.getInterval().getStartMillis();
    end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray2 = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray2[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray2[0], event.getElement());
  }

  /**
   * Test when the user changes from working on a Java element to another.
   */
  @Test
  public void testElementChanged() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    SWTBotEclipseEditor botEditor = new SWTBotEclipseEditor(BOT.activeEditor()
        .getReference(), BOT);

    // Set the editor to select the package declaration:
    IDocument document = getDocument(editor);
    final String content = document.get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Keeps the reference of the package declaration for testing latter:
    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    // Change the element the user is working on:
    long preEnd = System.currentTimeMillis();
    offset = content.indexOf(CLASS_NAME);
    int line = document.getLineOfOffset(offset);
    botEditor.navigateTo(line, 0);
    botEditor.typeText(" ");
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertEquals(elementArray[0], event.getElement());
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  /**
   * When the tracker is set to enable, but if there is no active workbench
   * window, no data will be collected.
   */
  @Test
  public void testEnable_noActiveWorkbenchWindow() throws Exception {
    final Shell[] shellHolder = new Shell[1];
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(CLASS_NAME);
    int length = CLASS_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);

        // Open a new shell to cause the workbench window to lose focus:
        final Shell shell = new Shell(Display.getCurrent());
        shell.open();
        shell.forceActive();

        shellHolder[0] = shell;
      }
    });

    try {
      tracker.setEnabled(true);
      Thread.sleep(30);
      tracker.setEnabled(false);
      assertEquals(0, tracker.getData().size());

    } catch (InterruptedException e) {
      fail(e.getMessage());

    } finally {
      final Shell shell = shellHolder[0];
      if (shell != null) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
          @Override
          public void run() {
            shell.dispose();
          }
        });
      }
    }

  }

  @Test
  public void testEnableThenDisable() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(CLASS_NAME);
    int length = CLASS_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on an anonymous. This event should be filtered on save, so
   * that instead of showing a user spent x amount of time on the anonymous
   * class, we show that a user spent x amount of time on the anonymous's parent
   * type element (a method or a type that is not anonymous).
   */
  @Test
  public void testFilter_anonymousClass() throws Exception {
    /*
     * Here we test that: a method containing an anonymous Runnable which also
     * contains another anonymous Runnable, and the most inner Runnable is
     * selected (to emulate that the user is working on that), then when filter
     * on save the data should indicate that the user has spent x amount of time
     * working on the method, not any of the Runnable's.
     */

    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

      @Override public void run() {
        //@formatter:off
        String anonymousClassText = 
            "void aMethod() {" + 
            "  new Runnable() { " + 
            "    public void run(){" + 
            "      new Runnable() {" +
            "        public void run() {}" +
            "      };" + 
            "    } " + 
            "  };" +
        		"}"; 
        //@formatter:on
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, anonymousClassText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf("Runnable", content.indexOf("Runnable") + 1);
        length = "Runnable".length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.TYPE);
    assertTrue(((IType) elementArray[0]).isAnonymous());
    // getParent().getParent().getParent() will give us the method:
    assertEquals(event.getElement().getElementType(), IJavaElement.METHOD);
    assertEquals("aMethod", event.getElement().getElementName());
  }

  /**
   * This test is for the same purpose as
   * {@link #testFilterDeletedElements_typeMembers()}, but with a little
   * difference, that is, if the whole Java file is deleted, all the data about
   * the Java elements in the file will be stored under the Java file, even
   * though it's deleted.
   * 
   * @see #testFilterDeletedElements_typeMembers()
   */
  @Test
  public void testFilter_deletedElement_mainType() throws Exception {

    String newClassName = CLASS_NAME + "abc";

    // Create a new class:
    BOT.menu("File").menu("New").menu("Class").click();
    BOT.textWithLabel("Package:").setText(PACKAGE_NAME);
    BOT.textWithLabel("Name:").setText(newClassName);
    BOT.button("Finish").click();
    BOT.waitUntil(Conditions.shellCloses(BOT.shell("New Java Class")));

    IFile file = ((IFolder) FILE.getParent()).getFile(newClassName + ".java");
    final JavaEditor editor = closeAndOpenEditor(file);

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(35);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // The document root:
    IJavaElement typeRoot = JavaCore.create(file);

    // Delete the file:
    file.delete(true, null);

    // Give the deletion some time:
    TimeUnit.SECONDS.sleep(2);

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    // One data should be in the collection (the parent of the previously
    // selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // Test the data is placed under the root element:
    assertEquals(typeRoot, event.getElement());
  }

  /**
   * Tests that if a Java element, a field for example, is deleted from the Java
   * source file, then when we save the tracker's data, the data about the
   * deleted field should be store as the parent's data.
   * <p>
   * For example: If we have a field called "fieldA" under the class "ClassA"
   * and the tracker has recorded that the user has spent 20 seconds working on
   * the field, but the field is then deleted from the class. So when the
   * tracker saves the data, instead of saving
   * "The user has spent 20 seconds working on fieldA" we store
   * "The user has spent 20 seconds working on classA".
   * </p>
   * <p>
   * Another important purpose of this feature is that: Then a user starts to
   * type a new java element, like a method, he/she knows what the name he/she
   * is going to type for the method, but we have no way of knowing that, so
   * lots of events may be recorded before he/she finishes typing the name. For
   * example, if the user want to type "hello" as the method name, there will be
   * events recorded about the java element "hel", or "hell", or "hello", we
   * only need one of them ("hello") but we also want to keep the time about the
   * invalid ones, so before we save the data, we check for non-existent java
   * elements, and instead of saving the data under those elements, we save the
   * data under the first existing parent of the elements, if all parents are
   * missing (e.g. deletes the file), we save it under the file parent, like
   * "File.java", even though the file has been deleted.
   * </p>
   * 
   * @see #testFilterDeletedElements_mainType()
   */
  @Test
  public void testFilter_deletedElement_typeMembers() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);

    // Place a field in the body of the class, note that we don't want to add
    // errors the class:

    // The name of the field we are about to insert to the class, this name is
    // very unique, so we could use String.indexOf to locate it in the file:
    final String fieldName = "aVeryUniqueFieldName";
    final String fieldStr = "private int " + fieldName + " = 0;";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String content = document.get();
        int offset = content.lastIndexOf('}') - 1;
        try {
          document.replace(offset, 0, fieldStr);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }
      }
    });

    // Set the editor to select the field:
    int offset = document.get().indexOf(fieldStr);
    int length = fieldStr.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(25);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Keeps a reference to the field statement first, for testing latter:
    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The field element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });

    // Now delete the field statement from the source file, note that there
    // is no need to save the document (and we should not save the document,
    // other tests may depend on it):
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        int offset = document.get().indexOf(fieldStr);
        int length = fieldStr.length();
        try {
          document.replace(offset, length, "");
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }
      }
    });

    // Wait a while to make sure the above operation is finished:
    TimeUnit.SECONDS.sleep(2);

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    // Gets the data, the data is remained in the tracker as long as we don't
    // enable it again (according to the contract of the tracker):
    //
    // One data should be in the collection
    // (the parent of the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // Now check that the element is the parent of the package declaration
    // instead of the deleted package declaration itself:
    assertEquals(elementArray[0].getParent(), event.getElement());
  }

  /**
   * Test an event on an import statement. This event should be filtered on
   * save, so that instead of showing a user spent x amount of time on the
   * import statement , we show that a user spent x amount of time on the type
   * root (ITypeRoot) element, (a.k.a the Java file).
   */
  @Test
  public void testFilter_existingElement_importStatement() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String importStatement = "import java.util.*;";
        String content = document.get();
        int offset = content.indexOf(";") + 1; // Position after package
                                               // declaration
        int length = 0;
        try {
          document.replace(offset, length, importStatement);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf(importStatement);
        length = importStatement.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final ITypeRoot[] elementArray = new ITypeRoot[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        elementArray[0] = getInput(editor);
      }
    });
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on a static initialiser. This event should not be filtered on
   * save.
   */
  @Test
  public void testFilter_existingElement_initializer() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    final String staticName = "static";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String methodText = staticName + " {}";
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, methodText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf(staticName);
        length = staticName.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.INITIALIZER);
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on a method, that is a member of an anonymous class. This
   * event should be filtered so that we so the user has spent x amount of time
   * on the method's first non-anonymous parent.
   */
  @Test
  public void testFilter_existingElement_methodParentIsAnonymous()
      throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

      @Override public void run() {
        //@formatter:off
        String anonymousClassText = 
            "void aMethod() {" +
            "  new Runnable() { " +
            "    public void run(){" +
            "      new Runnable() {" +
            "        public void run() {}" +
            "      };" +
            "    } " +
            "  };" +
            "}";
        //@formatter:on
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, anonymousClassText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf("run", content.indexOf("run") + 1);
        length = "run".length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.METHOD);
    assertEquals("aMethod", event.getElement().getElementName());
    assertEquals(event.getElement().getElementType(), IJavaElement.METHOD);
  }

  /**
   * Test an event on a method, that is a member of a non-anonymous class. This
   * event should not be filtered on save.
   */
  @Test
  public void testFilter_existingElement_methodParentNotAnonymous()
      throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    final String methodName = "aMethodName";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String methodText = "void " + methodName + "() {}";
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, methodText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf(methodName);
        length = methodName.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.METHOD);
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on an package declaration. This event should be filtered on
   * save, so that instead of showing a user spent x amount of time on the
   * package declaration , we show that a user spent x amount of time on the
   * main type element.
   */
  @Test
  public void testFilter_existingElement_packageDeclaration() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String content = document.get();
        int offset = content.indexOf(PACKAGE_NAME);
        int length = PACKAGE_NAME.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(),
        IJavaElement.PACKAGE_DECLARATION);
    assertEquals(elementArray[0].getParent(), event.getElement());
  }

  /**
   * Test an event on an anonymous type. This event should be filtered so that
   * we show the user has spent x amount of time on the type's first
   * non-anonymous parent.
   */
  @Test
  public void testFilter_existingElement_typeAnonymous() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

      @Override public void run() {
        //@formatter:off
        String anonymousClassText = 
            "void aMethod() {" +
            "  new Runnable() { " + 
            "    public void run(){" +
            "    } " +
            "  };" +
            "}";
        //@formatter:on
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, anonymousClassText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf("Runnable");
        length = "Runnable".length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(35);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.TYPE);
    assertEquals("aMethod", event.getElement().getElementName());
    assertEquals(event.getElement().getElementType(), IJavaElement.METHOD);
  }

  /**
   * Test an event on an inner class. This event should be not filtered on save.
   */
  @Test
  public void testFilter_existingElement_typeInner() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    final String innerClassName = "anInnerClassName";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String innerClassText = "\nstatic class " + innerClassName + " {}";
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, innerClassText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf(innerClassName);
        length = innerClassName.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.TYPE);
    assertEquals(elementArray[0].getElementName(), innerClassName);
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on a normal Java type (not anonymous, not inner class). This
   * event should not be filtered on save.
   */
  @Test
  public void testFilter_existingElement_typeNormal() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String content = getDocument(editor).get();
        int offset = content.indexOf(CLASS_NAME);
        int length = CLASS_NAME.length();
        final ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.TYPE);
    assertEquals(elementArray[0], event.getElement());
  }

  /**
   * Test an event on a field. This event should be filtered on save, so that
   * instead of showing a user spent x amount of time on the field, we show that
   * a user spent x amount of time on the field's parent type.
   */
  @Test
  public void testFilter_exsitingElement_field() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    final String fieldName = "aFieldName";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        String methodText = "private int " + fieldName + " = 1;";
        String content = document.get();
        int offset = content.indexOf("{") + 1;
        int length = 0;
        try {
          document.replace(offset, length, methodText);
        } catch (BadLocationException e) {
          fail(e.getMessage());
        }

        content = document.get();
        offset = content.indexOf(fieldName);
        length = fieldName.length();
        ITextSelection selection = new TextSelection(offset, length);
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    final JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // The filtered event should be on the field's parent, not on the field
    // itself:
    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0].getElementType(), IJavaElement.FIELD);
    assertEquals(elementArray[0].getParent(), event.getElement());
  }

  /**
   * Tests when the user becomes inactive.
   */
  @Test
  public void testUserInactive() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

  }

  /**
   * Tests when the window lose focus.
   */
  @Test
  public void testWindowDeactivated() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    final Shell shell = BOT.activeShell().widget;
    // Minimize the shell to cause it to lose focus:
    long preEnd = System.currentTimeMillis();
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(true);
      }
    });
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

    // Restore the shell:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(false);
      }
    });
  }

  /**
   * Tests that events are recorded properly with the different states of the
   * window.
   */
  @Test
  public void testWindowDeactivatedThenActivated() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String content = getDocument(editor).get();
    int offset = content.indexOf(PACKAGE_NAME);
    int length = PACKAGE_NAME.length();
    final ITextSelection selection = new TextSelection(offset, length);
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });

    final Shell shell = BOT.activeShell().widget;

    // Now run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    // Minimize the shell to cause it to lose focus:
    long preEnd = System.currentTimeMillis();
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(true);
      }
    });
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

    // Restore the shell to see if tracker will start tracking again:
    tracker.flushData();

    preStart = System.currentTimeMillis();
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(false);
      }
    });
    postStart = System.currentTimeMillis();

    Thread.sleep(25);

    // Minimise the shell to cause it to lose focus:
    preEnd = System.currentTimeMillis();
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(true);
      }
    });
    postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    events = tracker.getData();
    assertEquals(1, events.size());

    event = events.iterator().next();
    start = event.getInterval().getStartMillis();
    end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    final IJavaElement[] elementArray2 = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray2[0] = getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray2[0], event.getElement());

    // Restore the shell:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.setMinimized(false);
      }
    });
  }

  /**
   * Hacks the global idle detector to cause it to notify it's observers.
   */
  protected void callIdleDetectorToNotify() throws Exception {
    Field isActive = IdleDetector.class.getDeclaredField("isActive");
    isActive.setAccessible(true);

    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
    setChanged.setAccessible(true);

    Method notifyObservers = Observable.class
        .getDeclaredMethod("notifyObservers");
    notifyObservers.setAccessible(true);

    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
    detector.setRunning(true);
    isActive.set(detector, false);
    setChanged.invoke(detector);
    notifyObservers.invoke(detector);
    detector.setRunning(false);
  }

  /**
   * Closes all the editor in the workbench page, contents of editors are not
   * saved. Then opens the Java editor on {@link #FILE}.
   * 
   * @return The editor.
   */
  protected JavaEditor closeAndOpenEditor() {
    return closeAndOpenEditor(FILE);
  }

  /**
   * Closes all the editor in the workbench page, contents of editors are not
   * saved. Then opens the Java editor on the file.
   * 
   * @param file The file to open.
   * @return The editor.
   */
  protected JavaEditor closeAndOpenEditor(final IFile file) {
    final JavaEditor[] editorArray = new JavaEditor[1];
    BOT.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          IWorkbenchPage page = PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage();
          page.closeAllEditors(false);

          IEditorInput input = new FileEditorInput(file);
          editorArray[0] = (JavaEditor) page.openEditor(input, JAVA_EDITOR_ID);
        } catch (PartInitException e) {
          e.printStackTrace();
          fail("Unable to open editor");
        }
      }
    });
    return editorArray[0];
  }

  @Override
  protected JavaEvent createEvent() {
    return new JavaEvent(new Interval(0, 1),
        JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
  }

  @Override
  protected AbstractTracker<JavaEvent> createTracker() {
    return new JavaTracker();
  }

  /**
   * Gets the document from the editor.
   * 
   * @param editor The editor.
   * @return The document.
   */
  protected IDocument getDocument(JavaEditor editor) {
    IDocument doc = editor.getDocumentProvider().getDocument(
        editor.getEditorInput());
    if (doc == null) {
      fail("Document is null");
    }
    return doc;
  }

}
