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
package rabbit.tasks.ui.internal.util;

import java.util.Date;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

import rabbit.tasks.core.TaskId;

/**
 * Represents a task that has been missing or deleted.
 */
public final class MissingTask implements ITask {

	/**
	 * The id of this task.
	 */
	public final TaskId taskId;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of this task.
	 */
	public MissingTask(TaskId id) {
		if (id == null) {
			throw new NullPointerException();
		}
		this.taskId = id;
	}

	@Override
	public int compareTo(IRepositoryElement o) {
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MissingTask) {
			return ((MissingTask) obj).taskId.equals(taskId);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public String getAttribute(String key) {
		return null;
	}

	@Override
	public Map<String, String> getAttributes() {
		return null;
	}

	@Override
	public Date getCompletionDate() {
		return null;
	}

	@Override
	public String getConnectorKind() {
		return null;
	}

	@Override
	public Date getCreationDate() {
		return taskId.getCreationDate();
	}

	@Override
	public Date getDueDate() {
		return null;
	}

	@Override
	public String getHandleIdentifier() {
		return taskId.getHandleIdentifier();
	}

	@Override
	public Date getModificationDate() {
		return null;
	}

	@Override
	public String getOwner() {
		return null;
	}

	@Override
	public String getPriority() {
		return null;
	}

	@Override
	public String getRepositoryUrl() {
		return null;
	}

	@Override
	public String getSummary() {
		return "Handle ID: " + taskId.getHandleIdentifier()
				+ ", created on: " + taskId.getCreationDate().toString();
	}

	@Override
	public SynchronizationState getSynchronizationState() {
		return null;
	}

	@Override
	public String getTaskId() {
		return null;
	}

	@Override
	public String getTaskKey() {
		return null;
	}

	@Override
	public String getTaskKind() {
		return null;
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public int hashCode() {
		return taskId.hashCode();
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	@Override
	public void setAttribute(String key, String value) {
	}

	@Override
	public void setCompletionDate(Date completionDate) {
	}

	@Override
	public void setCreationDate(Date date) {
	}

	@Override
	public void setDueDate(Date date) {
	}

	@Override
	public void setModificationDate(Date modificationDate) {
	}

	@Override
	public void setOwner(String owner) {
	}

	@Override
	public void setPriority(String priority) {
	}

	@Override
	public void setSummary(String summary) {
	}

	@Override
	public void setTaskKey(String taskKey) {
	}

	@Override
	public void setTaskKind(String kind) {
	}

	@Override
	public void setUrl(String taskUrl) {
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + taskId.toString();
	}
}
