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

import java.util.Objects;
import java.util.Optional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2018 12:20:19 PM
 */
public class NodeListUtil {
    
    private final NodeList nodeList;
    
    private final XmlUtil xml;

    public NodeListUtil(NodeList nodeList) {
        this.nodeList = Objects.requireNonNull(nodeList);
        this.xml = new XmlUtil();
    }

    public Optional<Node> getNodeWithMostChildren() {

        Node output = null;

        if(nodeList != null && nodeList.getLength() > 0) {
            
            for(int i=0; i<nodeList.getLength(); i++) {
                
                final Node node = nodeList.item(i);
                
                if(node.hasChildNodes()) {
                    
                    final int lenX = output == null || !output.hasChildNodes() ? 0 : output.getChildNodes().getLength();
                    final int lenY = node.getChildNodes().getLength();
                    
                    if(lenY > lenX) {
                        
                        output = node;
                    }
                }
            }
        }

        return Optional.ofNullable(output);
    }

    public boolean contains(String attrName, String val) {
        return this.indexOf(attrName, val) > -1;
    }

    public int indexOf(String attrName, String val) {
        if (nodeList == null) {
            return -1;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (xml.hasAttribute(node, attrName, val)) {
                return i;
            }    
        }
        return -1;
    }
}
