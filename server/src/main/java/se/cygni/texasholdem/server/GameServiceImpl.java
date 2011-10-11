package se.cygni.texasholdem.server;

import org.apache.commons.lang.StringUtils;

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

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class GameServiceImpl implements ClientServer.GameService.Interface {

    private final GameServer gameServer;

    public GameServiceImpl(final GameServer gameServer) {

        this.gameServer = gameServer;
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

    }

    @Override
    public void withdraw(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<Void> done) {

    }

    @Override
    public void getMyChipAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<MyChipAmountResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final MyChipAmountResponse response = MyChipAmountResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getSmallBlindAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<SmallBlindAmountResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final SmallBlindAmountResponse response = SmallBlindAmountResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getBigBlindAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<BigBlindAmountResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final BigBlindAmountResponse response = BigBlindAmountResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getPotAmount(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PotAmountResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final PotAmountResponse response = PotAmountResponse.newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getPlayState(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PlayStateResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final PlayStateResponse response = PlayStateResponse.newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getPlayers(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<PlayersResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final PlayersResponse response = PlayersResponse.newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getDealerPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<DealerPlayerResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final DealerPlayerResponse response = DealerPlayerResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getSmallBlindPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<SmallBlindPlayerResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final SmallBlindPlayerResponse response = SmallBlindPlayerResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getBigBlindPlayer(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<BigBlindPlayerResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final BigBlindPlayerResponse response = BigBlindPlayerResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getCommunityCards(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<CommunityCardsResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final CommunityCardsResponse response = CommunityCardsResponse
                    .newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    @Override
    public void getMyCards(
            final RpcController controller,
            final VoidInSession request,
            final RpcCallback<MyCardsResponse> done) {

        final ExceptionEvent invalidSessionId = checkForValidSession(request);
        if (invalidSessionId != null) {
            final MyCardsResponse response = MyCardsResponse.newBuilder()
                    .setException(invalidSessionId).build();
            done.run(response);
        }

    }

    private ExceptionEvent checkForValidSession(final VoidInSession request) {

        final String sessionId = request.getSessionId();

        if (StringUtils.isEmpty(sessionId))
            return createInvalidSession("null");

        // Ask the GameServer to verify the session
        if (!gameServer.isValidSession(sessionId))
            return createInvalidSession(sessionId);

        // Valid sessionId!
        return null;
    }

    private ExceptionEvent createInvalidSession(final String sessionId) {

        return ExceptionEvent.newBuilder()
                .setExceptionType(PBExceptionType.INVALID_SESSION)
                .setMessage("The sessionId: [" + sessionId + "] is invalid")
                .build();
    }
}
