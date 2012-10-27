package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.util.PokerHandUtil;

public class SensiblePlayer extends TrainingPlayer {

    public SensiblePlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public SensiblePlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    @Override
    public Action actionRequired(ActionRequest request) {
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
        CurrentPlayState playState = getCurrentPlayState();

        // The current BigBlind
        long currentBB = playState.getBigBlind();

        // PokerHandUtil is a hand classifier that returns the best hand given
        // the current community cards and your cards.
        PokerHandUtil pokerHandUtil = new PokerHandUtil(playState.getCommunityCards(), playState.getMyCards());
        Hand myBestHand = pokerHandUtil.getBestHand();
        PokerHand myBestPokerHand = myBestHand.getPokerHand();

        // Let's go ALL IN if hand is better than or equal to THREE_OF_A_KIND
        if (allInAction != null && isHandBetterThan(myBestPokerHand, PokerHand.TWO_PAIRS)) {
            return allInAction;
        }

        // Otherwise, be more careful CHECK if possible.
        if (checkAction != null) {
            return checkAction;
        }

        // Okay, we have either CALL or RAISE left
        long callAmount = callAction == null ? -1 : callAction.getAmount();
        long raiseAmount = raiseAction == null ? -1 : raiseAction.getAmount();

        // Only call if ONE_PAIR or better
        if (isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR) && callAction != null) {
            return callAction;
        }

        // Do I have something better than ONE_PAIR and can RAISE?
        if (isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR) && raiseAction != null) {
            return raiseAction;
        }

        // I'm small blind and we're in PRE_FLOP, might just as well call
        if (playState.amISmallBlindPlayer() &&
                playState.getCurrentPlayState() == PlayState.PRE_FLOP &&
                callAction != null) {
            return callAction;
        }

        // failsafe
        return foldAction;
    }

    /**
     * Compares two pokerhands.
     *
     * @param myPokerHand
     * @param otherPokerHand
     *
     * @return TRUE if myPokerHand is valued higher than otherPokerHand
     */
    private boolean isHandBetterThan(PokerHand myPokerHand, PokerHand otherPokerHand) {
        return myPokerHand.getOrderValue() > otherPokerHand.getOrderValue();
    }
}


