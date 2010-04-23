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

import rabbit.data.IFileStore;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Content provider for a {@link TreeViewer} that accepts input as a
 * {@link Collection} of {@link FileDataDescriptor}.
 */
public class ResourcePageContentProvider extends AbstractValueContentProvider {

  /**
   * Categories supported by {@link ResourcePageContentProvider}. For
   * structuring the data.
   */
  public static enum Category implements ICategory {

    /** Date category */
    DATE("Dates", SharedImages.CALENDAR),

    /** Project category */
    PROJECT("Projects", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),

    /** Folder category */
    FOLDER("Folders", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)),

    /** File category */
    FILE("Files", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

    private String text;
    private ImageDescriptor image;

    private Category(String text, ImageDescriptor image) {
      this.text = text;
      this.image = image;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return image;
    }

    @Override
    public String getText() {
      return text;
    }
  }

  /**
   * Constructor a content provider for the given viewer.
   * 
   * @param viewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  public ResourcePageContentProvider(TreeViewer viewer) {
    super(viewer);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    reorganizeData((Collection<FileDataDescriptor>) newInput);
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return Category.values();
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.PROJECT;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.PROJECT, Category.FOLDER, Category.FILE };
  }

  @Override
  protected ImmutableBiMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableBiMap.<ICategory, Predicate<Object>> builder()
        .put(Category.PROJECT, Predicates.instanceOf(IProject.class))
        .put(Category.FOLDER,  Predicates.instanceOf(IFolder.class))
        .put(Category.FILE,    Predicates.instanceOf(IFile.class))
        .put(Category.DATE,    Predicates.instanceOf(LocalDate.class))
        .build();
  }

  /**
   * Reorganizes the data according to {@link #getSelectedCategories()}.
   */
  private void reorganizeData(Collection<FileDataDescriptor> data) {
    getRoot().setChildren(null);

    ICategory[] categories = getSelectedCategories();
    IFileStore store = DataHandler.getFileStore();
    for (FileDataDescriptor des : data) {

      IFile file = store.getFile(des.getFileId());
      if (file == null) {
        file = store.getExternalFile(des.getFileId());
      }
      if (file == null) {
        continue;
      }
      IProject project = file.getProject();
      IContainer folder = file.getParent();

      TreeNode node = getRoot();
      for (ICategory cat : categories) {
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.PROJECT == cat) {
          node = TreeNodes.findOrAppend(node, project);
          
        } else if (Category.FOLDER == cat) {
          if (!folder.equals(project))
            node = TreeNodes.findOrAppend(node, folder);
          
        } else if (Category.FILE == cat) {
          node = TreeNodes.findOrAppend(node, file);
        }
      }
      TreeNodes.appendToParent(node, des.getValue());
    }
  }
}