package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.authentication.AuthenticationService;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommitService {
    private final static String defaultBranch = "master";
    private final CommitRepository commitRepository;
    private final AuthenticationService authService;

    @Autowired
    public CommitService(CommitRepository commitRepository, AuthenticationService authService) {
        this.commitRepository = commitRepository;
        this.authService = authService;
    }

    public ArrayList<CommitDto> getAllCommits(String jwt, int projectID, Date since, Date until) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            return getAllCommitDTOs(gitLabApi, projectID, since, until);
        } else {
            return null;
        }
    }

    private ArrayList<CommitDto> getAllCommitDTOs(GitLabApi gitLabApi, int projectID, Date since, Date until) {
        try {
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectID, defaultBranch, since, until);
            ArrayList<CommitDto> allCommits = new ArrayList<>();
            for(Commit commit : allGitCommits) {
                CommitDto presentCommit = new CommitDto(gitLabApi, projectID, commit);
                allCommits.add(presentCommit);
            }
            return allCommits;
        } catch(GitLabApiException e) {
            return null;
        }
    }

    public CommitDto getSingleCommit(String jwt, int projectID, String sha) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            try {
                Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectID, sha);
                return new CommitDto(gitLabApi, projectID, gitCommit);
            } catch (GitLabApiException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Diff> getDiffOfCommit(String jwt, int projectID, String sha) {
        GitLabApi gitLabApi = authService.getGitLabApiFor(jwt);
        if(gitLabApi != null) {
            try {
                return gitLabApi.getCommitsApi().getDiff(projectID, sha);
            } catch (GitLabApiException e) {
                return null;
            }
        } else {
            return null;
        }
    }

}
