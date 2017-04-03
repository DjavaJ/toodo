(function() {
    'use strict';

    angular
        .module('toodoApp')
        .controller('TasksDetailController', TasksDetailController);

    TasksDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tasks'];

    function TasksDetailController($scope, $rootScope, $stateParams, previousState, entity, Tasks) {
        var vm = this;

        vm.tasks = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('toodoApp:tasksUpdate', function(event, result) {
            vm.tasks = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
