package se.cygni.texasholdem.game.definitions;

import java.util.Comparator;

import se.cygni.texasholdem.game.Card;

public enum CardSortBy {

    RANK(new Comparator<Card>() {

        @Override
        public int compare(final Card first, final Card second) {

            final Integer firstVal = Integer.valueOf(first.rank()
                    .getOrderValue());
            final Integer secondVal = Integer.valueOf(second.rank()
                    .getOrderValue());

            final int comparison = firstVal.compareTo(secondVal);
            if (comparison == 0) {
                final String firstStrVal = first.suit().getShortName();
                final String secondStrVal = second.suit().getShortName();

                return firstStrVal.compareTo(secondStrVal);
            }

            return comparison;
        }
    }),

    SUIT(new Comparator<Card>() {

        @Override
        public int compare(final Card first, final Card second) {

            final String firstVal = first.suit().getShortName();
            final String secondVal = second.suit().getShortName();

            final int comparison = firstVal.compareTo(secondVal);
            if (comparison == 0) {
                final Integer firstIntVal = Integer.valueOf(first.rank()
                        .getOrderValue());
                final Integer secondIntVal = Integer.valueOf(second.rank()
                        .getOrderValue());

                return firstIntVal.compareTo(secondIntVal);
            }

            return comparison;
        }
    });

    private final Comparator<Card> comparator;

    private CardSortBy(final Comparator<Card> comparator) {

        this.comparator = comparator;
    }

    public Comparator<Card> getComparator() {

        return comparator;
    }
}
