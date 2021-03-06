package ca.sfu.orcus.gitlabanalyzer.commit;

import ca.sfu.orcus.gitlabanalyzer.Constants;
import ca.sfu.orcus.gitlabanalyzer.utils.DateUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommitController {
    private final CommitService commitService;
    private static final Gson gson = new Gson();

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/api/project/{projectId}/commits")
    public String getCommits(@CookieValue(value = "sessionId") String jwt,
                             @PathVariable int projectId,
                             @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                             @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                             HttpServletResponse response) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<CommitDto> commits = commitService.getAllCommits(jwt, projectId, dateSince, dateUntil);
        response.setStatus(commits == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(commits);
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}")
    public String getSingleCommit(@CookieValue(value = "sessionId") String jwt,
                                  @PathVariable int projectId,
                                  @PathVariable String sha,
                                  HttpServletResponse response) {
        CommitDto commit = commitService.getSingleCommit(jwt, projectId, sha);
        response.setStatus(commit == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(commit);
    }

    @GetMapping("/api/project/{projectId}/commit/{sha}/diff")
    public String getSingleCommitDiffs(@CookieValue(value = "sessionId") String jwt,
                                       @PathVariable int projectId,
                                       @PathVariable String sha,
                                       HttpServletResponse response) {
        String diffs = commitService.getDiffOfCommit(jwt, projectId, sha);
        response.setStatus(diffs == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(diffs);
    }

    @GetMapping("/api/project/{projectId}/members/{memberName}/commits")
    public String getCommitsByMemberName(@CookieValue(value = "sessionId") String jwt,
                                         HttpServletResponse response,
                                         @PathVariable int projectId,
                                         @RequestParam(required = false, defaultValue = Constants.DEFAULT_SINCE) long since,
                                         @RequestParam(required = false, defaultValue = Constants.DEFAULT_UNTIL) long until,
                                         @PathVariable String memberName) {
        Date dateSince = DateUtils.getDateSinceOrEarliest(since);
        Date dateUntil = DateUtils.getDateUntilOrNow(until);
        List<CommitDto> allCommitsByMemberName = commitService.returnAllCommitsOfAMember(jwt, projectId, dateSince, dateUntil, memberName);
        response.setStatus(allCommitsByMemberName == null ? SC_UNAUTHORIZED : SC_OK);
        return gson.toJson(allCommitsByMemberName);
    }
}
