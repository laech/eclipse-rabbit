//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.01.26 at 03:08:12 PM NZDT 
//


package rabbit.tracking.storage.xml.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the rabbit.tracking.storage.xml.datatype package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Events_QNAME = new QName("", "events");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rabbit.tracking.storage.xml.datatype
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link WorkbenchEventType }
     * 
     */
    public WorkbenchEventType createWorkbenchEventType() {
        return new WorkbenchEventType();
    }

    /**
     * Create an instance of {@link CommandEventListType }
     * 
     */
    public CommandEventListType createCommandEventListType() {
        return new CommandEventListType();
    }

    /**
     * Create an instance of {@link EventGroup }
     * 
     */
    public EventGroup createTypeWithDate() {
        return new EventGroup();
    }

    /**
     * Create an instance of {@link CommandEventType }
     * 
     */
    public CommandEventType createCommandEventType() {
        return new CommandEventType();
    }

    /**
     * Create an instance of {@link WorkbenchEventListType }
     * 
     */
    public WorkbenchEventListType createWorkbenchEventListType() {
        return new WorkbenchEventListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "events")
    public JAXBElement<EventListType> createEvents(EventListType value) {
        return new JAXBElement<EventListType>(_Events_QNAME, EventListType.class, null, value);
    }

}
