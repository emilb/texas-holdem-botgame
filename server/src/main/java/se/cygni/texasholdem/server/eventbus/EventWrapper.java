package se.cygni.texasholdem.server.eventbus;

import se.cygni.texasholdem.communication.message.event.TexasEvent;
import se.cygni.texasholdem.game.BotPlayer;

import java.util.ArrayList;
import java.util.List;

public class EventWrapper {

    private final List<BotPlayer> receivers = new ArrayList<BotPlayer>();
    private final TexasEvent event;

    public EventWrapper(final TexasEvent event, final List<BotPlayer> receivers) {

        this.event = event;
        this.receivers.addAll(receivers);
    }

    public EventWrapper(final TexasEvent event, final BotPlayer player) {

        this.event = event;
        this.receivers.add(player);
    }

    public List<BotPlayer> getReceivers() {

        return receivers;
    }

    public TexasEvent getEvent() {

        return event;
    }

}
