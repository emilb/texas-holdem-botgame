package se.cygni.texasholdem.player;

import se.cygni.texasholdem.communication.message.event.CommunityHasBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerCalledEvent;
import se.cygni.texasholdem.communication.message.event.PlayerCheckedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerFoldedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerQuitEvent;
import se.cygni.texasholdem.communication.message.event.PlayerRaisedEvent;
import se.cygni.texasholdem.communication.message.event.PlayerWentAllInEvent;
import se.cygni.texasholdem.communication.message.event.ServerIsShuttingDownEvent;
import se.cygni.texasholdem.communication.message.event.ShowDownEvent;
import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.communication.message.event.YouWonAmountEvent;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public interface Player {

    public String getName();

    public void serverIsShuttingDown(ServerIsShuttingDownEvent event);

    public void onPlayIsStarted(PlayIsStartedEvent event);

    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event);

    public void onCommunityHasBeenDealtACard(
            CommunityHasBeenDealtACardEvent event);

    public void onPlayerFolded(PlayerFoldedEvent event);

    public void onPlayerCalled(PlayerCalledEvent event);

    public void onPlayerRaised(PlayerRaisedEvent event);

    public void onPlayerWentAllIn(PlayerWentAllInEvent event);

    public void onPlayerChecked(PlayerCheckedEvent event);

    public void onYouWonAmount(YouWonAmountEvent event);

    public void onShowDown(ShowDownEvent event);

    public void onGameCompleted(GameCompletedEvent event);

    public void onPlayerQuit(PlayerQuitEvent event);

    public Action actionRequired(ActionRequest request);

    public void connectionToGameServerLost();

    public void connectionToGameServerEstablished();
}
