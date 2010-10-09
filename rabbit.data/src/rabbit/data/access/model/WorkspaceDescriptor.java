package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO
 */
public class WorkspaceDescriptor {

  private final WorkspaceStorage ws;

  /**
   * Constructor.
   * 
   * @param ws The object containing information about a workspace and its
   *          storage location.
   * @throws NullPointerException If argument is null.
   */
  public WorkspaceDescriptor(WorkspaceStorage ws) {
    this.ws = checkNotNull(ws);
  }

  /**
   * @return The object containing information about a workspace and its storage
   *         location.
   */
  public WorkspaceStorage getWorkspaceStorage() {
    return ws;
  }
}
