package rabbit.data.test.access.model;

import rabbit.data.access.model.LaunchConfigurationDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see LaunchConfigurationDescriptor
 */
public class LaunchConfigurationDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_nameNull() {
    createDescriptor(null, "modeId", "typeId");
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_modeIdNull() {
    createDescriptor("name", null, "typeId");
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_typeIdNull() {
    createDescriptor("name", "modeId", null);
  }

  @Test
  public void testGetName() {
    String name = "aName";
    assertEquals(name, createDescriptor(name, "", "").getLaunchName());
  }

  @Test
  public void testGetModeId() {
    String id = "modeId";
    assertEquals(id, createDescriptor("", id, "").getLaunchModeId());
  }

  @Test
  public void testGetTypeId() {
    String id = "typeId";
    assertEquals(id, createDescriptor("", "", id).getLaunchTypeId());
  }
  
  @Test
  public void testHashCode() {
    String name = "name";
    String mode = "mode";
    String type = "type";
    int hashcode = (name.hashCode() + mode.hashCode() + type.hashCode()) % 31;
    
    LaunchConfigurationDescriptor des = createDescriptor(name, mode, type);
    assertEquals(hashcode, des.hashCode());
  }
  
  @Test
  public void testEquals() {
    String name = "name";
    String mode = "mode";
    String type = "type";
    
    LaunchConfigurationDescriptor des1 = createDescriptor(name, mode, type);
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(new Object()));
    assertTrue(des1.equals(des1));
    
    LaunchConfigurationDescriptor des2 = createDescriptor(name, mode, type);
    assertTrue(des1.equals(des2));
    
    des2 = createDescriptor(name + ".", mode, type);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(name, mode + ".", type);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(name, mode, type + ".");
    assertFalse(des1.equals(des2));
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param name The name of the descriptor.
   * @param modeId The mode ID of the descriptor.
   * @param typeId The type ID of the descriptor.
   * @return A descriptor created using the parameters.
   */
  protected LaunchConfigurationDescriptor createDescriptor(String name,
      String modeId, String typeId) {
    return new LaunchConfigurationDescriptor(name, modeId, typeId);
  }
}
