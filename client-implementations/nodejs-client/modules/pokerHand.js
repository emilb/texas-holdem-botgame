require('./sugar-1.3.min.js');

exports.pokerHand = function(myCardsAndCommunityCards) {
    "use strict";

    
    // Create Cards from array of poker cards { rank:"TEN", suit:"CLUBS"}
    function createCards(cardsArr) {
    	
    	function isEmpty() {
    		return cardsArr.isEmpty();
    	};
        // Group cards according to grouping condition function.
        // The function should return true if the previous and 
        // next card and should be grouped.
        // The result is an array of cardgroups(arrays) sorted by length. 
        function groupAny(groupFunction) {
        	if (isEmpty()) { 
        		return [[]];        		
        	}
        	var arrInit = [[cardsArr.first()]];
        	return cardsArr.from(1).reduce(function(arr, next) {
        		var currentArr = arr.last();
        		var prev = currentArr.last();
        		if (groupFunction(prev, next)) {
        			//console.log(""+next.asString+" is grouped with "+prev.asString);
        			currentArr.push(next);
        		} else {
        			// else put in new group
        			//console.log(""+next.asString+" is put in new group");
        			arr.push([next]);
        		}
    			return arr;
        	}, arrInit).sortBy(function(arr) { 
            	// sort group arr so that longest arr comes last
        		return arr.length;
        	}); // [ [S,S,S,S,S] , [D,D] ] -> [ [D,D], [S,S,S,S,S] ] -> 
        };

        function group(groupFunction, minLength) {
        	var sortedGroup = groupAny(groupFunction);
            // Get the last arr in group if length is sufficient 
        	var longest = sortedGroup.last(); // [S,S,S,S,S]
        	if (longest.length >= minLength) {
        		return createCards(longest);
        	} 
        	return createCards([]);
        };
        
        var sameRankFn = function(prev, next) {
        	return next.rank === prev.rank;
        };
        var straightFn = function(prev, next) {
        	return (next.rank === prev.rankPrevStraight + 1);
        };
        var flushFn = function(prev, next) {
        	return next.suit === prev.suit;
        };

        
    	return {
    		highestRank : function() {
    			return cardsArr.last() && cardsArr.last().rank || undefined;
    		},
            lowestRank : function() {
                return cardsArr.first() && cardsArr.first().rank || undefined;
            },
    		isHand : function() {
            	return cardsArr.length >= 5;
            },
            hasAce : function() {
                return cardsArr.last() && cardsArr.last().rank == 14;
            },
    		isFlush : function() {
    			return this.flushCards().bestRanked().isHand();
    		},
        	// [ D1,S2,S4,D8,S8,S9,S10] -> [D1,D8,S2,S4,S8,S8,S10]
            sortedByRank : function() {
                return createCards(cardsArr.sortBy('rank'));
            },
    		sortedBySuit : function() {
            	return createCards(cardsArr.sortBy(function(c) {
                	return c.suit;
                }));
    		},
            straightCards : function() {
                // [ [2,3], [5,6,7,8,9] ] -> [5,6,7,8,9] 
                // [ [2], [5,6,7,8,9,10] ] -> [5,6,7,8,9,10] 
                // [ [2], [4,5,6], [8,9] ] -> [] empty Cards
                // [ [2,3,4], [Q,K,A] ] -> [A,2,3,4] (A->1)
                // For this last case we create a new cardsArr with injected ACE before 2:
                // [ [2,3,4,5], [Q,K,A] ] -> [ [A,2,3,4,5], [Q,K,A] ] which will then give us the 
                // longest consequtive sequence [A,2,3,4,5]
                var arrStraightCards = cardsArr;
                if (this.hasAce()) {
                    arrStraightCards = [cardsArr.last()].add(cardsArr);
                } 
                var unsorted = createCards(arrStraightCards).straightCards2();
                var sorted = unsorted.sortedByRank();
                return sorted;
            },
            // TODO: internal fn only used by straightCards():
            straightCards2 : function() {
                return group(straightFn, 5);
            },
            // TODO: instead create a sorted&Curried array/card for
            // the three cases.
            // flush: suit-sorted and curried with flushFn etc
    		flushCards : function() {
            	return this.sortedBySuit().flushCards2();
            },
    		flushCards2 : function() {
            	// [ [D,D], [S,S,S,S,S] ] -> [S,S,S,S,S]
            	return group(flushFn, 5); //  [S,S,S,S,S]
            },
            handOrNull : function() {
            	return this.isHand() ? this : null;
    		},
            // Get five best ranked cards
            bestRanked : function() {
            	return createCards(cardsArr.last(5));
    		},
            // get Cards or null for given arrays of lengths sorted by highest first
            // [3,2] -> requests rank groups of lenght 3 and 2 (full house)
            // [4] -> fourOfAKind
            // [2,2] -> two pair
            getBestRanked : function(lengths) {
                var rankGroups = groupAny(sameRankFn);

                var matchingGroups = lengths.reduce(function(arr, next) {
                    // match against next from rankGroups
                    var grp = rankGroups.pop(); // last elements are biggest/longest
                    if (grp && grp.length === next) {
                        arr.push(grp);
                        //var grpstr = createCards(grp).asString();
                        //console.log("req len="+next+" grp="+grpstr);
                    }
                    return arr;
                }, []);

                if (matchingGroups.length === lengths.length) {
                    // unwrap groups to flat array
                    return createCards(matchingGroups.flatten());
                } else {
                    return null;
                }
            }, 
            asString : function () {
                return cardsArr.map(function (c) {
                    return "{"+c.rank+","+c.suit+"}";
                });
            }
    	};
    };

    // Private state

    var rankmap={
        "TWO" : 2,"THREE" : 3,"FOUR" : 4,"FIVE" : 5,"SIX" : 6,"SEVEN" : 7,
        "EIGHT" : 8,"NINE" : 9,
        "TEN" : 10,"JACK" : 11,"QUEEN" : 12,"KING" : 13,"ACE" : 14
    };
    
    var allcardsArr = myCardsAndCommunityCards.map(function(c) {
        var rankAsNumber = rankmap[c.rank];
        if (!rankAsNumber) {
             throw new Error('Illegal card rank: "'+c.rank+'" valid set is: '+Object.keys(rankmap));
        }
        return {
            rank : rankAsNumber, 
            rankPrevStraight : rankAsNumber == 14 ? 1 : rankAsNumber, 
            suit : c.suit, 
            org : c,
            asString : "rank:"+c.rank+" suite:"+c.suit
        };
    }).sortBy(function(c) {
    	return c.rank;
    });
    var allCards = createCards(allcardsArr);
    var flushCards = allCards.flushCards();
    var handCards = allCards.handOrNull();
    var emptyCards = createCards([]);
    
    // Private functions

    // Choose the straight from the top ranked card
    function bestStraight() {
    	return allCards.straightCards().bestRanked().handOrNull();
    };

    function getRoyalFlush() {
    	var straightFlush = getStraightFlush() || emptyCards;
    	if (straightFlush.isHand() 
                && straightFlush.hasAce()
                && straightFlush.lowestRank() !== 2) {
    		return straightFlush;
    	} else {
    		return null;
    	}
    };
    function getStraightFlush() {
    	var beststraight = bestStraight() || emptyCards;
    	return beststraight.isFlush() && beststraight || null;
    };
    function getFourOfAKind() {
    	return allCards.getBestRanked([4]);
    };
    function getFullHouse() {
    	return allCards.getBestRanked([3,2]);
    };
    function getFlush() {
    	return flushCards.handOrNull();
    };
    function getStraight() {
        return bestStraight();
    };
    function getThreeOfAKind() {
    	return allCards.getBestRanked([3]);
    };
    function getTwoPairs() {
    	return allCards.getBestRanked([2,2]);
    };
    function getOnePair() {
    	return allCards.getBestRanked([2]);
    };
    function getHighHand() {
    	return allCards.bestRanked().handOrNull();
    };


    function getBestHand() {
        return getRoyalFlush()
        	|| getStraightFlush()
        	|| getFourOfAKind()
        	|| getFullHouse()
        	|| getFlush()
        	|| getStraight()
            || getThreeOfAKind()
        	|| getTwoPairs()
            || getOnePair()
            || getHighHand();
    };


    var pokerhand = {

   		cards : allCards,
        bestHand : getBestHand,
        asString : function () {
            return allcardsArr.map(function (c) {
                return "{"+c.rank+","+c.suit+"}";
            });
        },

        isRoyalFlush : function () {
            return getRoyalFlush() != null;
        },
        isStraightFlush : function () {
            return getStraightFlush() != null;
        },
        isFourOfAKind : function () {
            return getFourOfAKind() != null;
        },
        isFullHouse : function () {
            return getFullHouse() != null;
        },
        isFlush : function () {
            return getFlush() != null;
        },
        isStraight : function() {
            return getStraight() != null;
        },
        isThreeOfAKind : function() {
            return getThreeOfAKind() != null;
        },
        isTwoPairs : function() {
            return getTwoPairs() != null;
        },
        isOnePair : function () {
            return getOnePair() != null;
        },
        isHighHand : function () {
            return getHighHand() != null;
        },

    };


    return pokerhand;
};

