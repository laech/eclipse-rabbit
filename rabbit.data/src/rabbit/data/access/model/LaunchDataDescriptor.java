package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * Date descriptor for launch events.
 */
public final class LaunchDataDescriptor extends DurationDescriptor {

  private final ImmutableSet<IPath> filePaths;
  private final LaunchConfigurationDescriptor launch;
  private final int count;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param launch The launch configuration description.
   * @param count The number of time this launch configuration is executed.
   * @param totalDuration The total duration of the launches.
   * @param filePaths The paths of the files involved, or an empty collection.
   * @throws NullPointerException If any of the arguments are null.
   * @throws IllegalArgumentException If count < 0.
   */
  public LaunchDataDescriptor(LocalDate dt,
                              LaunchConfigurationDescriptor launch, 
                              int count,
                              Duration totalDuration, 
                              Iterable<IPath> filePaths) {

    super(dt, totalDuration);
    checkArgument(count >= 0);

    this.filePaths = ImmutableSet.copyOf(checkNotNull(filePaths));
    this.launch = checkNotNull(launch);;
    this.count = count;
  }

  /**
   * Finds the associate files of this launch. The files does not need to exist
   * in the workspace, but their paths must be valid, files with invalid paths
   * are excluded by this method.
   * 
   * @return An unmodifiable set of files, or an empty set if no files.
   * @see #getFilePaths()
   */
  public final Set<IFile> findFiles() {
    ImmutableSet.Builder<IFile> builder = ImmutableSet.builder();
    IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
    for (IPath path : getFilePaths()) {
      if (path.segmentCount() >= 2) {
        builder.add(workspace.getFile(path));
      }
    }
    return builder.build();
  }

  /**
   * Gets the file paths of this launch.
   * 
   * @return An unmodifiable set of paths of the files involved, never null.
   */
  public final Set<IPath> getFilePaths() {
    return filePaths;
  }

  /**
   * Gets the launch configuration description.
   * 
   * @return The launch configuration description, never null.
   */
  public final LaunchConfigurationDescriptor getLaunchDescriptor() {
    return launch;
  }

  /**
   * Gets the number of time this launch configuration is executed.
   * 
   * @return The number of time this launch configuration is executed.
   */
  public final int getLaunchCount() {
    return count;
  }
}
