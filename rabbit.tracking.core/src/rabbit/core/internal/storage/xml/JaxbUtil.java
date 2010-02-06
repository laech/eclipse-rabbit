package rabbit.core.internal.storage.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * Utility class contains JAXB related operations.
 */
public class JaxbUtil {

	private static JAXBContext context;
	private static Marshaller mar;
	private static Unmarshaller unmar;

	static {
		try {
			initialize();
		} catch (JAXBException e) {
			try {
				initialize();
			} catch (JAXBException ex) {
				ex.printStackTrace();
			}
		}
	}

	private JaxbUtil() {
	}

	private static void initialize() throws JAXBException {
		context = JAXBContext.newInstance(ObjectFactory.class);
		mar = context.createMarshaller();
		unmar = context.createUnmarshaller();
	}

	/**
	 * Marshals the given element to file.
	 * 
	 * @param e
	 *            The element.
	 * @param f
	 *            The file.
	 * @throws JAXBException
	 *             If any unexpected problem occurs during the marshalling.
	 * @throws MarshalException
	 *             If the ValidationEventHandler returns false from its
	 *             handleEvent method or the Marshaller is unable to marshal obj
	 *             (or any object reachable from obj).
	 * @throws IllegalArgumentException
	 *             If any of the method parameters are null
	 */
	public static void marshal(JAXBElement<?> e, File f) throws JAXBException {
		mar.marshal(e, f);
	}

	/**
	 * Unmarshals a file.
	 * 
	 * @param <T>
	 *            The class type of the object to be unmarshaled.
	 * @param type
	 *            The class of the object to be unmarsharled.
	 * @param f
	 *            The file.
	 * @return The object specified by the type.
	 * @throws JAXBException
	 *             If any unexpected errors occur while unmarshalling
	 * @throws UnmarshalException
	 *             If the ValidationEventHandler returns false from its
	 *             handleEvent method or the Unmarshaller is unable to perform
	 *             the XML to Java binding.
	 * @throws IllegalArgumentException
	 *             If the file parameter is null
	 */
	public static <T> T unmarshal(Class<T> type, File f) throws JAXBException {
		@SuppressWarnings("unchecked")
		JAXBElement<T> doc = (JAXBElement<T>) unmar.unmarshal(f);
		return doc.getValue();
	}
}
