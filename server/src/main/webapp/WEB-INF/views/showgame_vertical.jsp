<%@ include file="/WEB-INF/views/includes/taglibs.jsp" %>

<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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

    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script src="${pageScope.jqueryJavascriptUrl}"></script>
    <script src="${pageScope.jqueryTmplJavascriptUrl}"></script>
    <script src="${pageScope.jqueryAtmosphereUrl}"></script>
    <script src="${pageScope.bootstrapUrl}"></script>
    <link rel="stylesheet" href="${pageScope.bootstrapCssUrl}"/>
    <link rel="stylesheet" href="${pageScope.bootstrapResponsiveCssUrl}"/>
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
                            <td><h:cardToImage cards="${gamelog.flopCards}"/></td>
                            <td><h:cardToImage cards="${gamelog.turnCards}"/></td>
                            <td><h:cardToImage cards="${gamelog.riverCards}"/></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>

                    <c:forEach var="player" items="${gamelog.players}">
                        <tr>
                            <td>
                                ${player.name}  <br/>
                                <c:if test="${player.dealer}">Dealer</c:if>
                                <c:if test="${player.bigBlind}">Big Blind ($${gamelog.bigBlind})</c:if>
                                <c:if test="${player.smallBlind}">Small Blind ($${gamelog.smallBlind})</c:if>
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
                    <li class="previous">
                        <a href="#" id="older">&larr; Older</a>
                    </li>
                    <li class="next">
                        <a href="#" id="newer">Newer &rarr;</a>
                    </li>
                </ul>
            <form:form action="changegame" commandName="position" method="POST" id="changegame">
                <form:hidden id="new_position" path="position" value="666"/>
                <%--<input type="submit" value="Submit" />--%>
            </form:form>
            </div>
        </div>
<hr>

<footer>
    <p> &copy; Cygni AB 2012</p>
</footer>

</div>

<script>
    $(document).ready(function() {
        $('#older').click(function()
        {
            $('#new_position').val(${position.previous});
            $('#changegame').trigger('submit');
        });

        $('#newer').click(function()
        {
            $('#new_position').val(${position.next});
            $('#changegame').trigger('submit');
        });
    });

</script>

</body>
</html>
