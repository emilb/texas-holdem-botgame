/*global pokerfront*/
'use strict';

pokerfront.factory('serverStatusService', function ($http) {

    return {
        get: function (callback) {
            console.log('invoking http request');
            $http.get('/serverstatus/json').success(function(data) {
                console.log('json success ' + new Date());
                callback(data);
            });
        }
    };
});