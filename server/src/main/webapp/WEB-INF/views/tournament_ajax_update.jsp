<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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
        <c:forEach var="player" items="${tournamentCurrent.playerRanking}" varStatus="position">
            <tr>
                <td>${position.index + 1}</td>
                <td>${player.name}</td>
                <td>
                    <div class="pull-right">$ ${player.chipCount}</div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${!tournamentCurrent.tournamentHasEnded}">
    <script type="text/javascript">
        reloadTimer = setTimeout(function () {
            loadTournament('${tournamentCurrent.id}')
        }, 1000);
    </script>
</c:if>