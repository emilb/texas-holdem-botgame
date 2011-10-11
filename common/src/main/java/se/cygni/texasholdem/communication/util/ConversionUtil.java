package se.cygni.texasholdem.communication.util;

import java.util.ArrayList;
import java.util.List;

import se.cygni.texasholdem.communication.ClientServer.ExceptionEvent;
import se.cygni.texasholdem.communication.ClientServer.PBAction;
import se.cygni.texasholdem.communication.ClientServer.PBActionType;
import se.cygni.texasholdem.communication.ClientServer.PBCard;
import se.cygni.texasholdem.communication.ClientServer.PBExceptionType;
import se.cygni.texasholdem.communication.ClientServer.PBPlayer;
import se.cygni.texasholdem.communication.ClientServer.PBPlayers;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.ActionType;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;
import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.game.exception.InvalidAmountException;
import se.cygni.texasholdem.game.exception.InvalidSessionException;
import se.cygni.texasholdem.game.exception.NotInCorrectPlayStateException;
import se.cygni.texasholdem.game.exception.PlayerNameAlreadyTakenException;

public class ConversionUtil {

    public static Player convertPBPlayer(final PBPlayer pbPlayer) {

        return new Player(pbPlayer.getName(),
                pbPlayer.getChipCount(), pbPlayer.getInPlay());
    }

    public static List<Player> convertPBPlayers(final PBPlayers pbPlayers) {

        final List<Player> players = new ArrayList<Player>();

        for (final PBPlayer pbPlayer : pbPlayers.getPlayersList()) {

            players.add(convertPBPlayer(pbPlayer));
        }

        return players;
    }

    public static Card convertPBCard(final PBCard pbCard) {

        final int rankNo = pbCard.getRank().getNumber();
        final int suitNo = pbCard.getSuit().getNumber();

        final Rank rank = Rank.values()[rankNo];
        final Suit suit = Suit.values()[suitNo];

        return Card.valueOf(rank, suit);
    }

    public static List<Card> convertPBCards(final List<PBCard> pbCards) {

        final List<Card> cards = new ArrayList<Card>();

        for (final PBCard pbCard : pbCards) {
            cards.add(convertPBCard(pbCard));
        }

        return cards;
    }

    public static Action convertPBAction(final PBAction pbAction) {

        final ActionType actionType = ActionType.values()[pbAction
                .getActionType().getNumber()];
        return new Action(actionType, pbAction.getAmount());
    }

    public static PBAction convertAction(final Action action) {

        return PBAction
                .newBuilder()
                .setAmount(action.getAmount())
                .setActionType(
                        PBActionType.valueOf(action.getActionType().ordinal()))
                .build();
    }

    public static void checkForException(final ExceptionEvent e)
            throws GameException {

        if (e == null)
            return;

        if (e.getExceptionType() == PBExceptionType.PLAYER_NAME_ALREADY_TAKEN)
            throw new PlayerNameAlreadyTakenException(e.getMessage());

        if (e.getExceptionType() == PBExceptionType.INVALID_SESSION)
            throw new InvalidSessionException(e.getMessage());

        if (e.getExceptionType() == PBExceptionType.NOT_IN_CORRECT_PLAY_STATE)
            throw new NotInCorrectPlayStateException(e.getMessage());

        if (e.getExceptionType() == PBExceptionType.INVALID_AMOUNT)
            throw new InvalidAmountException(e.getMessage());
    }
}
