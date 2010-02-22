package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.MillisConverter.toDefaultString;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.ui.internal.util.ResourceElement;
import rabbit.ui.internal.util.ResourceElement.ResourceType;

/**
 * Label provider for a {@link FilePage}.
 */
public class ResourcePageLabelProvider extends BaseLabelProvider implements ITableLabelProvider, IColorProvider {

	/** Maps the given path of a resource to an image (may be null). */
	private final Map<String, Image> resourceMap;
	/** Maps the given id of an editor to an image (may be null). */
	private final Map<String, Image> editorMap;

	private final IWorkspaceRoot workspace;
	private final Image projectImg;
	private final Image folderImg;
	private final Image fileImg;

	private final Color deletedResourceColor;

	private boolean showProjectValues;
	private boolean showFolderValues;
	private boolean showFileValues;

	public ResourcePageLabelProvider(boolean showProjectValues, boolean showFolderValues, boolean showFileValues) {
		workspace = ResourcesPlugin.getWorkspace().getRoot();
		deletedResourceColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		projectImg = images.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		folderImg = images.getImage(ISharedImages.IMG_OBJ_FOLDER);
		fileImg = images.getImage(ISharedImages.IMG_OBJ_FILE);

		editorMap = new HashMap<String, Image>();
		resourceMap = new HashMap<String, Image>();

		this.showProjectValues = showProjectValues;
		this.showFolderValues = showFolderValues;
		this.showFileValues = showFileValues;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ResourceElement) || columnIndex != 0) {
			return null;
		}

		ResourceElement resource = (ResourceElement) element;
		if (resource.getType() == ResourceType.PROJECT) {
			return projectImg;

		} else if (resource.getType() == ResourceType.FOLDER) {
			return folderImg;

		} else if (resource.getType() == ResourceType.FILE) {
			if (resourceMap.containsKey(resource.getPath())) {
				return resourceMap.get(resource.getPath());
			}

			IFile file = workspace.getFile(resource.getPath());
			IEditorDescriptor editor = IDE.getDefaultEditor(file);
			if (editor == null) {
				resourceMap.put(resource.getPath().toString(), fileImg);
				return fileImg;
			}

			if (editorMap.containsKey(editor.getId())) {
				return editorMap.get(editor.getId());
			}

			ImageDescriptor des = editor.getImageDescriptor();
			Image img = (des == null) ? null : des.createImage();
			editorMap.put(editor.getId(), img);
			return img;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof ResourceElement)) {
			return null;
		}
		ResourceElement resource = (ResourceElement) element;
		switch (columnIndex) {
		case 0:
			return resource.getName();
		case 1:
			if (resource.getType() == ResourceType.PROJECT) {
				return (showProjectValues) ? toDefaultString(resource.getValue()) : null;
			} else if (resource.getType() == ResourceType.FOLDER) {
				return (showFolderValues) ? toDefaultString(resource.getValue()) : null;
			} else if (resource.getType() == ResourceType.FILE) {
				return (showFileValues) ? toDefaultString(resource.getValue()) : null;
			}
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof ResourceElement && !((ResourceElement) element).exists()) {
			return deletedResourceColor;
		}
		return null;
	}

	@Override
	public void dispose() {
		for (Image img : resourceMap.values()) {
			if (img != null && !img.isDisposed()) {
				img.dispose();
			}
		}

		for (Image img : editorMap.values()) {
			if (img != null && !img.isDisposed()) {
				img.dispose();
			}
		}
		super.dispose();
	}
}