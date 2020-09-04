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

import java.util.AbstractList;
import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 1:42:04 PM
 */
public class Nodelist extends AbstractList<Node> {

    private final NodeList nodeList;
    
    public Nodelist(NodeList nodeList) {
        this.nodeList = Objects.requireNonNull(nodeList);
    }

    @Override
    public Node get(int index) {
        return nodeList.item(index);
    }

    @Override
    public int size() {
        return nodeList.getLength();
    }
}
