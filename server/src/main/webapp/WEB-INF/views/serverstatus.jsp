<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">

    <div class="hero-unit">
        <h1>Server Status</h1>
        <p class="pull-right"><a href="/registration">Register for contest</a></p>
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

</div>
<!--/.fluid-container-->