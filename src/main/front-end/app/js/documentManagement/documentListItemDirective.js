(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentListItem', ['Analysis', 'Document',
      function(Analysis, Document) {

    var directive = {
      restrict: 'A',
      scope: {
        document: '='
      },
      link: function(scope, element, attrs) {
        var document = scope.document;

        scope.deleteDocument = function() {
          var confirmed = confirm('Delete document "' + document.metadata.title + '" ?');
          if (confirmed) {
            Document.remove({documentId: document.id});
          }
        };

        scope.restartAnalysis = function() {
          var confirmed = confirm('Restart the analysis of "' + document.metadata.title + '" ?');
          if (confirmed) {
            Analysis.restart(document.id);
          }
        };

        scope.stopAnalysis = function() {
          var confirmed = confirm('Stop the analysis of "' + document.metadata.title + '" ?');
          if (confirmed) {
            Analysis.stop(document.id);
          }
        };

        setStatusIconAndDescription(scope);
        setOperationIconAndDescription(scope);
      },
      templateUrl: 'templates/documentlistitem.html'
    };

    function setStatusIconAndDescription(scope) {
      switch (scope.document.progress.status) {
      case 'cancelled':
        scope.statusIconClass = 'glyphicon-remove-circle';
        scope.statusDescription = 'Analysis was cancelled';
        break;
      case 'failed':
        scope.statusIconClass = 'glyphicon-exclamation-sign';
        scope.statusDescription = 'Analysis has failed';
        break;
      case 'running':
        scope.statusIconClass = 'glyphicon-play-circle';
        scope.statusDescription = 'Analysis is running';
        break;
      case 'scheduled':
        scope.statusIconClass = 'glyphicon-time';
        scope.statusDescription = 'Analysis is scheduled';
        break;
      case 'success':
        scope.statusIconClass = 'glyphicon-ok-circle';
        scope.statusDescription = 'Analysis successed';
        break;
      }
    }

    function setOperationIconAndDescription(scope) {
      switch (scope.document.progress.status) {
      case 'cancelled':
      case 'failed':
        scope.operationIconClass = 'glyphicon-repeat';
        scope.operationDescription = 'Repeat analysis';
        scope.operation = scope.restartAnalysis;
        break;
      case 'running':
      case 'scheduled':
        scope.operationIconClass = 'glyphicon-ban-circle';
        scope.operationDescription = 'Stop analysis';
        scope.operation = scope.stopAnalysis;
        break;
      case 'success':
        // no operation
        break;
      }
    }

    return directive;
  }]);

})(angular);
