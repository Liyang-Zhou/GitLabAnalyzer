package ca.sfu.orcus.gitlabanalyzer.analysis.cachedDtos;

import ca.sfu.orcus.gitlabanalyzer.config.ConfigDto;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import ca.sfu.orcus.gitlabanalyzer.mergeRequest.MergeRequestScoreCalculator;
import ca.sfu.orcus.gitlabanalyzer.config.ConfigService;
import ca.sfu.orcus.gitlabanalyzer.file.FileDto;
import org.gitlab4j.api.models.MergeRequest;

import java.util.List;
import java.util.Set;

public final class MergeRequestDtoDb {
    private int mergeRequestId;
    private String title;
    private String author;
    private int userId;
    private String description;
    private long time;
    private String webUrl;
    private boolean isSolo;

    private List<CommitDtoDb> commits;
    private Set<String> committerNames;
    private double sumOfCommitsScore;
    private boolean isIgnored;
    private List<FileDto> files;

    public MergeRequestDtoDb(String jwt,
                             ConfigService configService,
                             MergeRequest mergeRequest,
                             List<CommitDtoDb> commits,
                             Set<String> committerNames,
                             MergeRequest mergeRequestChanges,
                             double sumOfCommitsScore,
                             boolean isSolo) {
        setMergeRequestId(mergeRequest.getIid());
        setTitle(mergeRequest.getTitle());
        setAuthor(mergeRequest.getAuthor().getName());
        setUserId(mergeRequest.getAuthor().getId());
        setDescription(mergeRequest.getDescription());
        setTime(mergeRequest.getMergedAt().getTime());
        setWebUrl(mergeRequest.getWebUrl());
        setSolo(isSolo);

        setCommits(commits);
        setCommitterNames(committerNames);
        setSumOfCommitsScore(sumOfCommitsScore);
        setIgnored(false);

        MergeRequestScoreCalculator scoreCalculator = new MergeRequestScoreCalculator(configService);
        setFiles(scoreCalculator.getMergeRequestScore(jwt, mergeRequestChanges.getChanges()));
    }

    public MergeRequestDtoDb() {

    }

    public MergeRequestDtoDb setMergeRequestId(int mergeRequestId) {
        this.mergeRequestId = mergeRequestId;
        return this;
    }

    public MergeRequestDtoDb setTitle(String title) {
        this.title = title;
        return this;
    }

    public MergeRequestDtoDb setAuthor(String author) {
        this.author = author;
        return this;
    }

    public MergeRequestDtoDb setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public MergeRequestDtoDb setDescription(String description) {
        this.description = description;
        return this;
    }

    public MergeRequestDtoDb setTime(long time) {
        this.time = time;
        return this;
    }

    public MergeRequestDtoDb setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public MergeRequestDtoDb setCommits(List<CommitDtoDb> commits) {
        this.commits = commits;
        return this;
    }

    public MergeRequestDtoDb setCommitterNames(Set<String> committerNames) {
        this.committerNames = committerNames;
        return this;
    }

    public MergeRequestDtoDb setSumOfCommitsScore(double sumOfCommitsScore) {
        this.sumOfCommitsScore = sumOfCommitsScore;
        return this;
    }

    public MergeRequestDtoDb setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
        return this;
    }

    public MergeRequestDtoDb setFiles(List<FileDto> files) {
        this.files = files;
        return this;
    }

    public MergeRequestDtoDb setSolo(boolean isSolo) {
        this.isSolo = isSolo;
        return this;
    }

    public boolean isSolo() {
        return isSolo;
    }

    public int getUserId() {
        return userId;
    }

    public int getMergeRequestId() {
        return mergeRequestId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public long getTime() {
        return time;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<CommitDtoDb> getCommits() {
        return commits;
    }

    public Set<String> getCommitterNames() {
        return committerNames;
    }

    public double getSumOfCommitsScore() {
        return sumOfCommitsScore;
    }

    public boolean isIgnored() {
        return isIgnored;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MergeRequestDtoDb)) {
            return false;
        }

        MergeRequestDtoDb m = (MergeRequestDtoDb) o;

        return (this.mergeRequestId == m.mergeRequestId
                && this.title.equals(m.title)
                && this.author.equals(m.author)
                && this.userId == m.userId
                && this.description.equals(m.description)
                && this.time == m.time
                && this.webUrl.equals(m.webUrl)
                && this.commits.equals(m.commits)
                && this.committerNames.equals(m.committerNames)
                && this.sumOfCommitsScore == m.sumOfCommitsScore
                && this.isIgnored == m.isIgnored
                && this.files.equals(m.files));
    }
}
