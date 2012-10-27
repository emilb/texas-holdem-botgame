package se.cygni.texasholdem.player;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.client.PlayerClient;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Room;

public class DummyPlayer extends BasicPlayer {

    private static Logger log = LoggerFactory
            .getLogger(DummyPlayer.class);

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 4711;

    private static final String HOST_PROPERTY = "host";
    private static final String PORT_PROPERTY = "port";

    private String name = "dummmy_1";

    private PlayerClient playerClient;

    public DummyPlayer() {
        playerClient = new PlayerClient(this, getServerHost(), getServerPort());
    }

    private void populateNewName() {
        name = "dummmy_" + (int) (9000 * Math.random() + 10);
    }

    private String getServerHost() {
        String hostFromSystemProp = System.getProperty(HOST_PROPERTY);
        if (StringUtils.isEmpty(hostFromSystemProp)) {
            return DEFAULT_HOST;
        }

        return hostFromSystemProp;
    }

    private int getServerPort() {
        String portFromSystemProp = System.getProperty(PORT_PROPERTY);
        if (StringUtils.isEmpty(portFromSystemProp)) {
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(portFromSystemProp);
        } catch (Exception e) {
            log.warn("Failed to parse port from system properties");
        }

        return DEFAULT_PORT;
    }

    public static void main(String[] args) {
        DummyPlayer player = new DummyPlayer();
        player.playAGame();
    }

    public void playAGame() {
        try {
            playerClient.connect();
            playerClient.registerForPlay(Room.TRAINING);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void onTableChangedStateEvent(TableChangedStateEvent event) {
        log.info("Table state: {}", event.getState());
    }

    @Override
    public void onCommunityHasBeenDealtACard(CommunityHasBeenDealtACardEvent event) {
        log.info("Community got card: {}", event.getCard());
    }

    @Override
    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event) {
        log.info("I got a card: {}", event.getCard());
    }

    @Override
    public void onPlayIsStarted(PlayIsStartedEvent event) {
        log.info("Play is starting. My table id is: " + event.getTableId());
    }

    @Override
    public Action actionRequired(final ActionRequest request) {

//        waitFor(2500);

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
        if (callAction != null) {
            action = callAction;
        }
        else if (checkAction != null) {
            action = checkAction;
        }
        else {
            action = foldAction;
        }
        log.info("My time to act. Going to {} in state {}", action, playerClient.getCurrentPlayState().getCurrentPlayState());
        log.debug("{} returning action: {}", getName(), action);
        return action;
    }

    @Override
    public void connectionToGameServerLost() {
        log.info("I've lost my connection to the game server!");
        log.info("Connecting for another game!");
        populateNewName();
        playAGame();
//        System.exit(0);
    }

    @Override
    public void onTableIsDone(TableIsDoneEvent event) {

        playerClient.disconnect();
        populateNewName();
        //System.exit(0);
    }

    private void waitFor(long ms) {
        try {
            Thread.currentThread().sleep(ms);
        } catch (InterruptedException e) {

        }
    }
}
