package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.util.PokerHandUtil;

public class PhilHellmuthPlayer extends TrainingPlayer {


    public PhilHellmuthPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public PhilHellmuthPlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    @Override
    public Action actionRequired(ActionRequest request) {

        Action callAction = null;
        Action checkAction = null;
        Action foldAction = null;
        Action raiseAction = null;
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
                    break;
                default:
                    break;
            }
        }

        Action action = null;

        CurrentPlayState playState = getCurrentPlayState();

        PokerHandUtil handUtil = new PokerHandUtil(playState.getCommunityCards(), playState.getMyCards());
        PokerHand currentPokerHand = handUtil.getBestHand().getPokerHand();

        if (currentPokerHand.getOrderValue() > 5 && allInAction != null) {
            return allInAction;
        }

        // Always fold if unranked start
        if (getMyCardsTopTenRank() == 0 && currentPokerHand == PokerHand.HIGH_HAND && !playState.amIBigBlindPlayer()) {
            return foldAction;
        }

        if (raiseAction != null && (
                (currentPokerHand.getOrderValue() >= 3) ||
                        (getMyCardsTopTenRank() > 5))) {
            return raiseAction;
        }

        if (playState.amIBigBlindPlayer() && checkAction != null) {
            return checkAction;
        }

        if (checkAction != null) {
            return checkAction;
        }

        if (callAction != null) {
            return callAction;
        }

        return foldAction;
    }

    /**
     * Higher value is better
     *
     * @return
     */
    private int getMyCardsTopTenRank() {

        // 10: A - A
        if (doMyCardsContain(Rank.ACE, Rank.ACE)) {
            return 10;
        }

        //  9: K - K
        if (doMyCardsContain(Rank.KING, Rank.KING)) {
            return 9;
        }

        //  8: Q - Q
        if (doMyCardsContain(Rank.QUEEN, Rank.QUEEN)) {
            return 8;
        }

        //  7: A - K
        if (doMyCardsContain(Rank.ACE, Rank.KING)) {
            return 7;
        }

        //  6: J - J
        if (doMyCardsContain(Rank.JACK, Rank.JACK)) {
            return 6;
        }

        //  5: 10 - 10
        if (doMyCardsContain(Rank.TEN, Rank.TEN)) {
            return 5;
        }

        //  4: 9 - 9
        if (doMyCardsContain(Rank.NINE, Rank.NINE)) {
            return 4;
        }

        //  3: 8 - 8
        if (doMyCardsContain(Rank.EIGHT, Rank.EIGHT)) {
            return 3;
        }

        //  2: A - Q
        if (doMyCardsContain(Rank.ACE, Rank.QUEEN)) {
            return 2;
        }

        //  1: 7 - 7
        if (doMyCardsContain(Rank.SEVEN, Rank.SEVEN)) {
            return 1;
        }

        return 0;
    }

    private boolean doMyCardsContain(Rank rank1, Rank rank2) {
        Card c1 = getCurrentPlayState().getMyCards().get(0);
        Card c2 = getCurrentPlayState().getMyCards().get(1);

        return (c1.getRank() == rank1 && c2.getRank() == rank2) ||
                (c1.getRank() == rank2 && c2.getRank() == rank1);
    }
}
