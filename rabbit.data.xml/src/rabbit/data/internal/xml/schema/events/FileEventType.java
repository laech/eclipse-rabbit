//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.1-b02-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2010.04.01 at 11:52:54 AM NZDT
//

package rabbit.data.internal.xml.schema.events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for fileEventType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="fileEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{}durationEventType">
 *       &lt;attribute name="fileId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileEventType")
public class FileEventType extends DurationEventType {

  @XmlAttribute(required = true)
  protected String fileId;

  /**
   * Gets the value of the fileId property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getFileId() {
    return fileId;
  }

  /**
   * Sets the value of the fileId property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setFileId(String value) {
    this.fileId = value;
  }

}
