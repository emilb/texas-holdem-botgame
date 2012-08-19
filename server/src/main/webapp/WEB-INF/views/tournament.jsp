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

<h:navbar section="tournament"/>

<div class="container-fluid">
    <div class="span9">
        <div class="hero-unit">
            <h1>Tournaments</h1>

            <p>Status and player top-lists for current tournaments</p>
        </div>
        <div class="row-fluid">
            <div class="span6">
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
            <!--/span-->

            <div class="span6">
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
