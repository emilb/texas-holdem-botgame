package se.cygni.texasholdem.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.client.PlayerClient;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Room;

public class SimplestPossibleBot extends BasicPlayer {

    private static Logger log = LoggerFactory
            .getLogger(SimplestPossibleBot.class);

    private final String serverHost;
    private final int serverPort;
    private final PlayerClient playerClient;

    public SimplestPossibleBot(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        // Initialize the player client
        playerClient = new PlayerClient(this, serverHost, serverPort);
    }

    public void playATrainingGame() throws Exception {
        playerClient.connect();
        playerClient.registerForPlay(Room.TRAINING);
    }

    @Override
    public String getName() {
        throw new RuntimeException("Did you forget to specify a name for your bot?");
    }

    @Override
    public Action actionRequired(ActionRequest request) {

        Action callAction = null;
        Action checkAction = null;
        Action foldAction = null;

        for (final Action action : request.getPossibleActions()) {
            switch (action.getActionType()) {
                case CALL:
                    callAction = action;
                    break;
                case CHECK:
                    checkAction = action;
                    break;
                case FOLD:
                    foldAction = action;
                    break;
                default:
                    break;
            }
        }

        Action action = null;
        if (callAction != null)
            action = callAction;
        else if (checkAction != null)
            action = checkAction;
        else
            action = foldAction;

        log.debug("{} returning action: {}", getName(), action);
        return action;
    }

    @Override
    public void connectionToGameServerLost() {
        log.info("Connection to game server is lost. Exit time");
        System.exit(0);
    }

    public static void main(String... args) {
        SimplestPossibleBot bot = new SimplestPossibleBot("poker.cygni.se", 4711);

        try {
            bot.playATrainingGame();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
