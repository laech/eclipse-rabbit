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
package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.Pair;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;
import rabbit.ui.internal.viewers.TreeNodes;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import javax.annotation.Nullable;

/**
 * Label provider a {@link LaunchPage}.
 */
class LaunchPageLabelProvider extends LabelProvider 
    implements ITableLabelProvider, IColorProvider {

  private final ResourcePageLabelProvider resourceLabels;
  private final IValueProvider launchCounts;
  private final IValueProvider launchDurations;
  private final Image launchImage;
  private final Color gray;
  
  /**
   * Constructs a new label provider.
   * 
   * @param launchCountValueProvider The value provider for providing launch
   *          counts.
   * @param launchDurationValueProvider The value provider for providing launch
   *          durations.
   */
  LaunchPageLabelProvider(IValueProvider launchCountValueProvider,
                          IValueProvider launchDurationValueProvider) {

    launchCounts = checkNotNull(launchCountValueProvider);
    launchDurations = checkNotNull(launchDurationValueProvider);
    resourceLabels = new ResourcePageLabelProvider();
    launchImage = SharedImages.ELEMENT.createImage();
    gray = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    launchImage.dispose();
    resourceLabels.dispose();
  }

  @Override
  public Color getBackground(@Nullable Object element) {
    return null;
  }

  @Override
  public Image getImage(@Nullable Object element) {
    element = TreeNodes.getObject(element);
    if (element instanceof ILaunchMode) {
      return getLaunchModeImage(((ILaunchMode) element).getIdentifier());

    } else if (element instanceof ILaunchConfigurationType) {
      String id = ((ILaunchConfigurationType) element).getIdentifier();
      return DebugUITools.getImage(id);

    } else if (element instanceof Pair<?, ?>) { // <LaunchName, LaunchTypeId>
      return DebugUITools.getImage(((Pair<?, ?>) element).getSecond()
          .toString());
    }
    return resourceLabels.getImage(element);
  }

  @Override
  public Image getColumnImage(@Nullable Object element, int columnIndex) {
    return (columnIndex == 0) ? getImage(element) : null;
  }

  @Override
  public String getText(@Nullable Object element) {
    Object value = TreeNodes.getObject(element);
    if (value instanceof ILaunchMode) {
      return ((ILaunchMode) value).getLabel().replace("&", "");
      
    } else if (value instanceof ILaunchConfigurationType) {
      return ((ILaunchConfigurationType) value).getName();
      
    } else if (value instanceof Pair<?, ?>) { // <LaunchName, LaunchTypeId>
      return ((Pair<?, ?>) value).getFirst().toString();
      
    } else {
      return resourceLabels.getText(element);
    }
  }

  @Override
  public String getColumnText(@Nullable Object element, int columnIndex) {
    if (columnIndex == 0) {
      return getText(element);
    }

    switch (columnIndex) {
    case 1:
      if (launchCounts.shouldPaint(element)) {
        return launchCounts.getValue(element) + "";
      }
    case 3:
      if (launchDurations.shouldPaint(element)) {
        return format(launchDurations.getValue(element));
      }
    default:
      return null;
    }
  }

  @Override
  public Color getForeground(@Nullable Object element) {
    element = TreeNodes.getObject(element);
    if (element instanceof UndefinedLaunchMode
        || element instanceof UndefinedLaunchConfigurationType) {
      return gray;
      
    } else {
      return resourceLabels.getForeground(element);
    }
  }

  private Image getLaunchModeImage(String modeId) {
    if (modeId.equals(ILaunchManager.DEBUG_MODE)) {
      return DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_DEBUG);
      
    } else if (modeId.equals(ILaunchManager.RUN_MODE)) {
      return DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN);
      
    } else {
      return DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_ENVIRONMENT);
    }
  }
}
