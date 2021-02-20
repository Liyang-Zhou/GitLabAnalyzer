package ca.sfu.orcus.gitlabanalyzer.project;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import ca.sfu.orcus.gitlabanalyzer.member.MemberDTO;
import ca.sfu.orcus.gitlabanalyzer.member.MemberService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthenticationService authService;
    private final MemberService memberService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, AuthenticationService authService, MemberService memberService) {
        this.projectRepository = projectRepository;
        this.authService = authService;
        this.memberService = memberService;
    }

    public List<ProjectDto> getAllProjects(String jwt) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getAllProjects(gitLabApi);
        } else {
            return null;
        }
    }

    public ArrayList<ProjectDto> getAllProjects(GitLabApi gitLabApi) {
        try {
            ArrayList<ProjectDto> projectDtos = new ArrayList<>();
            List<Project> projects = gitLabApi.getProjectApi().getMemberProjects();
            for (Project p: projects) {
                ProjectDto projectDto = new ProjectDto(p);
                projectDtos.add(projectDto);
            }
            return projectDtos;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public ProjectExtendedDto getProject(String jwt, int projectId) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if (gitLabApi != null) {
            return getProject(gitLabApi, projectId);
        } else {
            return null;
        }
    }

    public ProjectExtendedDto getProject(GitLabApi gitLabApi, int projectId) {
        try {
            Project project = gitLabApi.getProjectApi().getProject(projectId, true);
            Long numBranches = (long) gitLabApi.getRepositoryApi().getBranches(projectId).size();
            List<MemberDTO> memberDtos = memberService.getAllMemberDTOs(gitLabApi, projectId);
            return new ProjectExtendedDto(project, memberDtos, numBranches);
        } catch (GitLabApiException e) {
            return null;
        }
    }

}
