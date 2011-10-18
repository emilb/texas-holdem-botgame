package se.cygni.texasholdem.server;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.BigBlindAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.BigBlindPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.CommunityCardsResponse;
import se.cygni.texasholdem.communication.ClientServer.DealerPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.ExceptionEvent;
import se.cygni.texasholdem.communication.ClientServer.MyCardsResponse;
import se.cygni.texasholdem.communication.ClientServer.MyChipAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.PBExceptionType;
import se.cygni.texasholdem.communication.ClientServer.Ping;
import se.cygni.texasholdem.communication.ClientServer.PlayStateResponse;
import se.cygni.texasholdem.communication.ClientServer.PlayersResponse;
import se.cygni.texasholdem.communication.ClientServer.PotAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.ClientServer.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.ClientServer.SmallBlindAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.SmallBlindPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.Void;
import se.cygni.texasholdem.communication.ClientServer.VoidInSession;
import se.cygni.texasholdem.game.BotPlayer;
import se.cygni.texasholdem.table.Table;
import se.cygni.texasholdem.table.TableManager;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

@Component
public class GameServiceImpl implements ClientServer.GameService.Interface {

    private final GameServer gameServer;
    private final TableManager tableManager;

    @Autowired
    public GameServiceImpl(final GameServer gameServer,
            final TableManager tableManager) {

        this.gameServer = gameServer;
        this.tableManager = tableManager;
    }

    @Override
    public void ping(
            final RpcController controller,
            final Void request,
            final RpcCallback<Ping> done) {

        done.run(Ping.getDefaultInstance());
    }

    @Override
    public void registerForPlay(
            final RpcController controller,
            final RegisterForPlayRequest request,
            final RpcCallback<RegisterForPlayResponse> done) {

        gameServer.registerNewPlayer(request.getPlayerName(),
                request.getSessionId());

        final RegisterForPlayResponse response = RegisterForPlayResponse
                .newBuilder().setSessionId(request.getSessionId()).build();

        done.run(response);
    }

    @Override
    public void quit(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<Void> done) {

    }

    @Override
    public void getMyChipAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<MyChipAmountResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                MyChipAmountResponse.class, controller, done))
            return;

        final BotPlayer player = gameServer.getPlayer(request.getSessionId());
        final MyChipAmountResponse resp = MyChipAmountResponse.newBuilder()
                .setAmount(player.getChipAmount()).build();

        done.run(resp);
    }

    @Override
    public void getSmallBlindAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<SmallBlindAmountResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                SmallBlindAmountResponse.class, controller,
                done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                SmallBlindAmountResponse.class, controller, done);
        if (table == null)
            return;

        final SmallBlindAmountResponse resp = SmallBlindAmountResponse
                .newBuilder().setAmount(table.getSmallBlind()).build();

        done.run(resp);
    }

    @Override
    public void getBigBlindAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<BigBlindAmountResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                BigBlindAmountResponse.class, controller, done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                BigBlindAmountResponse.class, controller, done);
        if (table == null)
            return;

        final BigBlindAmountResponse resp = BigBlindAmountResponse.newBuilder()
                .setAmount(table.getBigBlind()).build();

        done.run(resp);
    }

    @Override
    public void getPotAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PotAmountResponse> done) {

        if (returnExceptionIfInvalidSession(request, PotAmountResponse.class,
                controller,
                done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                PotAmountResponse.class, controller, done);
        if (table == null)
            return;

        final PotAmountResponse resp = PotAmountResponse.newBuilder()
                .setAmount(table.getPotAmount()).build();

        done.run(resp);
    }

    @Override
    public void getPlayState(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PlayStateResponse> done) {

        if (returnExceptionIfInvalidSession(request, PlayStateResponse.class,
                controller,
                done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                PlayStateResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add playstate converter
    }

    @Override
    public void getPlayers(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PlayersResponse> done) {

        if (returnExceptionIfInvalidSession(request, PlayersResponse.class,
                controller,
                done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                PlayersResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add BotPlayer converter
    }

    @Override
    public void getDealerPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<DealerPlayerResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                DealerPlayerResponse.class, controller, done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                DealerPlayerResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add BotPlayer converter
    }

    @Override
    public void getSmallBlindPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<SmallBlindPlayerResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                SmallBlindPlayerResponse.class, controller,
                done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                SmallBlindPlayerResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add BotPlayer converter
    }

    @Override
    public void getBigBlindPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<BigBlindPlayerResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                BigBlindPlayerResponse.class, controller, done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                BigBlindPlayerResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add BotPlayer converter
    }

    @Override
    public void getCommunityCards(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<CommunityCardsResponse> done) {

        if (returnExceptionIfInvalidSession(request,
                CommunityCardsResponse.class, controller, done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                CommunityCardsResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add List<Card> converter
    }

    @Override
    public void getMyCards(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<MyCardsResponse> done) {

        if (returnExceptionIfInvalidSession(request, MyCardsResponse.class,
                controller, done))
            return;

        final Table table = returnExceptionIfPlayerNotInTable(request,
                MyCardsResponse.class, controller, done);
        if (table == null)
            return;

        // TODO: add List<Card> converter
    }

    private ExceptionEvent checkForValidSession(final VoidInSession request) {

        final String sessionId = request.getSessionId();

        if (StringUtils.isEmpty(sessionId))
            return createInvalidSessionException("null");

        // Ask the GameServer to verify the session
        if (!gameServer.isValidSession(sessionId))
            return createInvalidSessionException(sessionId);

        // Valid sessionId!
        return null;
    }

    private ExceptionEvent createInvalidSessionException(final String sessionId) {

        return ExceptionEvent.newBuilder()
                .setExceptionType(PBExceptionType.INVALID_SESSION)
                .setMessage("The sessionId: [" + sessionId + "] is invalid")
                .build();
    }

    private <T> boolean returnExceptionIfInvalidSession(
            final VoidInSession request,
            final Class<T> msgClazz,
            final RpcController controller,
            final RpcCallback<T> done) {

        final ExceptionEvent invalidSessionEvent = checkForValidSession(request);
        if (invalidSessionEvent == null)
            return false;

        buildAndReturnException(msgClazz, invalidSessionEvent, controller, done);

        return true;
    }

    private ExceptionEvent checkThatPlayerIsInTable(final VoidInSession request) {

        final String sessionId = request.getSessionId();

        if (tableManager.getTableForSessionId(sessionId) == null) {
            return ExceptionEvent
                    .newBuilder()
                    .setExceptionType(
                            PBExceptionType.PLAYER_NOT_ASSIGNED_TO_TABLE_YET)
                    .setMessage("You are not assigned to a table yet")
                    .build();
        }

        return null;
    }

    private <T> Table returnExceptionIfPlayerNotInTable(
            final VoidInSession request,
            final Class<T> msgClazz,
            final RpcController controller,
            final RpcCallback<T> done) {

        final ExceptionEvent invalidTable = checkThatPlayerIsInTable(request);
        if (invalidTable == null)
            return tableManager.getTableForSessionId(request.getSessionId());

        buildAndReturnException(msgClazz, invalidTable, controller, done);

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> void buildAndReturnException(
            final Class<T> msgClazz,
            final ExceptionEvent ex,
            final RpcController controller,
            final RpcCallback<T> done) {

        try {
            final Method newBuilderMethod = msgClazz.getMethod("newBuilder");
            Object builder = newBuilderMethod.invoke(null);

            final Method setException = builder.getClass().getDeclaredMethod(
                    "setException",
                    ExceptionEvent.class);

            builder = setException.invoke(builder, ex);

            final Method build = builder.getClass()
                    .getDeclaredMethod("build");
            final T message = (T) build.invoke(builder);
            done.run(message);

        } catch (final Exception e) {
            // TODO: Log error

            // Cancel the reply
            controller.setFailed(e.getMessage());
        }
    }
}
