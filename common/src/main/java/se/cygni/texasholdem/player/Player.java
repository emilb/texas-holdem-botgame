package se.cygni.texasholdem.player;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public interface Player {

    String getName();

    void serverIsShuttingDown(ServerIsShuttingDownEvent event);

    void onPlayIsStarted(PlayIsStartedEvent event);

    void onTableChangedStateEvent(TableChangedStateEvent event);

    void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event);

    void onCommunityHasBeenDealtACard(
            CommunityHasBeenDealtACardEvent event);

    void onPlayerBetBigBlind(PlayerBetBigBlindEvent event);

    void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event);

    void onPlayerFolded(PlayerFoldedEvent event);

    void onPlayerCalled(PlayerCalledEvent event);

    void onPlayerRaised(PlayerRaisedEvent event);

    void onPlayerWentAllIn(PlayerWentAllInEvent event);

    void onPlayerChecked(PlayerCheckedEvent event);

    void onYouWonAmount(YouWonAmountEvent event);

    void onShowDown(ShowDownEvent event);

    void onTableIsDone(TableIsDoneEvent event);

    void onPlayerQuit(PlayerQuitEvent event);

    Action actionRequired(ActionRequest request);

    void connectionToGameServerLost();

    void connectionToGameServerEstablished();
}
