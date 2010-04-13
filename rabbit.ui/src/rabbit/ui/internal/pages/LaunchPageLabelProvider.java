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

import rabbit.data.access.model.ZLaunchDescriptor;
import rabbit.ui.internal.util.LaunchResource;
import rabbit.ui.internal.util.MillisConverter;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider a {@link LaunchPage}.
 */
public class LaunchPageLabelProvider extends BaseLabelProvider implements
    ITableLabelProvider, IColorProvider {

  protected final ResourcePageLabelProvider provider;
  protected ILaunchManager manager;

  /**
   * Constructs a new label provider.
   */
  public LaunchPageLabelProvider() {
    manager = DebugPlugin.getDefault().getLaunchManager();
    provider = new ResourcePageLabelProvider();
  }

  @Override
  public void dispose() {
    super.dispose();
    provider.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {

    if (columnIndex == 0) {
      if (element instanceof ZLaunchDescriptor) {
        return DebugUITools.getImage(((ZLaunchDescriptor) element)
            .getLaunchTypeId());

      } else if (element instanceof LaunchResource) {
        return provider.getImage(((LaunchResource) element).getResource());
      }
      return provider.getImage(element);

    } else if (columnIndex == 1) {
      if (element instanceof ZLaunchDescriptor) {
        String mode = ((ZLaunchDescriptor) element).getLaunchModeId();
        if (mode.equals(ILaunchManager.DEBUG_MODE)) {
          return DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_DEBUG);

        } else if (mode.equals(ILaunchManager.RUN_MODE)) {
          return DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN);
        }
      }
    }
    return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {

    switch (columnIndex) {
    case 0:
      if (element instanceof ZLaunchDescriptor) {
        return ((ZLaunchDescriptor) element).getLaunchName();

      } else if (element instanceof LaunchResource) {
        return provider.getText(((LaunchResource) element).getResource());
      }
      return provider.getText(element);

    case 1:
      if (element instanceof ZLaunchDescriptor) {
        return ((ZLaunchDescriptor) element).getLaunchModeId().toString();
      }
      break;

    case 2:
      if (element instanceof ZLaunchDescriptor) {
        return ((ZLaunchDescriptor) element).getCount() + "";
      }
      break;

    case 4:
      if (element instanceof ZLaunchDescriptor) {
        return MillisConverter.toDefaultString(((ZLaunchDescriptor) element)
            .getTotalDuration());
      }
      break;
    }
    return null;
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof LaunchResource) {
      return provider.getForeground(((LaunchResource) element).getResource());
    }
    return provider.getForeground(element);
  }
}
