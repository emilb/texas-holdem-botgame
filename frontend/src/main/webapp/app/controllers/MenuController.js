function MenuController($scope) {

    $scope.sections = [
        {url: '/houseRules', name: 'House Rules'},
        {url: '/texasHoldem', name: 'Texas Holde\'m'},
        {url: '/gettingStarted', name: 'Getting started'},
        {url: '/showGames', name: 'Show games'},
        {url: '/serverStatus', name: 'Server status'},
        {url: '/tournament', name: 'Tournament'},
        {url: '/credits', name: 'Credits'}
    ];

    $scope.setSelected = function(section) {
        console.log("Selected: " + section.name);
        $scope.selected = section;
    }

    $scope.isSelected = function(section) {
        return $scope.selected === section;
    }
}