/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser.pkg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * CPIO parser.
 */
public class CpioParser extends PackageParser {

    /**
     * Parses the given stream as a cpio file.
     */
    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, Map<String, Object> context)
            throws IOException, TikaException, SAXException {
        // At the end we want to close the cpio stream to release any associated
        // resources, but the underlying document stream should not be closed
        CpioArchiveInputStream cpio =
            new CpioArchiveInputStream(new CloseShieldInputStream(stream));
        try {
            parseArchive(cpio, handler, metadata, context);
        } finally {
            cpio.close();
        }
    }

}