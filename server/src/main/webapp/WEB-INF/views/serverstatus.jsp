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

<h:navbar section="serverstatus"/>

<div class="container">

    <div class="hero-unit">
        <h1>Server Status</h1>

        <p>Shows some statistics and connected players</p>
    </div>

    <div class="row">
        <div class="span6">
            <div class="well well-large">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th colspan="2">Statistics</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Uptime</td>
                        <td>${uptime}</td>
                    </tr>
                    <tr>
                        <td>Connected players</td>
                        <td>${noofPlayers}</td>
                    </tr>
                    <tr>
                        <td>Total clients served</td>
                        <td>${totalNoofConnections}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <!--/span-->

        <div class="span6">
            <div class="well well-large">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th colspan="2">Players</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="player" items="${players}">
                        <tr>
                            <td>${player.name}</td>
                            <td>$ ${player.chipCount}</td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
            </div>
        </div>
        <!--/span-->
    </div>

    <hr>

    <footer>
        <p> &copy; Cygni AB 2012</p>
    </footer>

</div>
<!--/.fluid-container-->


</body>
</html>
