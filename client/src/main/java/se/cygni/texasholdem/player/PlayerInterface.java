package se.cygni.texasholdem.player;

import java.util.List;

import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Player;

public interface PlayerInterface {

    public String getName();

    public void serverIsShuttingDown(String message);

    public void onPlayIsStarted(List<Player> players);

    public void onYouHaveBeenDealtACard(Card card);

    public void onCommunityHasBeenDealtACard(Card card);

    public void onPlayerFolded(Player player);

    public void onPlayerCalled(Player player, long callAmount);

    public void onPlayerRaised(Player player, long callAmount, long betAmount);

    public void onPlayerWentAllIn(Player player, long callAmount, long betAmount);

    public void onPlayerChecked(Player player);

    public void onYouWonAmount(long wonAmount);

    public void onPlayerWonAmount(Player player, long wonAmount);

    public void onPlayerQuit(Player player);

    public Action onActionRequired(List<Action> possibleActions);

    public void connectionToGameServerLost();

    public void connectionToGameServerEstablished();
}
