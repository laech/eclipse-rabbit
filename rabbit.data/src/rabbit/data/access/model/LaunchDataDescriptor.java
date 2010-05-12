package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.joda.time.LocalDate;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Date descriptor for launch events.
 */
public final class LaunchDataDescriptor extends DateDescriptor {

  @Nonnull
  private final ImmutableSet<IPath> filePaths;
  @Nonnull
  private final LaunchConfigurationDescriptor launch;
  private final long totalDuration;
  private final int count;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param launch The launch configuration description.
   * @param count The number of time this launch configuration is executed.
   * @param totalDuration The total duration of the launches, in milliseconds.
   * @param filePaths The paths of the files involved, or an empty collection.
   * @throws NullPointerException If date, or launch, or fileIds is null.
   * @throws IllegalArgumentException If count < 0, or totalDuration < 0.
   */
  public LaunchDataDescriptor(@Nonnull LocalDate dt,
                              @Nonnull LaunchConfigurationDescriptor launch, 
                                       int count,
                                       long totalDuration, 
                              @Nonnull Iterable<IPath> filePaths) {

    super(dt);
    checkNotNull(launch);
    checkArgument(totalDuration >= 0);
    checkArgument(count >= 0);
    checkNotNull(filePaths);

    this.filePaths = ImmutableSet.copyOf(filePaths);
    this.launch = launch;
    this.count = count;
    this.totalDuration = totalDuration;
  }

  @Override
  public int hashCode() {
    return getLaunchDescriptor().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    LaunchDataDescriptor des = (LaunchDataDescriptor) obj;
    return des.getDate().equals(getDate())
        && des.getLaunchCount() == getLaunchCount()
        && des.getTotalDuration() == getTotalDuration()
        && des.getFilePaths().equals(getFilePaths())
        && des.getLaunchDescriptor().equals(getLaunchDescriptor());
  }

  /**
   * Finds the associate files of this launch. The files does not need to exist
   * in the workspace, but their paths must be valid, files with invalid paths
   * are excluded by this method.
   * 
   * @return An unmodifiable set of files, or an empty set if no files.
   * @see #getFilePaths()
   */
  @Nonnull
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
  @Nonnull
  public final Set<IPath> getFilePaths() {
    return filePaths;
  }

  /**
   * Gets the launch configuration description.
   * 
   * @return The launch configuration description, never null.
   */
  @Nonnull
  public final LaunchConfigurationDescriptor getLaunchDescriptor() {
    return launch;
  }

  /**
   * Gets the total duration of the launches.
   * 
   * @return
   */
  public final long getTotalDuration() {
    return totalDuration;
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
