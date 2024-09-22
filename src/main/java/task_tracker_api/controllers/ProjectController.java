package task_tracker_api.controllers;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;

import org.springframework.web.bind.annotation.*;
import task_tracker_api.dto.ProjectDTO;
import task_tracker_api.exceptions.NotFoundException;
import task_tracker_api.factories.ProjectDTOFactory;
import task_tracker_api.store.entities.ProjectEntity;
import task_tracker_api.store.repositories.ProjectRepository;

import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;
    ProjectDTOFactory projectDTOFactory;

    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";

    @PostMapping(CREATE_PROJECT)
    public ProjectDTO createProject(@RequestParam String name) throws BadRequestException {

        if (name.trim().isEmpty()) {
            throw new BadRequestException(String.format("Project \"s%\" does not exist", name));
        }

        projectRepository.findByName(name)
                .ifPresent(project -> {
                    try {
                        throw new BadRequestException(String.format("Project \"s%\" already exists", name));
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        ProjectEntity projectEntity = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                        .build()
        );

        return projectDTOFactory.makeProjectDTO(projectEntity);
    }


    @PatchMapping(EDIT_PROJECT)
    public ProjectDTO editProject(@PathVariable("project_id") Long projectId, @RequestParam String name) throws BadRequestException, NotFoundException {

        if (name.trim().isEmpty()) {
            throw new BadRequestException(String.format("Project \"s%\" does not exist", name));
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Project \"s%\" not found", projectId))
                );

        projectRepository.findById(projectId)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    try {
                        throw new BadRequestException(String.format("Project \"s%\" already exists", name));
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                });

        projectEntity.setName(name);
        projectEntity = projectRepository.saveAndFlush(projectEntity);

        return projectDTOFactory.makeProjectDTO(projectEntity);
    }

}
