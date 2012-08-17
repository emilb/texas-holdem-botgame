package se.cygni.texasholdem.webclient;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.response.ActionResponse;
import se.cygni.texasholdem.game.exception.GameException;


/**
 * AtmosphereHandler that implement the logic to build a Cygni Texas Holdem poker web client
 *
 * @author Jonas Öhrn
 */
@AtmosphereHandlerService(path = "/poker")
public class PokerAtmosphereHandler implements AtmosphereHandler {

    private static Logger log = LoggerFactory
            .getLogger(PokerAtmosphereHandler.class);

    
    private Map<String, WebPlayerClient> clients = new ConcurrentHashMap<String, WebPlayerClient>();
    
    public PokerAtmosphereHandler() {
        log.info("PokerAtmosphereHandler created!");
    }

    @Override
    public void onRequest(AtmosphereResource r) throws IOException {
        AtmosphereRequest req = r.getRequest();

        if (req.getMethod().equalsIgnoreCase("GET")) {
            // GET is used for server-to-client events so suspend.
            log.info("poker: onRequest: GET");
        	r.suspend();
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
        	// POST => ActionRequest or RegisterForPlayRequest
        	String body = getRequestBody(r);
        	String connectionId = req.getHeader("connectionId");
        	TexasMessage texasMessage = TexasMessageParser.decodeMessage(body);

            WebPlayerClient webclient = webclientForConnection(connectionId, r, body, texasMessage);

        	if (webclient == null) {
        		return;
        	}
        	if (texasMessage instanceof RegisterForPlayRequest) {
        		return; // already taken care of in webclientForConnection()
        	}
            if (texasMessage instanceof ActionResponse) {
            	ActionResponse actionResponse = ((ActionResponse)texasMessage);
            	webclient.sendMessage(actionResponse);
            } else {
            	log.error("error: illegal web client request, body:" + body);
            }
        	
        } else if (req.getMethod().equalsIgnoreCase("DELETE")) {
        	// close / destroy
        	clients.remove(r.uuid());
        	destroy();
        }
    }


	private WebPlayerClient webclientForConnection(String connectionId, AtmosphereResource r,
			String body, TexasMessage texasMessage) throws IOException {
		WebPlayerClient webclient = clients.get(connectionId);
		if (webclient == null) {
			log.info("getting webclient for websocket connection: "+connectionId);
			// first request for this connection(AtmosphereResource)
			if (texasMessage instanceof RegisterForPlayRequest) {
				String playerName = ((RegisterForPlayRequest)texasMessage).name;
				log.info("Registring for play; "+playerName);

                try {
                    webclient = new WebPlayerClient(playerName, r);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
                try {
					boolean ok = webclient.registerForPlay();
					if (!ok) {
						log.error("error on webclient.registerForPlay()");
						return null;
					}
				} catch (GameException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				clients.put(connectionId, webclient);
			} else {
				String errorMsg = "First request must be a RegisterForPlayRequest but was: "+body;
				log.error(errorMsg);
				r.getResponse().getWriter().write("error: "+errorMsg);
				r.getResponse().getWriter().flush();
				return null;
			}
		}
		return webclient;
	}

    private String getRequestBody(AtmosphereResource r) {
        try {
            String val = r.getRequest().getReader().readLine();
            log.info("Poker: line: " + val);
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

        if (event.isSuspended()) {
            String msg = event.getMessage().toString();
            log.info("onStateChange: event.getMessage():" + msg);
        } else if (!event.isResuming()) {
            log.info("onStateChange: notSuspended and not resuming - closing");
        }
    }

    @Override
    public void destroy() {
    }
}
