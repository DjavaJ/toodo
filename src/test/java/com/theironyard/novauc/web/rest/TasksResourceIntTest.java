package com.theironyard.novauc.web.rest;

import com.theironyard.novauc.ToodoApp;

import com.theironyard.novauc.domain.Tasks;
import com.theironyard.novauc.repository.TasksRepository;
import com.theironyard.novauc.service.TasksService;
import com.theironyard.novauc.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TasksResource REST controller.
 *
 * @see TasksResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ToodoApp.class)
public class TasksResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DONE = false;
    private static final Boolean UPDATED_IS_DONE = true;

    private static final String DEFAULT_ITEM = "AAAAAAAAAA";
    private static final String UPDATED_ITEM = "BBBBBBBBBB";

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private TasksService tasksService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTasksMockMvc;

    private Tasks tasks;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TasksResource tasksResource = new TasksResource(tasksService);
        this.restTasksMockMvc = MockMvcBuilders.standaloneSetup(tasksResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tasks createEntity(EntityManager em) {
        Tasks tasks = new Tasks()
            .name(DEFAULT_NAME)
            .isDone(DEFAULT_IS_DONE)
            .item(DEFAULT_ITEM);
        return tasks;
    }

    @Before
    public void initTest() {
        tasks = createEntity(em);
    }

    @Test
    @Transactional
    public void createTasks() throws Exception {
        int databaseSizeBeforeCreate = tasksRepository.findAll().size();

        // Create the Tasks
        restTasksMockMvc.perform(post("/api/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tasks)))
            .andExpect(status().isCreated());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate + 1);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTasks.isIsDone()).isEqualTo(DEFAULT_IS_DONE);
        assertThat(testTasks.getItem()).isEqualTo(DEFAULT_ITEM);
    }

    @Test
    @Transactional
    public void createTasksWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tasksRepository.findAll().size();

        // Create the Tasks with an existing ID
        tasks.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTasksMockMvc.perform(post("/api/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tasks)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        // Get all the tasksList
        restTasksMockMvc.perform(get("/api/tasks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tasks.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].isDone").value(hasItem(DEFAULT_IS_DONE.booleanValue())))
            .andExpect(jsonPath("$.[*].item").value(hasItem(DEFAULT_ITEM.toString())));
    }

    @Test
    @Transactional
    public void getTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        // Get the tasks
        restTasksMockMvc.perform(get("/api/tasks/{id}", tasks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tasks.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.isDone").value(DEFAULT_IS_DONE.booleanValue()))
            .andExpect(jsonPath("$.item").value(DEFAULT_ITEM.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTasks() throws Exception {
        // Get the tasks
        restTasksMockMvc.perform(get("/api/tasks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTasks() throws Exception {
        // Initialize the database
        tasksService.save(tasks);

        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();

        // Update the tasks
        Tasks updatedTasks = tasksRepository.findOne(tasks.getId());
        updatedTasks
            .name(UPDATED_NAME)
            .isDone(UPDATED_IS_DONE)
            .item(UPDATED_ITEM);

        restTasksMockMvc.perform(put("/api/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTasks)))
            .andExpect(status().isOk());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTasks.isIsDone()).isEqualTo(UPDATED_IS_DONE);
        assertThat(testTasks.getItem()).isEqualTo(UPDATED_ITEM);
    }

    @Test
    @Transactional
    public void updateNonExistingTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();

        // Create the Tasks

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTasksMockMvc.perform(put("/api/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tasks)))
            .andExpect(status().isCreated());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTasks() throws Exception {
        // Initialize the database
        tasksService.save(tasks);

        int databaseSizeBeforeDelete = tasksRepository.findAll().size();

        // Get the tasks
        restTasksMockMvc.perform(delete("/api/tasks/{id}", tasks.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tasks.class);
    }
}
