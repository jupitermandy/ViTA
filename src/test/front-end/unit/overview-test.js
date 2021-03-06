describe('OverviewCtrl', function() {
  var scope, $httpBackend, ctrl;

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, $routeParams, TestData) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('webapi/documents/123/persons').respond(TestData.persons);
    $httpBackend.expectGET('webapi/documents/123').respond(TestData.singleDocument);
    $httpBackend.expectGET('webapi/documents/123/progress').respond(TestData.analysisProgress);

    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('OverviewCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model', inject(function($controller, TestData, Page) {
    expect(Page.document).not.toBeDefined();
    $httpBackend.flush();
    expect(Page.document).toEqualData(TestData.singleDocument);
  }));

  it('should retrieve the analysis status', inject(function(TestData) {
    expect(scope.progress).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.progress).toEqualData(TestData.analysisProgress);
  }));

  it('should load the status repeatedly', inject(function($interval, TestData) {
    // Ensure the current data are different from the changed data
    $httpBackend.flush();
    expect(scope.progress.graphView.isReady).toBe(false);

    // Respond with the changed data
    var changedProgress = angular.copy(TestData.analysisProgress);
    changedProgress.graphView.isReady = true;
    $httpBackend.expectGET('webapi/documents/123/progress').respond(changedProgress);
    // Simulate the expected time interval
    $interval.flush(1000);
    $httpBackend.flush();

    expect(scope.progress.graphView.isReady).toBe(true);
  }));

});
