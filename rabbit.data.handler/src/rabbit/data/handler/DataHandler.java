package rabbit.data.handler;

import rabbit.data.IFileMapper;
import rabbit.data.access.IAccessor;
import rabbit.data.access.model.LaunchDescriptor;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.xml.XmlFileMapper;
import rabbit.data.xml.access.CommandDataAccessor;
import rabbit.data.xml.access.FileDataAccessor;
import rabbit.data.xml.access.LaunchDataAccessor;
import rabbit.data.xml.access.PartDataAccessor;
import rabbit.data.xml.access.PerspectiveDataAccessor;
import rabbit.data.xml.access.SessionDataAccessor;
import rabbit.data.xml.store.CommandEventStorer;
import rabbit.data.xml.store.FileEventStorer;
import rabbit.data.xml.store.LaunchEventStorer;
import rabbit.data.xml.store.PartEventStorer;
import rabbit.data.xml.store.PerspectiveEventStorer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataHandler {

  /** Map<T, IStorer<T> */
  private static final Map<Class<?>, IStorer<?>> storers;

  private static final IAccessor<Set<LaunchDescriptor>> launchDataAccessor;
  private static final IAccessor<Map<String, Long>> perspectiveDataAccessor;
  private static final IAccessor<Map<String, Long>> commandDataAccessor;
  private static final IAccessor<Map<String, Long>> sessionDataAccessor;
  private static final IAccessor<Map<String, Long>> partDataAccessor;
  private static final IAccessor<Map<String, Long>> fileDataAccessor;

  static {
    Map<Class<?>, IStorer<?>> map = new HashMap<Class<?>, IStorer<?>>();
    map.put(PerspectiveEvent.class, PerspectiveEventStorer.getInstance());
    map.put(CommandEvent.class, CommandEventStorer.getInstance());
    map.put(FileEvent.class, FileEventStorer.getInstance());
    map.put(PartEvent.class, PartEventStorer.getInstance());
    map.put(LaunchEvent.class, LaunchEventStorer.getInstance());
    storers = Collections.unmodifiableMap(map);

    perspectiveDataAccessor = new PerspectiveDataAccessor();
    commandDataAccessor = new CommandDataAccessor();
    sessionDataAccessor = new SessionDataAccessor();
    launchDataAccessor = new LaunchDataAccessor();
    partDataAccessor = new PartDataAccessor();
    fileDataAccessor = new FileDataAccessor();
  }

  /**
   * Gets an IAccessor for accessing the command event data.
   * 
   * @return An IAccessor for accessing the command event data.
   */
  public static IAccessor<Map<String, Long>> getCommandDataAccessor() {
    return commandDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the file event data.
   * 
   * @return An IAccessor for accessing the file event data.
   */
  public static IAccessor<Map<String, Long>> getFileDataAccessor() {
    return fileDataAccessor;
  }

  /**
   * Gets the resource manager.
   * 
   * @return The resource manager.
   */
  public static IFileMapper getFileMapper() {
    return XmlFileMapper.INSTANCE;
  }

  /**
   * Gets an IAccessor for accessing the launch event data.
   * 
   * @return An IAccessor for accessing the launch event data.
   */
  public static IAccessor<Set<LaunchDescriptor>> getLaunchDataAccessor() {
    return launchDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the part event data.
   * 
   * @return An IAccessor for accessing the part event data.
   */
  public static IAccessor<Map<String, Long>> getPartDataAccessor() {
    return partDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the perspective event data.
   * 
   * @return An IAccessor for accessing the perspective event data.
   */
  public static IAccessor<Map<String, Long>> getPerspectiveDataAccessor() {
    return perspectiveDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the session event data.
   * 
   * @return An IAccessor for accessing the session event data.
   */
  public static IAccessor<Map<String, Long>> getSessionDataAccessor() {
    return sessionDataAccessor;
  }

  /**
   * Gets a storer that stores the objects of the given type.
   * <p>
   * The following object types are supported:
   * <ul>
   * <li>{@link CommandEvent}</li>
   * <li>{@link FileEvent}</li>
   * <li>{@link PartEvent}</li>
   * <li>{@link PerspectiveEvent}</li>
   * <li>{@link LaunchEvent}</li>
   * </ul>
   * </p>
   * 
   * @param <T> The type of the objects that the storer can store.
   * @param objectClass The class of the type.
   * @return A storer that stores the objects of the given type, or null.
   * @throws NullPointerException If null is passed in.
   */
  @SuppressWarnings("unchecked")
  public static <T> IStorer<T> getStorer(Class<T> objectClass) {
    if (null == objectClass) {
      throw new NullPointerException();
    }
    Object storer = storers.get(objectClass);
    return (null == storer) ? null : (IStorer<T>) storer;
  }

  private DataHandler() {
  }
}
