package se.cygni.texasholdem.client;

import java.io.IOException;

import se.cygni.texasholdem.game.GameService;
import se.cygni.texasholdem.player.DummyPlayer;
import se.cygni.texasholdem.player.PlayerInterface;

public class Main {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws Exception {

        final PlayerInterface player = new DummyPlayer();
        final GameService gameService = new GameService(player, 4711,
                "localhost");
        gameService.connect();

        gameService.registerForPlay();
    }

}
