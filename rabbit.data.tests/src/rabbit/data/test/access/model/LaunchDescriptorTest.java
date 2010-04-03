/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.data.test.access.model;

import rabbit.data.access.model.LaunchDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchDescriptor
 */
public class LaunchDescriptorTest {

  private LaunchDescriptor descriptor;

  @Before
  public void before() {
    descriptor = new LaunchDescriptor();
  }

  @Test
  public void testEquals() {
    long duration = 102;
    Set<String> fileIds = new HashSet<String>(Arrays.asList("1", "2"));
    String launchMode = "debug";
    String launchName = "name";
    String launchType = "type";

    LaunchDescriptor des1 = new LaunchDescriptor();
    LaunchDescriptor des2 = new LaunchDescriptor();
    assertTrue("Should be equal when first created.", des1.equals(des2));

    des1.setTotalDuration(duration);
    assertFalse("Should not be equal when fields are different.", des1
        .equals(des2));

    des1.getFileIds().addAll(fileIds);
    des1.setLaunchModeId(launchMode);
    des1.setLaunchName(launchName);
    des1.setLaunchTypeId(launchType);
    assertFalse("Should not be equal when fields are different.", des1
        .equals(des2));

    des2.setTotalDuration(duration);
    des2.getFileIds().addAll(fileIds);
    des2.setLaunchModeId(launchMode);
    des2.setLaunchName(launchName);
    des2.setLaunchTypeId(launchType);
    assertTrue("Should be equal when all fields are the same.", des1
        .equals(des2));
  }

  @Test
  public void testGetCount() {
    assertEquals("Count should be 0 by default", 0, descriptor.getCount());
  }

  @Test
  public void testGetDuration() {
    assertEquals("Duration should be 0 by default.", 0, descriptor
        .getTotalDuration());
  }

  @Test
  public void testGetFileIds() {
    assertNotNull("File IDs should not be null.", descriptor.getFileIds());

    assertTrue("File IDs should be empty by default.", descriptor.getFileIds()
        .isEmpty());
  }

  @Test
  public void testGetFileIds_modifiable() {
    try {
      descriptor.getFileIds().add("A");
    } catch (UnsupportedOperationException e) {
      fail();
    }
  }

  @Test
  public void testGetLaunchMode() {
    assertNotNull("Launch mode should not be null.", descriptor
        .getLaunchModeId());

    assertSame("Launch mode should be empty string by default", "", descriptor
        .getLaunchModeId());
  }

  @Test
  public void testGetLaunchName() {
    assertEquals("Launch name should be empty string by default.", "",
        descriptor.getLaunchName());
  }

  @Test
  public void testGetLaunchType() {
    assertEquals("Launch type should be empty string by default.", "",
        descriptor.getLaunchTypeId());
  }

  @Test
  public void testHashCode() {
    int hashCode = (descriptor.getFileIds().hashCode()
        + descriptor.getLaunchName().hashCode()
        + descriptor.getLaunchTypeId().hashCode() + descriptor
        .getLaunchModeId().hashCode()) % 31;
    assertEquals(hashCode, descriptor.hashCode());
  }

  @Test
  public void testSetCount() {
    int count = 13873;
    assertTrue(descriptor.setCount(count));
    assertEquals(count, descriptor.getCount());
  }

  @Test
  public void testSetCount_negative() {
    int count = -1;
    assertFalse(descriptor.setCount(count));
    assertFalse(descriptor.getCount() == count);
  }

  @Test
  public void testSetCount_zero() {
    int count = 0;
    assertTrue(descriptor.setCount(count));
    assertEquals(count, descriptor.getCount());
  }

  @Test
  public void testSetLaunchMode() {
    String mode = "run";
    assertTrue(descriptor.setLaunchModeId(mode));
    assertEquals(mode, descriptor.getLaunchModeId());

    mode = "debug";
    assertTrue(descriptor.setLaunchModeId(mode));
    assertEquals(mode, descriptor.getLaunchModeId());
  }

  @Test
  public void testSetLaunchMode_null() {
    assertFalse(descriptor.setLaunchModeId(null));
  }

  @Test
  public void testSetLaunchName() {
    String name = "adfasdf244";
    assertTrue(descriptor.setLaunchName(name));
    assertEquals(name, descriptor.getLaunchName());

    name = "asdfjh237";
    assertTrue(descriptor.setLaunchName(name));
    assertEquals(name, descriptor.getLaunchName());
  }

  @Test
  public void testSetLaunchName_null() {
    assertFalse(descriptor.setLaunchName(null));
  }

  @Test
  public void testSetLaunchType() {
    String type = "adfjh298f";
    assertTrue(descriptor.setLaunchTypeId(type));
    assertEquals(type, descriptor.getLaunchTypeId());

    type = "987324iuyfjsdg";
    assertTrue(descriptor.setLaunchTypeId(type));
    assertEquals(type, descriptor.getLaunchTypeId());
  }

  @Test
  public void testSetLaunchType_null() {
    assertFalse(descriptor.setLaunchTypeId(null));
  }

  @Test
  public void testSetTotalDuration() {
    long duration = System.currentTimeMillis();
    assertTrue(descriptor.setTotalDuration(duration));
    assertEquals(duration, descriptor.getTotalDuration());
  }

  @Test
  public void testSetTotalDuration_negative() {
    long duration = -1;
    assertFalse(descriptor.setTotalDuration(duration));
    assertFalse(duration == descriptor.getTotalDuration());
  }

  @Test
  public void testSetTotalDuration_zero() {
    long duration = 0;
    assertTrue(descriptor.setTotalDuration(duration));
    assertEquals(duration, descriptor.getTotalDuration());
  }
}