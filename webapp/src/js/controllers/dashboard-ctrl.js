angular.module('RDash').controller('DashboardController', ['$scope', '$firebaseObject', '$location', function($scope, $firebaseObject, $location) {
        $scope.loading = true;
        var ref = new Firebase("https://watchdog-app.firebaseio.com/Bill");
        var refSync = $firebaseObject(ref);
        $scope.user = {};
        refSync.$loaded().then(function() {
            refSync.$bindTo($scope, "user");
            $scope.loading = false;
        });

        $scope.getStyle = function(status) {
            if (status === 0) {
                return {
                    "background-color": "#4CAF50"
                };
            } else if (status == 1) {
                return {
                    "background-color": "#FFA726"
                };
            } else if (status == 2) {
                return {
                    "background-color": "#F44336"
                };
            } else {
                return {
                    "background-color": "white"
                };
            }
        };

        $scope.getNumberOfUsers = function() {
            if ($scope.user && $scope.user.People) {
                var users = Object.keys($scope.user.People);
                return users.length;
            } else {
                return 0;
            }
        };

        $scope.getNumberOfIssues = function(requests){
            var number = 0;
            if(requests){
                for(var person in $scope.user.People){
                    if($scope.user.People[person].status == 1){
                        number++;
                    }
                }
            } else {
                for(var person2 in $scope.user.People){
                    if($scope.user.People[person2].status == 2){
                        number++;
                    }
                }
            }
            return number;
        };

        $scope.debug = function() {
            console.log($scope.user.People.Andrew.sensors);
        };

        $scope.goToPerson = function(name) {
            console.log($scope.user.id + '/profiles/' + name);
            $location.path($scope.user.id + '/profiles/' + name);
        };

        $scope.getMostRecentTime = function(object) {
            var keys = Object.keys(object);
            var maxValue = Math.max.apply(Math, keys);
            return (object[maxValue]);
        };

        //heartrate watch
        $scope.$watch('user.People', function(newVal, oldVal) {
            var newAlertsArray = [];
            for(var person in newVal) {
                var keys = Object.keys(newVal[person].heartRate);
                //console.log(newVal[person].heartRate[keys[keys.length-1]]);
                //console.log(newVal[person].settings.heartrateHighThreshold);
                //console.log(newVal[person].heartRate[keys[keys.length-1]] >= newVal[person].settings.heartrateHighThreshold);
                //console.log(newVal[person].heartRate[keys[keys.length-1]] <= newVal[person].settings.heartrateLowThreshold && newVal[person].heartRate[keys[keys.length-1]] > 1);

                if(newVal[person].heartRate[keys[keys.length-1]] >= newVal[person].settings.heartrateHighThreshold || 
                   (newVal[person].heartRate[keys[keys.length-1]] <= newVal[person].settings.heartrateLowThreshold && newVal[person].heartRate[keys[keys.length-1]] > 1)){
                    console.log('heart rate threshold ' + person);
                    $scope.user.People[person].status = 2;
                    if($scope.user.People[person].alerts){
                        $scope.user.People[person].alerts.heartAlert = person + ' - Heartrate threshold met';
                    } else {
                        $scope.user.People[person].alerts = {};
                        $scope.user.People[person].alerts.heartAlert = person + ' - Heartrate threshold met';
                    }
                }
                //display alerts
                
                if(!$scope.user.People[person].alerts){
                    $scope.user.People[person].alerts = {};
                }

                for (var alert in newVal[person].alerts){
                    console.log(newVal[person].alerts[alert]);
                    newAlertsArray.push(newVal[person].alerts[alert]);
                }

                if(newVal[person].fellDown === true){
                    $scope.user.People[person].status = 2;
                    $scope.user.People[person].alerts.fellDown = person + ' - Fell down';
                }

                if(newVal[person].needsHelp === true){
                    $scope.user.People[person].status = 1;
                    $scope.user.People[person].alerts.helpRequest = person + ' - Request for help';
                }
                
                if(!newVal[person].alerts){
                    $scope.user.People[person].status = 0;
                }
            }
            $scope.alerts = newAlertsArray;
            console.log($scope.alerts);
        }, true);

        $scope.alerts = ['this is an alert'];
    }
]);
