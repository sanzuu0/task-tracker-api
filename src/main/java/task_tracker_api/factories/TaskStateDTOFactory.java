package task_tracker_api.factories;

import org.springframework.stereotype.Component;
import task_tracker_api.dto.TaskStateDTO;
import task_tracker_api.store.entities.TaskStateEntity;

@Component
public class TaskStateDTOFactory {

    public TaskStateDTO makeTaskStateDTO(TaskStateEntity taskStateEntity) {

        return TaskStateDTO.builder()
                .id(taskStateEntity.getId())
                .name(taskStateEntity.getName())
                .createdAt(taskStateEntity.getCreatedAt())
                .ordinal(taskStateEntity.getOrdinal())
                .build();
    }

}
