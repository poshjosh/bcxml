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
import java.io.InputStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2019 4:23:44 PM
 */
public interface DomReader {

    Document parse(InputSource in, String uriString) throws IOException, SAXException;

    Document read(String uriString);

    Document read(File file);

    Document read(InputStream in);

    Document read(InputSource in);

    Document read(InputSource in, String uriString);

}
