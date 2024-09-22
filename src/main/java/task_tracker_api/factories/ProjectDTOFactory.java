package task_tracker_api.factories;

import org.springframework.stereotype.Component;
import task_tracker_api.dto.ProjectDTO;
import task_tracker_api.store.entities.ProjectEntity;

@Component
public class ProjectDTOFactory {

    public ProjectDTO makeProjectDTO(ProjectEntity projectEntity) {

        return ProjectDTO.builder()
                .id(projectEntity.getId())
                .name(projectEntity.getName())
                .createdAt(projectEntity.getCreatedAt())
                .build();
    }
}
