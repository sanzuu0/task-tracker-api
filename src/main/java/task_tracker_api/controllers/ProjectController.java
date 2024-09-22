package task_tracker_api.controllers;


import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;

import org.springframework.web.bind.annotation.*;
import task_tracker_api.dto.AckDto;
import task_tracker_api.dto.ProjectDTO;
import task_tracker_api.exceptions.NotFoundException;
import task_tracker_api.factories.ProjectDTOFactory;
import task_tracker_api.store.entities.ProjectEntity;
import task_tracker_api.store.repositories.ProjectRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;
    ProjectDTOFactory projectDTOFactory;

    public static final String FETCH_PROJECT = "api/projects";
    public static final String CREATE_OR_UPDATE_PROJECT = "api/projects";
    public static final String DELETE_PROJECT = "api/projects{project_id}";


    @GetMapping(FETCH_PROJECT)
    public List<ProjectDTO> fetchProject(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectEntityStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectEntityStream.map(projectDTOFactory::makeProjectDTO)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDTO createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName) throws BadRequestException {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();

        ProjectEntity projectEntity = optionalProjectId
                .map(this::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        if (isCreate && optionalProjectName.isPresent()) {
            throw new BadRequestException("Project name can't be empty");
        }

        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectEntity.getId()))
                            .ifPresent(anotherProject -> {
                                try {
                                    throw new BadRequestException(String.format("Project \"s%\" already exists", projectName));
                                } catch (BadRequestException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    projectEntity.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(projectEntity);

        return projectDTOFactory.makeProjectDTO(savedProject);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AckDto deleteProject(@PathVariable("project_id") Long projectId) {

        getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);

    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Project \"s%\" not found", projectId))
                );
    }

}
