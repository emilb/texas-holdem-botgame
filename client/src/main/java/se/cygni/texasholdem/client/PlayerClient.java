package se.cygni.texasholdem.client;

import java.util.UUID;

import org.codemonkey.swiftsocketclient.SwiftSocketClient;

import se.cygni.texasholdem.client.message.ClientToServerMessage;
import se.cygni.texasholdem.client.message.ServerToClientMessage;
import se.cygni.texasholdem.communication.lock.ResponseLock;
import se.cygni.texasholdem.communication.message.TexasMessage;
import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.communication.message.exception.TexasException;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.message.request.TexasRequest;
import se.cygni.texasholdem.communication.message.response.ActionResponse;
import se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.message.response.TexasResponse;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Room;
import se.cygni.texasholdem.player.Player;

public class PlayerClient {

    private static final long RESPONSE_TIMEOUT = 80000;
    private static final long CONNECT_WAIT = 1200;

    private final EventDispatcher eventDispatcher;
    private final SyncMessageResponseManager responseManager;
    private final Player player;
    private SwiftSocketClient client;

    public PlayerClient(final Player player) {

        this.player = player;

        responseManager = new SyncMessageResponseManager();
        eventDispatcher = new EventDispatcher(player);

        connect();
    }

    protected void connect() {

        client = new SwiftSocketClient("localhost", 4711);
        client.registerClientMessageToServerType(1, ClientToServerMessage.class);
        client.registerServerMessageToClientType(1, ServerToClientMessage.class);
        client.registerExecutionContext(ServerToClientMessage.class, this);
        client.start();

        try {
            Thread.currentThread().sleep(CONNECT_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public Player getPlayer() {

        return player;
    }

    public void onMessageReceived(final TexasMessage message) {

        if (message instanceof TexasEvent) {
            eventDispatcher.onEvent((TexasEvent) message);
            return;
        }

        if (message instanceof ActionRequest) {
            final Action action = player
                    .actionRequired((ActionRequest) message);
            final ActionResponse response = new ActionResponse();
            response.setRequestId(((ActionRequest) message).getRequestId());
            response.setAction(action);
            client.sendMessage(new ClientToServerMessage(response));
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
    }

    public boolean registerForPlay(Room room)
            throws se.cygni.texasholdem.game.exception.GameException {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.setRequestId(getUniqueRequestId());
        request.name = player.getName();
        request.room = room;

        final TexasMessage resp = sendAndWaitForResponse(request);

        if (resp instanceof RegisterForPlayResponse)
            return true;

        if (resp instanceof TexasException) {
            final TexasException ge = (TexasException) resp;
            ge.throwException();
        }

        return false;
    }

    protected TexasResponse sendAndWaitForResponse(final TexasRequest request) {

        final ResponseLock lock = responseManager.push(request.getRequestId());
        sendRequest(request);
        synchronized (lock) {
            try {
                lock.wait(RESPONSE_TIMEOUT);
            } catch (final InterruptedException e) {
            }
        }

        if (lock.getResponse() == null)
            throw new RuntimeException("Did not get response in time");

        return lock.getResponse();
    }

    protected void sendRequest(final TexasRequest request) {

        client.sendMessage(new ClientToServerMessage(request));
    }

    protected String getUniqueRequestId() {

        return UUID.randomUUID().toString();
    }
}
