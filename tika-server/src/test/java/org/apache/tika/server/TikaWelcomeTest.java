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

package org.apache.tika.server;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.tika.Tika;
import org.junit.Test;

public class TikaWelcomeTest extends CXFTestBase {
   protected static final String WELCOME_PATH = "/";
   private static final String VERSION_PATH = TikaVersionTest.VERSION_PATH;

   @Override
   protected void setUpResources(JAXRSServerFactoryBean sf) {
       sf.setResourceClasses(TikaWelcome.class, TikaVersion.class);
       sf.setResourceProvider(
               TikaWelcome.class,
               new SingletonResourceProvider(new TikaWelcome(tika, sf))
       );
       sf.setResourceProvider(
               TikaVersion.class,
               new SingletonResourceProvider(new TikaVersion(tika))
       );
   }

   @Override
   protected void setUpProviders(JAXRSServerFactoryBean sf) {}

   @Test
   public void testGetHTMLWelcome() throws Exception {
       Response response = WebClient
               .create(endPoint + WELCOME_PATH)
               .type("text/html")
               .accept("text/html")
               .get();

       String html = getStringFromInputStream((InputStream) response.getEntity());
       
       assertContains(new Tika().toString(), html);
       assertContains("href=\"http", html);
       
       // Check our details were found
       assertContains("GET", html);
       assertContains(WELCOME_PATH, html);
       assertContains("text/plain", html);
       assertContains("text/html", html);
       
       // Check that the Tika Version details come through too
       assertContains(VERSION_PATH, html);
   }

   @Test
   public void testGetTextWelcome() throws Exception {
       Response response = WebClient
               .create(endPoint + WELCOME_PATH)
               .type("text/plain")
               .accept("text/plain")
               .get();

       String text = getStringFromInputStream((InputStream) response.getEntity());
       assertContains(new Tika().toString(), text);
       
       // Check our details were found
       assertContains("GET " + WELCOME_PATH, text);
       assertContains("=> text/plain", text);
       assertContains("=> text/html", text);
       
       // Check that the Tika Version details come through too
       assertContains("GET " + VERSION_PATH, text);
   }
}
