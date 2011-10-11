package se.cygni.texasholdem.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.CommunityHasBeenDealtACardEvent;
import se.cygni.texasholdem.communication.ClientServer.PBAction;
import se.cygni.texasholdem.communication.ClientServer.PBMessage;
import se.cygni.texasholdem.communication.ClientServer.PBPlayerWinAmount;
import se.cygni.texasholdem.communication.ClientServer.PBPossibleActions;
import se.cygni.texasholdem.communication.ClientServer.Ping;
import se.cygni.texasholdem.communication.ClientServer.PlayIsStartedEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerCalledEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerCheckedEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerFoldedEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerRaisedEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerWentAllInEvent;
import se.cygni.texasholdem.communication.ClientServer.PlayerWithdrewEvent;
import se.cygni.texasholdem.communication.ClientServer.ShowDownEvent;
import se.cygni.texasholdem.communication.ClientServer.Void;
import se.cygni.texasholdem.communication.ClientServer.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.communication.ClientServer.YouWonAmountEvent;
import se.cygni.texasholdem.communication.util.ConversionUtil;
import se.cygni.texasholdem.game.Action;
import se.cygni.texasholdem.player.PlayerInterface;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class PlayerServiceImpl implements
        ClientServer.PlayerService.Interface {

    private static Logger log = LoggerFactory
            .getLogger(PlayerServiceImpl.class);

    private final PlayerInterface player;

    public PlayerServiceImpl(final PlayerInterface player) {

        this.player = player;
    }

    @Override
    public void ping(
            final RpcController controller,
            final Void request,
            final RpcCallback<Ping> done) {

        log.debug("Returning a Ping");
        done.run(Ping.getDefaultInstance());
    }

    @Override
    public void serverIsShuttingDown(
            final RpcController controller,
            final PBMessage request,
            final RpcCallback<Void> done) {

        try {
            player.serverIsShuttingDown(request.getMessage());
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayIsStarted(
            final RpcController controller,
            final PlayIsStartedEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayIsStarted(ConversionUtil
                    .convertPBPlayers(request.getPlayers()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onYouHaveBeenDealtACard(
            final RpcController controller,
            final YouHaveBeenDealtACardEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onYouHaveBeenDealtACard(ConversionUtil.convertPBCard(request
                    .getCard()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onCommunityHasBeenDealtACard(
            final RpcController controller,
            final CommunityHasBeenDealtACardEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onCommunityHasBeenDealtACard(ConversionUtil
                    .convertPBCard(request
                            .getCard()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerFolded(
            final RpcController controller,
            final PlayerFoldedEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerFolded(ConversionUtil.convertPBPlayer(request
                    .getPlayer()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerCalled(
            final RpcController controller,
            final PlayerCalledEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerFolded(ConversionUtil.convertPBPlayer(request
                    .getPlayer()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerRaised(
            final RpcController controller,
            final PlayerRaisedEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerRaised(ConversionUtil.convertPBPlayer(request
                    .getPlayer()), request.getCallBet(), request.getRaiseBet());
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerWentAllIn(
            final RpcController controller,
            final PlayerWentAllInEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerRaised(ConversionUtil.convertPBPlayer(request
                    .getPlayer()), request.getCallBet(), request.getRaiseBet());
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerChecked(
            final RpcController controller,
            final PlayerCheckedEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerChecked(ConversionUtil.convertPBPlayer(request
                    .getPlayer()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onShowDown(
            final RpcController controller,
            final ShowDownEvent request,
            final RpcCallback<Void> done) {

        try {
            for (final PBPlayerWinAmount pw : request.getPlayersWinAmount()
                    .getPlayersWinAmountList()) {
                player.onPlayerWonAmount(
                        ConversionUtil.convertPBPlayer(pw.getPlayer()),
                        pw.getWinAmount());
            }
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onYouWonAmount(
            final RpcController controller,
            final YouWonAmountEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onYouWonAmount(request.getAmount());
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onPlayerWithdrew(
            final RpcController controller,
            final PlayerWithdrewEvent request,
            final RpcCallback<Void> done) {

        try {
            player.onPlayerWithdrew(ConversionUtil.convertPBPlayer(request
                    .getPlayer()));
        } finally {
            returnVoid(done);
        }
    }

    @Override
    public void onYouMustAct(
            final RpcController controller,
            final PBPossibleActions request,
            final RpcCallback<PBAction> done) {

        try {
            final List<Action> possibleActions = new ArrayList<Action>();

            for (final PBAction pbAction : request.getActionsList()) {
                possibleActions.add(ConversionUtil.convertPBAction(pbAction));
            }

            final Action action = player.onActionRequired(possibleActions);

            log.debug("Returning action {} amount {}", action.getActionType(),
                    action.getAmount());

            done.run(ConversionUtil.convertAction(action));

        } catch (final Exception e) {
            log.error("Failed to reply with an action", e);
            controller.setFailed(e.getMessage());
        }
    }

    private void returnVoid(final RpcCallback<Void> done) {

        log.debug("Returning void");
        done.run(Void.getDefaultInstance());
    }
}
