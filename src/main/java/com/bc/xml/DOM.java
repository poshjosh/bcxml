package com.bc.xml;

import java.io.IOException;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author poshjosh
 */
public interface DOM {

    Node add(String tagName, String attrName, String attrVal);

    List<Node> get(String tagName, String attrName, String val);
        
    boolean contains(String tagName, String attrName, String val);

    NodeList get(String tagName);

    Document getDocument();

    Node getRootNode();

    String getRootNodeName();

    boolean isAutoSaveChanges();

    boolean isEmpty();

    Node removeFirst(String tagName, String attrName, String val);

    boolean save();
    
    String toXmlText(String charset) throws IOException, TransformerException;
}
