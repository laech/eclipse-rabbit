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
package rabbit.ui.internal.util;

import org.eclipse.core.resources.IResource;

import rabbit.core.storage.LaunchDescriptor;

public final class LaunchResource {
	
	private final LaunchDescriptor launchDescriptor;
	private final IResource resource;

	public LaunchResource(LaunchDescriptor des, IResource res) {
		this.launchDescriptor = des;
		this.resource = res;
	}
	
	public LaunchDescriptor getLaunchDescriptor() {
		return launchDescriptor;
	}
	
	public IResource getResource() {
		return resource;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LaunchResource) {
			LaunchResource res = (LaunchResource) obj;
			return res.launchDescriptor.equals(launchDescriptor) 
					&& res.resource.equals(resource);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (resource.hashCode() + launchDescriptor.hashCode()) % 31;
	}
}
