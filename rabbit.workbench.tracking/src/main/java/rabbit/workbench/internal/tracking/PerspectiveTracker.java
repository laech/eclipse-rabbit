package rabbit.workbench.internal.tracking;
///*
// * Copyright 2010 The Rabbit Eclipse Plug-in Project
// * 
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// * 
// * http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package rabbit.tracking.internal.workbench;
//
//import static com.google.common.base.Preconditions.checkNotNull;
//
//import org.eclipse.ui.IPerspectiveDescriptor;
//import org.eclipse.ui.IPerspectiveListener;
//import org.eclipse.ui.IWindowListener;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PerspectiveAdapter;
//import org.eclipse.ui.PlatformUI;
//
//import rabbit.tracking.AbstractTracker;
//import rabbit.tracking.IPersistable;
//import rabbit.tracking.IUserMonitor;
//import rabbit.tracking.IUserMonitor.IUserListener;
//import rabbit.tracking.internal.workbench.util.WorkbenchUtil;
//import rabbit.tracking.util.IPersistableEventListenerSupport;
//import rabbit.tracking.util.Recorder;
//import rabbit.tracking.util.Recorder.IRecordListener;
//import rabbit.tracking.util.Recorder.Record;
//import rabbit.tracking.workbench.IPerspectiveEvent;
//
//public final class PerspectiveTracker
//    extends AbstractTracker implements IPersistable {
//
//  private final IPersistableEventListenerSupport<IPerspectiveEvent> support;
//  private final IUserMonitor monitor;
//
//  private final Recorder recorder = Recorder.withListeners(
//      new IRecordListener() {
//        @Override public void onRecord(Record record) {
//          support.notifyOnEvent(new PerspectiveEvent(
//              record.getStart(),
//              record.getDuration(),
//              (IPerspectiveDescriptor)record.getData()));
//        }
//      });
//
//  private final IUserListener userListener = new IUserListener() {
//    @Override public void onInactive() {
//      recorder.stop();
//    }
//
//    @Override public void onActive() {
//      checkStart();
//    }
//  };
//
//  private final IPerspectiveListener perspectiveListener = new PerspectiveAdapter() {
//    @Override public void perspectiveActivated(
//        IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//      recorder.start(perspective);
//    }
//
//    @Override public void perspectiveDeactivated(
//        IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//      recorder.stop();
//    }
//  };
//
//  private final IWindowListener winlistener = new IWindowListener() {
//
//    @Override public void windowActivated(IWorkbenchWindow win) {
//      recorder.start(WorkbenchUtil.getPerspective(win));
//    }
//
//    @Override public void windowClosed(IWorkbenchWindow win) {
//      win.removePerspectiveListener(perspectiveListener);
//      recorder.stop();
//    }
//
//    @Override public void windowDeactivated(IWorkbenchWindow win) {
//      recorder.stop();
//    }
//
//    @Override public void windowOpened(IWorkbenchWindow win) {
//      win.addPerspectiveListener(perspectiveListener);
//      if (WorkbenchUtil.isActiveShell(win)) {
//        checkStart(win);
//      }
//    }
//  };
//
//  public PerspectiveTracker(
//      IUserMonitor monitor,
//      IPersistableEventListenerSupport<IPerspectiveEvent> support) {
//    this.monitor = checkNotNull(monitor, "monitor");
//    this.support = checkNotNull(support, "support");
//  }
//
//  @Override protected void onDisable() {
//    recorder.stop();
//    for (IWorkbenchWindow win : getWorkbenchWindows()) {
//      win.removePerspectiveListener(perspectiveListener);
//    }
//    monitor.removeListener(userListener);
//    PlatformUI.getWorkbench().removeWindowListener(winlistener);
//  }
//
//  @Override protected void onEnable() {
//    checkStart();
//    for (IWorkbenchWindow win : getWorkbenchWindows()) {
//      win.addPerspectiveListener(perspectiveListener);
//    }
//    monitor.addListener(userListener);
//    PlatformUI.getWorkbench().addWindowListener(winlistener);
//  }
//
//  private void checkStart() {
//    IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
//    if (WorkbenchUtil.isActiveShell(win)) {
//      checkStart(win);
//    }
//  }
//
//  private void checkStart(IWorkbenchWindow activeWin) {
//    IPerspectiveDescriptor p = WorkbenchUtil.getPerspective(activeWin);
//    if (p != null) {
//      recorder.start(p);
//    }
//  }
//
//  private IWorkbenchWindow[] getWorkbenchWindows() {
//    return PlatformUI.getWorkbench().getWorkbenchWindows();
//  }
//
//  @Override public void save() {
//    support.notifyOnSave();
//  }
//
// }