package se.cygni.texasholdem.game.trainingplayers;

import se.cygni.texasholdem.client.ClientEventDispatcher;
import se.cygni.texasholdem.client.CurrentPlayState;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.player.Player;

public abstract class TrainingPlayer extends BotPlayer implements Player {

    ClientEventDispatcher eventDispatcher = new ClientEventDispatcher(this);
    ClientEventDispatcher currentPlayStateDispatcher;
    CurrentPlayState currentPlayState;

    public TrainingPlayer(String name, String sessionId, long chipAmount) {
        super(name, sessionId, chipAmount);
        currentPlayState = new CurrentPlayState(name);
        currentPlayStateDispatcher = new ClientEventDispatcher(currentPlayState.getPlayerImpl());
    }

    public TrainingPlayer(String name, String sessionId) {
        this(name, sessionId, 0);
    }

    public CurrentPlayState getCurrentPlayState() {
        return currentPlayState;
    }

    public void dispatchEvent(TexasEvent event) {
        currentPlayStateDispatcher.onEvent(event);
        eventDispatcher.onEvent(event);
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
    public void connectionToGameServerLost() {
    }

    @Override
    public void connectionToGameServerEstablished() {
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
    }
}
