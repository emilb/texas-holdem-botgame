<%@ include file="/WEB-INF/views/includes/taglibs.jsp" %>

<spring:url scope="page" var="jqueryJavascriptUrl" value="/resources/js/jquery-1.7.1.js"/>
<spring:url scope="page" var="jqueryTmplJavascriptUrl" value="/resources/js/jquery.tmpl.min.js"/>
<spring:url scope="page" var="jqueryAtmosphereUrl" value="/resources/js/jquery.atmosphere.js"/>
<spring:url scope="page" var="bootstrapUrl" value="/resources/js/bootstrap.js"/>
<spring:url scope="page" var="bootstrapCssUrl" value="/resources/css/bootstrap.css"/>
<spring:url scope="page" var="bootstrapResponsiveCssUrl" value="/resources/css/bootstrap-responsive.css"/>

<!DOCTYPE HTML>
<html>
<head>

    <title>Cygni Texas Hold'em</title>

    <%@ include file="/WEB-INF/views/includes/head.jsp" %>
</head>
<body>

<h:navbar section="showgame"/>

<div class="container">
        <div class="hero-unit">
            <h1>Show games</h1>

            <p>Inspect games played</p>
        </div>

        <div class="row">

            <div class="span12">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Play state</th>
                        <th>Community</th>
                        <c:forEach var="player" items="${gamelog.players}">
                        <th>${player.name}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td></td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>
                                <c:if test="${player.dealer}">Dealer</c:if>
                                <c:if test="${player.bigBlind}">Big Blind ($${gamelog.bigBlind})</c:if>
                                <c:if test="${player.smallBlind}">Small Blind ($${gamelog.smallBlind})</c:if>
                            </td>
                        </c:forEach>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td><h:cardToImage cards="${player.cards}"/></td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Pre Flop</td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.preflopBet}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Flop</td>
                        <td><h:cardToImage cards="${gamelog.flopCards}"/></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.flopBet}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Turn</td>
                        <td><h:cardToImage cards="${gamelog.turnCards}"/></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.turnBet}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>River</td>
                        <td><h:cardToImage cards="${gamelog.riverCards}"/></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.riverBet}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Showdown</td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td><h:cardToImage cards="${player.cardsBestHand}"/></td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Highest hand</td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>${player.pokerHand}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Winnings</td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.winnings}</td>
                        </c:forEach>
                    </tr>

                    <tr>
                        <td>Chips</td>
                        <td></td>
                        <c:forEach var="player" items="${gamelog.players}">
                            <td>$${player.chipsAfterGame}</td>
                        </c:forEach>
                    </tr>

                    </tbody>
                </table>
            </div>
            <!--/span-->

        </div>


<hr>

<footer>
    <p> &copy; Cygni AB 2012</p>
</footer>

</div>


</body>
</html>
