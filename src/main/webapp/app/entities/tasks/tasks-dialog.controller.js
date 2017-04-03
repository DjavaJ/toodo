(function() {
    'use strict';

    angular
        .module('toodoApp')
        .controller('TasksDialogController', TasksDialogController);

    TasksDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tasks'];

    function TasksDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tasks) {
        var vm = this;

        vm.tasks = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tasks.id !== null) {
                Tasks.update(vm.tasks, onSaveSuccess, onSaveError);
            } else {
                Tasks.save(vm.tasks, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('toodoApp:tasksUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
