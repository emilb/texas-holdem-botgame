package se.cygni.texasholdem.game;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cygni.texasholdem.client.PlayerServiceImpl;
import se.cygni.texasholdem.communication.ClientServer;
import se.cygni.texasholdem.communication.ClientServer.BigBlindAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.BigBlindPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.CommunityCardsResponse;
import se.cygni.texasholdem.communication.ClientServer.DealerPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.GameService.BlockingInterface;
import se.cygni.texasholdem.communication.ClientServer.MyCardsResponse;
import se.cygni.texasholdem.communication.ClientServer.MyChipAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.PlayStateResponse;
import se.cygni.texasholdem.communication.ClientServer.PlayersResponse;
import se.cygni.texasholdem.communication.ClientServer.PotAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.RegisterForPlayRequest;
import se.cygni.texasholdem.communication.ClientServer.RegisterForPlayResponse;
import se.cygni.texasholdem.communication.ClientServer.SmallBlindAmountResponse;
import se.cygni.texasholdem.communication.ClientServer.SmallBlindPlayerResponse;
import se.cygni.texasholdem.communication.ClientServer.VoidInSession;
import se.cygni.texasholdem.communication.util.ConversionUtil;
import se.cygni.texasholdem.game.definitions.PlayState;
import se.cygni.texasholdem.game.exception.CommunicationException;
import se.cygni.texasholdem.game.exception.GameException;
import se.cygni.texasholdem.player.PlayerInterface;

import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;

public class GameService {

    private static Logger log = LoggerFactory.getLogger(GameService.class);

    private final PlayerInterface player;
    private final int clientPort = 1147;
    private final String clientHostname = UUID.randomUUID().toString();

    private final PlayerServiceImpl playerService;
    private DuplexTcpClientBootstrap bootstrap;
    private final PeerInfo clientPeerInfo;
    private final PeerInfo serverPeerInfo;

    private RpcClientChannel serverChannel;
    private BlockingInterface gameService;

    private String sessionId;

    public GameService(final PlayerInterface player, final int serverPort,
            final String serverHostname) {

        this.player = player;

        clientPeerInfo = new PeerInfo(clientHostname, clientPort);
        serverPeerInfo = new PeerInfo(serverHostname, serverPort);

        playerService = new PlayerServiceImpl(player);
    }

    public long getMyChipAmount() throws GameException {

        log.debug("calling server for getMyChipAmount");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final MyChipAmountResponse response = gameService.getMyChipAmount(
                    controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            log.debug("myChipAmount: {}", response.getAmount());

            return response.getAmount();

        } catch (final ServiceException e) {
            log.error("Failed to call getMyChipAmount()", e);
            throw new CommunicationException(e);
        }
    }

    public long getSmallBlindAmount() throws GameException {

        log.debug("calling server for getSmallBlindAmount");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final SmallBlindAmountResponse response = gameService
                    .getSmallBlindAmount(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            log.debug("smallBlindAmount: {}", response.getAmount());

            return response.getAmount();

        } catch (final ServiceException e) {
            log.error("Failed to call getSmallBlindAmount()", e);
            throw new CommunicationException(e);
        }
    }

    public long getBigBlindAmount() throws GameException {

        log.debug("calling server for getBigBlindAmount");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final BigBlindAmountResponse response = gameService
                    .getBigBlindAmount(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            log.debug("bigBlindAmount: {}", response.getAmount());

            return response.getAmount();

        } catch (final ServiceException e) {
            log.error("Failed to call getBigBlindAmount()", e);
            throw new CommunicationException(e);
        }
    }

    public long getPotAmount() throws GameException {

        log.debug("calling server for getPotAmount");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final PotAmountResponse response = gameService
                    .getPotAmount(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            log.debug("potAmount: {}", response.getAmount());

            return response.getAmount();

        } catch (final ServiceException e) {
            log.error("Failed to call getPotAmount()", e);
            throw new CommunicationException(e);
        }
    }

    public PlayState getPlayState() throws GameException {

        log.debug("calling server for getPlayState");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final PlayStateResponse response = gameService
                    .getPlayState(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final PlayState playSate = PlayState.values()[response.getState()
                    .getNumber()];
            log.debug("playState: {}", playSate);

            return playSate;

        } catch (final ServiceException e) {
            log.error("Failed to call getPlayState()", e);
            throw new CommunicationException(e);
        }
    }

    public List<Player> getPlayers() throws GameException {

        log.debug("calling server for getPlayers");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final PlayersResponse response = gameService
                    .getPlayers(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final List<Player> players = ConversionUtil
                    .convertPBPlayers(response.getPlayers());

            log.debug("playState: {}", players);

            return players;

        } catch (final ServiceException e) {
            log.error("Failed to call getPlayers()", e);
            throw new CommunicationException(e);
        }
    }

    public Player getDealerPlayer() throws GameException {

        log.debug("calling server for getDealerPlayer");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final DealerPlayerResponse response = gameService
                    .getDealerPlayer(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final Player player = ConversionUtil.convertPBPlayer(response
                    .getPlayer());

            log.debug("dealerPlayer: {}", player);

            return player;

        } catch (final ServiceException e) {
            log.error("Failed to call getDealerPlayer()", e);
            throw new CommunicationException(e);
        }
    }

    public Player getSmallBlindPlayer() throws GameException {

        log.debug("calling server for getSmallBlindPlayer");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final SmallBlindPlayerResponse response = gameService
                    .getSmallBlindPlayer(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final Player player = ConversionUtil.convertPBPlayer(response
                    .getPlayer());

            log.debug("smallBlindPlayer: {}", player);

            return player;

        } catch (final ServiceException e) {
            log.error("Failed to call getSmallBlindPlayer()", e);
            throw new CommunicationException(e);
        }
    }

    public Player getBigBlindPlayer() throws GameException {

        log.debug("calling server for getSmallBlindPlayer");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final BigBlindPlayerResponse response = gameService
                    .getBigBlindPlayer(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final Player player = ConversionUtil.convertPBPlayer(response
                    .getPlayer());

            log.debug("bigBlindPlayer: {}", player);

            return player;

        } catch (final ServiceException e) {
            log.error("Failed to call getBigBlindPlayer()", e);
            throw new CommunicationException(e);
        }
    }

    public List<Card> getCommunityCards() throws GameException {

        log.debug("calling server for getCommunityCards");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final CommunityCardsResponse response = gameService
                    .getCommunityCards(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final List<Card> cards = ConversionUtil
                    .convertPBCards(response.getCardsList());

            log.debug("communityCards: {}", cards);

            return cards;

        } catch (final ServiceException e) {
            log.error("Failed to call getCommunityCards()", e);
            throw new CommunicationException(e);
        }
    }

    public List<Card> getMyCards() throws GameException {

        log.debug("calling server for getMyCards");
        try {
            final RpcController controller = serverChannel.newRpcController();
            final MyCardsResponse response = gameService
                    .getMyCards(
                            controller, getVoidInSession());

            ConversionUtil.checkForException(response.getException());

            final List<Card> cards = ConversionUtil
                    .convertPBCards(response.getCardsList());

            log.debug("MyCards: {}", cards);

            return cards;

        } catch (final ServiceException e) {
            log.error("Failed to call getMyCards()", e);
            throw new CommunicationException(e);
        }
    }

    public void registerForPlay() throws GameException {

        log.debug("calling server for registerForPlay");
        try {
            final RpcController controller = serverChannel.newRpcController();

            final RegisterForPlayRequest request =
                    RegisterForPlayRequest.newBuilder()
                            .setPlayerName(player.getName())
                            .setPreviousSessionId(getSessionId()).build();

            final RegisterForPlayResponse response = gameService
                    .registerForPlay(
                            controller,
                            request);

            ConversionUtil.checkForException(response.getException());
            setSessionId(response.getSessionId());

            log.debug("My sessionId: {}", getSessionId());

        } catch (final ServiceException e) {
            log.error("Failed to call getMyCards()", e);
            throw new CommunicationException(e);
        }
    }

    private VoidInSession getVoidInSession() {

        return VoidInSession.newBuilder().setSessionId(getSessionId()).build();
    }

    public void connect() throws IOException {

        final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(3, 10);

        bootstrap = new DuplexTcpClientBootstrap(
                clientPeerInfo,
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()),
                executor);

        // Configure the client
        final Service clientService = ClientServer.PlayerService
                .newReflectiveService(playerService);
        bootstrap.getRpcServiceRegistry().registerService(clientService);

        // Set up the event pipeline factory.
        bootstrap.setOption("connectTimeoutMillis", 10000);
        bootstrap.setOption("connectResponseTimeoutMillis", 10000);
        bootstrap.setOption("sendBufferSize", 1048576);
        bootstrap.setOption("receiveBufferSize", 1048576);
        bootstrap.setOption("tcpNoDelay", false);

        final CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(bootstrap);

        // setup a RPC event listener - it just logs what happens
        final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(
                    final RpcClientChannel clientChannel) {

                log.info("connectionReestablished " + clientChannel);
                player.connectionToGameServerEstablished();
            }

            @Override
            public void connectionOpened(final RpcClientChannel clientChannel) {

                log.info("connectionOpened " + clientChannel);
                player.connectionToGameServerEstablished();
            }

            @Override
            public void connectionLost(final RpcClientChannel clientChannel) {

                log.info("connectionLost " + clientChannel);
                player.connectionToGameServerLost();
            }

            @Override
            public void connectionChanged(final RpcClientChannel clientChannel) {

                log.info("connectionChanged " + clientChannel);
            }
        };
        rpcEventNotifier.setEventListener(listener);
        bootstrap.registerConnectionEventListener(rpcEventNotifier);

        // Connect to server
        serverChannel = bootstrap.peerWith(serverPeerInfo);
        gameService = se.cygni.texasholdem.communication.ClientServer.GameService
                .newBlockingStub(serverChannel);
    }

    public String getSessionId() {

        return sessionId;
    }

    public void setSessionId(final String sessionId) {

        this.sessionId = sessionId;
    }

}
