package se.cygni.texasholdem.table;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.communication.message.event.TableIsDoneEvent;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.trainingplayers.TrainingPlayer;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.server.eventbus.EventBusUtil;
import se.cygni.texasholdem.server.eventbus.TableDoneEvent;
import se.cygni.texasholdem.server.room.Room;
import se.cygni.texasholdem.server.session.SessionManager;
import se.cygni.texasholdem.server.statistics.AtomicCounter;
import se.cygni.texasholdem.util.PlayerTypeConverter;

import java.util.*;

public class Table implements Runnable {

    private static Logger log = LoggerFactory
            .getLogger(Table.class);

    public static final String COUNTER_ID = "table";
    public static final int MAX_NOOF_PLAYERS = 11;

    protected final String tableId = UUID.randomUUID().toString();
    protected final long tableCounter;

    protected final List<BotPlayer> players = Collections
            .synchronizedList(new ArrayList<BotPlayer>());

    protected final GamePlan gamePlan;
    protected final EventBus eventBus;
    protected final Room room;
    protected final SessionManager sessionManager;

    protected BotPlayer dealerPlayer = null;
    protected GameRound currentGameRound = null;

    protected long smallBlind;
    protected long bigBlind;

    protected boolean gameHasStarted = false;
    protected boolean stopTable = false;
    protected boolean gameHasStopped = false;

    public Table(final GamePlan gamePlan, final Room room,
                 final EventBus eventBus, final SessionManager sessionManager) {

        this.gamePlan = gamePlan;
        this.room = room;
        this.eventBus = eventBus;
        this.sessionManager = sessionManager;
        this.tableCounter = AtomicCounter.increment(COUNTER_ID);
    }

    @Override
    public void run() {

        log.info("Starting the GAME!");
        gameHasStarted = true;

        smallBlind = gamePlan.getSmallBlindStart();
        bigBlind = gamePlan.getBigBlindStart();
        int roundCounter = 0;

        while (!stopTable &&
                !GameUtil.isThereAWinner(players)
                && atLeastOnePlayerIsReal()) {

            final List<BotPlayer> currentPlayers = new ArrayList<BotPlayer>(players);

            dealerPlayer = GameUtil.getNextPlayerInPlay(currentPlayers,
                    dealerPlayer, null);

            currentGameRound = new GameRound(
                    tableCounter,
                    currentPlayers,
                    dealerPlayer,
                    smallBlind, bigBlind,
                    gamePlan.getMaxNoofTurnsPerState(), gamePlan.getMaxNoofActionRetries(),
                    eventBus, sessionManager);

            currentGameRound.playGameRound();

            GameLog gameLog = currentGameRound.getGameLog();
            gameLog.tableCounter = tableCounter;
            gameLog.roundNumber = roundCounter;
            eventBus.post(gameLog);

            // Is it time to increase blinds?
            roundCounter++;
            updateBlinds(roundCounter);

        }

        gameHasStopped = true;
        stopGame();
        log.info("Game is finished, " + getWinner() + " won!");
        notifyPlayersOfTableIsDone();

        // For statistics
        eventBus.post(new TableDoneEvent(this));
        room.onTableGameDone(this);
    }

    protected boolean atLeastOnePlayerIsReal() {
        for (BotPlayer player : players) {
            if (!(player instanceof TrainingPlayer) &&
                    player.getChipAmount() > 0) {
                return true;
            }
        }

        return false;
    }

    public void stopGame() {
         stopTable = true;
    }

    protected void notifyPlayersOfTableIsDone() {
        TableIsDoneEvent event = new TableIsDoneEvent(PlayerTypeConverter.listOfBotPlayers(players));
        EventBusUtil.postToEventBus(eventBus, event, players);
    }

    protected void updateBlinds(final int currentRound) {

        if (currentRound % gamePlan.getPlaysBetweenBlindRaise() == 0) {

            switch (gamePlan.getBlindRaiseStrategy()) {
                case FIX_AMOUNT:
                    smallBlind += gamePlan.getSmallBlindRaiseStrategyValue();
                    bigBlind += gamePlan.getBigBlindRaiseStrategyValue();
                    break;

                case FACTOR:
                    smallBlind = smallBlind
                            * gamePlan.getSmallBlindRaiseStrategyValue();
                    bigBlind = bigBlind
                            * gamePlan.getBigBlindRaiseStrategyValue();
                    break;
            }

            log.debug("Updated blinds, smallBlind: {}, bigBlind: {}",
                    smallBlind, bigBlind);
        }
    }

    protected BotPlayer getWinner() {

        final Iterator<BotPlayer> iter = players.iterator();
        while (iter.hasNext()) {
            final BotPlayer player = iter.next();
            if (player.getChipAmount() > 0) {
                return player;
            }
        }

        return null;
    }

    public boolean gameHasStarted() {

        return gameHasStarted;
    }

    public boolean gameHasStopped() {
        return gameHasStopped;
    }

    public void addPlayer(final BotPlayer player) {

        players.add(player);
    }

    public void addPlayers(final List<BotPlayer> players) {

        this.players.addAll(players);
    }

    public void removePlayer(final BotPlayer player) {

        log.info("Removing player {}", player);
        players.remove(player);
        if (currentGameRound != null) {
            currentGameRound.removePlayerFromGame(player);
        }
    }

    public List<BotPlayer> getPlayers() {

        return new ArrayList<BotPlayer>(players);
    }

    public long getTableCounter() {
        return tableCounter;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Table other = (Table) obj;
        if (tableId == null) {
            if (other.tableId != null)
                return false;
        } else if (!tableId.equals(other.tableId))
            return false;
        return true;
    }

}
