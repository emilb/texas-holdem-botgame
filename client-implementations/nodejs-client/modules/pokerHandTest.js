var assert = require('assert');
require('assert');
var pokerHandContainer = require('./pokerHand.js');



assert.equal(typeof pokerHandContainer, 'object',"not an object");
//assert.equal("");

var royalFlush = [
    {"rank":"ACE", "suit":"CLUBS"},
    {"rank":"KING", "suit":"CLUBS"},
    {"rank":"QUEEN", "suit":"CLUBS"},
    {"rank":"JACK", "suit":"CLUBS"},
    {"rank":"TEN", "suit":"CLUBS"}
];

var straight = [
    {"rank":"ACE", "suit":"DIAMONDS"},
    {"rank":"KING", "suit":"CLUBS"},
    {"rank":"QUEEN", "suit":"SPADES"},
    {"rank":"JACK", "suit":"SPADES"},
    {"rank":"TEN", "suit":"HEARTS"}
];
var straight2 = [
    {"rank":"ACE", "suit":"SPADES"},
    {"rank":"TWO", "suit":"SPADES"},
    {"rank":"FIVE", "suit":"HEARTS"},
    {"rank":"FOUR", "suit":"SPADES"},
    {"rank":"THREE", "suit":"SPADES"}
];

var straightFlush = [
    {"rank":"SEVEN", "suit":"HEARTS"},
    {"rank":"SIX", "suit":"HEARTS"},
    {"rank":"FIVE", "suit":"HEARTS"},
    {"rank":"FOUR", "suit":"HEARTS"},
    {"rank":"THREE", "suit":"HEARTS"}
];
var straightFlush2 = [
    {"rank":"ACE", "suit":"SPADES"},
    {"rank":"TWO", "suit":"SPADES"},
    {"rank":"FIVE", "suit":"SPADES"},
    {"rank":"FOUR", "suit":"SPADES"},
    {"rank":"THREE", "suit":"SPADES"}
];
var flush = [
    {"rank":"NINE", "suit":"CLUBS"},
    {"rank":"SIX", "suit":"CLUBS"},
    {"rank":"FIVE", "suit":"CLUBS"},
    {"rank":"FOUR", "suit":"CLUBS"},
    {"rank":"THREE", "suit":"CLUBS"}
];
var fullHouse = [
    {"rank":"FIVE", "suit":"CLUBS"},
    {"rank":"FIVE", "suit":"HEARTS"},
    {"rank":"FIVE", "suit":"DIAMONDS"},
    {"rank":"THREE", "suit":"CLUBS"},
    {"rank":"THREE", "suit":"HEARTS"}
];
var fourOfAKind = [
    {"rank":"SEVEN", "suit":"CLUBS"},
    {"rank":"THREE", "suit":"HEARTS"},
    {"rank":"THREE", "suit":"DIAMONDS"},
    {"rank":"THREE", "suit":"CLUBS"},
    {"rank":"THREE", "suit":"HEARTS"}
];
var threeOfAKind = [
    {"rank":"TEN", "suit":"DIAMONDS"},
    {"rank":"TEN", "suit":"CLUBS"},
    {"rank":"QUEEN", "suit":"SPADES"},
    {"rank":"JACK", "suit":"SPADES"},
    {"rank":"TEN", "suit":"HEARTS"}
];
var twoPairs = [
    {"rank":"QUEEN", "suit":"DIAMONDS"},
    {"rank":"KING", "suit":"CLUBS"},
    {"rank":"QUEEN", "suit":"SPADES"},
    {"rank":"JACK", "suit":"SPADES"},
    {"rank":"JACK", "suit":"HEARTS"}
];
var onePair = [
    {"rank":"TWO", "suit":"DIAMONDS"},
    {"rank":"FOUR", "suit":"CLUBS"},
    {"rank":"QUEEN", "suit":"SPADES"},
    {"rank":"JACK", "suit":"SPADES"},
    {"rank":"JACK", "suit":"HEARTS"}
];

var allCards =  [{ "rank" : "FIVE","suit" : "CLUBS"},
           {"rank" : "FIVE", "suit" : "SPADES"},
           {"rank" : "KING", "suit" : "DIAMONDS"},
           {"rank" : "QUEEN", "suit" : "HEARTS"},
           {"rank" : "NINE", "suit" : "HEARTS"}];

var straightFlush =  [{ "rank" : "FIVE","suit" : "HEARTS"},
                 {"rank" : "SIX", "suit" : "HEARTS"},
                 {"rank" : "SEVEN", "suit" : "HEARTS"},
                 {"rank" : "EIGHT", "suit" : "HEARTS"},
                 {"rank" : "NINE", "suit" : "HEARTS"}];


function assertStraightFlush(cardsArray) {
  var pokerHand = pokerHandContainer.pokerHand(cardsArray);
  assert(typeof pokerHand === 'object',"not an object");
  assert(pokerHand.cards);
  assert(pokerHand.cards.isHand());
  assert(pokerHand.cards.isFlush());

  assert(!pokerHand.isRoyalFlush());
  assert(pokerHand.isStraight());
  assert(pokerHand.isStraightFlush(), "should be a straight flush:"+pokerHand.asString());
  assert(pokerHand.isFlush());
};

assertStraightFlush(straightFlush);
assertStraightFlush(straightFlush2);

function assertStraight(pokerHand) {
  assert(!pokerHand.cards.isFlush());
  assert(pokerHand.isStraight(), "should be a straight:"+pokerHand.asString());
  assert(!pokerHand.isRoyalFlush());
  assert(!pokerHand.isStraightFlush());
  assert(!pokerHand.isFlush());
};

var straightHand = pokerHandContainer.pokerHand(straight);
assertStraight(straightHand);
assert.equal(straightHand.cards.highestRank(), 14);
assert(straightHand.cards.hasAce());
var straightHand2 = pokerHandContainer.pokerHand(straight2);
assertStraight(straightHand2);


function assertRoyalFlush(cardsArray) {
  var pokerHand = pokerHandContainer.pokerHand(cardsArray);
  assert(pokerHand.cards.isFlush());

  assert.equal(pokerHand.cards.highestRank(), 14);
  assert(pokerHand.cards.hasAce());

  assert(pokerHand.isStraight(), "should be a straight:"+pokerHand.asString());
  assert(pokerHand.isRoyalFlush());
  assert(pokerHand.isStraightFlush());
  assert(pokerHand.isFlush());
};
assertRoyalFlush(royalFlush);


function assertFullHouse(cardsArray) {
  var pokerHand = pokerHandContainer.pokerHand(cardsArray);
  
  assert(!pokerHand.isStraight(), "should be a straight:"+pokerHand.asString());
  assert(!pokerHand.isRoyalFlush());
  assert(!pokerHand.isStraightFlush());

  assert(!pokerHand.isFourOfAKind());
  assert(pokerHand.isFullHouse());
  assert(pokerHand.isThreeOfAKind());
  assert(!pokerHand.isTwoPairs());
  assert(!pokerHand.isOnePair());
};
assertFullHouse(fullHouse);
    

function assertFourOfAKind(cardsArray) {
  var pokerHand = pokerHandContainer.pokerHand(cardsArray);
  
  assert(!pokerHand.isStraight(), "should be a straight:"+pokerHand.asString());
  assert(!pokerHand.isRoyalFlush());
  assert(!pokerHand.isStraightFlush());
  assert(!pokerHand.isFlush());

  assert(pokerHand.isFourOfAKind());
  assert(!pokerHand.isFullHouse());
  assert(!pokerHand.isThreeOfAKind());
  assert(!pokerHand.isTwoPairs());
  assert(!pokerHand.isOnePair());

  // TODOs: Add more useful public functions for pokerhand
  // pokerHand.bestHand() returns Hand/Cards with attributes:
  // pokerHand.bestHand().isFourOfAKind()
  // and maybe functions to see how close the hand is to be complete or the possibility for getting complete, etc
  // pokerHand.bestHand().possibilityFor( {hand : "FourOfAKind", nrOfCards : "1"} )

};

assertFourOfAKind(fourOfAKind);



var threeOfAKindHand = pokerHandContainer.pokerHand(threeOfAKind);
assert(!threeOfAKindHand.isFourOfAKind());
assert(threeOfAKindHand.isThreeOfAKind());
assert(!threeOfAKindHand.isTwoPairs());
assert(!threeOfAKindHand.isOnePair());

var twoPairsHand = pokerHandContainer.pokerHand(twoPairs);
assert(!twoPairsHand.isFourOfAKind());
assert(!twoPairsHand.isThreeOfAKind());
assert(twoPairsHand.isTwoPairs());
assert(twoPairsHand.isOnePair());

var onePairHand = pokerHandContainer.pokerHand(onePair);
assert(!onePairHand.isThreeOfAKind());
assert(onePairHand.isOnePair());
