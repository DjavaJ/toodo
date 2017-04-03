package com.theironyard.novauc.repository;

import com.theironyard.novauc.domain.Tasks;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tasks entity.
 */
@SuppressWarnings("unused")
public interface TasksRepository extends JpaRepository<Tasks,Long> {

}
