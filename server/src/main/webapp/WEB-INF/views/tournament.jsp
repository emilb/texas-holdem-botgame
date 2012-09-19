<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
    function loadTournament(id) {
        clearTimeout(reloadTimer);

        $.ajax({
            type:"GET",
            url:"/tournament/subview?id=" + id,
            success:function (response) {
                $("#tournament_status").html(response);
            }
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
        <!--/span-->

        <div class="span9" id="tournament_status">

            <h2>Tournament #${tournamentCurrent.tournamentCounter}</h2>

            <h3>Status: ${tournamentCurrent.status}</h3>

            <c:if test="${tournamentCurrent.canStart}">
                <div class="pagination-centered">

                    <form:form cssClass="form-horizontal" action="startTournament" commandName="tournamentCurrentStart"
                               method="POST">
                        <form:hidden id="id" path="id"/>
                        <button type="submit" class="btn btn-large btn-primary">START</button>
                    </form:form>

                </div>
            </c:if>

            <div class="span4">
                <h3>Tables</h3>

                <c:forEach var="tableId" items="${tournamentCurrent.tableIds}">
                    <a href="/timemachine?tableId=${tableId}&position=0" class="btn btn-success">${tableId}</a>
                </c:forEach>
            </div>

            <div class="span4">
                <h3>Players</h3>

                <table class="table table-striped">
                    <tbody>
                    <c:forEach var="player" items="${tournamentCurrent.playerRanking}">
                        <tr>
                            <td>${player.name}</td>
                            <td>
                                <div class="pull-right">$ ${player.chipCount}</div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </div>
        <!--/span-->
    </div>
</div>
<!--/.fluid-container-->
