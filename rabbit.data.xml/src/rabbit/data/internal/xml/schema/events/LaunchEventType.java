//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.04 at 03:07:53 PM NZST 
//


package rabbit.data.internal.xml.schema.events;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for launchEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="launchEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{}countEventType">
 *       &lt;sequence>
 *         &lt;element name="filePath" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="launchModeId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="launchTypeId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="totalDuration" use="required" type="{}durationType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "launchEventType", propOrder = {
    "filePath"
})
public class LaunchEventType
    extends CountEventType
{

    protected List<String> filePath;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String launchModeId;
    @XmlAttribute
    protected String launchTypeId;
    @XmlAttribute(required = true)
    protected long totalDuration;

    /**
     * Gets the value of the filePath property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filePath property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilePath().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFilePath() {
        if (filePath == null) {
            filePath = new ArrayList<String>();
        }
        return this.filePath;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the launchModeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaunchModeId() {
        return launchModeId;
    }

    /**
     * Sets the value of the launchModeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaunchModeId(String value) {
        this.launchModeId = value;
    }

    /**
     * Gets the value of the launchTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaunchTypeId() {
        return launchTypeId;
    }

    /**
     * Sets the value of the launchTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaunchTypeId(String value) {
        this.launchTypeId = value;
    }

    /**
     * Gets the value of the totalDuration property.
     * 
     */
    public long getTotalDuration() {
        return totalDuration;
    }

    /**
     * Sets the value of the totalDuration property.
     * 
     */
    public void setTotalDuration(long value) {
        this.totalDuration = value;
    }

}
