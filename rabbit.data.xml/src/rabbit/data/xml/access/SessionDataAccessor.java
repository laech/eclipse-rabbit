package rabbit.data.xml.access;

import rabbit.data.access.IAccessor2;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.access.model.SessionDataDescriptor;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Map;

/**
 * Accesses session data events.
 */
public class SessionDataAccessor implements IAccessor2<SessionDataDescriptor> {

  /** Uses a perspective data accessor to get the data out. */
  private final IAccessor2<PerspectiveDataDescriptor> accessor;

  /**
   * Constructor.
   */
  public SessionDataAccessor() {
    accessor = new PerspectiveDataAccessor();
  }

  @Override
  public ImmutableCollection<SessionDataDescriptor> getData(LocalDate start,
                                                            LocalDate end) {

    Collection<PerspectiveDataDescriptor> data = accessor.getData(start, end);

    Map<LocalDate, Long> map = Maps.newLinkedHashMap();
    for (PerspectiveDataDescriptor des : data) {
      Long value = map.get(des.getDate());
      map.put(des.getDate(),
          (value != null) ? value + des.getValue() : des.getValue());
    }
    
    ImmutableSet.Builder<SessionDataDescriptor> result = ImmutableSet.builder();
    for (Map.Entry<LocalDate, Long> entry : map.entrySet())
      result.add(new SessionDataDescriptor(entry.getKey(), entry.getValue()));

    return result.build();
  }

}
