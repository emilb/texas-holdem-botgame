package se.cygni.texasholdem.game;

import java.util.List;

import se.cygni.texasholdem.game.definitions.PokerHand;

public class BestHand implements Comparable<BestHand> {

    private final List<Card> cards;
    private final PokerHand pokerHand;
    private BotPlayer player;

    public BestHand(final List<Card> cards, final PokerHand pokerHand) {

        this.cards = cards;
        this.pokerHand = pokerHand;
    }

    public BestHand(final List<Card> cards, final PokerHand pokerHand,
            final BotPlayer player) {

        this.cards = cards;
        this.pokerHand = pokerHand;
        this.player = player;
    }

    public List<Card> getCards() {

        return cards;
    }

    public PokerHand getPokerHand() {

        return pokerHand;
    }

    public BotPlayer getPlayer() {

        return player;
    }

    public void setPlayer(final BotPlayer player) {

        this.player = player;
    }

    @Override
    public int compareTo(final BestHand other) {

        if (pokerHand.getOrderValue() < other.getPokerHand().getOrderValue())
            return 1;

        if (pokerHand.getOrderValue() > other.getPokerHand().getOrderValue())
            return -1;

        // Must be equal pokerhands, let the one with the highest card win
        for (int i = 0; i < cards.size(); i++) {
            final Card card = cards.get(i);
            final Card otherCard = other.getCards().get(i);

            if (card.getRank() == otherCard.getRank())
                continue;

            if (card.getRank().getOrderValue() < otherCard.getRank().getOrderValue())
                return 1;

            return -1;
        }

        // Cards are value-wise equal
        return 0;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        sb.append(player.getName()).append(" cards: ");
        for (final Card c : cards) {
            sb.append(c).append(", ");
        }
        sb.append("hand: ").append(pokerHand);

        return sb.toString();
    }
}
