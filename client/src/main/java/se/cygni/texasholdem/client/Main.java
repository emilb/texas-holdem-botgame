package se.cygni.texasholdem.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.player.DummyPlayer;
import se.cygni.texasholdem.player.PlayerInterface;
import se.cygni.texasholdem.player.RaiserPlayer;
import se.cygni.texasholdem.player.RandomPlayer;

public class Main {

    final static long MAX_RUNNING_TIME = 25 * 1000; // 25 seconds
    private static final Logger log = LoggerFactory.getLogger(Main.class);

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
        clients.add(createRaiser());
        clients.add(createRaiser());

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                final long now = System.currentTimeMillis();

                try {
                    for (final PlayerClient client : clients) {
                        final boolean result = client.registerForPlay();
                        log.debug(client.getPlayer().getName()
                                + " is connected: " + result);
                    }
                } catch (final GameException ge) {
                    ge.printStackTrace();
                    System.exit(-1);
                }

                while (now + MAX_RUNNING_TIME > System.currentTimeMillis()) {
                    waitForRandom();
                }
                log.info("Time for game is up, System.exit(0)");
                System.exit(0);
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

    private static PlayerClient createRaiser() {

        return new PlayerClient(new RaiserPlayer());
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
