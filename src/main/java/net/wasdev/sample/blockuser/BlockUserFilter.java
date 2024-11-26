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
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BlockUserFilter implements Filter {

    private static final Set<String> blockUsers = new HashSet<>();

    private static final String BLOCKED_USERS_HTML;

    static {
        InputStream in = BlockUserFilter.class.getResourceAsStream("/blockedUsersMessage.html");

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder builder = new StringBuilder();

        reader.lines().forEach(a -> builder.append(a).append("\r\n"));

        BLOCKED_USERS_HTML = builder.toString();
    }

    @Override
    public void init(FilterConfig cfg) {
        
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
                if (req instanceof HttpServletRequest) {
                    HttpServletRequest httpReq = (HttpServletRequest)req;
                    String remoteUser = httpReq.getRemoteUser();
                    
                    // check to see if the remote user is in the list of blocked users
                    if (blockUsers.contains(remoteUser)) {
                        HttpServletResponse httpResp = (HttpServletResponse)resp;
                        // if they are log them out
                        httpReq.logout();
                        // set the status to 403 (not authorized)
                        httpResp.setStatus(403);
                        PrintWriter writer = resp.getWriter();
                        // write some simple html to say the user is blocked and provide a link to the admin console
                        writer.println(BLOCKED_USERS_HTML);
                        
                        return;
                    }
                }
                chain.doFilter(req, resp);
    }

    public static void addBlockUser(String user) {
        blockUsers.add(user);
    }

    public static boolean isBlockUsers() {
        return !blockUsers.isEmpty();
    }

    @Override
    public void destroy() {
    }

    public static String getBlockUsers() {
        return String.valueOf(blockUsers);
    }

}