package se.cygni.texasholdem.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.player.DummyPlayer;
import se.cygni.texasholdem.player.PlayerInterface;
import se.cygni.texasholdem.player.RandomPlayer;

public class Main {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws Exception {

        final List<PlayerClient> clients = new ArrayList<PlayerClient>();
        clients.add(createDummy());
        clients.add(createDummy());
        clients.add(createRandom());
        clients.add(createRandom());

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                waitForRandom();
                try {
                    for (final PlayerClient client : clients) {
                        final boolean result = client.registerForPlay();
                        System.out.println(client.getPlayer().getName()
                                + " is connected: " + result);
                    }
                } catch (final GameException ge) {
                    ge.printStackTrace();
                }

                while (true) {
                    waitForRandom();
                }
            }
        });

        t.start();
    }

    private static PlayerClient createDummy() {

        return new PlayerClient(new DummyPlayer());
    }

    private static PlayerClient createRandom() {

        return new PlayerClient(new RandomPlayer());
    }

    private static PlayerClient createPlayerClient() {

        return new PlayerClient(createPlayer());
    }

    private static PlayerInterface createPlayer() {

        if (Math.random() < 0.5)
            return new DummyPlayer();

        return new RandomPlayer();
    }

    private static void waitForRandom() {

        try {

            Thread.sleep((long) (1500.0 * Math.random()));
        } catch (final Exception e) {
        }
    }
}
