package ca.sfu.orcus.gitlabanalyzer.project;

import org.gitlab4j.api.models.Project;

public class ProjectDto {
    private Integer id;
    private String name;
    private String role;
    private Long lastActivityAt;
    private Boolean analyzed;

    public ProjectDto(Project project) {
        setId(project.getId());
        setName(project.getName());
        setRole("TODO");
        setLastActivityAt(project.getLastActivityAt().getTime());
        setAnalyzed(false); // TODO
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLastActivityAt(Long lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public void setAnalyzed(Boolean analyzed) {
        this.analyzed = analyzed;
    }
}