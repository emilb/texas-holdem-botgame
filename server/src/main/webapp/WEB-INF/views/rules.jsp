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

    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script src="${pageScope.jqueryJavascriptUrl}"></script>
    <script src="${pageScope.jqueryTmplJavascriptUrl}"></script>
    <script src="${pageScope.jqueryAtmosphereUrl}"></script>
    <script src="${pageScope.bootstrapUrl}"></script>
    <link rel="stylesheet" href="${pageScope.bootstrapCssUrl}"/>
    <link rel="stylesheet" href="${pageScope.bootstrapResponsiveCssUrl}"/>
</head>
<body>

<h:navbar section="rules"/>

<div class="container-fluid">
    <div class="span12">
        <div class="hero-unit">
            <h1>House Rules</h1>

            <p>and settings</p>
        </div>
        <div class="row-fluid">
            <div class="span6">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th colspan="2">Numbers</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Starting chip amount</td>
                        <td><div class="pull-right">$${gamePlan.startingChipsAmount}</div></td>
                    </tr>
                    <tr>
                        <td>Big blind</td>
                        <td><div class="pull-right">$${gamePlan.bigBlindStart}</div></td>
                    </tr>
                    <tr>
                        <td>Small blind</td>
                        <td><div class="pull-right">$${gamePlan.smallBlindStart}</div></td>
                    </tr>
                    <tr>
                        <td>Blind raise strategy</td>
                        <td><div class="pull-right">${gamePlan.blindRaiseStrategy}</div></td>
                    </tr>
                    <tr>
                        <td>Big blind raise</td>
                        <td><div class="pull-right">${gamePlan.bigBlindRaiseStrategyValue}</div></td>
                    </tr>
                    <tr>
                        <td>Small blind raise</td>
                        <td><div class="pull-right">${gamePlan.smallBlindRaiseStrategyValue}</div></td>
                    </tr>
                    <tr>
                        <td># rounds between blind raise</td>
                        <td><div class="pull-right">${gamePlan.playsBetweenBlindRaise}</div></td>
                    </tr>
                    <tr>
                        <td>Max # turns per state</td>
                        <td><div class="pull-right">${gamePlan.maxNoofTurnsPerState}</div></td>
                    </tr>
                    <tr>
                        <td>Max # action retries</td>
                        <td><div class="pull-right">${gamePlan.maxNoofActionRetries}</div></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <!--/span-->

            <div class="span6">
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
                    A bot player that fails to respond in time (30 sec) or responds with non valid actions (i.e.
                    trying to CHECK when a CALL or RAISE is needed) more than ${gamePlan.maxNoofActionRetries}
                    times in a row will automatically be folded in the current game bout.
                </p>
            </div>
            <!--/span-->
        </div>

    </div>
    <!--/span-->
</div>
<!--/row-->

<hr>

<footer>
    <p> &copy; Cygni AB 2012</p>
</footer>

</div><!--/.fluid-container-->


</body>
</html>
