/*******************************************************************************
 * Copyright 2024 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.wasdev.sample.blockuser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class BlockUserServletContainerInit implements ServletContainerInitializer {

    /**
     * This class is invoked on web app start and registgers the BlockFilter if the console.user.block.list
     * exists. It also reads the content of that file to ensure the users are blocked. The console.user.block.list
     * is read from the classloader, but this means it can be placed in the WAS profile properties directory.
     */
    @Override
    public void onStartup(Set<Class<?>> arg0, ServletContext arg1) throws ServletException {

        try {
            Enumeration<URL> blockFiles = BlockUserServletContainerInit.class.getClassLoader().getResources("/console.user.block.list");

            while (blockFiles.hasMoreElements()) {
                URL url = blockFiles.nextElement();
                try (InputStream in = url.openStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
        
                    while ((line = reader.readLine()) != null) {
                        BlockUserFilter.addBlockUser(line);
                    }    
                    
                } catch (IOException ioe) {
                    System.out.println("Exception occured trying to load " + url + " - " + ioe.getLocalizedMessage());
                }
            }

            if (BlockUserFilter.isBlockUsers()) {
                arg1.addFilter("blockFilter", BlockUserFilter.class).addMappingForUrlPatterns(null, false, "/*");
                System.out.println("Blocking the following users from admin console " + BlockUserFilter.getBlockUsers());
            } else {
                System.out.println("No users blocked from admin console.");
            }

        } catch (IOException ioe) {
            System.out.println("Exception occurred trying to load console.user.block.lists - " + ioe.getLocalizedMessage());
        }

    }
    
}
