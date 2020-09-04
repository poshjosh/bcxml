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

import com.bc.xml.DocumentBuilderSupplier;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2019 4:00:38 PM
 */
public class DomReaderImpl implements DomReader {

    private static final Logger LOG = Logger.getLogger(DomReaderImpl.class.getName());

    @Override
    public Document read(String uriString) {
        Objects.requireNonNull(uriString, "URI cannot be null");
        final InputSource in = new InputSource(uriString);
        return this.read(in, uriString);
    }
    
    @Override
    public Document read(File file) {
        Objects.requireNonNull(file, "File cannot be null");
        //convert file to appropriate URI, f.toURI().toASCIIString()
        //converts the URI to string as per rule specified in
        //RFC 2396,
        final String uriString = file.toURI().toASCIIString();
        final InputSource in = new InputSource(uriString);
        return DomReaderImpl.this.read(in, uriString);
    }
    
    @Override
    public Document read(InputStream in) {
        
        return this.read(new InputSource(in), null);
    }
    
    @Override
    public Document read(InputSource in) {
        
        return read(in, null);
    }
    
    @Override
    public Document read(InputSource in, String uriString) {
        
        LOG.log(Level.FINER, "Loading document from: {0}", uriString);

        Document doc = null;
        try {
            doc = this.parse(in, uriString);
        }
        catch (SAXException e) {
            LOG.log(Level.WARNING, "Could not parse XML"+(uriString==null?"":" at: "+uriString), e);
        }
        catch (IOException e) {
            // Lighter logging for this, no stack trace
            LOG.log(Level.WARNING, "Could not read XML"+(uriString==null?"":" at: "+uriString)+", reason: {0}", e.toString());
        }
        return doc;
    }
    
    @Override
    public Document parse(InputSource in, String uriString) 
            throws IOException, SAXException {
        
        LOG.log(Level.FINER, "Loading document from: {0}", uriString);

        Document doc = null;
        final DocumentBuilder docBuilder = new DocumentBuilderSupplier().getOrDefault(null);
        if(docBuilder != null) {
            doc = docBuilder.parse(in);
        }
        if(doc != null) {
            if(doc.getDocumentURI() == null) {
                doc.setDocumentURI(uriString);
            }
            LOG.log(Level.FINE, "Doc URL: {0}, Base URI: {1}", 
            new Object[]{doc.getDocumentURI(), doc.getBaseURI()});                    
        }
        return doc;
    }
}
