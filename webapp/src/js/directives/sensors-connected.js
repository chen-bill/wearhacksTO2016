/**
 * Widget Body Directive
 */

angular.module('RDash').directive('sensorsUsed', function() {
    var directive = {
        scope: {
            sensors: '='
        },
        template: '<i ng-show="sensor.on" class="sensorIcon fa {{sensor.class}}" ng-repeat="sensor in sensors"></i>',
        replace: false,
        restrict: 'E',
        link: function(scope, element, attr){
            scope.debug = function(){
                console.log(scope.sensors);
            };
            var template = '';
            var sensorHtml = {
                'heartrate': '<i class="sensorIcon fa fa-heart"><i>',
                'gps': '<i class="sensorIcon fa fa-location-arrow"><i>',
            };
            for (var i = 0; i < scope.sensors.length; i++) {
                template = template + sensorHtml[scope.sensors[i]];
            }
            directive.template = template;
        }
    };
    return directive;
});
