<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="h" %>

<div class="container">
    <div class="hero-unit">
        <h1>Show games</h1>

        <p>Inspect games played</p>
    </div>

    <div class="row">
        <div class="span12">
            <form class="form-inline">
                Table Id: <input type="text" class="input-small" placeholder="Table Id" id="tableId">
                Game round: <input type="text" class="input-small" placeholder="Game round" id="gameRoundNo">
                User filter: <input type="text" class="input-large" placeholder="Filter on user" id="userFilter">
                <label class="checkbox">
                    <input type="checkbox"> Auto forward
                </label>
            </form>
        </div>

        <div class="span12">
            <ul class="pager">
                <li>
                    <a href="#" id="nav_first">&larr; First</a>
                </li>
                <li>
                    <a href="#" id="nav_previous">&larr; Previous</a>
                </li>
                <li>
                    <a href="#" id="nav_next">Next &rarr;</a>
                </li>
                <li>
                    <a href="#" id="nav_last">Last &rarr;</a>
                </li>
            </ul>
        </div>

    </div>

    <hr/>

    <div id="placeHolderGame">

    </div>

</div>

<script>
    $(document).ready(function () {
        $('#nav_first').click(function () {
            $('#new_position').val(0);
            $('#changegame').trigger('submit');
        });

        $('#nav_previous').click(function () {
            $('#new_position').val(${position.previous});
            $('#changegame').trigger('submit');
        });

        $('#nav_next').click(function () {
            $('#new_position').val(${position.next});
            $('#changegame').trigger('submit');
        });

        $('#nav_last').click(function () {
            $('#new_position').val(-1);
            $('#changegame').trigger('submit');
        });

        $('#btn_update').click(function () {
            $('#new_position').val($('#inputGameRound').val().replace(/[^\d]/, ''));
            $('#tableId').val($('#inputTable').val());
            $('#changegame').trigger('submit');
        });

        // Only allow numbers
        $('#inputGameRound').keyup(function () {
            $(this).val($(this).val().replace(/[^\d]/, ''));
        });

        $.ajax({
            type:"GET",
            url:"/timemachine/table/-1/gameround/-1",
            success:function (response) {
                result = ich.gameRoundTemplate(response);
                console.log(response);
                $("#placeHolderGame").html(result);
                $("#tableId").val(response.tableCounter);
                $("#gameRoundNo").val(response.roundNumber);

                if ($("[rel=tooltip]").length) {
                    $("[rel=tooltip]").tooltip();
                }

                console.log(response);
            }
        });

        if ($("[rel=tooltip]").length) {
            $("[rel=tooltip]").tooltip();
        }


    });

</script>


<script id="gameRoundTemplate" type="text/html">
    <div class="row">
        <div class="span12">
            <table class="table">
                <thead>
                <tr>
                    <th></th>
                    <th></th>
                    <th>Pre Flop</th>
                    <th>Flop</th>
                    <th>Turn</th>
                    <th>River</th>
                    <th>Showdown</th>
                    <th>Hand</th>
                    <th>Standing</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td><b>Community</b></td>
                    <td></td>
                    <td></td>
                    <td>
                        {{#flopCards}}
                            <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50" rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}" />
                        {{/flopCards}}
                    </td>
                    <td>
                        {{#turnCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50" rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}" />
                        {{/turnCards}}
                    </td>
                    <td>
                        {{#riverCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50" rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}" />
                        {{/riverCards}}
                    </td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>

                {{#players}}
                    <tr>
                        <td>
                                {{name}} <br/>
                            <c:if test="${player.dealer}">Dealer</c:if>
                            <c:if test="${player.bigBlind}">Big Blind ($${gameLog.bigBlind})</c:if>
                            <c:if test="${player.smallBlind}">Small Blind ($${gameLog.smallBlind})</c:if>
                        </td>
                        <td>
                            {{#cards}}
                                <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50" rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}" />
                            {{/cards}}
                            <h:cardToImage cards="${player.cards}"/>
                        </td>
                        <td>$ {{preflopBet}}
                            {{#preflopFolded}}
                            <br/>Folded
                            {{/preflopFolded}}
                            {{#preflopAllIn}}
                            <br/>All In
                            {{/preflopAllIn}}
                        </td>
                        <td>$ {{flopBet}}
                            {{#flopFolded}}
                            <br/>Folded
                            {{/flopFolded}}
                            {{#flopAllIn}}
                            <br/>All In
                            {{/flopAllIn}}
                        </td>
                        <td>$ {{turnBet}}
                            {{#turnFolded}}
                            <br/>Folded
                            {{/turnFolded}}
                            {{#turnAllIn}}
                            <br/>All In
                            {{/turnAllIn}}
                        </td>
                        <td>$ {{riverBet}}
                            {{#riverFolded}}
                            <br/>Folded
                            {{/riverFolded}}
                            {{#riverAllIn}}
                            <br/>All In
                            {{/riverAllIn}}
                        </td>
                        <td>
                            {{#cardsBestHand}}
                            <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50" rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}" />
                            {{/cardsBestHand}}
                            <br/>
                        </td>
                        <td>{{pokerHand}}</td>
                        <td>
                            {{#winnings}}
                                <b>Won: $ {{winnings}}</b> <br/>
                            {{/winnings}}
                            Chips: $ {{chipsAfterGame}}
                        </td>

                    </tr>
                {{/players}}


                </tbody>
            </table>
        </div>
    </div>
</script>
