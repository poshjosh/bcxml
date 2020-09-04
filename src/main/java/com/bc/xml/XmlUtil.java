package com.bc.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @(#)XMLUtils.java   23-May-2014 16:33:46
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class XmlUtil {

    private static final transient Logger LOG = Logger.getLogger(XmlUtil.class.getName());

    private final DomReader domReader;
    private final DomWriter domWriter;
    public XmlUtil() { 
        this.domReader = new DomReaderImpl();
        this.domWriter = new DomWriterImpl();
    }

    public Node add(Document document, String rootNodeName, String tagName, String attrName, String attrVal) {
            Element newNode = document.createElement(tagName);
            newNode.setAttribute(attrName, attrVal);
            NodeList list = document.getElementsByTagName(tagName);
            Node added = null;
            if (list == null || list.getLength() == 0) {
                    if (rootNodeName == null)
                            throw new NullPointerException();
                    Node rootNode = document.getElementsByTagName(rootNodeName).item(0);
                    added = rootNode.appendChild(newNode);
            } else {
                    Node refNode = list.item(0);
                    added = refNode.getParentNode().insertBefore(newNode, refNode);
            }
            return added;
    }

    public Optional<Node> getFirstOptional(Document document, String tagName, String attrName, String val) {
            final List<Node> found = this.get(document, tagName, attrName, val, 1);
            return found.stream().findFirst();
    }
    
    public List<Node> getAll(Document document, String tagName, String attrName, String val) {
            return this.get(document, tagName, attrName, val, Integer.MAX_VALUE);
    }

    public List<Node> get(Document document, String tagName, 
            String attrName, String val, int limit) {
            List<Node> output = new ArrayList<>();
            NodeList list = document.getElementsByTagName(tagName);
            if (list == null)
                    return Collections.EMPTY_LIST;
            for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (output.size() < limit && hasAttribute(node, attrName, val))
                            output.add(node);
            }
            return output.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(output);
    }

    public Optional<Node> removeFirst(Document document, String tagName, String attrName, String val) {
            final List<Node> removed = this.remove(document, tagName, attrName, val, 1);
            return removed.stream().findFirst();
    }

    public List<Node> removeAll(Document document, String tagName, String attrName, String val) {
            return this.remove(document, tagName, attrName, val, Integer.MAX_VALUE);
    }

    public List<Node> remove(Document document, String tagName, 
            String attrName, String val, int limit) {
            final List<Node> output = new ArrayList<>();
            NodeList list = document.getElementsByTagName(tagName);
            if (list == null)
                    return null;
            Node parent = null;
            for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (parent == null)
                            parent = node.getParentNode();
                    if (output.size() < limit && hasAttribute(node, attrName, val)) {
                            Node removed = parent.removeChild(node);
                            output.add(removed);
                    }
            }

            return output.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(output);
    }

    public boolean hasAttribute(Node node, String attrName) {
            NamedNodeMap nodeMap = node.getAttributes();
            return nodeMap.getNamedItem(attrName) != null;
    }

    public boolean hasAttribute(Node node, String attrName, String attrVal) {
            NamedNodeMap nodeMap = node.getAttributes();
            String nodeVal = nodeMap.getNamedItem(attrName).getNodeValue();
            if(attrVal != null) {
                attrVal = attrVal.trim();
            }
            if(nodeVal != null) {
                nodeVal = nodeVal.trim();
            }
            return Objects.equals(attrVal, nodeVal);
    }

    public boolean hasMatchingAttribute(Node node, String attrName, String attrVal) {
            Objects.requireNonNull(attrVal);
            final NamedNodeMap nodeMap = node.getAttributes();
            final Node named = nodeMap.getNamedItem(attrName);
            if(named == null) {
                return false;
            }else{
                String nodeVal = named.getNodeValue();
                if(attrVal != null) {
                    attrVal = attrVal.trim().toLowerCase();
                }
                if(nodeVal != null) {
                    nodeVal = nodeVal.trim().toLowerCase();
                }
                return attrVal.contains(nodeVal) || (nodeVal != null && nodeVal.contains(attrVal));
            }
    }

    public Node getAttribute(Node node, String name) {
            NamedNodeMap nodeMap = node.getAttributes();
            if (nodeMap == null)
                    return null;
            else
                    return nodeMap.getNamedItem(name);
    }

    public String getAttributeValue(Node node, String name) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return null;
            else
                    return attribute.getNodeValue();
    }

    public double getAttributeNumber(Node node, String name) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return -1D;
            String val = attribute.getNodeValue();
            if (val == null)
                    return -1D;
            else
                    return Double.parseDouble(val);
    }

    public String[] getAttributeValues(Node node, String name) {
            return getAttributeValues(node, name, ",");
    }

    public String[] getAttributeValues(Node node, String name, String separatorRegex) {
            Node attribute = getAttribute(node, name);
            if (attribute == null)
                    return null;
            String nodeValue = attribute.getNodeValue();
            if (nodeValue == null)
                    return null;
            else
                    return nodeValue.split(separatorRegex);
    }

    /**
     * @param doc
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Document doc) {
        return domWriter.write(doc);
    }
    
    /**
     * @param node
     * @param uri
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Node node, String uri) {
        return domWriter.write(node, uri);
    }
    
    /**
     * @param node
     * @param uri
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Node node, URI uri) {
        return domWriter.write(node, uri);
    }

    /**
     * @param node
     * @param file
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Node node, File file) {
        return domWriter.write(node, file);
    }

    /**
     * @param node
     * @param out
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Node node, OutputStream out) {
        return domWriter.write(node, out);
    }
    
    /**
     * @param node
     * @param out
     * @param path
     * @return <code>true</code> if the operation was successful 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomWriter} <code>write(...)</code> methods
     */
    @Deprecated
    public boolean save(Node node, OutputStream out, String path) {
        return domWriter.write(node, out, path);
    }

    /**
     * @param uriString
     * @return The read Document
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document load(String uriString) {
        return domReader.read(uriString);
    }
    
    /**
     * @param file
     * @return The read Document
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document load(File file) {
        return domReader.read(file);
    }

    /**
     * @param in
     * @return The read Document
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document load(InputStream in) {
        return domReader.read(in);
    }
    
    /**
     * @param in
     * @return The read Document
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document load(InputSource in) {
        return domReader.read(in);
    }
    
    /**
     * @param in
     * @param uriString
     * @return The read Document
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document load(InputSource in, String uriString) {
        return domReader.read(in, uriString);
    }
    
    /**
     * @param in
     * @param uriString
     * @return The read Document
     * @throws IOException
     * @throws SAXException 
     * @deprecated Rather use the {@linkplain com.bc.xml.DomReader} <code>read(...)</code> methods
     */
    @Deprecated
    public Document parse(InputSource in, String uriString) 
            throws IOException, SAXException {
        return domReader.parse(in, uriString);
    }

    public StringBuilder stringValue(Node node, int maxLen) {
            StringBuilder builder = (new StringBuilder("Node:")).append(node.getNodeName());
            builder.append(", localName:").append(node.getLocalName()).append(", nameSpaceURI:").append(node.getNamespaceURI());
            builder.append(", nodeType:").append(node.getNodeType()).append(", nodeValue:").append(truncate(maxLen, node.getNodeValue()));
            builder.append(", prefix:").append(node.getPrefix()).append(", textContent:").append(truncate(maxLen, node.getTextContent()));
            NamedNodeMap nodeMap = node.getAttributes();
            if (nodeMap != null && nodeMap.getLength() > 0)
                    builder.append("\nAttibutes:: ").append(stringValue(nodeMap, maxLen));
            return builder;
    }

    public StringBuilder stringValue(NamedNodeMap map, int maxLen) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < map.getLength(); i++) {
                    Node node = map.item(i);
                    builder.append("\nnode[").append(i).append("]::");
                    builder.append(node.getNodeName()).append("=").append(node.getNodeValue());
            }

            return builder;
    }

    private String truncate(int maxLen, String val) {
            if (maxLen < 0)
                    return val;
            if (val == null || val.length() <= maxLen)
                    return val;
            else
                    return val.substring(0, maxLen);
    }

    public String toString(Node node) {

        StringWriter writer = null;

        try{
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            // To preserve the XML document's DOCTYPE setting, it is also necessary to add the following code
            if(node instanceof Document) {
                final Document doc = (Document)node;
                if (doc.getDoctype() != null){
                    String systemValue = new GetFilename().apply(doc.getDoctype());
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
                }
            }

            DOMSource source = new DOMSource(node);

            writer = new StringWriter();

            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

        } catch (TransformerConfigurationException e) {

            // Error generated by the parser
            LOG.log(Level.WARNING, "Transformer Factory Error", e);

        } catch (TransformerException e) {

            // Error generated by the parser
            LOG.log(Level.WARNING, "Transformation Error", e);
        }

        return writer == null ? null : writer.getBuffer().toString();
    }

    /**
     * @param doc
     * @param charset
     * @return
     * @throws IOException
     * @throws TransformerException 
     * @see https://stackoverflow.com/questions/2325388/what-is-the-shortest-way-to-pretty-print-a-org-w3c-dom-document-to-stdout
     */
    public String toString(Document doc, String charset) 
            throws IOException, TransformerException {
        final StringWriter writer = new StringWriter();
        this.printDocument(doc, writer, charset);
        return writer.toString();
    }

    public void printDocument(Document doc, OutputStream out, String charset) 
            throws IOException, TransformerException {
        this.printDocument(doc, new OutputStreamWriter(out, charset), charset);
    }
    
    public void printDocument(Document doc, Writer writer, String charset) 
            throws IOException, TransformerException {
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, charset);
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(writer));
    }
}
