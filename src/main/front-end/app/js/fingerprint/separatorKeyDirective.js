(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fingerprintTopRow', ['FingerprintSynchronizer', function(FingerprintSynchronizer) {
    function link(scope) {
      scope.FingerprintSynchronizer = FingerprintSynchronizer;
    }

    return {
      restrict: 'A',
      templateUrl: 'templates/fingerprinttoprow.html',
      link: link
    };
  }]);

})(angular);
