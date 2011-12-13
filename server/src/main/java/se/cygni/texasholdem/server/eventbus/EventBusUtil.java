package se.cygni.texasholdem.server.eventbus;

import java.util.ArrayList;
import java.util.List;

import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.game.BotPlayer;

import com.google.common.eventbus.EventBus;

public class EventBusUtil {

    public static void postToEventBus(
            final EventBus eventBus,
            final TexasEvent event,
            final BotPlayer... players) {

        final List<BotPlayer> recipients = new ArrayList<BotPlayer>();
        for (final BotPlayer player : players)
            recipients.add(player);

        postToEventBus(eventBus, event, recipients);
    }

    public static void postToEventBus(
            final EventBus eventBus,
            final TexasEvent event,
            final List<BotPlayer> recipients) {

        eventBus.post(new EventWrapper(event, recipients));
    }
}
