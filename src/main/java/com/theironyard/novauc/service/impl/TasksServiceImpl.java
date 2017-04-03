package com.theironyard.novauc.service.impl;

import com.theironyard.novauc.service.TasksService;
import com.theironyard.novauc.domain.Tasks;
import com.theironyard.novauc.repository.TasksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Implementation for managing Tasks.
 */
@Service
@Transactional
public class TasksServiceImpl implements TasksService{

    private final Logger log = LoggerFactory.getLogger(TasksServiceImpl.class);
    
    private final TasksRepository tasksRepository;

    public TasksServiceImpl(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    /**
     * Save a tasks.
     *
     * @param tasks the entity to save
     * @return the persisted entity
     */
    @Override
    public Tasks save(Tasks tasks) {
        log.debug("Request to save Tasks : {}", tasks);
        Tasks result = tasksRepository.save(tasks);
        return result;
    }

    /**
     *  Get all the tasks.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Tasks> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        Page<Tasks> result = tasksRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one tasks by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Tasks findOne(Long id) {
        log.debug("Request to get Tasks : {}", id);
        Tasks tasks = tasksRepository.findOne(id);
        return tasks;
    }

    /**
     *  Delete the  tasks by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tasks : {}", id);
        tasksRepository.delete(id);
    }
}
