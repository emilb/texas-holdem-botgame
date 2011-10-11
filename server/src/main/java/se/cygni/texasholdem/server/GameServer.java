package se.cygni.texasholdem.server;

import java.util.HashMap;
import java.util.Map;

import se.cygni.texasholdem.game.BotPlayer;

import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

public class GameServer {

    final Map<String, RpcClientChannel> sessionChannelMap = new HashMap<String, RpcClientChannel>();
    final Map<BotPlayer, String> playerSessionMap = new HashMap<BotPlayer, String>();

    public boolean isValidSession(final String sessionId) {

        if (!sessionChannelMap.containsKey(sessionId))
            return false;

        if (!sessionChannelMap.containsKey(sessionId))
            return false;

        return true;
    }
}
