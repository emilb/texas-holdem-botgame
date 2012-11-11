package se.cygni.texasholdem.server.eventbus;

import com.google.common.eventbus.EventBus;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.event.PlayerQuitEvent;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import java.util.ArrayList;
import java.util.List;

public class EventBusUtil {

    private EventBusUtil() {
    }

    public static void postToEventBus(
            final EventBus eventBus,
            final TexasEvent event,
            final BotPlayer... players) {

        final List<BotPlayer> recipients = new ArrayList<BotPlayer>();

        for (final BotPlayer player : players) {
            recipients.add(player);
        }

        postToEventBus(eventBus, event, recipients);
    }

    public static void postToEventBus(
            final EventBus eventBus,
            final TexasEvent event,
            final List<BotPlayer> recipients) {

        eventBus.post(new EventWrapper(event, recipients));
    }

    public static void postPlayIsStarted(
            final EventBus eventBus,
            final long smallBlind,
            final long bigBlind,
            final BotPlayer dealerPlayer,
            final BotPlayer smallBlindPlayer,
            final BotPlayer bigBlindPlayer,
            final long tableId,
            final List<BotPlayer> players,
            final List<BotPlayer> recipients) {

        postToEventBus(eventBus,
                new PlayIsStartedEvent(PlayerTypeConverter.listOfBotPlayers(players),
                        smallBlind, bigBlind,
                        PlayerTypeConverter.fromBotPlayer(dealerPlayer),
                        PlayerTypeConverter.fromBotPlayer(smallBlindPlayer),
                        PlayerTypeConverter.fromBotPlayer(bigBlindPlayer),
                        tableId),
                recipients);
    }

    public static void postCommunityHasBeenDealtACard(
            final EventBus eventBus,
            final Card card,
            final List<BotPlayer> recipients) {

        postToEventBus(eventBus, new CommunityHasBeenDealtACardEvent(card), recipients);
    }

    public static void postPlayerBetBigBlind(
            final EventBus eventBus,
            final BotPlayer player,
            final long bigBlind,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerBetBigBlindEvent(PlayerTypeConverter.fromBotPlayer(player), bigBlind),
                recipients);
    }

    public static void postPlayerBetSmallBlind(
            final EventBus eventBus,
            final BotPlayer player,
            final long smallBlind,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerBetSmallBlindEvent(PlayerTypeConverter.fromBotPlayer(player), smallBlind),
                recipients);
    }

    public static void postPlayerCalled(
            final EventBus eventBus,
            final BotPlayer player,
            final long callBet,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerCalledEvent(PlayerTypeConverter.fromBotPlayer(player), callBet),
                recipients);
    }

    public static void postPlayerChecked(
            final EventBus eventBus,
            final BotPlayer player,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerCheckedEvent(PlayerTypeConverter.fromBotPlayer(player)),
                recipients);
    }

    public static void postPlayerFolded(
            final EventBus eventBus,
            final BotPlayer player,
            final long investmentInPot,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerFoldedEvent(PlayerTypeConverter.fromBotPlayer(player), investmentInPot),
                recipients);
    }

    public static void postPlayerForcedFolded(
            final EventBus eventBus,
            final BotPlayer player,
            final long investmentInPot) {

        postToEventBus(
                eventBus,
                new PlayerForcedFoldedEvent(PlayerTypeConverter.fromBotPlayer(player), investmentInPot),
                player);
    }

    public static void postPlayerQuit(
            final EventBus eventBus,
            final BotPlayer player,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerQuitEvent(PlayerTypeConverter.fromBotPlayer(player)),
                recipients);
    }

    public static void postPlayerRaised(
            final EventBus eventBus,
            final BotPlayer player,
            final long raiseBet,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerRaisedEvent(PlayerTypeConverter.fromBotPlayer(player), raiseBet),
                recipients);
    }

    public static void postPlayerWentAllIn(
            final EventBus eventBus,
            final BotPlayer player,
            final long allInAmount,
            final List<BotPlayer> recipients) {

        postToEventBus(
                eventBus,
                new PlayerWentAllInEvent(PlayerTypeConverter.fromBotPlayer(player), allInAmount),
                recipients);
    }
}
