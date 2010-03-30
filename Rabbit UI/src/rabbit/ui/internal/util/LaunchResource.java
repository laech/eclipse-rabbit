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
