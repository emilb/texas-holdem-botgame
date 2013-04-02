/*global angular*/
/*jshint unused:false*/
'use strict';

/**
 * The main pokerfront app module.
 *
 * @type {angular.Module}
 */
var pokerfront = angular.module('pokerfront', [], function($routeProvider, $locationProvider) {
    $routeProvider.when('/houseRules', {
        templateUrl: 'partials/houseRules.html'
    });
    $routeProvider.when('/gettingStarted', {
        templateUrl: 'partials/gettingStarted.html'
    });
    $routeProvider.when('/serverStatus', {
        templateUrl: 'partials/serverStatus.html'
        //controller: ServerStatusController
    });
    $routeProvider.
        otherwise({
            redirectTo: '/'
        });


    // Add controller if applicable:
    /*
     $routeProvider.
     when('/gettingStarted', {
     templateUrl: 'partials/gettingStarted.html',
     controller: HomeController
     });
     */
    // configure html5 to get links working
    // If you don't do this, you URLs will be base.com/#/home rather than base.com/home
    //$locationProvider.html5Mode(true);
});

