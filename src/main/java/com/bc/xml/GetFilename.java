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
import java.util.function.Function;
import org.w3c.dom.DocumentType;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 6, 2019 4:31:12 PM
 */
public class GetFilename implements Function<DocumentType, String>{

    @Override
    public String apply(DocumentType documentType) {
        return getFileName(documentType.getSystemId());
    }

    /**
     * Mirrors logic of method {@link java.io.File#getName()}.
     * Use this method if its not necessary to create a new File object.
     * @param path The path to the file whose name is required
     * @return The name of the file at the specified path
     */
    private String getFileName(String path) {
        String output = getFileName(path, File.separatorChar);
        if(output == null) {
            output = getFileName(path, '/');
            if(output == null) {
                output = getFileName(path, '\\');
            }
        }
        return output;
    }
    
    private String getFileName(String path, char separatorChar) {
	int index = path.lastIndexOf(separatorChar);
	if (index == -1 || index == 0) return null;
	return path.substring(index + 1);
    }
}
