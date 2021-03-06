package ca.sfu.orcus.gitlabanalyzer.mergeRequest;

import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Participant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeRequestDto {
    private int mergeRequestId;
    private String title;
    private boolean hasConflicts;
    private boolean isOpen;
    private int userId;
    private String assignedTo;
    private String author;
    private String description;
    private String sourceBranch;
    private String targetBranch;
    private List<String> committers;
    private List<Participant> participants;
    private long time;
    boolean isIgnored;
    private List<FileDto> files;
    private double sumOfCommitsScore;
    private List<MergeRequestCommitsDto> commitsInfoInMergeRequest = new ArrayList<>();
    private String webUrl;

    public MergeRequestDto(GitLabApi gitLabApi, int projectId, MergeRequest presentMergeRequest, List<FileDto> fileScores, double sumOfCommitsScore, List<MergeRequestCommitsDto> mrCommitsInfo) throws GitLabApiException {
        int mergeRequestId = presentMergeRequest.getIid();

        setMergeRequestId(mergeRequestId);
        setMergeRequestTitle(presentMergeRequest.getTitle());
        setHasConflicts(presentMergeRequest.getHasConflicts());
        setOpen(presentMergeRequest.getState().toLowerCase().compareTo("opened") == 0);
        setUserId(presentMergeRequest.getAuthor().getId());

        if (presentMergeRequest.getAssignee() == null) {
            setAssignedTo("Unassigned");
        } else {
            setAssignedTo(presentMergeRequest.getAssignee().getName());
        }

        setAuthor(presentMergeRequest.getAuthor().getName());
        setDescription(presentMergeRequest.getDescription());
        setSourceBranch(presentMergeRequest.getSourceBranch());
        setTargetBranch(presentMergeRequest.getTargetBranch());

        List<Commit> commits = gitLabApi.getMergeRequestApi().getCommits(projectId, mergeRequestId);
        setCommitters(commits);
        setSumOfCommitsScore(sumOfCommitsScore);

        setCommitsInfoInMergeRequest(mrCommitsInfo);

        setParticipants(gitLabApi.getMergeRequestApi().getParticipants(projectId, mergeRequestId));
        setTime(presentMergeRequest.getMergedAt().getTime());

        setWebUrl(presentMergeRequest.getWebUrl());
        setFiles(fileScores);
        setIgnored(false);
    }

    public void setMergeRequestId(int mergeRequestId) {
        this.mergeRequestId = mergeRequestId;
    }

    public void setMergeRequestTitle(String title) {
        this.title = title;
    }

    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }

    public void setSumOfCommitsScore(double sumOfCommitsScore) {
        this.sumOfCommitsScore = sumOfCommitsScore;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public void setCommitters(List<Commit> commits) {
        Set<String> commitAuthorsSet = new HashSet<>();
        for (Commit c : commits) {
            commitAuthorsSet.add(c.getAuthorName());
        }
        committers = new ArrayList<>(commitAuthorsSet);
    }

    public void setCommitsInfoInMergeRequest(List<MergeRequestCommitsDto> commitsInfoInMergeRequest) {
        this.commitsInfoInMergeRequest = commitsInfoInMergeRequest;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MergeRequestDto)) {
            return false;
        }

        MergeRequestDto m = (MergeRequestDto) o;

        return (this.mergeRequestId == (m.mergeRequestId)
                && this.title.equals(m.title)
                && this.hasConflicts == (m.hasConflicts)
                && this.isOpen == (m.isOpen)
                && this.userId == (m.userId)
                && this.assignedTo.equals(m.assignedTo)
                && this.author.equals(m.author)
                && this.description.equals(m.description)
                && this.sourceBranch.equals(m.sourceBranch)
                && this.targetBranch.equals(m.targetBranch)
                && this.sumOfCommitsScore == (m.sumOfCommitsScore)
                && this.committers.equals(m.committers)
                && this.participants.equals(m.participants)
                && this.time == (m.time)
                && this.files.equals(m.files)
                && this.commitsInfoInMergeRequest.equals(m.commitsInfoInMergeRequest)
                && this.isIgnored == m.isIgnored);
    }
}
