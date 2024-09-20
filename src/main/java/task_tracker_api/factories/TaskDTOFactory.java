package task_tracker_api.factories;

import org.springframework.stereotype.Component;
import task_tracker_api.dto.TaskDTO;
import task_tracker_api.store.entities.TaskEntity;

@Component
public class TaskDTOFactory {

    public TaskDTO makeTaskDTO(TaskEntity taskEntity) {

        return TaskDTO.builder()
                .id(taskEntity.getId())
                .name(taskEntity.getName())
                .createdAt(taskEntity.getCreatedAt())
                .build();
    }
}
