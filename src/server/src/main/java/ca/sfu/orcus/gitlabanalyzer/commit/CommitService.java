package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestDto;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommitService {
    private final CommitRepository commitRepository;
    private final GitLabApiWrapper gitLabApiWrapper;

    @Autowired
    public CommitService(CommitRepository commitRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.commitRepository = commitRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    public List<CommitDto> getAllCommits(String jwt, int projectId, Date since, Date until) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        return getAllCommitDtos(gitLabApi, projectId, since, until);
    }

    private List<CommitDto> getAllCommitDtos(GitLabApi gitLabApi, int projectId, Date since, Date until) {
        try {
            String defaultBranch = gitLabApi.getProjectApi().getProject(projectId).getDefaultBranch();
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until);
            List<CommitDto> allCommits = new ArrayList<>();
            for (Commit commit : allGitCommits) {
                CommitDto presentCommit = new CommitDto(gitLabApi, projectId, commit);
                allCommits.add(presentCommit);
            }
            return allCommits;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public List<CommitDto> returnAllCommits(GitLabApi gitLabApi, int projectId, Date since, Date until, String name) {
        if (gitLabApi == null) {
            return null;
        }
        try {
            String defaultBranch = gitLabApi.getProjectApi().getProject(projectId).getDefaultBranch();
            List<Commit> allGitCommits = gitLabApi.getCommitsApi().getCommits(projectId, defaultBranch, since, until);
            List<CommitDto> allCommits = new ArrayList<>();
            for (Commit commit : allGitCommits) {
                if (commit.getAuthorName().equalsIgnoreCase(name)) {
                    CommitDto presentCommit = new CommitDto(gitLabApi, projectId, commit);
                    allCommits.add(presentCommit);
                }
            }
            return allCommits;
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public CommitDto getSingleCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        try {
            Commit gitCommit = gitLabApi.getCommitsApi().getCommit(projectId, sha);
            return new CommitDto(gitLabApi, projectId, gitCommit);
        } catch (GitLabApiException e) {
            return null;
        }
    }

    public String getDiffOfCommit(String jwt, int projectId, String sha) {
        GitLabApi gitLabApi = gitLabApiWrapper.getGitLabApiFor(jwt);
        if (gitLabApi == null) {
            return null;
        }
        CommitDto commitDto = getSingleCommit(jwt, projectId, sha);
        return commitDto.getDiffs();
    }

    public List<MergeRequestDto> getOrphanMergeRequestsByMemberName(GitLabApi gitLabApi, int projectId, Date since, Date until, String memberName) {
        try {
            List<MergeRequestDto> orphanMergeRequestByMemberName = new ArrayList<>();
            List<CommitDto> allCommitsByMemberName = returnAllCommits(gitLabApi, projectId, since, until, memberName);
            for (CommitDto c : allCommitsByMemberName) {
                List<MergeRequest> relatedMergeRequests = gitLabApi.getCommitsApi().getMergeRequests(projectId, c.getSha());
                for (MergeRequest mr : relatedMergeRequests) {
                    if (!c.getAuthor().equalsIgnoreCase(mr.getAuthor().getName()) && !c.getAuthor().equalsIgnoreCase(mr.getAuthor().getUsername())) {
                        orphanMergeRequestByMemberName.add(new MergeRequestDto(gitLabApi, projectId, mr));
                    }
                }
            }
            return orphanMergeRequestByMemberName;

        } catch (GitLabApiException e) {
            return  null;
        }
    }
}
