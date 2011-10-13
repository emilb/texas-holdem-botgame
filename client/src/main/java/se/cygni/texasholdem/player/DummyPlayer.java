package se.cygni.texasholdem.player;

import java.util.List;

import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Player;

public class DummyPlayer implements PlayerInterface {

    @Override
    public String getName() {

        System.out.println("Event: getName");
        return "dummmy";
    }

    @Override
    public void serverIsShuttingDown(final String message) {

        System.out.println("Event: serverIsShuttingDown");

    }

    @Override
    public void onPlayIsStarted(final List<Player> players) {

        System.out.println("Event: onPlayIsStarted");

    }

    @Override
    public void onYouHaveBeenDealtACard(final Card card) {

        System.out.println("Event: onYouHaveBeenDealtACard");

    }

    @Override
    public void onCommunityHasBeenDealtACard(final Card card) {

        System.out.println("Event: onCommunityHasBeenDealtACard");

    }

    @Override
    public void onPlayerFolded(final Player player) {

        System.out.println("Event: onPlayerFolded");

    }

    @Override
    public void onPlayerCalled(final Player player, final long callAmount) {

        System.out.println("onPlayerCalled");

    }

    @Override
    public void onPlayerRaised(
            final Player player,
            final long callAmount,
            final long betAmount) {

        System.out.println("Event: onPlayerRaised");

    }

    @Override
    public void onPlayerWentAllIn(
            final Player player,
            final long callAmount,
            final long betAmount) {

        System.out.println("Event: onPlayerWentAllIn");

    }

    @Override
    public void onPlayerChecked(final Player player) {

        System.out.println("Event: onPlayerChecked");

    }

    @Override
    public void onYouWonAmount(final long wonAmount) {

        System.out.println("Event: onYouWonAmount");

    }

    @Override
    public void onPlayerWonAmount(final Player player, final long wonAmount) {

        System.out.println("Event: onPlayerWonAmount");

    }

    @Override
    public void onPlayerWithdrew(final Player player) {

        System.out.println("Event: onPlayerWithdrew");

    }

    @Override
    public Action onActionRequired(final List<Action> possibleActions) {

        System.out.println("Event: onActionRequired");
        return null;
    }

    @Override
    public void connectionToGameServerLost() {

        System.out.println("Event: connectionToGameServerLost");

    }

    @Override
    public void connectionToGameServerEstablished() {

        System.out.println("Event: connectionToGameServerEstablished");

    }

}
