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
import java.util.Objects;
import org.w3c.dom.Node;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 20, 2018 10:28:21 PM
 */
public class AttributeNodeImpl implements Serializable, AttributeNode {

    private final Node node;
    
    private final XmlUtil xml;

    public AttributeNodeImpl(Node node) {
        this.node = Objects.requireNonNull(node);
        this.xml = new XmlUtil();
    }
    
    @Override
    public boolean hasAttribute(String attrName, String attrVal) {
        return this.xml.hasAttribute(node, attrName, attrVal);
    }

    @Override
    public boolean hasMatchingAttribute(String attrName, String attrVal) {
        return this.xml.hasMatchingAttribute(node, attrName, attrVal);
    }

    @Override
    public Node getAttribute(String name) {
        return this.xml.getAttribute(node, name);
    }

    @Override
    public String getAttributeValue(String name) {
        return this.xml.getAttributeValue(node, name);
    }

    @Override
    public double getAttributeNumber(String name) {
        return this.xml.getAttributeNumber(node, name);
    }

    @Override
    public String[] getAttributeValues(String name) {
        return this.xml.getAttributeValues(node, name);
    }

    @Override
    public String[] getAttributeValues(String name, String separatorRegex) {
        return this.xml.getAttributeValues(node, name, separatorRegex);
    }
}
