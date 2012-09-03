package se.cygni.texasholdem.communication.message;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import se.cygni.texasholdem.communication.message.event.*;
import se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest;
import se.cygni.texasholdem.game.GamePlayer;
import se.cygni.texasholdem.game.Room;
import se.cygni.texasholdem.test.util.DomainUtil;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TexasEventParserTest extends AbstractEventParserTestUtil {

    @Test
    public void testRegisterForPlayRequest()
            throws JsonGenerationException, JsonMappingException, IOException {

        final RegisterForPlayRequest request = new RegisterForPlayRequest();
        request.room = Room.TRAINING;
        request.name = "Tester";

        final RegisterForPlayRequest decodedMsg = assertEncodeDecode(request);

        assertEquals(request.name, decodedMsg.name);
        assertEquals(request.room, decodedMsg.room);
    }

    @Test
    public void testCommunityHasBeenDealtACardEvent()
            throws JsonGenerationException, JsonMappingException, IOException {

        final CommunityHasBeenDealtACardEvent event = new CommunityHasBeenDealtACardEvent(
                DomainUtil.card());

        final CommunityHasBeenDealtACardEvent decodedMsg = assertEncodeDecode(event);

        assertEquals(event.getCard(), decodedMsg.getCard());
    }

    @Test
    public void testPlayerCalledEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerCalledEvent event = new PlayerCalledEvent(
                DomainUtil.player(), DomainUtil.randomLong());

        final PlayerCalledEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
        assertEquals(event.getCallBet(), decodedMsg.getCallBet());
    }

    @Test
    public void testPlayerCheckedEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerCheckedEvent event = new PlayerCheckedEvent(
                DomainUtil.player());

        final PlayerCheckedEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
    }

    @Test
    public void testPlayerFoldedEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerFoldedEvent event = new PlayerFoldedEvent(
                DomainUtil.player(), DomainUtil.randomLong());

        final PlayerFoldedEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
        assertEquals(event.getInvestmentInPot(),
                decodedMsg.getInvestmentInPot());
    }

    @Test
    public void testPlayerQuitEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerQuitEvent event = new PlayerQuitEvent(
                DomainUtil.player());

        final PlayerQuitEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
    }

    @Test
    public void testPlayerRaisedEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerRaisedEvent event = new PlayerRaisedEvent(
                DomainUtil.player(),
                DomainUtil.randomLong());

        final PlayerRaisedEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
        assertEquals(event.getRaiseBet(),
                decodedMsg.getRaiseBet());
    }

    @Test
    public void testPlayerWentAllInEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final PlayerWentAllInEvent event = new PlayerWentAllInEvent(
                DomainUtil.player(), DomainUtil.randomLong());

        final PlayerWentAllInEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getPlayer(), decodedMsg.getPlayer());
        assertEquals(event.getAllInAmount(),
                decodedMsg.getAllInAmount());
    }

    @Test
    public void testPlayIsStartedEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final List<GamePlayer> players = DomainUtil.players(5);

        final PlayIsStartedEvent event = new PlayIsStartedEvent(
                players, DomainUtil.randomLong(), DomainUtil.randomLong(),
                players.get(0), players.get(1), players.get(2), 358);

        final PlayIsStartedEvent decodedMsg = assertEncodeDecode(event);

        assertEqualss(event.getDealer(), decodedMsg.getDealer());
        assertEqualss(event.getSmallBlindPlayer(),
                decodedMsg.getSmallBlindPlayer());
        assertEqualss(event.getBigBlindPlayer(), decodedMsg.getBigBlindPlayer());

        assertEquals(event.getSmallBlindAmount(),
                decodedMsg.getSmallBlindAmount());

        assertEquals(event.getBigBlindAmount(),
                decodedMsg.getBigBlindAmount());

        assertEquals(event.getTableId(),
                decodedMsg.getTableId());

        for (int i = 0; i < players.size(); i++) {
            assertEqualss(event.getPlayers().get(i), decodedMsg.getPlayers()
                    .get(i));
        }
    }

    @Test
    public void testServerIsShuttingDownEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final ServerIsShuttingDownEvent event = new ServerIsShuttingDownEvent(
                DomainUtil.randomString(25));

        final ServerIsShuttingDownEvent decodedMsg = assertEncodeDecode(event);

        assertEquals(event.getMessage(),
                decodedMsg.getMessage());
    }

    @Test
    public void testShowDownEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final ShowDownEvent event = new ShowDownEvent(
                DomainUtil.playerShowdowns(5));

        final ShowDownEvent decodedMsg = assertEncodeDecode(event);

        for (int i = 0; i < event.getPlayersShowDown().size(); i++) {
            assertEqualss(event.getPlayersShowDown().get(i),
                    decodedMsg.getPlayersShowDown().get(i));
        }
    }

    @Test
    public void testYouHaveBeenDealtACardEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final YouHaveBeenDealtACardEvent event = new YouHaveBeenDealtACardEvent(
                DomainUtil.card());

        final YouHaveBeenDealtACardEvent decodedMsg = assertEncodeDecode(event);

        assertEquals(event.getCard(),
                decodedMsg.getCard());
    }

    @Test
    public void testYouWonAmountEvent() throws JsonParseException,
            JsonMappingException,
            IOException {

        final YouWonAmountEvent event = new YouWonAmountEvent(
                DomainUtil.randomLong(), DomainUtil.randomLong());

        final YouWonAmountEvent decodedMsg = assertEncodeDecode(event);

        assertEquals(event.getWonAmount(),
                decodedMsg.getWonAmount());

        assertEquals(event.getYourChipAmount(),
                decodedMsg.getYourChipAmount());
    }

}
