package se.cygni.texasholdem.client;

import java.io.IOException;

import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.player.DummyPlayer;

public class Main {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws Exception {

        final DummyPlayer player = new DummyPlayer();
        final PlayerClient client = new PlayerClient(player);

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                waitForRandom();
                try {
                    final boolean result = client.registerForPlay();
                    if (result)
                        System.out.println("Login success!");
                    else
                        System.out.println("Login failed");
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

    private static void waitForRandom() {

        try {

            Thread.sleep((long) (1500.0 * Math.random()));
        } catch (final Exception e) {
        }
    }
}
