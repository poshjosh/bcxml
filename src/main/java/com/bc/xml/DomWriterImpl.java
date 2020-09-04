/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
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
import org.w3c.dom.Node;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2019 4:13:57 PM
 */
public class DomWriterImpl implements DomWriter {

    private static final Logger LOG = Logger.getLogger(DomWriterImpl.class.getName());

    @Override
    public boolean write(Document doc) {
        
        return write(doc, doc.getDocumentURI());
    }
    
    @Override
    public boolean write(Node node, String uriString) {
        if(node == null || uriString == null) {
            throw new NullPointerException();
        }
        try{
            URI uri = new URI(uriString);
            return write(node, uri);
        }catch(URISyntaxException e) {
            LOG.log(Level.WARNING, "Error saving document to: "+uriString, e);
            return false;
        }
    }
    
    @Override
    public boolean write(Node node, URI uri) {
        LOG.log(Level.FINER, "Saving: {0}", uri);                    
        if(node == null || uri == null) {
            throw new NullPointerException();
        }
        try{
            boolean saved = write(node, Paths.get(uri).toFile());
            LOG.log(Level.FINER, "Saved URI: {0}", saved);                    

            if(!saved) {
                URL url = uri.toURL();
                try{
                    return write(node, url.openConnection().getOutputStream());
                }catch(IOException ioe) {
                    LOG.log(Level.WARNING, "Error saving document to: "+uri, ioe);
                    return false;
                }
            }else{
                return saved;
            }
        }catch(MalformedURLException e) {
            LOG.log(Level.WARNING, "Error saving document to: "+uri, e);
            return false;
        }
    }

    @Override
    public boolean write(Node node, File file) {
        if(node == null || file == null) {
            throw new NullPointerException();
        }
        try{
            return write(node, new FileOutputStream(file), file.getAbsolutePath());
        }catch(FileNotFoundException e) {
            LOG.log(Level.WARNING, "Error saving document to: "+file, e);
            return false;
        }
    }

    @Override
    public boolean write(Node node, OutputStream out) {
        return write(node, out, null);
    }
    
    @Override
    public boolean write(Node node, OutputStream out, String path) {
        boolean saved = false;
        try{
            saved = writeNode(node, out, path);
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Error saving document"+(path==null?"":" to: "+path), e);
        } catch (TransformerConfigurationException e) {
            LOG.log(Level.WARNING, "Transformer Factory Error", e);
        } catch (TransformerException e) {
            LOG.log(Level.WARNING, "Transformation Error", e);
        }
        
        return saved;
    }

    @Override
    public boolean writeNode(Node node, OutputStream out, String path) 
            throws IOException, TransformerException{

        if(node == null || out == null) {
            throw new NullPointerException();
        }

        boolean saved = false;

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

        BufferedOutputStream bos = new BufferedOutputStream(out);
        OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8"); // XML encoding

        try{

            StreamResult result = new StreamResult(osw);

            transformer.transform(source, result);

            osw.flush();

            saved = true;

        }finally{
            if(osw != null) try{ osw.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
            if(bos != null) try{ bos.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
            if(out != null) try{ out.close(); }catch(IOException e){
                LOG.log(Level.WARNING, "", e);
            }
        }
        
        return saved;
    }
}
