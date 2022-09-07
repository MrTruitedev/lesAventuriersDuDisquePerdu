
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.NamedNodeMap;

public class ReadXMLFile {

    public static void main(String argv[]) {
        try {
//creating a constructor of file class and parsing an XML file  
            File file = new File("C:\\Users\\thomas\\Desktop\\Stage\\java\\ReadXMLFile\\XMLFile.xml");
//an instance of factory that gives a document builder  
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//an instance of builder to parse the specified xml file  
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("DIRECTORY");
// nodeList is not iterable, so we are using for loop  
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                //System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    NamedNodeMap attrList = eElement.getAttributes();
                    System.out.println("Directory attributes: ");
                    for (int j = 0; j < attrList.getLength(); j++) {
                        //System.out.print(attrList.item(j).getNodeName());
                        // System.out.print(attrList.item(j).getNodeValue());
                    }
                    //System.out.println();
                    NodeList nodeChildsList = node.getChildNodes();

                    //for (int j = 0; j < nodeChildsList.getLength(); j++) {
                        if (nodeChildsList.item(0).getNodeValue() == null) {
                            System.out.println(">>>" + nodeChildsList.item(0).getN);
                            // System.out.println("Resume : " + nodeChildsList.item(j).getNodeValue());

                        }

                        //System.out.println("Keywords: " + eElement.getTextContent().trim());
                        // eElement.ge
                    //}
                }
            }
                
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
