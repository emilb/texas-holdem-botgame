pokerfront.controller('ServerStatusController', function ServerStatusController($scope, $timeout, serverStatusService, serverStatusSocket) {

    $scope.uptime = '';
    $scope.noofPlayers = 0;
    $scope.totalNoofConnections = 0;

    console.log('Created a ServerStatusController!');

    var updateServerStatus = function() {
        console.log('updating server status from controller');
        serverStatusService.get(function(data) {
             $scope.uptime = data.uptime;
             $scope.noofPlayers = data.noofPlayers;
             $scope.totalNoofConnections = data.totalNoofConnections;
        });
    }

    updateServerStatus();
});