package com.theironyard.novauc.service;

import com.theironyard.novauc.domain.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Tasks.
 */
public interface TasksService {

    /**
     * Save a tasks.
     *
     * @param tasks the entity to save
     * @return the persisted entity
     */
    Tasks save(Tasks tasks);

    /**
     *  Get all the tasks.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Tasks> findAll(Pageable pageable);

    /**
     *  Get the "id" tasks.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Tasks findOne(Long id);

    /**
     *  Delete the "id" tasks.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
