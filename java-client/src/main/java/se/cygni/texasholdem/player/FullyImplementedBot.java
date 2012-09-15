package se.cygni.texasholdem.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.client.PlayerClient;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.*;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.util.PokerHandUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FullyImplementedBot implements Player {

    private static Logger log = LoggerFactory
            .getLogger(FullyImplementedBot.class);

    private final String serverHost;
    private final int serverPort;
    private final PlayerClient playerClient;

    public FullyImplementedBot(String serverHost, int serverPort) {
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
    public void serverIsShuttingDown(ServerIsShuttingDownEvent event) {
    }

    @Override
    public void onPlayIsStarted(PlayIsStartedEvent event) {
    }

    @Override
    public void onTableChangedStateEvent(TableChangedStateEvent event) {
    }

    @Override
    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event) {
    }

    @Override
    public void onCommunityHasBeenDealtACard(CommunityHasBeenDealtACardEvent event) {
    }

    @Override
    public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event) {
    }

    @Override
    public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event) {
    }

    @Override
    public void onPlayerFolded(PlayerFoldedEvent event) {
    }

    @Override
    public void onPlayerCalled(PlayerCalledEvent event) {
    }

    @Override
    public void onPlayerRaised(PlayerRaisedEvent event) {
    }

    @Override
    public void onPlayerWentAllIn(PlayerWentAllInEvent event) {
    }

    @Override
    public void onPlayerChecked(PlayerCheckedEvent event) {
    }

    @Override
    public void onYouWonAmount(YouWonAmountEvent event) {
    }

    @Override
    public void onShowDown(ShowDownEvent event) {
    }

    @Override
    public void onTableIsDone(TableIsDoneEvent event) {
        log.debug("Table is done, I'm leaving the table with ${}", playerClient.getCurrentPlayState().getMyCurrentChipAmount());
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @Override
    public Action actionRequired(ActionRequest request) {

        Action response = getBestAction(request);
        log.info("I'm returning {}", response);
        return response;
    }

    private Action getBestAction(ActionRequest request) {
        Action callAction = null;
        Action checkAction = null;
        Action raiseAction = null;
        Action foldAction = null;
        Action allInAction = null;

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
                case RAISE:
                    raiseAction = action;
                    break;
                case ALL_IN:
                    allInAction = action;
                default:
                    break;
            }
        }

        // The current play state is accessible through this class. It
        // keeps track of basic events and other players.
        CurrentPlayState playState = playerClient.getCurrentPlayState();
        long currentBB = playState.getBigBlind();

        // PokerHandUtil is a hand classifier that returns the best hand given
        // the current community cards and your cards.
        PokerHandUtil pokerHandUtil = new PokerHandUtil(playState.getCommunityCards(), playState.getMyCards());
        Hand myBestHand = pokerHandUtil.getBestHand();
        PokerHand myBestPokerHand = myBestHand.getPokerHand();


        // Let's go ALL IN if ROYAL FLUSH or STRAIGHT FLUSH
        if (allInAction != null && isHandBetterThan(myBestPokerHand, PokerHand.FOUR_OF_A_KIND)) {
            return allInAction;
        }

        // Otherwise, be more careful CHECK if possible.
        if (checkAction != null)
            return checkAction;

        // Okay, either CALL or RAISE
        long callAmount = callAction == null ? -1 : callAction.getAmount();
        long raiseAmount = raiseAction == null ? -1 : raiseAction.getAmount();

        // Only call if ONE_PAIR or better
        if (isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR) && callAction != null) {
            return callAction;
        }

        // Do I have something better than TWO_PAIR and can RAISE?
        if (isHandBetterThan(myBestPokerHand, PokerHand.TWO_PAIRS) && raiseAction != null) {
            return raiseAction;
        }

        // I'm small blind and we're in PRE_FLOP, might just as well call
        if (playState.getCurrentPlayState() == PlayState.PRE_FLOP) {
            if (callAction != null && callAction.getAmount() < currentBB)
                return callAction;
        }


        // failsafe
        return foldAction;
    }

    private boolean isHandBetterThan(PokerHand myPokerHand, PokerHand otherPokerHand) {
        return myPokerHand.getOrderValue() > otherPokerHand.getOrderValue();
    }

    @Override
    public void connectionToGameServerLost() {
        log.info("Connection to game server is lost. Exit time");
        System.exit(0);
    }

    @Override
    public void connectionToGameServerEstablished() {
    }

    public static void main(String... args) {
        FullyImplementedBot bot = new FullyImplementedBot("poker.cygni.se", 4711);

        try {
            bot.playATrainingGame();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
