package se.cygni.texasholdem.webclient;

import java.io.IOException;
import java.util.UUID;

import org.atmosphere.cpr.AtmosphereResource;
import org.codemonkey.swiftsocketclient.SwiftSocketClient;

import se.cygni.texasholdem.client.SyncMessageResponseManager;
import se.cygni.texasholdem.client.message.ClientToServerMessage;
import se.cygni.texasholdem.client.message.ServerMessageReceiver;
import se.cygni.texasholdem.client.message.ServerToClientMessage;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.communication.message.exception.TexasException;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;

public class WebPlayerClient implements ServerMessageReceiver {

    private static final long RESPONSE_TIMEOUT = 8000;

	private AtmosphereResource atmosphereResource;
    private final SyncMessageResponseManager responseManager;
    private SwiftSocketClient client;
    private String playerName;

	public WebPlayerClient(final String playerName, final AtmosphereResource atmosphereResource) {
		this.playerName = playerName;
        responseManager = new SyncMessageResponseManager();
		this.atmosphereResource = atmosphereResource;
		connect();
	}

	protected void connect() {
        client = new SwiftSocketClient("localhost", 4711);
        client.registerClientMessageToServerType(1, ClientToServerMessage.class);
        client.registerServerMessageToClientType(1, ServerToClientMessage.class);
        client.registerExecutionContext(ServerToClientMessage.class, this);
        client.start();
        waitForClientConnected();
    }

	// TODO: 
	// rensa upp, blockar inte här - i så fall känn av att respons kommit i webklienten
    public boolean registerForPlay()
            throws se.cygni.texasholdem.game.exception.GameException {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.setRequestId(getUniqueRequestId());
        request.name = getPlayerName();
        
        // Skicka request och vänta på svar
        final TexasMessage resp = sendAndWaitForResponse(request);
       
		String responseJson = null;
		try {
			responseJson = TexasMessageParser.encodeMessage(resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (resp instanceof RegisterForPlayResponse) {
    		try {
				respondAndFlush(responseJson);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return true;
        }

        if (resp instanceof TexasException) {
            final TexasException ge = (TexasException) resp;
            ge.throwException();
        }

        return false;
    }

    /**
     * SwiftSocketClient takes a few 10ths of a seconds to start up.
     */
	private void waitForClientConnected() {
		int count=0;
        while (!(client.isConnected() && client.isRunning()) && ++count<100) {
	        	try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
			}
        }
	}

	protected String getPlayerName() {
		return playerName;
	}
	
   protected TexasResponse sendAndWaitForResponse(final TexasRequest request) {

        final ResponseLock lock = responseManager.push(request.getRequestId());
        sendRequest(request);
        synchronized (lock) {
        	if (lock.getResponse() == null) {
	            try {
	                lock.wait(RESPONSE_TIMEOUT);
	            } catch (final InterruptedException e) {
	            }
            }
        }

        if (lock.getResponse() == null)
            throw new RuntimeException("Did not get response in time");

        return lock.getResponse();
    }

	   
	/**
	 * Handler for server-to-client messages
	 * 
	 */
    public void onMessageReceived(final TexasMessage message) {

        if (message instanceof TexasEvent) {
        	String texasEventJson;
			try {
				texasEventJson = TexasMessageParser.encodeMessage(message);
				respondAndFlush(texasEventJson);
			} catch (Exception e) {
				// TODO felhantering
				throw new RuntimeException("Error on forwarding TexasEvent to client", e);
			}
            return;
        }

        // Deferr this to web client by resuming suspended websocket/request with 
        // ActionRequest response. This will cause the javascript webclient to respond to
        // with Action taken by that player, sent as a POST-message to the handler
        // which won't be suspended
        if (message instanceof ActionRequest) {
        	try {
				String actionRequestJson = TexasMessageParser.encodeMessage(message);
				respondAndFlush(actionRequestJson);
			} catch (IOException e) {
				// TODO felhantering
				throw new RuntimeException("Error on forwarding ActionRequest to client", e);
			}
        }

        if (message instanceof TexasResponse) {
            final TexasResponse response = (TexasResponse) message;
            final String requestId = response.getRequestId();

            final ResponseLock lock = responseManager.pop(requestId);
            lock.setResponse(response);

            synchronized (lock) {
                lock.notifyAll();
            }
        }

        // Variant om vi inte gör sendAndWaitForResponse() - svara klienten direkt här
//        if (message instanceof TexasResponse) {
//        	try {
//				String actionRequestJson = TexasMessageParser.encodeMessage(message);
//				respondAndFlush(actionRequestJson);
//			} catch (IOException e) {
//				// TODO felhantering
//				throw new RuntimeException("Error on forwarding TexasResponse to client", e);
//			}
//        }
    }


	private void respondAndFlush(String jsonResponse) throws IOException {
		atmosphereResource.getResponse().getWriter().write(jsonResponse);
		atmosphereResource.getResponse().getWriter().flush();
	}

    protected void sendRequest(final TexasRequest request) {
        client.sendMessage(new ClientToServerMessage(request));
    }

    public void sendResponse(final TexasResponse response) {
        client.sendMessage(new ClientToServerMessage(response));
    }

    protected String getUniqueRequestId() {

        return UUID.randomUUID().toString();
    }

}
