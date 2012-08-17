package se.cygni.texasholdem.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.game.Room;
import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.player.DummyPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestPlayMain {

    final static long MAX_RUNNING_TIME = 90 * 1000; // 25 seconds
    private static final Logger log = LoggerFactory
            .getLogger(TestPlayMain.class);

    /**
     * Creates six players and lets them connect and play session of poker.
     * <p/>
     * After MAX_RUNNING_TIME ms the program will terminate (as a convenience
     * when running from Eclipse)
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws Exception {

        final List<PlayerClient> clients = new ArrayList<PlayerClient>();
        clients.add(createDummy());

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                final long now = System.currentTimeMillis();

                try {
                    for (final PlayerClient client : clients) {
                        log.debug("Letting " + client.getPlayerName()
                                + " connect...");
                        final boolean result = client.registerForPlay(Room.TRAINING);
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

    private static void waitForRandom() {

        try {

            Thread.sleep((long) (1500.0 * Math.random()));
        } catch (final Exception e) {
        }
    }
}
