package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchMode;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Data descriptor for launch configurations.
 */
public class LaunchConfigurationDescriptor {

  @Nonnull
  private final String launchName;
  @Nonnull
  private final String launchModeId;
  @Nonnull
  private final String launchTypeId;

  /**
   * Constructs a new descriptor.
   * 
   * @param name The name of the launch configuration.
   * @param launchModeId The ID of the launch mode.
   * @param launchTypeId The ID of the launch type.
   * @throws NullPointerException If any of the arguments is null.
   */
  public LaunchConfigurationDescriptor(@Nonnull String name,
      @Nonnull String launchModeId, @Nonnull String launchTypeId) {
    checkNotNull(name);
    checkNotNull(launchModeId);
    checkNotNull(launchTypeId);

    this.launchName = name;
    this.launchModeId = launchModeId;
    this.launchTypeId = launchTypeId;
  }
  
  /**
   * Finds the launch configuration type that has the same ID as
   * {@link #getLaunchTypeId()}.
   * @return The launch configuration type, or null if not found.
   */
  @CheckForNull
  public final ILaunchConfigurationType findLaunchConfigurationType() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(getLaunchTypeId());
  }
  
  /**
   * Finds the launch mode that has the same ID as {@link #getLaunchModeId()}.
   * @return The launch mode, or null if not found.
   */
  @CheckForNull
  public final ILaunchMode findLaunchMode() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchMode(getLaunchModeId());
  }

  /**
   * Gets the ID of the launch mode.
   * 
   * @return The ID of the launch mode, never null.
   */
  @Nonnull
  public final String getLaunchModeId() {
    return launchModeId;
  }

  /**
   * Gets the ID of the launch type.
   * 
   * @return The ID of the launch type, never null.
   */
  @Nonnull
  public final String getLaunchTypeId() {
    return launchTypeId;
  }

  /**
   * Gets the name of the launch configuration.
   * 
   * @return The name of the launch configuration, never null.
   */
  @Nonnull
  public final String getLaunchName() {
    return launchName;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getLaunchName(), getLaunchModeId(),
        getLaunchTypeId());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    LaunchConfigurationDescriptor des = (LaunchConfigurationDescriptor) obj;
    return getLaunchName().equals(des.getLaunchName())
        && getLaunchModeId().equals(des.getLaunchModeId())
        && getLaunchTypeId().equals(des.getLaunchTypeId());
  }
}
