package se.cygni.texasholdem.game.util;

import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.dao.model.PlayerInGame;
import se.cygni.texasholdem.game.*;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.pot.Pot;
import se.cygni.texasholdem.game.pot.PotTransaction;

import java.util.*;

public class GameUtil {

    /**
     * Returns the next player that still has chips to play for.
     *
     * @param players
     * @param startingFromPlayer
     * @return
     */
    public static BotPlayer getNextPlayerInPlay(
            final List<BotPlayer> players,
            final BotPlayer startingFromPlayer,
            final Pot pot) {

        final int ix = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        // Start from where startingFromPlayer is positioned
        for (int currIx = ix + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if ((pot != null && pot.isAbleToBet(nextPlayer)) ||
                    (pot == null && playerHasChips(nextPlayer)))
                return nextPlayer;
        }

        // Didn't find a player, start from beginning
        for (int currIx = 0; currIx < ix + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if ((pot != null && pot.isAbleToBet(nextPlayer)) ||
                    (pot == null && playerHasChips(nextPlayer)))
                return nextPlayer;
        }

        return null;
    }

    public static List<BotPlayer> getOrderedListOfPlayersInPlay(
            final List<BotPlayer> players,
            final BotPlayer startingFromPlayer,
            final Pot pot) {

        final List<BotPlayer> result = new ArrayList<BotPlayer>();

        final int startPos = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        for (int currIx = startPos + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if ((pot != null && pot.isAbleToBet(nextPlayer)) ||
                    (pot == null && playerHasChips(nextPlayer)))
                result.add(nextPlayer);
        }

        for (int currIx = 0; currIx < startPos + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            if ((pot != null && pot.isAbleToBet(nextPlayer)) ||
                    (pot == null && playerHasChips(nextPlayer)))
                result.add(nextPlayer);
        }

        return result;
    }

    public static List<BotPlayer> getOrderedListOfPlayers(
            final List<BotPlayer> players,
            final BotPlayer startingFromPlayer) {

        final List<BotPlayer> result = new ArrayList<BotPlayer>();

        final int startPos = startingFromPlayer == null ? -1 : players
                .indexOf(startingFromPlayer);

        for (int currIx = startPos + 1; currIx < players.size(); currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            result.add(nextPlayer);
        }

        for (int currIx = 0; currIx < startPos + 1; currIx++) {
            final BotPlayer nextPlayer = players.get(currIx);
            result.add(nextPlayer);
        }

        return result;
    }

    /**
     * Creates a list of players still active in current game (i.e. has not
     * folded and has money left)
     *
     * @return
     */
    public static List<BotPlayer> getActivePlayersWithChipsLeft(
            final List<BotPlayer> players) {

        final List<BotPlayer> playersWithChipsLeft = new ArrayList<BotPlayer>();

        final Iterator<BotPlayer> iter = players.iterator();
        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            if (GameUtil.playerHasChips(player))
                playersWithChipsLeft.add(player);
        }

        return playersWithChipsLeft;
    }

    public static int getNoofPlayersWithChipsLeft(final List<BotPlayer> players) {
        return getActivePlayersWithChipsLeft(players).size();
    }

    public static List<BotPlayer> getBustedPlayers(
            final List<BotPlayer> players) {

        final List<BotPlayer> playersWithoutChipsLeft = new ArrayList<BotPlayer>();

        final Iterator<BotPlayer> iter = players.iterator();
        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            if (!GameUtil.playerHasChips(player))
                playersWithoutChipsLeft.add(player);
        }

        return playersWithoutChipsLeft;
    }

    public static boolean playerHasChips(final BotPlayer nextPlayer) {

        return nextPlayer.getChipAmount() > 0;
    }

    public static void clearAllCards(List<BotPlayer> players) {

        final Iterator<BotPlayer> iter = players.iterator();

        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            player.clearCards();
        }
    }

    public static boolean isThereAWinner(List<BotPlayer> players) {

        if (players.size() <= 1)
            return true;

        int noofPlayersWithChipsLeft = 0;
        for (final BotPlayer player : players) {
            if (player.getChipAmount() > 0)
                noofPlayersWithChipsLeft++;

            if (noofPlayersWithChipsLeft > 1)
                return false;
        }

        return true;
    }

    public static List<Action> getPossibleActions(BotPlayer player, Pot pot, long smallBlind, long bigBlind, boolean raiseAllowed) {
        List<Action> actions = new ArrayList<Action>();

        // Fold is always allowed
        actions.add(new Action(ActionType.FOLD, 0));

        // Check is Ok as long as this player is even with the pot
        long amountNeededToCall = pot.getAmountNeededToCall(player);
        if (amountNeededToCall == 0)
            actions.add(new Action(ActionType.CHECK, 0));


        // If player has enough money to CALL
        if (amountNeededToCall > 0 && player.getChipAmount() >= amountNeededToCall) {
            actions.add(new Action(ActionType.CALL, amountNeededToCall));
        }

        // All-in is Ok as long as the player has any money left
        if (player.getChipAmount() > 0) {
            actions.add(new Action(ActionType.ALL_IN, player.getChipAmount()));
        }

        // Raise is done in increments of the bigBlind
        if (raiseAllowed && player.getChipAmount() >= amountNeededToCall + bigBlind) {
            actions.add(new Action(ActionType.RAISE, amountNeededToCall + bigBlind));
        }

        return actions;
    }

    public static boolean isActionValid(List<Action> possibleActions, Action playerAction) {
        if (playerAction == null)
            return false;

        if (playerAction.getActionType() == null)
            return false;

        switch (playerAction.getActionType()) {
            case FOLD:
                return true;

            case CHECK:
                return containsActionType(possibleActions, ActionType.CHECK);

            case CALL:
                if (!containsActionType(possibleActions, ActionType.CALL))
                    return false;

                Action allowedCall = getActionOfType(possibleActions, ActionType.CALL);
                return (playerAction.getAmount() == allowedCall.getAmount());

            case RAISE:
                if (!containsActionType(possibleActions, ActionType.RAISE))
                    return false;

                Action allowedRaise = getActionOfType(possibleActions, ActionType.RAISE);
                return (playerAction.getAmount() == allowedRaise.getAmount());

            case ALL_IN:
                if (!containsActionType(possibleActions, ActionType.ALL_IN))
                    return false;

                Action allowedAllIn = getActionOfType(possibleActions, ActionType.ALL_IN);
                return (playerAction.getAmount() == allowedAllIn.getAmount());
        }

        return false;
    }

    private static boolean containsActionType(final List<Action> possibleActions, ActionType actionType) {
        return getActionOfType(possibleActions, actionType) != null;
    }

    private static Action getActionOfType(final List<Action> possibleActions, ActionType actionType) {

        for (Action possibleAction : possibleActions) {
            if (possibleAction.getActionType() == actionType)
                return possibleAction;
        }

        return null;
    }

    public static GameLog createGameLog(long smallBlind, long bigBlind,
                                           BotPlayer dealerPlayer, BotPlayer bigBlindPlayer, BotPlayer smallBlindPlayer,
                                           List<BotPlayer> players, List<Card> communityCards,
                                           Pot pot, Map<BotPlayer, Long> payoutResult, PokerHandRankUtil rankUtil) {

        GameLog gameLog = new GameLog();
        gameLog.smallBlind = smallBlind;
        gameLog.bigBlind = bigBlind;
        gameLog.communityCards.addAll(communityCards);

        for (BotPlayer player : players) {
            if (pot.getTotalBetAmountForPlayer(player) == 0 && !pot.hasFolded(player))
                continue;

            PlayerInGame pg = new PlayerInGame();

            pg.name = player.getName();

            if (player == dealerPlayer)
                pg.dealer = true;

            if (player == smallBlindPlayer)
                pg.smallBlind = true;

            if (player == bigBlindPlayer)
                pg.bigBlind = true;

            pg.preflopBet = pot.getTotalBetAmountForPlayerInPlayState(player, PlayState.PRE_FLOP);
            pg.flopBet =  pot.getTotalBetAmountForPlayerInPlayState(player, PlayState.FLOP);
            pg.turnBet =  pot.getTotalBetAmountForPlayerInPlayState(player, PlayState.TURN);
            pg.riverBet =  pot.getTotalBetAmountForPlayerInPlayState(player, PlayState.RIVER);

            pg.allIn = pot.isAllIn(player);

            if (pot.isAllIn(player)) {

                switch (pot.getPlayStateForAllIn(player)) {
                    case PRE_FLOP:
                        pg.preflopAllIn = true;
                        break;
                    case  FLOP:
                        pg.flopAllIn = true;
                        break;
                    case TURN:
                        pg.turnAllIn = true;
                        break;
                    case RIVER:
                        pg.riverAllIn = true;
                        break;
                }
            }

            if (pot.hasFolded(player)) {

                switch (pot.getPlayStateForFold(player)) {
                    case PRE_FLOP:
                        pg.preflopFolded = true;
                        break;
                    case  FLOP:
                        pg.flopFolded = true;
                        break;
                    case TURN:
                        pg.turnFolded = true;
                        break;
                    case RIVER:
                        pg.riverFolded = true;
                        break;
                }
            }

            try {
                pg.cards.addAll(player.getCards());

                BestHand bestHand = rankUtil.getBestHand(player);
                pg.pokerHand = bestHand.getPokerHand();
                pg.cardsBestHand.addAll(bestHand.getCards());
            } catch (Exception e) {
            }

            pg.winnings = payoutResult.get(player) == null ? 0 : payoutResult.get(player);
            pg.chipsAfterGame = player.getChipAmount();

            gameLog.players.add(pg);
        }

        return gameLog;
    }

    public static String printTransactions(long smallBlind, long bigBlind,
                                           BotPlayer dealerPlayer, BotPlayer bigBlindPlayer, BotPlayer smallBlindPlayer,
                                           List<BotPlayer> players,
                                           Pot pot, Map<BotPlayer, Long> payoutResult, PokerHandRankUtil rankUtil) {

        final StringBuilder sb = new StringBuilder();
        final Formatter formatter = new Formatter(sb);

        sb.append("\n\n** Transactions during game round **\n\n");
        sb.append("Small blind:        ").append(smallBlind);
        sb.append("\nBig blind:          ").append(bigBlind);
        sb.append("\nDealer:             ").append(dealerPlayer.getName());
        sb.append("\nSmall blind player: ").append(smallBlindPlayer.getName());
        sb.append("\nBig blind player:   ").append(bigBlindPlayer.getName())
                .append("\n");

        for (final PlayState state : PlayState.values()) {
            final List<PotTransaction> transactions = pot
                    .getTransactionsForState(state);

            sb.append("\nState: ").append(state).append("\n");

            if (transactions.size() > 0) {
                sb.append("Tx no  Player        Bet All in\n");

                for (final PotTransaction trans : transactions) {
                    formatter.format("%04d %10s %8d %s \n",
                            trans.getTransactionNumber(),
                            trans.getPlayer().getName(),
                            trans.getAmount(), trans.isAllIn());
                }
            } else {
                sb.append("No transactions\n");
            }
        }

        sb.append("\nGame round result:\n");
        formatter.format("%-10s %8s %-15s %-14s %-13s\n", "Player", "Won",
                "Hand",
                "Cards", "Comment");
        for (final Map.Entry<BotPlayer, Long> entry : payoutResult.entrySet()) {

            final BotPlayer player = entry.getKey();
            final Long amount = entry.getValue();
            final BestHand bestHand = rankUtil.getBestHand(player);
            formatter.format("%-10s %8d %-15s %-14s %-6s %-6s \n",
                    player.getName(), amount,
                    bestHand.getPokerHand().getName(),
                    bestHand.cardsToShortString(),
                    (pot.hasFolded(player) ? "folded" : ""),
                    (pot.isAllIn(player) ? "all in" : ""));
        }

        sb.append("\nPlayer standing now:\n");
        formatter.format("%-10s %8s\n", "Player", "Cash");
        for (final BotPlayer player : players) {
            formatter.format("%-10s %8d\n", player.getName(),
                    player.getChipAmount());
        }

        sb.append("\n** ------------------------------ **\n");
        return sb.toString();
    }
}
