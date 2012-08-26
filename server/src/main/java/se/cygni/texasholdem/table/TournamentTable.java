package se.cygni.texasholdem.table;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.cygni.texasholdem.dao.model.GameLog;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.game.util.GameUtil;
import se.cygni.texasholdem.server.eventbus.TableDoneEvent;
import se.cygni.texasholdem.server.room.Room;
import se.cygni.texasholdem.server.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TournamentTable extends Table {

    private static Logger log = LoggerFactory
            .getLogger(TournamentTable.class);

    private final int playTillNoofPlayersLeft;

    private List<BotPlayer> bustedPlayers;

    public TournamentTable(GamePlan gamePlan, Room room, EventBus eventBus, SessionManager sessionManager, int playTillNoofPlayersLeft) {
        super(gamePlan, room, eventBus, sessionManager);

        this.playTillNoofPlayersLeft = playTillNoofPlayersLeft;
    }

    @Override
    public void run() {

        log.info("Starting the GAME!");
        gameHasStarted = true;

        smallBlind = gamePlan.getSmallBlindStart();
        bigBlind = gamePlan.getBigBlindStart();
        int roundCounter = 0;

        bustedPlayers = GameUtil.getBustedPlayers(players);

        while (!stopTable &&
                !GameUtil.isThereAWinner(players) &&
                GameUtil.getActivePlayersWithChipsLeft(players).size() > playTillNoofPlayersLeft) {

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

            updateBustedPlayers();

        }

        gameHasStopped = true;
        stopGame();
        log.info("Table is finished, {} players still in play", GameUtil.getActivePlayersWithChipsLeft(players).size());

        // For statistics
        eventBus.post(new TableDoneEvent(this));

        int noofActivePlayersLeft = GameUtil.getActivePlayersWithChipsLeft(players).size();
        if (noofActivePlayersLeft == 1 || noofActivePlayersLeft <= playTillNoofPlayersLeft)
            room.onTableGameDone(this);

    }

    private void updateBustedPlayers() {
        for (BotPlayer player : players) {
            if (!GameUtil.playerHasChips(player)) {
                if (!bustedPlayers.contains(player)) {
                    room.onPlayerBusted(player);
                    bustedPlayers.add(player);
                }
            }
        }
    }
}
