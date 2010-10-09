package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IPath;

import javax.annotation.Nullable;

public final class WorkspaceStorage {
  
  private final IPath storagePath;
  private final IPath workspacePath;
  
  public WorkspaceStorage(IPath storagePath, @Nullable IPath workspacePath) {
    this.storagePath = checkNotNull(storagePath);
    this.workspacePath = workspacePath;
  }

  public IPath getWorkspacePath() {
    return workspacePath;
  }

  public IPath getStoragePath() {
    return storagePath;
  }
}
