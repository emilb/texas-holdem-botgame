/*
 * Copyright 2012 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.samples.chat;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Simple AtmosphereHandler that implement the logic to build a Chat application.
 *
 * @author Jeanfrancois Arcand
 */
@AtmosphereHandlerService(path = "/chat")
public class ChatAtmosphereHandler implements AtmosphereHandler {

    private static Logger log = LoggerFactory
            .getLogger(ChatAtmosphereHandler.class);

    public ChatAtmosphereHandler() {
        log.info("ChatAtmosphereHandler created!");
    }

    @Override
    public void onRequest(AtmosphereResource r) throws IOException {
        log.info("onRequest: " + r.getRequest().getMethod());
        String requestValue = getRequestValue(r);

        AtmosphereRequest req = r.getRequest();

        // First, tell Atmosphere to allow bi-directional communication by suspending.
        if (req.getMethod().equalsIgnoreCase("GET")) {
            // The negotiation header is just needed by the sample to list all the supported transport.
            if (req.getHeader("negotiating") == null) {
                r.suspend();
            } else {
                r.getResponse().getWriter().write("OK");
            }
            // Second, broadcast message to all connected users.
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            r.getBroadcaster().broadcast(requestValue);
        }
    }

    private String getRequestValue(AtmosphereResource r) {
        try {
            String val = r.getRequest().getReader().readLine();
            log.info("Request: " + val);
            val = val == null ? "" : val;
            return val.trim();
        } catch (Exception e) {
            log.error("Failed to print request", e);
        }
        return null;
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        AtmosphereResource r = event.getResource();
        AtmosphereResponse res = r.getResponse();

        if (event.isSuspended()) {
            String body = event.getMessage().toString();

            // Simple JSON -- Use Jackson for more complex structure
            // Message looks like { "author" : "foo", "message" : "bar" }
            String author = body.substring(body.indexOf(":") + 2, body.indexOf(",") - 1);
            String message = body.substring(body.lastIndexOf(":") + 2, body.length() - 2);

            res.getWriter().write(new Data(author, message).toString());
            switch (r.transport()) {
                case JSONP:
                case AJAX:
                case LONG_POLLING:
                    event.getResource().resume();
                    break;
                default:
                    res.getWriter().flush();
                    break;
            }
        } else if (!event.isResuming()) {
            event.broadcaster().broadcast(new Data("Someone", "say bye bye!").toString());
        }
    }

    @Override
    public void destroy() {
    }

    private final static class Data {

        private final String text;
        private final String author;

        public Data(String author, String text) {
            this.author = author;
            this.text = text;
        }

        public String toString() {
            return "{ \"text\" : \"" + text + "\", \"author\" : \"" + author + "\" , \"time\" : " + new Date().getTime() + "}";
        }
    }
}
