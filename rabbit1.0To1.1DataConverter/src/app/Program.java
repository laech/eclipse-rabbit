package app;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Program {
  
  private static final String ATTR_DATE = "date";
  private static final String ATTR_DURATION = "duration";
  private static final String ATTR_FILE_ID = "fileId";
  private static final String ATTR_FILE_PATH = "filePath";
  private static final String TAG_EVENT_LIST = "events";
  private static final String TAG_FILE_EVENT = "fileEvent";
  private static final String TAG_FILE_EVENT_LIST = "fileEvents";
  private static final String TAG_PERSPECTIVE_EVENT = "perspectiveEvent";
  private static final String TAG_PERSPECTIVE_EVENT_LIST = "perspectiveEvents";
  private static final String TAG_SESSION_EVENT = "sessionEvent";
  private static final String TAG_SESSION_EVENT_LIST = "sessionEvents";
  
  /**
   * An unmodifiable map of file IDs to file paths.
   */
  private static Map<String, String> fileIdToFilePath;

  private static DocumentBuilder builder;
  private static Transformer transformer;
  
  static {
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      System.err.println(e.getMessage());
    }
    
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      System.err.println(e.getMessage());
    } catch (TransformerFactoryConfigurationError e) {
      System.err.println(e.getMessage());
    }
  }
  
  public static void main(String[] args) {
    System.out.println("Working...");
    
    String[] perspectiveEventFileNames = new String[] {
        "perspectiveEvents-2010-03.xml",
        "perspectiveEvents-2010-04.xml",
        "perspectiveEvents-2010-05.xml",
        "perspectiveEvents-2010-06.xml",
    };
    String[] fileEventFileNames = new String[] {
        "fileEvents-2010-03.xml",
        "fileEvents-2010-04.xml",
        "fileEvents-2010-05.xml",
        "fileEvents-2010-06.xml",
    };
    
    String path = System.getProperty("user.home") + File.separator + "Rabbit";
    File home = new File(path);
    File[] subdirs = home.listFiles();
    if (subdirs == null) {
      return;
    }
    
    Map<String, String> fileIdToPath = new HashMap<String, String>();
    Set<File> perspectiveEventFiles = new HashSet<File>();
    Set<File> fileEventFiles = new HashSet<File>();
    for (File dir : subdirs) {
      
      // Loads the perspective event files to be processed:
      for (String fileName : perspectiveEventFileNames) {
        File file = new File(dir.getAbsoluteFile() + File.separator + fileName);
        if (file.exists()) {
          perspectiveEventFiles.add(file);
        }
      }
      
      // Loads the file event files to be processed:
      for (String fileName : fileEventFileNames) {
        File file = new File(dir.getAbsoluteFile() + File.separator + fileName);
        if (file.exists()) {
          fileEventFiles.add(file);
        }
      }
      
      // Loads the resource file paths and IDs:
      File resourceFile = new File(dir.getAbsoluteFile() + File.separator 
          + "ResourceDB" + File.separator + "Resources.xml");
      if (resourceFile.exists()) {
        try {
          loadResourceMappings(resourceFile, fileIdToPath);
        } catch (SAXException e) {
          System.err.println(e.getMessage());
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      } else {
        System.err.println("Not exist: " + resourceFile.getAbsolutePath());
      }
    }
    
    fileIdToFilePath = Collections.unmodifiableMap(fileIdToPath);
    

    // Start:
    for (File file : perspectiveEventFiles) {
      try {
        handlePerspectiveEventFile(file);
      } catch (SAXException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      } catch (TransformerException e) {
        System.err.println(e.getMessage());
      }
    }
    for (File file : fileEventFiles) {
      try {
        handleFileEventFile(file);
      } catch (SAXException e) {
        System.err.println(e.getMessage());
      } catch (IOException e) {
        System.err.println(e.getMessage());
      } catch (TransformerException e) {
        System.err.println(e.getMessage());
      }
    }
    
    System.out.println("Done.");
  }
  
  /**
   * Converts the file event data to the new format.
   * 
   * @param file The file containing the XML data.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   * @throws TransformerException If an unrecoverable error occurs during the
   *           course of the transformation while saving the data.
   */
  static void handleFileEventFile(File file) 
      throws SAXException, IOException, TransformerException {
    
    Document document = builder.parse(file);
    Element root = (Element) document.getFirstChild();
    NodeList eventLists = root.getElementsByTagName(TAG_FILE_EVENT_LIST);
    for (int i = 0; i < eventLists.getLength(); i++) {
      
      Element eventList = (Element) eventLists.item(i);
      NodeList oldEvents = eventList.getElementsByTagName(TAG_FILE_EVENT);
      for (int j = 0; j < oldEvents.getLength(); j++) {
        
        Element event = (Element) oldEvents.item(j);
        String fileId = event.getAttribute(ATTR_FILE_ID);
        String filePath = fileIdToFilePath.get(fileId);
        if (filePath != null) {
          event.setAttribute(ATTR_FILE_PATH, filePath);
          event.removeAttribute(ATTR_FILE_ID);
        } else {
          eventList.removeChild(event);
        }
      }
    }
    
    FileOutputStream out = new FileOutputStream(file);
    try {
      transformer.transform(new DOMSource(document), new StreamResult(out));
    } finally {
      out.close();
    }
  }
  
  /**
   * Converts the old perspective event data to separate session data. 
   * Independent session tracking is introduced in Rabbit 1.1, will not rely on
   * perspective event data any more.
   * 
   * @param file The file containing the XML data.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   * @throws TransformerException If an unrecoverable error occurs during the
   *           course of the transformation while saving the data.
   */
  static void handlePerspectiveEventFile(File file) 
      throws SAXException, IOException, TransformerException {
    
    Document oldDocument = builder.parse(file);
    Document newDocument = builder.newDocument();
    Element newRoot = newDocument.createElement(TAG_EVENT_LIST);
    newDocument.appendChild(newRoot);
    
    Element oldRoot = (Element) oldDocument.getFirstChild();
    NodeList oldEventLists = oldRoot.getElementsByTagName(TAG_PERSPECTIVE_EVENT_LIST);
    for (int i = 0; i < oldEventLists.getLength(); i++) {
      
      Element oldEventList = (Element) oldEventLists.item(i);
      Attr oldDate = oldEventList.getAttributeNode(ATTR_DATE);
      if (oldDate == null) {
        System.err.println("Attr == null");
        continue;
      }
      Element newEventList = newDocument.createElement(TAG_SESSION_EVENT_LIST);
      Attr newDate = newDocument.createAttribute(oldDate.getName());
      newDate.setValue(oldDate.getValue());
      newEventList.setAttributeNode(newDate);
      newRoot.appendChild(newEventList);
      
      NodeList oldEvents = oldEventList.getElementsByTagName(TAG_PERSPECTIVE_EVENT);
      for (int j = 0; j < oldEvents.getLength(); j++) {
        
        Element oldEvent = (Element) oldEvents.item(j);
        Attr oldDuration = oldEvent.getAttributeNode(ATTR_DURATION);
        if (oldDuration == null) {
          System.err.println("Duration == null");
          continue;
        }
        
        Element newEvent = newDocument.createElement(TAG_SESSION_EVENT);
        Attr newDuration = newDocument.createAttribute(oldDuration.getName());
        newDuration.setValue(oldDuration.getValue());
        newEvent.setAttributeNode(newDuration);
        newEventList.appendChild(newEvent);
      }
    }
    
    String fileName = file.getName();
    fileName = "sessionEvents" + fileName.substring(fileName.indexOf('-'));
    file = new File(file.getParentFile().getAbsoluteFile() + File.separator + fileName);
    FileOutputStream out = new FileOutputStream(file);
    try {
      transformer.transform(new DOMSource(newDocument), new StreamResult(out));
    } finally {
      out.close();
    }
  }
  
  /**
   * Loads the resource mapping from the file into the map.
   * 
   * @param file The file containing the mapping of file IDs and file paths.
   * @param idToPath The map that maps from file ID to file paths, results will
   *          be put into this map.
   * @throws NullPointerException If file is null.
   * @throws SAXException If any parse errors occur.
   * @throws IOException If any IO errors occur.
   */
  static void loadResourceMappings(File file, Map<String, String> idToPath) 
      throws SAXException, IOException {
    
    Document document = builder.parse(file);
    Element root = (Element) document.getFirstChild();
    NodeList resources = root.getElementsByTagName("resource");
    for (int i = 0; i < resources.getLength(); i++) {
      
      Element resource = (Element) resources.item(i);
      NodeList resourceIds = resource.getElementsByTagName("resourceId");
      for (int j = 0; j < resourceIds.getLength(); j++) {
        
        Element resourceId = (Element) resourceIds.item(j);
        String fileId = resourceId.getTextContent();
        String filePath = resource.getAttribute("path");
        idToPath.put(fileId, filePath);
      }
    }
  }
}
