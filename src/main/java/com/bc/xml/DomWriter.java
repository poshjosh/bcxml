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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2019 4:24:16 PM
 */
public interface DomWriter {

    boolean write(Document doc);

    boolean write(Node node, String uriString);

    boolean write(Node node, URI uri);

    boolean write(Node node, File file);

    boolean write(Node node, OutputStream out);

    boolean write(Node node, OutputStream out, String path);

    boolean writeNode(Node node, OutputStream out, String path) throws IOException, TransformerException;

}
