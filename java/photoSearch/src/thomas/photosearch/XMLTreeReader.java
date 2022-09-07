/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thomas.photosearch;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author thomas
 */
public class XMLTreeReader {

    public final static String TAG_PHOTO_CAT = "PHOTO_CAT";
    public final static String TAG_DIRECTORY = "DIRECTORY";
    public final static String TAG_KEYWORDS= "KEYWORDS";
    public final static String TAG_RESUME= "RESUME";
    
    public final static String ATTR_CONTAINER= "container";
    public final static String ATTR_VERSION = "version";
    
    public final static int CONTAINER_TYPE_WORK = 1;
    public final static int CONTAINER_TYPE_STOCK = 2;
    public final static int CONTAINER_TYPE_UNKNOW = 0;
    
    private int length = 0;
    public int containerType = CONTAINER_TYPE_UNKNOW;
    
    private NodeList nodeList;
    private String version;

    public String getVersion() {
        return version;
    }

    public int getContainerType() {
        return containerType;
    }

    public XMLTreeReader(String fileName) {
        try {
            // creating a constructor of file class and
            // parsing an XML file
            File file = new File(fileName);

            // Defines a factory API that enables
            // applications to obtain a parser that produces
            // DOM object trees from XML documents.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // we are creating an object of builder to parse
            // the  xml file.
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            /*here normalize method Puts all Text nodes in
            the full depth of the sub-tree underneath this
            Node, including attribute nodes, into a "normal"
            form where only structure separates
            Text nodes, i.e., there are neither adjacent
            Text nodes nor empty Text nodes. */
            doc.getDocumentElement().normalize();
            //recuperer le type de container dans le tag PHOTO CAT

            nodeList = doc.getElementsByTagName(TAG_PHOTO_CAT);
           
            switch (nodeList.item(0).getAttributes().getNamedItem(ATTR_CONTAINER).getNodeValue()) {
                case DirectoryInfo.TYPE_WORK:
                    containerType = CONTAINER_TYPE_WORK;
                    break;

                case DirectoryInfo.TYPE_STOCK:
                    containerType = CONTAINER_TYPE_STOCK;
                    break;

                default:
                    containerType = CONTAINER_TYPE_UNKNOW;
            }
            version = nodeList.item(0).getAttributes().getNamedItem(ATTR_VERSION).getNodeValue();
            //System.out.println(nodeList.item(0).getAttributes().getNamedItem("container").getNodeValue());
            // Here nodeList contains all the nodes with
            // name DIRECTORY.
            nodeList = doc.getElementsByTagName(TAG_DIRECTORY);// Liste des noeuds liés au tag DIRECTORY
            length = nodeList.getLength();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public int getLength() {
        return length;
    }

    public DirectoryInfo getLine(int index) {
        if (index > length) {
            return null;
        }
        DirectoryInfo di = new DirectoryInfo();

        Node node = nodeList.item(index);// Pointe sur le tag DIRECTORY à la position [index]

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element tElement = (Element) node;
            NamedNodeMap attrList = tElement.getAttributes();
            NodeList nodeChildsList = node.getChildNodes(); //Crée la liste des noeuds enfants de DIRECTORY

            // get DIRECTORY attributes
            if (attrList.getLength() > 0) {
                for (int i = 0; i < attrList.getLength(); i++) {
                    di.addDirectoryAttr(attrList.item(i).getNodeName(), attrList.item(i).getNodeValue());
                }
            }
            //Boucle pour parcourir les noeuds enfant de DIRECTORY
            if (nodeChildsList.getLength() > 0) {
                for (int j = 0; j < nodeChildsList.getLength(); j++) {
                    switch (nodeChildsList.item(j).getNodeName().toUpperCase()) {
                        case TAG_KEYWORDS:
                            di.addKeywords(nodeChildsList.item(j).getTextContent());
                            break;
                        case TAG_RESUME:
                            NamedNodeMap attrs = nodeChildsList.item(j).getAttributes();
                            for (int h = 0; h < attrs.getLength(); h++) {
                                di.addResumeAttr(attrs.item(h).getNodeName(), attrs.item(h).getNodeValue());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            di.setContainerType(this.getContainerType());
        }
        //System.out.println("thomas.photosearch.XMLTreeReader.getLine()");
        return di;

    }

}
