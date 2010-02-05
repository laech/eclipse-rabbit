package rabbit.tracking.storage.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import rabbit.tracking.storage.xml.schema.ObjectFactory;

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

	private JaxbUtil() {}

	private static void initialize() throws JAXBException {
		context = JAXBContext.newInstance(ObjectFactory.class);
		mar = context.createMarshaller();
		unmar = context.createUnmarshaller();
	}

	public static void marshal(JAXBElement<?> e, File f) throws JAXBException {
		mar.marshal(e, f);
	}

	public static <T> T unmarshal(Class<T> type, File f) throws JAXBException {
		@SuppressWarnings("unchecked") JAXBElement<T> doc = (JAXBElement<T>) unmar.unmarshal(f);
		return doc.getValue();
	}
}
