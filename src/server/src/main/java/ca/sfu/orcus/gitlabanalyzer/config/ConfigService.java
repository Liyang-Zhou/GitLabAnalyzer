package ca.sfu.orcus.gitlabanalyzer.config;

import ca.sfu.orcus.gitlabanalyzer.authentication.GitLabApiWrapper;
import com.google.gson.Gson;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;
    private final GitLabApiWrapper gitLabApiWrapper;
    private static final Gson gson = new Gson();

    @Autowired
    public ConfigService(ConfigRepository configRepository, GitLabApiWrapper gitLabApiWrapper) {
        this.configRepository = configRepository;
        this.gitLabApiWrapper = gitLabApiWrapper;
    }

    String addNewConfig(String jwt, ConfigDto configDto) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        String generatedConfigId = configRepository.addNewConfig(configDto);
        configRepository.addConfigToUserProfile(userId, generatedConfigId);
        return generatedConfigId;
    }

    void deleteConfig(String jwt, String configId) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        configRepository.deleteConfigForUser(userId, configId);

        if (configRepository.getNumSubscribersOfConfig(configId) == 0) {
            configRepository.deleteConfig(configId);
        }
    }

    String getConfigJsonForCurrentUser(String jwt, String configId) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        if (configRepository.userHasConfig(userId, configId)) {
            Optional<String> configJson = configRepository.getConfigJsonById(configId);
            return configJson.orElse("");
        }

        return "";
    }

    String getAllConfigJsonsForCurrentUser(String jwt) throws GitLabApiException {
        int userId = gitLabApiWrapper.getGitLabUserIdFromJwt(jwt);

        List<ConfigDto> configDtos = new ArrayList<>();
        List<String> configIds = configRepository.getAllConfigIdsForCurrentUser(userId).orElse(new ArrayList<>());
        for (String id : configIds) {
            Optional<ConfigDto> configDto = configRepository.getConfigDtoById(id);
            configDto.ifPresent(configDtos::add);
        }

        return gson.toJson(configDtos);
    }
}
