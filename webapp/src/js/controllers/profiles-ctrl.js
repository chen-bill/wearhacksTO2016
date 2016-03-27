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
                    if ($scope.profile.heartRate[timestampKeys[timestampKeys.length - 1]] === -1) {
                        $scope.heartrateValues[0].push(0);
                    } else {
                        $scope.heartrateValues[0].push($scope.profile.heartRate[timestampKeys[timestampKeys.length - 1]]);
                    }
                } else {
                    $scope.heartrateValues[0].push($scope.profile.heartRate[timestampKeys[timestampKeys.length - 1]]);
                }
                var date = new Date(timestampKeys[timestampKeys.length - 1] * 1000);
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                if (timestampCounter % 5 == 4) {
                    $scope.heartrateTimes.push(hours + ":" + minutes + ":" + seconds);
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
                    if (timestampCounter % 5 == 4) {
                        $scope.heartrateTimes.push(hours + ":" + minutes + ":" + seconds);
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
                    console.log($scope.heartrateValues[0]);
                    console.log($scope.heartrateTimes);
                }
            };

            //----------------------------------------------------------events

            var ref = new Firebase("https://watchdog-app.firebaseio.com/Bill");
            var refSyncUser = $firebaseObject(ref);
            $scope.user = {};
            refSyncUser.$loaded().then(function() {
                refSyncUser.$bindTo($scope, "user");
            });

            $scope.$watch('user.People', function(newVal, oldVal) {
                for (var person in newVal) {
                    var keys = Object.keys(newVal[person].heartRate);
                    console.log(newVal[person].heartRate[keys[keys.length - 1]]);
                    console.log(newVal[person].settings.hearthreateHighThreshold);
                    if (newVal[person].heartRate[keys[keys.length - 1]] >= newVal[person].settings.heartrateHighThreshold ||
                        (newVal[person].heartRate[keys[keys.length - 1]] <= newVal[person].settings.heartrateLowThreshold && newVal[person].heartRate[keys[keys.length - 1]] > 1)) {
                        console.log('heartbeat threshold ' + person);
                        $scope.user.People[person].status = 2;
                    }
                }
            }, true);

            $scope.getMemoTimestamp = function(timestamp) {
                var date = new Date(timestamp);
                var day = date.getDate();
                var monthIndex = date.getMonth();
                var year = date.getFullYear();
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                return ('' + day + '/' + (monthIndex + 1).toString() + ' - ' + hours + ':' + minutes + ':' + seconds);
            };

            $scope.events = [{
                badgeClass: 'info',
                badgeIconClass: 'glyphicon-check',
                title: 'First heading',
                content: 'Some awesome content.'

            }, {
                badgeClass: 'warning',
                badgeIconClass: 'glyphicon-credit-card',
                title: 'Second heading',
                content: 'More awesome content.'
            }];

            //================================ alerts

            $scope.deleteAlert = function(index) {
                console.log(index);
                console.log($scope.profile.alerts[index]);
                console.log($scope.profile.alerts);
                console.log(Object.keys($scope.profile.alerts));
                if (index == 'helpRequest'){
                    console.log('settings needs help to false');
                    $scope.profile.needsHelp = false;
                }
                if (index == 'fellDown'){
                    $scope.profile.fellDown = false;
                }

                delete $scope.profile.alerts[index];
                if (Object.keys($scope.profile.alerts).length === 0) {
                    $scope.profile.status = 0;
                } else if (Object.keys($scope.profile.alerts).indexOf('heartAlert') !== -1) {
                    $scope.profile.status = 2;
                } else if (Object.keys($scope.profile.alerts).indexOf('fellDown') !== -1) {
                    $scope.profile.status = 2;
                } else if (Object.keys($scope.profile.alerts).indexOf('helpRequest') !== -1) {
                    $scope.profile.status = 1;
                }
            };
        }
    ]);
