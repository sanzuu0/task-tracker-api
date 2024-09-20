package org.my.task.tracker.api.store.repositories;

import org.my.task.tracker.api.store.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
