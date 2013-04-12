package se.cygni.webapp.socket;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.webapp.controllers.controllers.model.WebSocketCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PokerWebSocketAdapter extends WebSocketAdapter {

    private static ObjectMapper mapper = new ObjectMapper();
    private EventBus eventBus;
    private Timer timer;
    private Session session;

    private long subscribeTableId;



    public PokerWebSocketAdapter(EventBus eventBus) {

        this.eventBus = eventBus;

    }

    @Subscribe
    public void addGameLog(final GameLog gameLog) {
        if (gameLog.tableCounter == subscribeTableId) {
            System.out.println("gamelog is a match to current subscription!");
        }
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
        try {
            WebSocketCommand cmd = toWebSocketCommand(message);

            // Do something based om command in message
            switch (cmd.command) {
                case "options" :
                    sendAndForget("Good boy!");
                    break;

                case "subscribeToTable" :

                    break;

                case "subscribeToPlayers" :

                    break;

                default:
                    sendAndForget(new WebSocketCommand("fail", "Failed to handle your request"));
                    break;
            }
        }
        catch (Exception e) {
            sendAndForget(new WebSocketCommand("fail", e.getMessage()   ));
        }
    }


    private void sendAndForget(WebSocketCommand cmd) {
        try {
            sendAndForget(toJsonString(cmd));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toJsonString(Object result) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, result);
        return out.toString();
    }

    private WebSocketCommand toWebSocketCommand(String msg) throws IOException {
        return mapper.readValue(msg, WebSocketCommand.class);
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
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        discard();
    }
}
