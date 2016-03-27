
console.log("added map");
//Angular App Module and Controller
angular.module('RDash')
.controller('MapController', function ($scope, $firebaseObject) {


	$scope.geocoder = new google.maps.Geocoder;
	$scope.geoLocations = {};
	$scope.styles = {};
	$scope.profilePics = {};
	$scope.reloaded = false;
	$scope.mapStyle = [{"featureType":"all","elementType":"all","stylers":[{"invert_lightness":true},{"saturation":10},{"lightness":30},{"gamma":0.5},{"hue":"#435158"}]}];

	// console.log("Started");
	var ref = new Firebase("https://watchdog-app.firebaseio.com/Bill");
	var refSync = $firebaseObject(ref);
	$scope.data = {};
	$scope.locations = [];
	refSync.$bindTo($scope, "data");
	// console.log("binded data");
	// console.log($scope.data);

    $scope.$watch('data', function(newValue, oldValue) {
    	// if (newValue != oldValue) {
    	// 	console.log("Data changed");
    	// 	console.log($scope.data);
/*    	if (Object.keys(newValue).length != 0) {
		    	if ($scope.reloaded == false) {
			    	// $scope.reloaded = true;
			    	// console.log("location changed");
			    	// // return true;
			    	$scope.updateMap();
			    }
    	}
    	else*/ if (newValue != oldValue) {
    		console.log("data changed");
	    	if ($scope.locationChanged(newValue, oldValue)) {
	    		$scope.updateMap();
	    	}
    	}

    }, true);

    $scope.locationChanged = function(newValue, oldValue) {
   		if (Object.keys(oldValue).length == 0) {
   			// console.log("old value {}");
   			return true;
   		}
   		else if (Object.keys(newValue.People).length > 0) {
			for(var person in newValue.People) {
	        	if (newValue.People[person].location.lat != oldValue.People[person].location.lat || newValue.People[person].location.lng != oldValue.People[person].location.lng)
	        		return true;
			};
   		}
   		return false;
    };

    $scope.updateMap = function() {
    	console.log("update map");
        console.log($scope.data);
		$scope.locations = [];
        for(var person in $scope.data.People) {
        	console.log(person);
		    $scope.locations.push({
		    	name: person,
		    	lat: $scope.data.People[person].location.lat,
		    	lng: $scope.data.People[person].location.lng
		    });

		    // console.log("Attempting geocode");

		    (function(ref) {
		      	$scope.geocoder.geocode({'location': {
			    	lat: parseFloat($scope.data.People[ref].location.lat),
			    	lng: parseFloat($scope.data.People[ref].location.lng)
			    }}, function(results, status) {
				    if (status === google.maps.GeocoderStatus.OK) {
				      if (results[1]) {
				        // map.setZoom(11);
				        // var marker = new google.maps.Marker({
				        //   position: latlng,
				        //   map: map
				        // });
				        // infowindow.setContent(results[1].formatted_address);
				        // infowindow.open(map, marker);
				        // console.log("geocoded" + ref);
				        console.log(results[1].formatted_address);
				        $scope.geoLocations[ref] = results[1].formatted_address;
				        console.log($scope.geoLocations);
				      } /*else {
				        window.alert('No results found');
				      }*/
				    }/* else {
				      window.alert('Geocoder failed due to: ' + status);
				    }*/
				    $scope.$apply();
	  			});

		    })(person);
		};

		console.log($scope.locations);
		console.log("creating map");
		$scope.getProfilePics();
		$scope.getStyles();
		$scope.createMap();

    };

    $scope.getProfilePics = function() {
    	$scope.profilePics = {};
        for(var person in $scope.data.People) {
        	// console.log(person);
		    $scope.profilePics[person] = $scope.data.People[person].profilePic;
		};
    };


    $scope.getStyles = function() {
    	$scope.styles = {};
        for(var person in $scope.data.People) {
        	// console.log(person);
		    $scope.styles[person] = $scope.getStyle($scope.data.People[person].status);
		};
    };

    $scope.debug = function() {
        // console.log($scope.user.People.Andrew.sensors);
        console.log("debug");
        console.log($scope.data);

        for(var person in $scope.data.People) {
        	console.log(person);
		    $scope.locations.push({
		    	name: person,
		    	lat: $scope.data.People[person].location.lat,
		    	lng: $scope.data.People[person].location.lat
		    });
		};

		console.log($scope.locations);


    };

    $scope.getStyle = function(status) {
    	// var status = data.People[names].status;
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

    
    $scope.createMarker = function (info){

		// var image = {
		// 	url: $scope.data.People[info.name].profilePic,
		// 	// This marker is 20 pixels wide by 32 pixels high.
		// 	size: new google.maps.Size(32, 32),
		// 	// The origin for this image is (0, 0).
		// 	origin: new google.maps.Point(0, 0),
		// 	// The anchor for this image is the base of the flagpole at (0, 32).
		// 	anchor: new google.maps.Point(0, 32)
		// };

		// var image = {
		// 	url: 'http://plebeosaur.us/etc/map/bluedot_retina.png',
		// 	null,
		// 	null,
		// 	new google.maps.Point( 8, 8 )
		// 	// new google.maps.Size( 17, 17 )
		// };

		// var image = {
		// 	url: 'http://plebeosaur.us/etc/map/bluedot_retina.png',
		// 	size: new google.maps.Size(17, 17),
		// 	origin: new google.maps.Point(0, 0),
		// 	anchor: new google.maps.Point(8, 8)
		// };	

		var image = new google.maps.MarkerImage(
			'http://cdn.mysitemyway.com/icons-watermarks/flat-circle-white-on-orange/bfa/bfa_map-marker/bfa_map-marker_flat-circle-white-on-orange_512x512.png',
			null, // size
			null, // origin
			new google.maps.Point( 8, 8 ), // anchor (move to center of marker)
			new google.maps.Size( 24, 24 ) // scaled size (required for Retina display icon)
		);	

		// var shape = {
		// 	// coords: [10,1],
		// 	type: 'cirlce'
		// };
		        
        var marker = new google.maps.Marker({
            map: $scope.map,
            position: new google.maps.LatLng(info.lat, info.lng),
            title: info.name,
            // title: "I might be here",
            flat: true,
            optimized: false,
            // icon: $scope.data.People[info.name].profilePic
            icon: image
            // shape: shape
        });
        marker.content = '<div class="infoWindowContent">' + /*info.desc *//*$scope.geoLocations[marker.title]+ */'</div>';
        
        google.maps.event.addListener(marker, 'click', function(){
        	console.log("clicked on");
        	console.log(marker.title);
            $scope.infoWindow.setContent('<h2>' + marker.title + '</h2>' + marker.content);
            $scope.infoWindow.open($scope.map, marker);
        });
        
        $scope.markers.push(marker);
        
    }  
    
    // for (i = 0; i < cities.length; i++){
    //     createMarker(cities[i]);
    // }

    $scope.openInfoWindow = function(e, selectedMarker){
        e.preventDefault();
        google.maps.event.trigger(selectedMarker, 'click');
    }

    $scope.createMap = function() {

	   $scope.mapOptions = {
	        zoom: 4,
	        center: new google.maps.LatLng(40.0000, -98.0000),
	        mapTypeId: google.maps.MapTypeId.TERRAIN,
	        styles: $scope.mapStyle,
	        mapTypeId: google.maps.MapTypeId.ROADMAP,
	        mapTypeControl: false
	    }

	    $scope.map = new google.maps.Map(document.getElementById('map'), $scope.mapOptions);
	    $scope.markers = [];
	    
	    $scope.infoWindow = new google.maps.InfoWindow();

	    // for (i = 0; i < $scope.cities.length; i++){
	    //     $scope.createMarker($scope.cities[i]);
	    // }

	    for (i = 0; i < $scope.locations.length; i++){
	        $scope.createMarker($scope.locations[i]);
	        // console.log("pushed" + $scope.locations[i].name);
	    }

		var bounds = new google.maps.LatLngBounds();
		for (var i = 0; i < $scope.markers.length; i++) {
		 bounds.extend($scope.markers[i].getPosition());
		}

		$scope.map.fitBounds(bounds);
    };

    $scope.createMap();
    $scope.updateMap();
    // $route.reload();
    if ($scope.reloaded = false) {
    	$scope.reloaded = true;
    	console.log("reloaded");
    	location.reload();
    }


    setTimeout($scope.updateMap, 1000);

});
