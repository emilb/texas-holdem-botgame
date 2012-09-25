package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.*;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;
import se.cygni.texasholdem.game.util.CardsUtil;
import se.cygni.texasholdem.game.util.PokerHandUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticalPlayer extends TrainingPlayer {

    public StatisticalPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public StatisticalPlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    @Override
    public Action actionRequired(ActionRequest request) {

        if (getCurrentPlayState().getCurrentPlayState() == PlayState.PRE_FLOP) {
            return useHellmuthStartStrategy(request);
        }

        ActionManager actionManager = new ActionManager(request);
        CurrentPlayState ps = getCurrentPlayState();

        Map<PokerHand, Double> distribution = getPossibleHandsStatisticalDistribution();
        return actionManager.getFoldAction();
    }

    private Action useHellmuthStartStrategy(ActionRequest request) {

        CurrentPlayState ps = getCurrentPlayState();

        ActionManager actionManager = new ActionManager(request);
        int startingHandRank = getMyCardsTopTenRank();

        // Fold if rank is zero
        if (startingHandRank == 0)
            return actionManager.getFoldAction();

        long myInvestment = ps.getMyInvestmentInPot();

        // Bet as long my investment will not be larger than rank * currentBigBlind
        if (actionManager.has(ActionType.RAISE)) {
            long raiseCost = actionManager.getRaiseAction().getAmount();
            if (myInvestment + raiseCost < ps.getBigBlind() * startingHandRank)
                return actionManager.getRaiseAction();
        }

        // Else see if we can call
        if (actionManager.has(ActionType.CALL)) {
            long callCost = actionManager.getCallAction().getAmount();
            if (myInvestment + callCost < ps.getBigBlind() * startingHandRank)
                return actionManager.getCallAction();
        }

        // Check if possible
        if (actionManager.has(ActionType.CHECK))
            return actionManager.getCheckAction();

        return actionManager.getFoldAction();
    }

    private Map<PokerHand, Double> getPossibleHandsStatisticalDistribution() {

        // init result
        Map<PokerHand, AtomicInteger> distribution = new HashMap<PokerHand, AtomicInteger>();
        for (PokerHand hand : PokerHand.values()) { distribution.put(hand, new AtomicInteger(0)); }

        int noofIterations = 1000;

        CurrentPlayState ps = getCurrentPlayState();
        int noofOtherPlayers = ps.getPlayers().size()-1;

        for (int i = 0; i < noofIterations; i++) {
            InspectableDeck deck = new InspectableDeck(ps.getCurrentPlayState().ordinal(), noofOtherPlayers, ps.getMyCardsAndCommunityCards());

            List<Card> communityCards = new ArrayList<Card>(ps.getCommunityCards());
            switch (ps.getCurrentPlayState()) {
                case PRE_FLOP:
                    deck.burn();
                    communityCards.add(deck.getNextCard());
                    communityCards.add(deck.getNextCard());
                    communityCards.add(deck.getNextCard());
                    deck.burn();
                    communityCards.add(deck.getNextCard());
                    deck.burn();
                    communityCards.add(deck.getNextCard());
                    break;

                case FLOP:
                    deck.burn();
                    communityCards.add(deck.getNextCard());
                    deck.burn();
                    communityCards.add(deck.getNextCard());
                    break;

                case TURN:
                    deck.burn();
                    communityCards.add(deck.getNextCard());
            }

            PokerHandUtil pokerHandUtil = new PokerHandUtil(communityCards, ps.getMyCards());
            Hand myBestHand = pokerHandUtil.getBestHand();
            PokerHand myBestPokerHand = myBestHand.getPokerHand();

            distribution.get(myBestPokerHand).incrementAndGet();
        }

        // Recalc distribution to percentages
        Map<PokerHand, Double> distributionPercentage = new HashMap<PokerHand, Double>();
        for (PokerHand hand : PokerHand.values()) {
            distributionPercentage.put(hand, Double.valueOf(
                    distribution.get(hand).doubleValue() / noofIterations));
        }

        return distributionPercentage;
    }

    /**
     * Compares two pokerhands.
     *
     * @param myPokerHand
     * @param otherPokerHand
     * @return TRUE if myPokerHand is valued higher than otherPokerHand
     */
    private boolean isHandBetterThan(PokerHand myPokerHand, PokerHand otherPokerHand) {
        return myPokerHand.getOrderValue() > otherPokerHand.getOrderValue();
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

    private class ActionManager {

        Action callAction = null;
        Action checkAction = null;
        Action raiseAction = null;
        Action foldAction = null;
        Action allInAction = null;

        public ActionManager(ActionRequest request) {

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
        }

        public boolean has(ActionType type) {
            switch (type) {
                case CALL:
                    return callAction != null;
                case CHECK:
                    return checkAction != null;
                case FOLD:
                    return foldAction != null;
                case RAISE:
                    return raiseAction != null;
                case ALL_IN:
                    return allInAction != null;
            }
            return false;
        }

        public Action getAction(ActionType type) {
            switch (type) {
                case CALL:
                    return callAction;
                case CHECK:
                    return checkAction;
                case FOLD:
                    return foldAction;
                case RAISE:
                    return raiseAction;
                case ALL_IN:
                    return allInAction;
            }
            return null;
        }

        public Action getCallAction() {
            return callAction;
        }

        public Action getCheckAction() {
            return checkAction;
        }

        public Action getRaiseAction() {
            return raiseAction;
        }

        public Action getFoldAction() {
            return foldAction;
        }

        public Action getAllInAction() {
            return allInAction;
        }
    }

    private class InspectableDeck {

        private List<Card> deck;

        public InspectableDeck(int cardsToBurn, int noofOtherPlayers, List<Card> knownCards) {

            deck = new ArrayList<Card>(52);

            // list of cards, excluding known cards
            for (final Suit suit : Suit.values()) {
                for (final Rank rank : Rank.values()) {
                    Card card = Card.valueOf(rank, suit);

                    if (!knownCards.contains(card))
                        deck.add(Card.valueOf(rank, suit));
                }
            }

            // Shuffle the deck
            Collections.shuffle(deck);

            // Burn the specified noof cards
            for (int i = 0; i < cardsToBurn + noofOtherPlayers * 2; i++) {
                burn();
            }
        }

        public void burn() {
            deck.remove(0);
        }

        public Card getNextCard() {
            return deck.remove(0);
        }
    }
}


