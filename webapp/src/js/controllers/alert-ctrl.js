angular.module('RDash')
.controller('AlertsCtrl', ['$scope','$firebaseObject', function ($scope, $firebaseObject) {
    var ref = new Firebase("https://watchdog-app.firebaseio.com/Bill/alerts");
    var refSync = $firebaseObject(ref);
    $scope.alerts = {};
    refSync.$bindTo($scope, "alerts");

    $scope.debug = function(){
        console.log($routeParams);
    };

    $scope.getAlertString = function(index){
        return $scope.alerts[index].name + " - " + $scope.alerts[index].description;
    };

    $scope.closeAlert = function(index) {
        delete $scope.alerts[index];
    };
}]);
