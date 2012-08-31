package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.ActionRequest;
import se.cygni.texasholdem.game.Action;

public class GodPlayer extends TrainingPlayer {

    public GodPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
    }

    public GodPlayer(String name, String sessionId) {
        super(name, sessionId);
    }

    @Override
    public void serverIsShuttingDown(ServerIsShuttingDownEvent event) {
    }

    @Override
    public void onPlayIsStarted(PlayIsStartedEvent event) {
    }

    @Override
    public void onTableChangedStateEvent(TableChangedStateEvent event) {
    }

    @Override
    public void onYouHaveBeenDealtACard(YouHaveBeenDealtACardEvent event) {
    }

    @Override
    public void onCommunityHasBeenDealtACard(CommunityHasBeenDealtACardEvent event) {
    }

    @Override
    public void onPlayerBetBigBlind(PlayerBetBigBlindEvent event) {
    }

    @Override
    public void onPlayerBetSmallBlind(PlayerBetSmallBlindEvent event) {
    }

    @Override
    public void onPlayerFolded(PlayerFoldedEvent event) {
    }

    @Override
    public void onPlayerCalled(PlayerCalledEvent event) {
    }

    @Override
    public void onPlayerRaised(PlayerRaisedEvent event) {
    }

    @Override
    public void onPlayerWentAllIn(PlayerWentAllInEvent event) {
    }

    @Override
    public void onPlayerChecked(PlayerCheckedEvent event) {
    }

    @Override
    public void onYouWonAmount(YouWonAmountEvent event) {
    }

    @Override
    public void onShowDown(ShowDownEvent event) {
    }

    @Override
    public void onTableIsDone(TableIsDoneEvent event) {
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    }

    @Override
    public Action actionRequired(ActionRequest request) {
        return null;
    }

    @Override
    public void connectionToGameServerLost() {
    }

    @Override
    public void connectionToGameServerEstablished() {
    }
}
