angular.module('RDash')
    .controller('ProfilesCtrl', ['$scope', '$firebaseObject', '$stateParams',
        function($scope, $firebaseObject, $stateParams) {
            var date;
            var timestampCounter = 0;
            var hours;
            var minutes;
            var seconds;
            var datapoints = 50;
            var firstRun = true;
            var profile = new Firebase("https://watchdog-app.firebaseio.com/Bill/People/" + $stateParams.name);
            var refSync = $firebaseObject(profile);
            $scope.profile = {};
            refSync.$loaded().then(function() {
                refSync.$bindTo($scope, "profile");
                //console.log(refSync.$value);
                //generateHeartrateData(refSync.$value);
            });

            $scope.debug = function() {
                console.log($stateParams);
                console.log($scope.profile);
            };

            $scope.$watch('profile.heartRate', function(newVal, oldVal) {
                if (newVal !== oldVal) {
                    if (firstRun) {
                        generateHeartrateData($scope.profile.heartRate);
                        firstRun = false;
                    } else {
                        updateChart($scope.profile.heartRate);
                    }
                }
            }, true);

            $scope.heartrateOptions = {
                animation: false,
            };
            $scope.heartrateTimes = [];
            $scope.heartrateValues = [
                []
            ];

            var updateChart = function(heartrateObject) {
                var timestampKeys = Object.keys(heartrateObject);
                if ($scope.heartrateTimes.length > datapoints) {
                    $scope.heartrateTimes.splice(0, 1);
                    $scope.heartrateValues[0].splice(0, 1);
                    if ($scope.profile.heartRate[timestampKeys[timestampKeys.length-1]] === -1) {
                        $scope.heartrateValues[0].push(0);
                    } else {
                        $scope.heartrateValues[0].push($scope.profile.heartRate[timestampKeys[timestampKeys.length-1]]);
                    }
                } else {
                    $scope.heartrateValues[0].push($scope.profile.heartRate[timestampKeys[timestampKeys.length - 1]]);
                }
                var date = new Date(timestampKeys[timestampKeys.length-1] * 1000);
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                if(timestampCounter % 5 == 4){
                    $scope.heartrateTimes.push(hours + ":" +  minutes + ":" + seconds);
                    timestampCounter = 0;
                } else {
                    $scope.heartrateTimes.push(' ');
                    timestampCounter++;
                }
            };

            var generateHeartrateData = function(heartrateObject) {
                var timestampKeys = Object.keys(heartrateObject);
                var beginningTimestamp = 0;
                if (timestampKeys.length > datapoints) {
                    beginningTimestamp = timestampKeys.length - datapoints;
                } else {
                    beginningTimestamp = 0;
                }

                for (var i = beginningTimestamp; i < timestampKeys.length - 1; i++) {
                    var date = new Date(timestampKeys[i] * 1000);
                    var hours = date.getHours();
                    var minutes = date.getMinutes();
                    var seconds = date.getSeconds();
                    if(timestampCounter % 5 == 4){
                        $scope.heartrateTimes.push(hours + ":" +  minutes + ":" + seconds);
                        timestampCounter = 0;
                    } else {
                        $scope.heartrateTimes.push(' ');
                        timestampCounter++;
                    }

                    if ($scope.profile.heartRate[timestampKeys[i]] === -1) {
                        $scope.heartrateValues[0].push(0);
                    } else {
                        $scope.heartrateValues[0].push($scope.profile.heartRate[timestampKeys[i]]);
                    }
                }
            };
        }
    ]);
