<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="h" %>

<div class="container">
    <div class="hero-unit">
        <h1>Show games</h1>
        <p class="pull-right"><a href="/registration">Register for contest</a></p>
        <p>Inspect games played</p>
    </div>

    <div class="row-fluid">
        <div class="span12 well">
            <legend>Navigate</legend>
            <form>
                <div class="row-fluid">
                    <div class="span4">
                        <label>Table Id:</label>
                        <input type="text" class="input-small search-query" placeholder="Table Id" id="tableId" />
                    </div>
                    <div class="span4">
                        <label>Game round:</label>
                        <input type="text" class="input-small search-query" placeholder="Game round"
                               id="gameRoundNo" />
                    </div>
                    <div class="span4">
                        <label>Auto forward:</label>
                        <input type="checkbox" id="autoForward"/>
                    </div>
                </div>

            </form>
            <div class="row-fluid">
                <ul class="pager">
                    <li>
                        <a href="#" id="nav_first">&larr; First</a>
                    </li>
                    <li>
                        <a href="#" id="nav_previous">&larr; Previous</a>
                    </li>
                    <li>
                        <a href="#" id="nav_next">Next &rarr;</a>
                    </li>
                    <li>
                        <a href="#" id="nav_last">Last &rarr;</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div id="placeHolderGame"> </div>

    <hr />

    <div class="row">
        <div id="actionsGraphContainer" class="span6">
            <div id="statActionsGraph"></div>
        </div>
        <div id="chipsGraphContainer" class="span6">
            <div id="statChipsGraph"></div>
        </div>
        <div class="span12">
            <button id="btnResetChipsGraph" class="btn btn-small pull-right" type="button" onclick="">Reset</button>
        </div>
    </div>


</div>

<script>
    var reloadTimer;
    var actionsGraph;
    var chipsGraph;

    var currentView = {
        lastTableId : 0,
        lastGameRound : 0
    };

    $(document).ready(function () {

        $('#nav_first').click(function (event) {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(0);
            updateGameView();
        });

        $('#nav_previous').click(function () {
            event.stopPropagation();
            event.preventDefault();

            var currentGameNo = parseInt($("#gameRoundNo").val(), 10);
            var currentTableId = parseInt($("#tableId").val(), 10);

            if (currentGameNo <= 0) {
                $("#tableId").val(currentTableId - 1);
                $("#gameRoundNo").val(-1);
                updateGameView();
            } else {
                $("#gameRoundNo").val(currentGameNo - 1);
                updateGameView();
            }
        });

        $('#nav_next').click(function () {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(parseInt($("#gameRoundNo").val(), 10) + 1);
            updateGameView();
        });

        $('#nav_last').click(function () {
            event.stopPropagation();
            event.preventDefault();

            $("#gameRoundNo").val(-1);
            updateGameView();
        });

        $('#autoForward').change(function() {
            if ($(this).attr("checked")) {
                reloadTimer = setInterval(function () {
                    $("#gameRoundNo").val(parseInt($("#gameRoundNo").val(), 10) + 1);
                    updateGameView();
                }, 1000);
            } else {
                clearTimeout(reloadTimer);
            }
        });

        $('#tableId').keypress(function (e) {
            if (e.which == 13) {
                updateGameView();
            }
        });

        $('#gameRoundNo').keypress(function (e) {
            if (e.which == 13) {
                updateGameView();
            }
        });

        $('#tableId').focusout(function () {
            updateGameView();
        });

        $('#gameRoundNo').focusout(function () {
            updateGameView();
        });

        // Only allow numbers
        $('#gameRoundNo').keyup(function () {
            $(this).val($(this).val().replace(/[^\d]/, ''));
        });

        $('#btnResetChipsGraph').click(function() {
            if (chipsGraph) {
                chipsGraph.resetZoom();
            }
        });

        var requestedTableId = '${tableId}';
        if (requestedTableId) {
            updateGameViewWith(requestedTableId, -1);
        } else {
            updateGameViewWith(-1, -1);
        }
    });

    function updateGameView() {
        var tableId = $("#tableId").val();
        var gameRoundNo = $("#gameRoundNo").val();

        updateGameViewWith(tableId, gameRoundNo);
    }

    function updateGameViewWith(tableId, gameRoundNo) {

        if (tableId == currentView.lastTableId &&
                gameRoundNo == currentView.lastGameRound) {
            return;
        }

        if (chipsGraph) { chipsGraph.destroy(); }
        if (actionsGraph) { actionsGraph.destroy(); }



        $("#placeHolderGame").fadeOut('fast', function() {

            $.ajax({
                type: "GET",
                url: "/timemachine/table/" + tableId + "/gameround/" + gameRoundNo,
                success: function (response) {

                    currentView.lastGameRound = response.roundNumber;
                    currentView.lastTableId = response.tableCounter;

                    // If last game on table, stop auto forward
                    if (response.lastGame) {
                        $('#autoForward').prop('checked', false);
                        clearTimeout(reloadTimer);
                    }

                    result = ich.gameRoundTemplate(response);
                    $("#placeHolderGame").html(result).fadeIn('fast');

                    $("#tableId").val(response.tableCounter);
                    $("#gameRoundNo").val(response.roundNumber);

                    if ($("[rel=tooltip]").length) {
                        $("[rel=tooltip]").tooltip();
                    }

                    updateActionStatistics(response.tableCounter, response.roundNumber);
                    updateChipsStatistics(response.tableCounter, response.roundNumber);
                }
            });
        });


    }

    function updateActionStatistics(tableId, gameRoundNo) {

        if (actionsGraph) {
            actionsGraph.destroy();
        }

        $.ajax({
            type: "GET",
            url: "/timemachine/statsAction/table/" + tableId + "/gameround/" + gameRoundNo,
            success: function (response) {

                if (response.players.length > 4) {
                    $('#actionsGraphContainer').attr('class', 'span12');
                } else {
                    $('#actionsGraphContainer').attr('class', 'span6');
                }

                // Can specify a custom tick Array.
                // Ticks should match up one for each y value (category) in the series.
                var ticks = response.players;

                actionsGraph = $.jqplot('statActionsGraph', [
                    response.foldedStat,
                    response.calledStat,
                    response.raisedStat,
                    response.allInStat], {

                    title : 'Actions per player and type',

                    // The "seriesDefaults" option is an options object that will
                    // be applied to all series in the chart.
                    seriesDefaults:{
                        renderer:$.jqplot.BarRenderer,
                        rendererOptions: {fillToZero: true},
                        pointLabels: { show:true }
                    },
                    // Custom labels for the series are specified with the "label"
                    // option on the series option.  Here a series option object
                    // is specified for each series.
                    series:[
                        {label:'Folded'},
                        {label:'Called'},
                        {label:'Raised'},
                        {label:'All-in'}
                    ],
                    // Show the legend and put it outside the grid, but inside the
                    // plot container, shrinking the grid to accomodate the legend.
                    // A value of "outside" would not shrink the grid and allow
                    // the legend to overflow the container.
                    legend: {
                        show: true,
                        placement: 'outsideGrid'
                    },
                    axes: {
                        // Use a category axis on the x axis and use our custom ticks.
                        xaxis: {
                            renderer: $.jqplot.CategoryAxisRenderer,
                            ticks: ticks
                        },
                        // Pad the y axis just a little so bars can get close to, but
                        // not touch, the grid boundaries.  1.2 is the default padding.
                        yaxis: {
                            pad: 1.05,
                            padMin : 0,
                            tickOptions: {formatString: '%d'},
                            autoscale : true
                        }
                    }
                });
            }
        });
    }

    function updateChipsStatistics(tableId, gameRoundNo) {

        if (chipsGraph) {
            chipsGraph.destroy();
        }

        $.ajax({
            type: "GET",
            url: "/timemachine/statsChip/table/" + tableId + "/gameround/" + gameRoundNo,
            success: function (response) {

                if (response.players.length > 4) {
                    $('#chipsGraphContainer').attr('class', 'span12');
                } else {
                    $('#chipsGraphContainer').attr('class', 'span6');
                }

                var data = new Array();
                var seriesData = new Array();

                for (var i = 0; i < response.players.length; i++) {
                    data.push(response.chipsPerPlayerPerRound[response.players[i]]);
                    seriesData.push(
                            {
                                label : response.players[i],
                                lineWidth : 2,
                                showMarker : false,
                                shadow : false
                            });
                }

                chipsGraph = $.jqplot('statChipsGraph', data, {

                    title : 'Chips per player and round',

                    // Custom labels for the series are specified with the "label"
                    // option on the series option.  Here a series option object
                    // is specified for each series.
                    series: seriesData,

                    // Show the legend and put it outside the grid, but inside the
                    // plot container, shrinking the grid to accomodate the legend.
                    // A value of "outside" would not shrink the grid and allow
                    // the legend to overflow the container.
                    legend: {
                        show: true,
                        placement: 'outsideGrid'
                    },

                    axes: {
                        xaxis: {
                            padMin : 0
                        },
                        // Pad the y axis just a little so bars can get close to, but
                        // not touch, the grid boundaries.  1.2 is the default padding.
                        yaxis: {
                            pad: 1.05,
                            padMin : 0,
                            tickOptions: {formatString: '$%d'},
                            autoscale : true
                        }
                    },
                    highlighter: {
                        show: true,
                        sizeAdjust: 7.5,
                        fadeTooltip: true,
                        tooltipAxes: 'y'
                    },
                    cursor:{
                        show: true,
                        zoom:true,
                        showTooltip:false
                    }
                });
            }
        });
    }
</script>

<script id="gameRoundTemplate" type="text/html">
    <div class="row">
        <div class="span12">
            <table class="table">
                <thead>
                <tr>
                    <th>{{executionDate}}</th>
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
                    <td>
                        {{#flopCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="{{rank}} of {{suit}}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/flopCards}}
                    </td>
                    <td>
                        {{#turnCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="{{rank}} of {{suit}}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/turnCards}}
                    </td>
                    <td>
                        {{#riverCards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="{{rank}} of {{suit}}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/riverCards}}
                    </td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>

                {{#players}}
                <tr>
                    <td>
                        {{name}} <br/>
                        {{#dealer}}Dealer{{/dealer}}
                        {{#bigBlind}}Big Blind ($ {{bigBlindValue}}){{/bigBlind}}
                        {{#smallBlind}}Small Blind ($ {{smallBlindValue}}){{/smallBlind}}
                    </td>
                    <td>
                        {{#cards}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="{{rank}} of {{suit}}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/cards}}
                    </td>
                    <td>$ {{preflopBet}}
                        {{#preflopFolded}}
                        <br/>Folded
                        {{/preflopFolded}}
                        {{#preflopAllIn}}
                        <br/>All In
                        {{/preflopAllIn}}
                    </td>
                    <td>$ {{flopBet}}
                        {{#flopFolded}}
                        <br/>Folded
                        {{/flopFolded}}
                        {{#flopAllIn}}
                        <br/>All In
                        {{/flopAllIn}}
                    </td>
                    <td>$ {{turnBet}}
                        {{#turnFolded}}
                        <br/>Folded
                        {{/turnFolded}}
                        {{#turnAllIn}}
                        <br/>All In
                        {{/turnAllIn}}
                    </td>
                    <td>$ {{riverBet}}
                        {{#riverFolded}}
                        <br/>Folded
                        {{/riverFolded}}
                        {{#riverAllIn}}
                        <br/>All In
                        {{/riverAllIn}}
                    </td>
                    <td>
                        {{#cardsBestHand}}
                        <img src="/resources/img/cards/{{rank}}_{{suit}}_small.png" alt="${card}" width="36" height="50"
                             rel="tooltip" data-placement="top" data-original-title="{{rank}} of {{suit}}"/>
                        {{/cardsBestHand}}
                        <br/>
                    </td>
                    <td>{{pokerHand}}</td>
                    <td>
                        {{#winnings}}
                        <b>Won: $ {{winnings}}</b> <br/>
                        {{/winnings}}
                        Chips: $ {{chipsAfterGame}}
                    </td>

                </tr>
                {{/players}}
                </tbody>
            </table>
        </div>
    </div>
</script>

<script src="<c:url value="/resources/js/jquery.jqplot.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.barRenderer.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.categoryAxisRenderer.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.pointLabels.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.canvasTextRenderer.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.canvasAxisLabelRenderer.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.cursor.min.js" />"></script>
<script src="<c:url value="/resources/js/jqplot-plugins/jqplot.highlighter.js" />"></script>


