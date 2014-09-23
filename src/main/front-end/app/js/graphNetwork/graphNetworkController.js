(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', ['$scope', '$routeParams', 'TestData',
      function($scope, $routeParams, TestData) {
        // TODO replace when implementing the graph network page
        $scope.entities = TestData.graphNetworkEntities;
      }]);

})(angular);