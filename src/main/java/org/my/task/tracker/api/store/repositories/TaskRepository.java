package org.my.task.tracker.api.store.repositories;

import org.my.task.tracker.api.store.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
