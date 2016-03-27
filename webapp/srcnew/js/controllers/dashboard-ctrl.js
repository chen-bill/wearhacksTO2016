console.log('added');
angular.module('RDash')
.controller('DashboardController', ['$scope', '$firebaseObject', function($scope, $firebaseObject){
    var ref = new Firebase("https://watchdog-app.firebaseio.com/");
    var refSync = $firebaseObject(ref);
    console.log('working');
    refSync.$bindTo($scope, "data");

    $scope.debug = function (){
        console.log('working');
        console.log($scope.data);
    };
}]);
