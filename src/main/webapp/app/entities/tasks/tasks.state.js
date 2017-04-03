(function() {
    'use strict';

    angular
        .module('toodoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tasks', {
            parent: 'entity',
            url: '/tasks?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'toodoApp.tasks.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tasks/tasks.html',
                    controller: 'TasksController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tasks');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tasks-detail', {
            parent: 'tasks',
            url: '/tasks/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'toodoApp.tasks.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tasks/tasks-detail.html',
                    controller: 'TasksDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tasks');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tasks', function($stateParams, Tasks) {
                    return Tasks.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tasks',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tasks-detail.edit', {
            parent: 'tasks-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasks/tasks-dialog.html',
                    controller: 'TasksDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tasks', function(Tasks) {
                            return Tasks.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tasks.new', {
            parent: 'tasks',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasks/tasks-dialog.html',
                    controller: 'TasksDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                isDone: null,
                                item: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tasks', null, { reload: 'tasks' });
                }, function() {
                    $state.go('tasks');
                });
            }]
        })
        .state('tasks.edit', {
            parent: 'tasks',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasks/tasks-dialog.html',
                    controller: 'TasksDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tasks', function(Tasks) {
                            return Tasks.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tasks', null, { reload: 'tasks' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tasks.delete', {
            parent: 'tasks',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasks/tasks-delete-dialog.html',
                    controller: 'TasksDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tasks', function(Tasks) {
                            return Tasks.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tasks', null, { reload: 'tasks' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
