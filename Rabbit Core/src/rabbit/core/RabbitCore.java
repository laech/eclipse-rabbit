/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rabbit.core.events.CommandEvent;
import rabbit.core.events.FileEvent;
import rabbit.core.events.PartEvent;
import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.CommandDataAccessor;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.FileDataAccessor;
import rabbit.core.internal.storage.xml.FileEventStorer;
import rabbit.core.internal.storage.xml.LaunchDataAccessor;
import rabbit.core.internal.storage.xml.PartDataAccessor;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.PerspectiveDataAccessor;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;
import rabbit.core.internal.storage.xml.SessionDataAccessor;
import rabbit.core.internal.storage.xml.XmlFileMapper;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IFileMapper;
import rabbit.core.storage.IStorer;
import rabbit.core.storage.LaunchDescriptor;

public class RabbitCore {

	public static enum AccessorType {
		PERSPECTIVE, PART, SESSION, COMMAND, FILE,
	}

	/** Map<T, IStorer<T> */
	private static final Map<Class<?>, IStorer<?>> storers;

	private static final Map<AccessorType, IAccessor<Map<String, Long>>> accessors;

	static {
		Map<Class<?>, IStorer<?>> map = new HashMap<Class<?>, IStorer<?>>();
		map.put(PerspectiveEvent.class, PerspectiveEventStorer.getInstance());
		map.put(CommandEvent.class, CommandEventStorer.getInstance());
		map.put(FileEvent.class, FileEventStorer.getInstance());
		map.put(PartEvent.class, PartEventStorer.getInstance());
		storers = Collections.unmodifiableMap(map);

		Map<AccessorType, IAccessor<Map<String, Long>>> accessorMap =
				new HashMap<AccessorType, IAccessor<Map<String, Long>>>();
		accessorMap.put(AccessorType.PERSPECTIVE, new PerspectiveDataAccessor());
		accessorMap.put(AccessorType.COMMAND, new CommandDataAccessor());
		accessorMap.put(AccessorType.FILE, new FileDataAccessor());
		accessorMap.put(AccessorType.PART, new PartDataAccessor());
		accessorMap.put(AccessorType.SESSION, new SessionDataAccessor());
		accessors = Collections.unmodifiableMap(accessorMap);
	}

	/**
	 * Gets an accessor that gets the data from the database.
	 * 
	 * @param type
	 *            The type of accessor.
	 * @return An accessor that can get the data.
	 * @throws NullPointerException
	 *             If null is passed in.
	 */
	public static IAccessor<Map<String, Long>> getAccessor(AccessorType type) {
		if (null == type) {
			throw new NullPointerException();
		}
		IAccessor<Map<String, Long>> accessor = accessors.get(type);
		return (accessor == null) ? null : accessor;
	}
	
	private final static IAccessor<Set<LaunchDescriptor>> launchDataAccessor = new LaunchDataAccessor();
	//TODO
	public static IAccessor<Set<LaunchDescriptor>> getLaunchDataAccessor() {
		return launchDataAccessor;
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
	 * Gets a storer that stores the objects of the given type.
	 * <p>
	 * The following object types are supported:
	 * <ul>
	 * <li>{@link CommandEvent}</li>
	 * <li>{@link FileEvent}</li>
	 * <li>{@link PartEvent}</li>
	 * <li>{@link PerspectiveEvent}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param <T>
	 *            The type of the objects that the storer can store.
	 * @param objectClass
	 *            The class of the type.
	 * @return A storer that stores the objects of the given type, or null.
	 * @throws NullPointerException
	 *             If null is passed in.
	 */
	@SuppressWarnings("unchecked")
	public static <T> IStorer<T> getStorer(Class<T> objectClass) {
		if (null == objectClass) {
			throw new NullPointerException();
		}
		Object storer = storers.get(objectClass);
		return (null == storer) ? null : (IStorer<T>) storer;
	}

	/**
	 * The constructor.
	 */
	private RabbitCore() {
	}
}
