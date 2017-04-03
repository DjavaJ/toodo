package com.theironyard.novauc.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.theironyard.novauc.domain.Tasks;
import com.theironyard.novauc.service.TasksService;
import com.theironyard.novauc.web.rest.util.HeaderUtil;
import com.theironyard.novauc.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Tasks.
 */
@RestController
@RequestMapping("/api")
public class TasksResource {

    private final Logger log = LoggerFactory.getLogger(TasksResource.class);

    private static final String ENTITY_NAME = "tasks";
        
    private final TasksService tasksService;

    public TasksResource(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    /**
     * POST  /tasks : Create a new tasks.
     *
     * @param tasks the tasks to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tasks, or with status 400 (Bad Request) if the tasks has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tasks")
    @Timed
    public ResponseEntity<Tasks> createTasks(@RequestBody Tasks tasks) throws URISyntaxException {
        log.debug("REST request to save Tasks : {}", tasks);
        if (tasks.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new tasks cannot already have an ID")).body(null);
        }
        Tasks result = tasksService.save(tasks);
        return ResponseEntity.created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tasks : Updates an existing tasks.
     *
     * @param tasks the tasks to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tasks,
     * or with status 400 (Bad Request) if the tasks is not valid,
     * or with status 500 (Internal Server Error) if the tasks couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tasks")
    @Timed
    public ResponseEntity<Tasks> updateTasks(@RequestBody Tasks tasks) throws URISyntaxException {
        log.debug("REST request to update Tasks : {}", tasks);
        if (tasks.getId() == null) {
            return createTasks(tasks);
        }
        Tasks result = tasksService.save(tasks);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tasks.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tasks : get all the tasks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tasks in body
     */
    @GetMapping("/tasks")
    @Timed
    public ResponseEntity<List<Tasks>> getAllTasks(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Tasks");
        Page<Tasks> page = tasksService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tasks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tasks/:id : get the "id" tasks.
     *
     * @param id the id of the tasks to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tasks, or with status 404 (Not Found)
     */
    @GetMapping("/tasks/{id}")
    @Timed
    public ResponseEntity<Tasks> getTasks(@PathVariable Long id) {
        log.debug("REST request to get Tasks : {}", id);
        Tasks tasks = tasksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tasks));
    }

    /**
     * DELETE  /tasks/:id : delete the "id" tasks.
     *
     * @param id the id of the tasks to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tasks/{id}")
    @Timed
    public ResponseEntity<Void> deleteTasks(@PathVariable Long id) {
        log.debug("REST request to delete Tasks : {}", id);
        tasksService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
