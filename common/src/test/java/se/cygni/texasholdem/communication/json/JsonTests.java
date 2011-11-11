package se.cygni.texasholdem.communication.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import se.cygni.texasholdem.communication.message.TexasMessageParser;
import se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent;
import se.cygni.texasholdem.game.Card;
import se.cygni.texasholdem.game.Hand;
import se.cygni.texasholdem.game.Player;
import se.cygni.texasholdem.game.PlayerShowDown;
import se.cygni.texasholdem.game.definitions.PokerHand;
import se.cygni.texasholdem.game.definitions.Rank;
import se.cygni.texasholdem.game.definitions.Suit;

public class JsonTests {

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRankToAndFromJson() throws JsonGenerationException,
            JsonMappingException, IOException {

        final Rank rank = Rank.FOUR;

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, rank);
        final String json = out.toString();

        final Rank r = mapper.readValue(json, Rank.class);
        Assert.assertEquals(rank, r);
    }

    @Test
    public void testSuitToAndFromJson() throws JsonParseException,
            JsonMappingException, IOException {

        final Suit suit = Suit.HEARTS;

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, suit);
        final String json = out.toString();

        final Suit s = mapper.readValue(json, Suit.class);
        Assert.assertEquals(suit, s);
    }

    @Test
    public void testCardToAndFromJson() throws JsonParseException,
            JsonMappingException, IOException {

        final Card card = Card.valueOf(Rank.THREE, Suit.DIAMONDS);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, card);
        final String json = out.toString();

        final Card c = mapper.readValue(json, Card.class);
        Assert.assertEquals(card, c);
    }

    @Test
    public void testListOfCardsToAndFromJson() throws JsonGenerationException,
            JsonMappingException, IOException {

        final List<Card> cards = new ArrayList<Card>();
        cards.add(Card.valueOf(Rank.ACE, Suit.HEARTS));
        // cards.add(Card.valueOf(Rank.THREE, Suit.DIAMONDS));
        // cards.add(Card.valueOf(Rank.TEN, Suit.CLUBS));

        final PlayerShowDown psd = new PlayerShowDown(new Player("test", 343),
                new Hand(cards, PokerHand.HIGH_HAND), 0);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, psd);
        final String json = out.toString();
        System.out.println(json);

        final PlayerShowDown psdReread = mapper.readValue(json,
                PlayerShowDown.class);

        for (int i = 0; i < cards.size(); i++)
            Assert.assertEquals(cards.get(i), psdReread.getHand().getCards()
                    .get(i));
    }

    @Test
    public void testEventToAndFromJson() throws JsonParseException,
            JsonMappingException, IOException {

        final YouHaveBeenDealtACardEvent event = new YouHaveBeenDealtACardEvent(
                Card.valueOf(Rank.NINE, Suit.HEARTS));

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, event);
        final String json = out.toString();
        System.out.println(json);
        final YouHaveBeenDealtACardEvent e = mapper.readValue(json,
                YouHaveBeenDealtACardEvent.class);

        Assert.assertEquals(event.getCard(), e.getCard());
    }

    @Test
    public void testEventDeserializationWithMessageParser()
            throws JsonParseException, JsonMappingException, IOException {

        final YouHaveBeenDealtACardEvent event = (YouHaveBeenDealtACardEvent) TexasMessageParser
                .decodeMessage("{\"card\":{\"c\":\"9h\"},\"type\":\"se.cygni.texasholdem.communication.message.event.YouHaveBeenDealtACardEvent\"}");

        Assert.assertNotNull(event.getCard());
    }
}
