package se.cygni.webapp.socket;


import com.google.common.eventbus.EventBus;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PokerWebSocketAdapter extends WebSocketAdapter {

    private EventBus eventBus;
    private Timer timer;
    private Session session;

    public PokerWebSocketAdapter(EventBus eventBus) {

        this.eventBus = eventBus;

    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);

        eventBus.register(this);
        this.session = session;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendAndForget("Hi from server socket, now with eventbus");
            }
        }, 1000L, 1000L);
    }

    @Override
    public void onWebSocketText(String message) {
        // Do something based om command in message
        switch (message) {
            case "listCommands" :

                break;

            case "subscribeToGame" :

                break;

            default:
                sendAndForget("You did not specify a valid command!");
                break;
        }
    }

    private void sendAndForget(String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
        }
    }

    private void discard() {
        eventBus.unregister(this);
        eventBus = null;
        timer.cancel();
        try {
            session.close();
            session = null;
        } catch (IOException e) {}
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        super.onWebSocketBinary(payload, offset, len);
        System.out.println(new String(payload));
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        discard();
    }
}
