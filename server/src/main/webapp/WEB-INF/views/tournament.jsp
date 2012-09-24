<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>

    var reloadTimer;

    function loadTournament(id) {
        clearTimeout(reloadTimer);

        $.ajax({
            type: "GET",
            url: "/tournament/details/" + id,
            success: function (response) {
                var result = "Nothing to show";
                var shouldReload = true;

                if (!response.tournamentHasStarted && !response.tournamentHasEnded) {
                    result = ich.tournamentNotStarted(response);
                }

                if (response.tournamentHasStarted && !response.tournamentHasEnded) {
                    result = ich.tournamentStarted(response);
                }

                if (response.tournamentHasEnded) {
                    result = ich.tournamentEnded(response);
                    shouldReload = false;
                }

                $("#placeHolderTournament").html(result);

                if (shouldReload) {
                    reloadTimer = setInterval(function () {
                        loadTournament(id);
                    }, 1000);
                }
            }
        });
    }

    function startTournament(id) {
        console.log('Starting tournament with id: ' + id);
        $.ajax({
            type: "GET",
            url: "/tournament/start/" + id
        });
    }

    var reloadTimer;
</script>

<div class="container">
    <div class="hero-unit">
        <h1>Tournament</h1>

        <p>results and settings</p>
    </div>

    <div class="row">
        <div class="span3">
            <div class="well well-large">
                <h3>Tournaments</h3>

                <table class="table table-striped">
                    <tbody>

                    <c:forEach var="tournamentLog" items="${tournamentList}">
                        <tr>
                            <td>${tournamentLog.tournamentCounter}</td>
                            <td><a href="#" onclick="loadTournament('${tournamentLog.id}');">
                                    ${tournamentLog.createdDate} (players: ${fn:length(tournamentLog.playerRanking)})
                            </a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div id="placeHolderTournament">

        </div>

    </div>
</div>

<script>


</script>

<script id="tournamentNotStarted" type="text/html">
    <div class="span9">
        <h2>Tournament # {{tournamentCounter}} </h2>

        <h3>Status: {{status}}</h3>

        <div class="span12">
        {{#canStart}}<button type="button" class="btn btn-large btn-primary" onclick="startTournament('{{id}}')">START</button>{{/canStart}}
        </div>
        <div class="span3">
            <h3>Connected Players</h3>

            <table class="table table-striped">
                <tbody>
                {{#playerRanking}}
                    <tr>
                        <td>{{name}}</td>
                        <td>
                            <div class="pull-right">$ {{chipCount}}</div>
                        </td>
                    </tr>
                {{/playerRanking}}
                </tbody>
            </table>
        </div>
    </div>
</script>

<script id="tournamentStarted" type="text/html">
    <div class="span9">
        <h2>Tournament # {{tournamentCounter}} </h2>

        <h3>Status: {{status}}</h3>


        <div class="span4">
            <h3>Tables</h3>

            <table class="table table-striped">
                <tbody>
                {{#tablePartitions}}
                <tr>
                    <td>Partition {{index}}</td>
                    <td>
                        {{#tableIds}}
                        <a class="btn btn-info btn-mini" href="/showgame/table/{{.}}">{{.}}</a>
                        {{/tableIds}}
                    </td>
                </tr>
                {{/tablePartitions}}
                </tbody>
            </table>
        </div>

        <div class="span4">
            <h3>Ranking for Players</h3>

            <table class="table table-striped">
                <tbody>
                {{#playerRanking}}
                <tr>
                    <td>{{name}}</td>
                    <td>
                        <div class="pull-right">$ {{chipCount}}</div>
                    </td>
                </tr>
                {{/playerRanking}}
                </tbody>
            </table>
        </div>
    </div>
</script>

<script id="tournamentEnded" type="text/html">
    <div class="span9">
        <h2>Tournament # {{tournamentCounter}} </h2>

        <h3>Status: {{status}}</h3>


        <div class="span4">
            <h3>Tables</h3>

            <table class="table table-striped">
                <tbody>
                {{#tablePartitions}}
                <tr>
                    <td>Partition {{index}}</td>
                    <td>
                    {{#tableIds}}
                        <a class="btn btn-info btn-mini" href="/showgame/table/{{.}}">{{.}}</a>
                    {{/tableIds}}
                    </td>
                </tr>
                {{/tablePartitions}}
                </tbody>
            </table>
        </div>

        <div class="span4">
            <h3>Ranking for Players</h3>

            <table class="table table-striped">
                <tbody>
                {{#playerRanking}}
                <tr>
                    <td>{{name}}</td>
                    <td>
                        <div class="pull-right">$ {{chipCount}}</div>
                    </td>
                </tr>
                {{/playerRanking}}
                </tbody>
            </table>
        </div>
    </div>
</script>