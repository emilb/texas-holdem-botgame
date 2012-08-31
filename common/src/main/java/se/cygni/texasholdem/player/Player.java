package se.cygni.texasholdem.player;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public interface Player {

    public String getName();

    public void serverIsShuttingDown(ServerIsShuttingDownEvent event);

    public void onPlayIsStarted(PlayIsStartedEvent event);

    public void onTableChangedStateEvent(TableChangedStateEvent event);

    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event);

    public void onCommunityHasBeenDealtACard(
            CommunityHasBeenDealtACardEvent event);

    public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event);

    public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event);

    public void onPlayerFolded(PlayerFoldedEvent event);

    public void onPlayerCalled(PlayerCalledEvent event);

    public void onPlayerRaised(PlayerRaisedEvent event);

    public void onPlayerWentAllIn(PlayerWentAllInEvent event);

    public void onPlayerChecked(PlayerCheckedEvent event);

    public void onYouWonAmount(YouWonAmountEvent event);

    public void onShowDown(ShowDownEvent event);

    public void onTableIsDone(TableIsDoneEvent event);

    public void onPlayerQuit(PlayerQuitEvent event);

    public Action actionRequired(ActionRequest request);

    public void connectionToGameServerLost();

    public void connectionToGameServerEstablished();
}
