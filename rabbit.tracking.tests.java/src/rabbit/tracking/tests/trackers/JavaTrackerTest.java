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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.actions.OpenJavaPerspectiveAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
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
import org.joda.time.DateTime;
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
  private static final IFile FILE = ResourcesPlugin.getWorkspace().getRoot()
      .getFile(new Path("/" + PROJECT_NAME + "/src/" + PACKAGE_NAME + "/" + CLASS_NAME + ".java"));
  
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
      @Override public void run() {
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    // Sets another view to be active to cause the editor to lose focus:
    BOT.viewByTitle("Outline").show();
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    BOT.viewByTitle("Outline").show();
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());
    
    
    
    // Now we activate the editor again to see if the tracker will start to 
    // track events again:
    tracker.flushData();

    timeout = 30;
    before = new DateTime();
    BOT.editorByTitle(FILE.getName()).show();
    TimeUnit.MILLISECONDS.sleep(timeout);
    BOT.viewByTitle("Outline").show();
    after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    events = tracker.getData();
    assertEquals(1, events.size());

    event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray2 = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray2[0] = SelectionConverter.getElementAtOffset(editor);
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
    SWTBotEclipseEditor botEditor = new SWTBotEclipseEditor(
        BOT.activeEditor().getReference(), BOT);
    
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
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    
    // Run the tracker to capture the event:
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    // Change the element the user is working on:
    offset = content.indexOf(CLASS_NAME);
    int line = document.getLineOfOffset(offset);
    botEditor.navigateTo(line, 0);
    botEditor.typeText(" ");
    DateTime after = new DateTime();
    
    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.isBefore(event.getTime()));
    assertTrue(after.isAfter(event.getTime()));
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 200 >= event.getDuration());
    assertEquals(elementArray[0], event.getElement());
  }
  
  @Test
  public void testEnableThenDisable() throws Exception {
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    tracker.setEnabled(false);
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.isBefore(event.getTime()));
    assertTrue(after.isAfter(event.getTime()));
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());
    
    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());
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
  public void testFilterDeletedElements_mainType() throws Exception {
    
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    tracker.setEnabled(false);
    DateTime after = new DateTime();
    
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
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    // Test the data is placed under the root element:
    assertEquals(typeRoot, event.getElement());
  }
  
  /**
   * Tests that if a Java element, a field for example, is deleted from the Java
   * source file, then when we save the tracker's data, the data about the
   * deleted field should be store as the parent's data.
   * <p>
   * For example: If we have a field called "fieldA" under the class "ClassA"
   * and the tracker has recorded that the user has spent 10 seconds working on
   * the field, but the field is then deleted from the class. So when the
   * tracker saves the data, instead of saving
   * "The user has spent 10 seconds working on fieldA" we store
   * "The user has spent 10 seconds working on classA".
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
  public void testFilterDeletedElements_typeMembers() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    
    // Place a field in the body of the class, note that we don't want to add
    // errors the class:

    // The name of the field we are about to insert to the class, this name is
    // very unique, so we could use String.indexOf to locate it in the file:
    final String fieldName = "aVeryUniqueFieldName";
    final String fieldStr = "private int " + fieldName + " = 0;";
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
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
      @Override public void run() {
        editor.getSelectionProvider().setSelection(selection);
      }
    });
    
    // Run the tracker to capture the event:
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    tracker.setEnabled(false);
    DateTime after = new DateTime();
    
    // Keeps a reference to the field statement first, for testing latter:
    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        try {
          // The field element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    
    // Now delete the field statement from the source file, note that there
    // is no need to save the document (and we should not save the document,
    // other tests may depend on it):
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
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
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    // Now check that the element is the parent of the package declaration
    // instead of the deleted package declaration itself:
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    callIdleDetectorToNotify();
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);

    final Shell shell = BOT.activeShell().widget;
    // Minimize the shell to cause it to lose focus:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        shell.setMinimized(true);
      }
    });
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

    // Restore the shell:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
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
    long timeout = 30;
    DateTime before = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(timeout);
    // Minimize the shell to cause it to lose focus:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        shell.setMinimized(true);
      }
    });
    DateTime after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    Collection<JavaEvent> events = tracker.getData();
    assertEquals(1, events.size());

    JavaEvent event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray[0], event.getElement());

    
  
    // Restore the shell to see if tracker will start tracking again:
    tracker.flushData();
    
    timeout = 30;
    before = new DateTime();
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        shell.setMinimized(false);
      }
    });
    TimeUnit.MILLISECONDS.sleep(timeout);
    // Minimize the shell to cause it to lose focus:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        shell.setMinimized(true);
      }
    });
    after = new DateTime();

    // One data should be in the collection (the selected package declaration):
    events = tracker.getData();
    assertEquals(1, events.size());

    event = events.iterator().next();
    assertTrue(before.compareTo(event.getTime()) <= 0);
    assertTrue(after.compareTo(event.getTime()) >= 0);
    assertTrue(timeout <= event.getDuration());
    assertTrue(timeout + 100 >= event.getDuration());

    final IJavaElement[] elementArray2 = new IJavaElement[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // The package declaration element:
          elementArray2[0] = SelectionConverter.getElementAtOffset(editor);
        } catch (JavaModelException e) {
          fail(e.getMessage());
        }
      }
    });
    assertEquals(elementArray2[0], event.getElement());

    // Restore the shell:
    BOT.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
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

    Method notifyObservers = Observable.class.getDeclaredMethod("notifyObservers");
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
   * @return The editor.
   */
  protected JavaEditor closeAndOpenEditor() {
    return closeAndOpenEditor(FILE);
  }
  
  /**
   * Closes all the editor in the workbench page, contents of editors are not
   * saved. Then opens the Java editor on the file.
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
    return new JavaEvent(new DateTime(), 10, 
        JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
  }

  @Override
  protected AbstractTracker<JavaEvent> createTracker() {
    return new JavaTracker();
  }

  /**
   * Gets the document from the editor.
   * @param editor The editor.
   * @return The document.
   */
  protected IDocument getDocument(JavaEditor editor) {
    IDocument doc = editor.getDocumentProvider()
        .getDocument(editor.getEditorInput());
    if (doc == null) {
      fail("Document is null");
    }
    return doc;
  }

}
