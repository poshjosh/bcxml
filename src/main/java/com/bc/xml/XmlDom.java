package com.bc.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @(#)DOM.java   23-May-2014 16:33:09
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
public class XmlDom implements DOM {
    
    /**
     * Determines if changes made to {@linkplain #document} will be
     * immediately propagated to the local file system.
     */
    private final boolean autoSaveChanges;
    
    private final String rootNodeName;

    private transient final Document document;
    
    private final XmlUtil xml;

    public XmlDom(File file, String rootNodeName, boolean autoSaveChanges) {
        this(new XmlUtil().load(file), rootNodeName, autoSaveChanges);
    }
    
    public XmlDom(URI uri, String rootNodeName, boolean autoSaveChanges) {
        this(new XmlUtil().load(uri.toString()), rootNodeName, autoSaveChanges);
    }

    public XmlDom(InputSource is, String rootNodeName, boolean autoSaveChanges) {
        this(new XmlUtil().load(is), rootNodeName, autoSaveChanges);
    }

    /**
     * If you use this constructor, then you must manually set the document
     * URI via {@link #getDocument()}{@link org.w3c.dom.Document#setDocumentURI(java.lang.String) #setDocumentURI(java.lang.String)} if you intend using the {@link #save()} method
     * @param in
     * @param rootNodeName
     * @param autoSaveChanges 
     */
    public XmlDom(InputStream in, String rootNodeName, boolean autoSaveChanges) {
        this(new XmlUtil().load(in), rootNodeName, autoSaveChanges);
    }
    
    public XmlDom(Document doc, String rootNodeName, boolean autoSaveChanges) {
        this.document = Objects.requireNonNull(doc);
        this.autoSaveChanges = autoSaveChanges;
        this.rootNodeName = Objects.requireNonNull(rootNodeName);
        this.xml = new XmlUtil();
    }

    @Override
    public String getRootNodeName() {
        return this.rootNodeName;
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.document == null || !this.document.hasChildNodes();
    }

    @Override
    public synchronized Node getRootNode() {
        
        NodeList list;
        
        if(this.getRootNodeName() != null) {
            list = this.get(this.getRootNodeName());
            if(list != null && list.getLength() > 0) {
                return list.item(0);
            }
        }
        
        list = this.document.getChildNodes();
        if(list == null || list.getLength() == 0) {
            throw new UnsupportedOperationException("Could not find any root node in document");
        }
        
//<?xml version="1.0" encoding="UTF-8"?>
//<root>
// ADD CONTENT HERE        
//</root>        
//        
// The <?xml node is not included in the NodeList        
        return list.item(0);
    }
    
    @Override
    public synchronized Node add(String tagName, String attrName, String attrVal) {
        final Node added = xml.add(document, getRootNodeName(), tagName, attrName, attrVal);
        if (autoSaveChanges) {
            save();
        }
        return added;
    }

    @Override
    public synchronized NodeList get(String tagName) {
        if(this.document == null) throw new NullPointerException("Config document is null");
        return document.getElementsByTagName(tagName);
    }
    
    @Override
    public synchronized List<Node> get(String tagName, String attrName, String val) {
        
        return xml.getAll(document, tagName, attrName, val);
    }

    @Override
    public synchronized boolean contains(String tagName, String attrName, String val) {
        return new NodeListUtil(document.getElementsByTagName(tagName)).contains(attrName, val);
    }

    @Override
    public synchronized Node removeFirst(String tagName, String attrName, String val) {
        Node removed = xml.removeFirst(document, tagName, attrName, val).orElse(null);
        if (autoSaveChanges && removed != null)  {
            save();
        }
        return removed;
    }

    @Override
    public synchronized boolean save() {
        if(document.getDocumentURI() == null) {
            throw new NullPointerException();
        }else{
            return xml.save(document, document.getDocumentURI());
        }
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public boolean isAutoSaveChanges() {
        return autoSaveChanges;
    }
    
    @Override
    public String toXmlText(String charset) throws IOException, TransformerException {
        return this.xml.toString(document, charset);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(", encoding: ").append(this.document.getXmlEncoding());
        builder.append(", doctype: ").append(this.document.getDoctype());
        builder.append(", uri: ").append(this.document.getDocumentURI());
        return builder.toString();
    }
}
