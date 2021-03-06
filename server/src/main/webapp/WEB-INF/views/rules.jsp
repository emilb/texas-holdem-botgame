<div class="container">
    <div class="hero-unit">
        <h1>House Rules</h1>

        <p>and settings</p>
    </div>

    <div class="row">
        <div class="span6">
            <div class="well well-large">
                <h2>House rules</h2>

                <p>
                    A table is played until either a winner has been established, all real players have quit
                    or if a Player shuffle is decided from within a tournament.
                </p>

                <p>
                    A game bout is played till either all players but one has folded or the game has
                    entered the state SHOWDOWN. If the latter the pot is divided according to player hands,
                    bet amounts etc.
                </p>

                <p>
                    A maximum of ${gamePlan.maxNoofTurnsPerState} turns are allowed per player and state.
                    This is to hinder raise races between players. It is always possible to go ALL-IN.
                </p>

                <p>
                    Raises are always fixed to the current value of the big blind.
                </p>

                <p>
                    A bot player that <em>fails to respond in time (3 sec)</em> or responds with non valid actions (i.e.
                    trying to CHECK when a CALL or RAISE is needed) more than ${gamePlan.maxNoofActionRetries}
                    times in a row will automatically be folded in the current game bout.
                </p>

                <p>
                    Player names must be unique, if your name is taken you will not be able to connect and play.
                </p>

                <h2>Training</h2>

                <p>
                    A bot player may train against a few server-bot players that are always alive and eager to play!
                    This is done by joining the room TRAINING upon connection. The table stops playing as soon as
                    the bot player has either won or lost all of its chips.
                </p>

                <h2>Free play</h2>

                <p>
                    If you'd rather play against other bot players, or battle different versions of your own bot
                    join the room FREEPLAY. Whenever 3 or more players are connected to a room in FREEPLAY the
                    game starts (after an extra minute of wait to let latecomers join).
                </p>

                <h2>Tournament</h2>

                <p>
                    When it is time for a tournament all interested players connect and join the room TOURNAMENT.
                    An administrator starts the tournament manually and depending on how many players have joined
                    (max 11 players per table) a suitable amount of tables are started and players are placed.
                </p>

                <p>
                    Whenever a table has 3 or less players left all tables are stopped and the
                    remaining players are shuffled and placed on new tables. This is to even the odds and keep
                    the game balanced.
                </p>

                <p>
                    Upon that time that only 11 players or less are remaining in play all tables are stopped and
                    players are joined on one table. Showdown time!
                </p>

                <p>
                    The ultimate winner of a tournament is the last standing player. The tournament keeps track of
                    when a player becomes bankrupt and uses this timestamp for establishing the total rank between
                    players (i.e the earlier a player is bankrupt the lower the score).
                </p>
            </div>
        </div>
        <!--/span-->

        <div class="span6">
            <div class="well well-large">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th colspan="2">Numbers</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Starting chip amount</td>
                        <td>
                            <div class="pull-right">$${gamePlan.startingChipsAmount}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Big blind</td>
                        <td>
                            <div class="pull-right">$${gamePlan.bigBlindStart}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Small blind</td>
                        <td>
                            <div class="pull-right">$${gamePlan.smallBlindStart}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Blind raise strategy</td>
                        <td>
                            <div class="pull-right">${gamePlan.blindRaiseStrategy}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Big blind raise</td>
                        <td>
                            <div class="pull-right">${gamePlan.bigBlindRaiseStrategyValue}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Small blind raise</td>
                        <td>
                            <div class="pull-right">${gamePlan.smallBlindRaiseStrategyValue}</div>
                        </td>
                    </tr>
                    <tr>
                        <td># rounds between blind raise</td>
                        <td>
                            <div class="pull-right">${gamePlan.playsBetweenBlindRaise}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Max # turns per state</td>
                        <td>
                            <div class="pull-right">${gamePlan.maxNoofTurnsPerState}</div>
                        </td>
                    </tr>
                    <tr>
                        <td>Max # action retries</td>
                        <td>
                            <div class="pull-right">${gamePlan.maxNoofActionRetries}</div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <!--/span-->
    </div>

    <!--/span-->
    <!--/row-->
</div>
<!--/.fluid-container-->
