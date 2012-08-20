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

            <p>Game rules</p>
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
                        <td>${gamePlan.startingChipsAmount}</td>
                    </tr>
                    <tr>
                        <td>Big blind</td>
                        <td>${gamePlan.bigBlindStart}</td>
                    </tr>
                    <tr>
                        <td>Small blind</td>
                        <td>${gamePlan.smallBlindStart}</td>
                    </tr>
                    <tr>
                        <td>Blind raise strategy</td>
                        <td>${gamePlan.blindRaiseStrategy}</td>
                    </tr>
                    <tr>
                        <td>Big blind raise</td>
                        <td>${gamePlan.bigBlindRaiseStrategyValue}</td>
                    </tr>
                    <tr>
                        <td>Small blind raise</td>
                        <td>${gamePlan.smallBlindRaiseStrategyValue}</td>
                    </tr>
                    <tr>
                        <td># rounds between blind raise</td>
                        <td>${gamePlan.playsBetweenBlindRaise}</td>
                    </tr>
                    <tr>
                        <td>Max # turns per state</td>
                        <td>${gamePlan.maxNoofTurnsPerState}</td>
                    </tr>
                    <tr>
                        <td>Max # action retries</td>
                        <td>${gamePlan.maxNoofActionRetries}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <!--/span-->

            <div class="span6">
                <p>
                    A maximum of
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
