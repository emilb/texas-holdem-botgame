<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="h" %>

<div class="container">
    <div class="hero-unit">
        <h1>Show games</h1>

        <p>Inspect games played</p>
    </div>

    <div class="row-fluid">
        <div class="span12 well">
            <legend>Navigate</legend>
            <form>
                <div class="row-fluid">
                    <div class="span4">
                        <label>Table Id:</label>
                        <input type="text" class="input-small search-query" placeholder="Table Id" id="tableId" />
                    </div>
                    <div class="span4">
                        <label>Game round:</label>
                        <input type="text" class="input-small search-query" placeholder="Game round"
                               id="gameRoundNo" />
                    </div>
                    <div class="span4">
                        <label>Auto forward:</label>
                        <input type="checkbox" id="autoForward"/>
                    </div>
                </div>

            </form>
            <div class="row-fluid">
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
    </div>

    <div id="placeHolderGame">

    </div>

</div>

<script>
    var reloadTimer;

    $(document).ready(function () {
        $('#nav_first').click(function (event) {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(0);
            updateGameView();
        });

        $('#nav_previous').click(function () {
            event.stopPropagation();
            event.preventDefault();

            var currentGameNo = parseInt($("#gameRoundNo").val(), 10);
            var currentTableId = parseInt($("#tableId").val(), 10);

            if (currentGameNo <= 0) {
                $("#tableId").val(currentTableId - 1);
                $("#gameRoundNo").val(-1);
                updateGameView();
            } else {
                $("#gameRoundNo").val(currentGameNo - 1);
                updateGameView();
            }
        });

        $('#nav_next').click(function () {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(parseInt($("#gameRoundNo").val(), 10) + 1);
            updateGameView();
        });

        $('#nav_last').click(function () {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(-1);
            updateGameView();
        });

        $('#autoForward').change(function() {
            if ($(this).attr("checked")) {
                reloadTimer = setInterval(function () {
                    $("#gameRoundNo").val(parseInt($("#gameRoundNo").val(), 10) + 1);
                    updateGameView();
                }, 1000);
            } else {
                clearTimeout(reloadTimer);
            }
        });

        $('#tableId').focusout(function () {
            updateGameView();
        });

        $('#gameRoundNo').focusout(function () {
            updateGameView();
        });

        // Only allow numbers
        $('#gameRoundNo').keyup(function () {
            $(this).val($(this).val().replace(/[^\d]/, ''));
        });

        $.ajax({
            type:"GET",
            url:"/timemachine/table/-1/gameround/-1",
            success:function (response) {
                result = ich.gameRoundTemplate(response);
                $("#placeHolderGame").html(result);
                $("#tableId").val(response.tableCounter);
                $("#gameRoundNo").val(response.roundNumber);

                if ($("[rel=tooltip]").length) {
                    $("[rel=tooltip]").tooltip();
                }
            }
        });

        if ($("[rel=tooltip]").length) {
            $("[rel=tooltip]").tooltip();
        }


    });

    function updateGameView() {
        var tableId = $("#tableId").val();
        var gameRound = $("#gameRoundNo").val();

        $.ajax({
            type:"GET",
            url:"/timemachine/table/" + tableId + "/gameround/" + gameRound,
            success:function (response) {
                result = ich.gameRoundTemplate(response);
                $("#placeHolderGame").html(result);
                $("#tableId").val(response.tableCounter);
                $("#gameRoundNo").val(response.roundNumber);

                if ($("[rel=tooltip]").length) {
                    $("[rel=tooltip]").tooltip();
                }
            }
        });
    }
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
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/flopCards}}
                    </td>
                    <td>
                        {{#turnCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/turnCards}}
                    </td>
                    <td>
                        {{#riverCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
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
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
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
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
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
