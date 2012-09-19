<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="h" %>

<div class="container">
    <div class="hero-unit">
        <h1>Show games</h1>

        <p>Inspect games played</p>
    </div>

    <div class="row show-grid">
        <div class="span4">
            <form class="form-inline">
                <input type="text" class="input-small" placeholder="Table Id">
                <input type="text" class="input-small" placeholder="Game round">
                <label class="checkbox">
                    <input type="checkbox"> Auto forward
                </label>
            </form>
        </div>

        <div class="span4">
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

    <div class="row">
        <div class="span12">

            <div class="well">

                <form class="form-horizontal">
                    <div class="span4">
                        <div class="control-group">
                            <label class="control-label" for="inputTable">Table id</label>

                            <div class="controls">
                                <select class="span1" id="inputTable">
                                    <c:forEach var="tableId" items="${tableIds}">
                                        <option value="${tableId}"
                                                <c:if test="${tableId == gameLog.tableCounter}">selected</c:if>>${tableId}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="span4">
                        <div class="control-group">
                            <label class="control-label" for="inputGameRound">Game round</label>

                            <div class="controls">
                                <input class="span1" type="text" id="inputGameRound" value="${position.position}">
                            </div>
                        </div>
                    </div>
                    <div class="span4">
                        <button type="button" class="btn btn-primary" id="btn_update">Update</button>
                    </div>

                </form>

                <form:form action="timemachine" commandName="position" method="GET" id="changegame">
                    <form:hidden id="tableId" path="tableId"/>
                    <form:hidden id="new_position" path="position"/>
                </form:form>


            </div>
        </div>
    </div>

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
                    <td><h:cardToImage cards="${gameLog.flopCards}"/></td>
                    <td><h:cardToImage cards="${gameLog.turnCards}"/></td>
                    <td><h:cardToImage cards="${gameLog.riverCards}"/></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>

                <c:forEach var="player" items="${gameLog.players}">
                    <tr>
                        <td>
                                ${player.name} <br/>
                            <c:if test="${player.dealer}">Dealer</c:if>
                            <c:if test="${player.bigBlind}">Big Blind ($${gameLog.bigBlind})</c:if>
                            <c:if test="${player.smallBlind}">Small Blind ($${gameLog.smallBlind})</c:if>
                        </td>
                        <td>
                            <h:cardToImage cards="${player.cards}"/>
                        </td>
                        <td>$ ${player.preflopBet}
                            <c:if test="${player.preflopFolded}"><br/>Folded</c:if>
                            <c:if test="${player.preflopAllIn}"><br/>All In</c:if>
                        </td>
                        <td>$ ${player.flopBet}
                            <c:if test="${player.flopFolded}"><br/>Folded</c:if>
                            <c:if test="${player.flopAllIn}"><br/>All In</c:if>
                        </td>
                        <td>$ ${player.turnBet}
                            <c:if test="${player.turnFolded}"><br/>Folded</c:if>
                            <c:if test="${player.turnAllIn}"><br/>All In</c:if>
                        </td>
                        <td>$ ${player.riverBet}
                            <c:if test="${player.riverFolded}"><br/>Folded</c:if>
                            <c:if test="${player.riverAllIn}"><br/>All In</c:if>
                        </td>
                        <td>
                            <h:cardToImage cards="${player.cardsBestHand}"/> <br/>
                        </td>
                        <td>${player.pokerHand}</td>
                        <td>
                            <c:if test="${player.winnings > 0}">
                                <b>Won: $ ${player.winnings}</b> <br/>
                            </c:if>
                            Chips: $ ${player.chipsAfterGame}
                        </td>

                    </tr>
                </c:forEach>


                </tbody>
            </table>
        </div>
        <!--/span-->

    </div>

    <div class="row">
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
    });

</script>