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
import rabbit.data.handler.DataHandler2;
import rabbit.ui.CellPainter.IValueProvider;
import rabbit.ui.internal.util.TreeNodes;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Map;

// TODO test
public class ResourcePageContentProvider extends TreeNodeContentProvider
    implements IValueProvider {

  private ResourcePage page;

  public ResourcePageContentProvider(ResourcePage parent) {
    this.page = parent;

    maxFileValue = 0;
    maxFolderValue = 0;
    maxProjectValue = 0;
    treeNodeValues = Maps.newHashMap();
    root = new TreeNode(new Object());
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return (root.getChildren() != null) ? root.getChildren() : new Object[0];
  }

  @Override
  public boolean hasChildren(Object element) {
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null || node.getChildren().length == 0) {
      return false;
    }

    /*
     * Hides the pure numeric tree nodes, these are nodes with java.lang.Long
     * objects hanging at the end of the branches, we only use those to
     * calculate values for the parents, not to be shown to the users:
     */
    if (node.getChildren()[0].getValue() instanceof Long) {
      return false;
    }
    
    switch (page.getShowMode()) {
    case PROJECT:
      return !(node.getValue() instanceof IProject);
    case FOLDER:
      return !(node.getValue() instanceof IFolder);
    default:
      return true;
    }
  }

  private TreeNode root;
  private Map<TreeNode, Long> treeNodeValues;

  @SuppressWarnings("unchecked")
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    if (newInput == null) {
      root.setChildren(null);
    }

    root = new TreeNode(new Object());
    IFileStore store = DataHandler2.getFileMapper();
    Collection<FileDataDescriptor> data = (Collection<FileDataDescriptor>) newInput;
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

      TreeNode node = root;
      //node = TreeNodes.findOrAppend(node, des.getDate());
      node = TreeNodes.findOrAppend(node, project);
      if (!folder.equals(project)) {
        node = TreeNodes.findOrAppend(node, folder);
      }
      node = TreeNodes.findOrAppend(node, file);
      TreeNodes.appendParent(node, des.getValue());
    }

    treeNodeValues = Maps.newHashMapWithExpectedSize(data.size() * 2);
    maxFileValue = TreeNodes.findMaxValue(root, IFile.class);
    maxFolderValue = TreeNodes.findMaxValue(root, IFolder.class);
    maxProjectValue = TreeNodes.findMaxValue(root, IProject.class);
  }

  private long maxFileValue;
  private long maxFolderValue;
  private long maxProjectValue;

  @Override
  public long getMaxValue() {
    switch (page.getShowMode()) {
    case FILE:
      return maxFileValue;
    case FOLDER:
      return maxFolderValue;
    case PROJECT:
      return maxProjectValue;
    default:
      return 0;
    }
  }

  @Override
  public long getValue(Object element) {
    if (false == element instanceof TreeNode)
      return 0;

    TreeNode node = (TreeNode) element;
    Long value = treeNodeValues.get(node);
    if (value == null) {
      value = TreeNodes.getLongValue(node);
      treeNodeValues.put(node, value);
    }
    return value;
  }

  @Override
  public boolean shouldPaint(Object element) {
    if (false == element instanceof TreeNode)
      return false;

    TreeNode node = (TreeNode) element;
    switch (page.getShowMode()) {
    case FILE:
      return node.getValue() instanceof IFile;
    case FOLDER:
      return node.getValue() instanceof IFolder;
    case PROJECT:
      return node.getValue() instanceof IProject;
    }
    return false;
  }
}