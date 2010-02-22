package rabbit.core.internal.storage.xml;

import java.io.File;

import javax.xml.bind.JAXBElement;

import org.junit.Test;

import rabbit.core.internal.storage.xml.JaxbUtil;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

// TODO
public class JaxbUtilTest {

	private ObjectFactory objectFactory = new ObjectFactory();

	@Test(expected = IllegalArgumentException.class)
	public void testMarshal_withFileNull() throws Exception {
		JAXBElement<EventListType> doc = objectFactory.createEvents(objectFactory.createEventListType());
		JaxbUtil.marshal(doc, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMarshal_withElementNull() throws Exception {
		File f = File.createTempFile("tmpJaxb", "abc");
		JaxbUtil.marshal(null, f);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMarshal_withNull() throws Exception {
		JaxbUtil.marshal(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnmarshal_withFileNull() throws Exception {
		JaxbUtil.unmarshal(Object.class, null);
	}
}
