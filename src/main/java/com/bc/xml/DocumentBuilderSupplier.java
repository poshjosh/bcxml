/*
 * Copyright 2018 NUROX Ltd.
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

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 1, 2018 12:14:48 AM
 */
public class DocumentBuilderSupplier implements Supplier<DocumentBuilder>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(DocumentBuilderSupplier.class.getName());

    private transient static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private transient static DocumentBuilder docBuilder;
    
    public synchronized Optional<DocumentBuilder> getOptional() {
        return Optional.ofNullable(get());
    }
    
    public synchronized DocumentBuilder getOrDefault(DocumentBuilder outputIfNone) {
        final DocumentBuilder result = this.get();
        return result == null ? outputIfNone : result;
    }

    @Override
    public synchronized DocumentBuilder get() {
        if(docBuilder != null) {
            try{
                docBuilder.reset();
            }catch(RuntimeException e) {
                LOG.log(Level.WARNING, null, e);
                docBuilder = null;
            }
        }
        
        if(docBuilder == null) {
            try {
                docBuilder = factory.newDocumentBuilder();
            }catch (ParserConfigurationException e) {
                final String msg = "Could not obtain SAX parser";
                LOG.log(Level.SEVERE, msg, e);
                throw new RuntimeException(msg);
            }
        }
        return docBuilder;
    }
}
