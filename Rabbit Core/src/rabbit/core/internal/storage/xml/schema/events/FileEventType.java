//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.15 at 11:02:17 AM NZDT 
//


package rabbit.core.internal.storage.xml.schema.events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fileEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fileId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute ref="{}duration use="required""/>
 *       &lt;attribute name="taskHandleId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileEventType")
public class FileEventType {

    @XmlAttribute(required = true)
    protected String fileId;
    @XmlAttribute(required = true)
    protected long duration;
    @XmlAttribute
    protected String taskHandleId;

    /**
     * Gets the value of the fileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the value of the fileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileId(String value) {
        this.fileId = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     */
    public void setDuration(long value) {
        this.duration = value;
    }

    /**
     * Gets the value of the taskHandleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskHandleId() {
        return taskHandleId;
    }

    /**
     * Sets the value of the taskHandleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskHandleId(String value) {
        this.taskHandleId = value;
    }

}
