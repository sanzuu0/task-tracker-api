package task_tracker_api.controllers;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task_tracker_api.dto.ProjectDTO;
import task_tracker_api.factories.ProjectDTOFactory;
import task_tracker_api.store.repositories.ProjectRepository;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;
    ProjectDTOFactory projectDTOFactory;

    public static final String CREATE_PROJECT = "/api/projects";

    @PostMapping(CREATE_PROJECT)
    public ProjectDTO createProject(@RequestParam String name) throws BadRequestException {

        projectRepository.findByName(name)
                .ifPresent(project -> {
                    try {
                        throw new BadRequestException(String.format("Project \"s%\" already exists", name));
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        throw new BadRequestException(String.format("Project \"s%\" already exists", name));
        //return null;
    }

}
