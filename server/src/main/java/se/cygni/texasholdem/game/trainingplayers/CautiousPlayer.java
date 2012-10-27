package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.util.PokerHandUtil;

public class CautiousPlayer extends TrainingPlayer {

    public CautiousPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public CautiousPlayer(String name, String sessionId) {
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
        long currentBB = playState.getBigBlind();

        // PokerHandUtil is a hand classifier that returns the best hand given
        // the current community cards and your cards.
        PokerHandUtil pokerHandUtil = new PokerHandUtil(playState.getCommunityCards(), playState.getMyCards());
        Hand myBestHand = pokerHandUtil.getBestHand();
        PokerHand myBestPokerHand = myBestHand.getPokerHand();


        // Let's go ALL IN if ROYAL FLUSH or STRAIGHT FLUSH
        if (allInAction != null && (
                myBestPokerHand == PokerHand.STRAIGHT_FLUSH ||
                        myBestPokerHand == PokerHand.ROYAL_FLUSH)) {
            return allInAction;
        }

        // Otherwise, be more careful CHECK if possible.
        if (checkAction != null) {
            return checkAction;
        }

        // Okay, either CALL or RAISE
        long callAmount = callAction == null ? -1 : callAction.getAmount();
        long raiseAmount = raiseAction == null ? -1 : raiseAction.getAmount();

        // Do I have something better than ONE_PAIR and can RAISE?
        if (isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR) && raiseAction != null) {
            // This is a big raise... only RAISE if better than THREE_OF_A_KIND
            if (raiseAmount - currentBB > currentBB * 2 && isHandBetterThan(myBestPokerHand, PokerHand.THREE_OF_A_KIND)) {
                return raiseAction;
            }
            else if (raiseAmount == currentBB) {
                return raiseAction;
            }
        }

        // Only call if ONE_PAIR or better
        if (isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR) && callAction != null) {
            // This was an expensive CALL... only do if better than THREE_OF_A_KIND
            if (callAmount - currentBB > currentBB * 2 && isHandBetterThan(myBestPokerHand, PokerHand.ONE_PAIR)) {
                return callAction;
            }
            else if (callAmount <= currentBB) {
                return callAction;
            }
        }

        if (playState.getCurrentPlayState() == PlayState.PRE_FLOP && isHandBetterThan(myBestPokerHand, PokerHand.HIGH_HAND)) {
            if (callAction != null) {
                return callAction;
            }
            if (raiseAction != null && raiseAction.getAmount() < currentBB * 4) {
                return raiseAction;
            }
        }

        if (playState.getCurrentPlayState() == PlayState.PRE_FLOP) {
            if (callAction != null && callAction.getAmount() < currentBB) {
                return callAction;
            }
        }


        // failsafe
        return foldAction;
    }

    private boolean isHandBetterThan(PokerHand myPokerHand, PokerHand otherPokerHand) {
        return myPokerHand.getOrderValue() > otherPokerHand.getOrderValue();
    }

    @Override
    public void connectionToGameServerLost() {
    }

    @Override
    public void connectionToGameServerEstablished() {
    }
}
