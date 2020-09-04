/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bc.xml;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 20, 2018 9:53:17 PM
 */
public class WebXmlDom extends XmlDom {

    private transient static final Logger LOG = Logger.getLogger(WebXmlDom.class.getName());

    private Map<String, String> servlets;
    
    private Map<String, Set<String>> servletMappings;
    
    private Set<String> urlPatterns;

    public WebXmlDom(URI uri) { 
        super(uri, "web-app", false);
    }

    public WebXmlDom(File path) { 
        super(path, "web-app", false);
    }
    
    public WebXmlDom(InputStream in) { 
        super(in, "web-app", false);
    }
    
    public WebXmlDom(Document doc) { 
        super(doc, "web-app", false);
    }
    
    public boolean isAsyncSupported(boolean resultIfNone) {
//    <servlet>
//        <servlet-name>Admin</servlet-name>
//        <servlet-class>com.loosebox.web.servlets.Admin</servlet-class>
//        <async-supported>true</async-supported>
//        <multipart-config>
//            <max-file-size>10000000</max-file-size>
//            <max-request-size>100000000</max-request-size>
//        </multipart-config>
//    </servlet>
        String result = null;
        final NodeList list = this.getDocument().getElementsByTagName("servlet");
        outer:
        for(int i=0; i<list.getLength(); i++) {
            final Node servletNode = list.item(i);
            final NodeList children = servletNode.getChildNodes();
            for(int j=0; j<children.getLength(); j++) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();
                if("async-supported".equals(childName)) {
                    
                    result = child.getTextContent();
                    
                    break outer;
                }
            }
        }

        LOG.log(Level.FINE, "async-supported: {0}", result); 
        
        return result == null ? resultIfNone : Boolean.parseBoolean(result);
    }

    public int getMaxFileSize(String className) {
//    <servlet>
//        <servlet-name>Admin</servlet-name>
//        <servlet-class>com.loosebox.web.servlets.Admin</servlet-class>
//        <multipart-config>
//            <max-file-size>10000000</max-file-size>
//            <max-request-size>100000000</max-request-size>
//        </multipart-config>
//    </servlet>
        int output = -1;
        final NodeList list = this.getDocument().getElementsByTagName("servlet");
        for(int i=0; i<list.getLength(); i++) {
            final Node servletNode = list.item(i);
            final NodeList children = servletNode.getChildNodes();
            boolean found = false;
            int maxFileSize = -1;
            for(int j=0; j<children.getLength(); j++) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();
                if(childName == null) continue;
                if(childName.equals("servlet-class")) {
                    
                    if( ! this.hasMatchingTextContent(child, className)) {
                        continue;
                    }
                    
                    if(maxFileSize != -1) { // already found
                        break;
                    }
                    
                    found = true;
                    
                }else if(childName.equals("multipart-config")) {
                    final NodeList grandChildren = child.getChildNodes();
                    for(int k=0; k<grandChildren.getLength(); k++) {
                        final Node grandChild = grandChildren.item(k);
                        final String grandChildName = grandChild.getNodeName();
                        if(grandChildName == null) continue;
                        if(grandChildName.trim().equals("max-file-size")) {
                            maxFileSize  = Integer.parseInt(grandChild.getTextContent());
                        }
                    }
                    if(found) {
                        break;
                    }else{
                        continue;
                    }
                }
            }
            if(maxFileSize != -1) {
                output = maxFileSize;
                break;
            }
        }
        
        final int maxFileSize = output;
        
        LOG.log(Level.FINER, () -> 
                MessageFormat.format("Max File Size: {0}, class: {1}", 
                maxFileSize, className)); 
        
        return maxFileSize;
    }
    
    public boolean hasMatchingTextContent(final Node child, String className) {
        if(className != null) {
            className = className.trim();
        }
        String text = child.getTextContent();
        if(text != null) {
            text = text.trim();
        }
        return Objects.equals(text, className);
    }
    
    public Set<String> getServletNames() {
        return new HashSet<>(this.getServlets().keySet());
    }

    /**
     * @return Map. Each entry has key equal to servlet-name and value equal
     * to servlet-class entry for each servlet in the respective web.xml file.
     */
    public Map<String, String> getServlets() {
            if (servlets == null) {
                    HashSet otherNames = new HashSet();
                    otherNames.add("servlet-class");
                    servlets = init("servlet", "servlet-name", otherNames);
//System.out.println("org.lb.dom.WebDOM. Servlets: "+servlets);                                        
            }
            return servlets;
    }

    public Map<String, Set<String>> getServletMappings() {
            if (servletMappings == null) {
                    Set otherNames = new HashSet();
                    otherNames.add("url-pattern");
                    servletMappings = initMultiple("servlet-mapping", "servlet-name", otherNames);
//System.out.println("org.lb.dom.WebDOM. ServletMappings: "+servletMappings);                    
            }
            return servletMappings;
    }

    public Set<String> getUrlPatterns() {
            if (urlPatterns == null) {
                    urlPatterns = new HashSet<>();
                    Iterator i$ = getServletMappings().values().iterator();
                    while (i$.hasNext()) {
                            Set patterns = (Set)i$.next();
                            Iterator i2$ = patterns.iterator();
                            while (i2$.hasNext())  {
                                    String pattern = (String)i2$.next();
                                    urlPatterns.add(pattern);
                            }
                    }
            LOG.log(Level.FINER, "URL Patterns: {0}", urlPatterns);                    
            }
            return urlPatterns;
    }

    public boolean pathContainsServletPattern(String path, String servletClass) {
            String servletName = getSerlvetName(servletClass);
            Set patterns = (Set)getServletMappings().get(servletName);
            for (Iterator i$ = patterns.iterator(); i$.hasNext();) {
                    String pattern = (String)i$.next();
                    if (path.contains(pattern))
                            return true;
            }

            return false;
    }

    public boolean isUrlPattern(String str) {
            boolean output = false;
            Set<String> set = this.getUrlPatterns();
            for (String pattern:set) {
                    if (str.contains(pattern)) {
                            output = true;
                            break;
                    }        
            }
//System.out.println("Link: "+str+", is web.xml url-pattern: "+output);
            return output;
    }

    public String getSerlvetName(String servletClass) {
            for (Iterator i$ = getServlets().entrySet().iterator(); i$.hasNext();) {
                    java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
                    if (((String)entry.getValue()).equals(servletClass))
                            return (String)entry.getKey();
            }
            
            return null;
    }

    public String getSerlvetClass(String servletName) {
            for (Iterator i$ = getServlets().entrySet().iterator(); i$.hasNext();) {
                    java.util.Map.Entry entry = (java.util.Map.Entry)i$.next();
                    if (((String)entry.getKey()).equals(servletName))
                            return (String)entry.getValue();
            }
            
            return null;
    }
    
    public Set<String> getUrlPatterns(String servletName) {
            return new HashSet<>(getServletMappings().get(servletName));
    }

    public Map<String, String> init(String outerTag, String firstInnerTag, Set otherChildTagNames) {
            LinkedHashMap<String, String> output = new LinkedHashMap<>();
            NodeList nodeList = getRootNode().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (outerTag.equalsIgnoreCase(node.getNodeName())) {
                            NodeList children = node.getChildNodes();
                            List<String> list = getList(children, firstInnerTag, otherChildTagNames);
//System.out.println("List: "+c);                            
                            output.put(list.get(0), list.get(1));
                    }
            }

            return output;
    }

    public Map<String, Set<String>> initMultiple(String outerTag, String firstInnerTag, Set otherChildTagNames) {
            LinkedHashMap<String, Set<String>> output = new LinkedHashMap<>();
            NodeList nodeList = getRootNode().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (outerTag.equalsIgnoreCase(node.getNodeName())) {
                            NodeList children = node.getChildNodes();
                            List<String> list = getList(children, firstInnerTag, otherChildTagNames);
//System.out.println(node.getNodeName()+"="+list);                            
                            String servletName = (String)list.get(0);
                            
                            HashSet<String> servletmappings = (HashSet<String>)output.get(servletName);
                            
                            if(servletmappings == null) {
                                servletmappings = new HashSet<>(list.size()-1, 1.0f);
                                output.put(servletName, servletmappings);
                            }
                            
                            servletmappings.addAll(list.subList(1, list.size()));
                    }
            }

            return output;
    }

    private List<String> getList(NodeList children, String firstChildTagName, Set otherChildTagNames) {
            LinkedList<String> output = new LinkedList<>();
            for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    String nodeName = child.getNodeName();
                    String textContent = child.getTextContent();
                    if (output.contains(textContent))
                            throw new RuntimeException((new StringBuilder()).append("Duplicate ").append(nodeName).append(": ").append(textContent).append(", in ").append(this.getDocument().getDocumentURI()).toString());
                    if (firstChildTagName.equalsIgnoreCase(nodeName)) {
                            output.addFirst(textContent);
                            continue;
                    }
                    if (otherChildTagNames == null || otherChildTagNames.contains(nodeName)) {
                            output.add(textContent);
                    }        
            }

            return output;
    }
}
/**
 * 
 We removed these methods because we now have only one servlet with many 
 different patterns e.g /mailReceiver, /ux, /search etc which call different 
 sub servlets.
    public String getUrlPatternForClass(String servletClass) {
            String servletName = getSerlvetName(servletClass);
            return getUrlPattern(servletName);
    }
    public String getShortestPatternForClass(String servletClass) {
            String servletName = getSerlvetName(servletClass);
            return getShortestPattern(servletName);
    }
    public String getShortestPattern(String servletName) {
            Set patterns = (Set)getServletMappings().get(servletName);
            String shortest = null;
            Iterator i$ = patterns.iterator();
            do {
                    if (!i$.hasNext())
                            break;
                    String pattern = (String)i$.next();
                    if (shortest == null)
                            shortest = pattern;
                    else
                    if (pattern.length() < shortest.length())
                            shortest = pattern;
            } while (true);
            return shortest;
    }

 * 
 */