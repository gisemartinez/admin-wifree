package controllers.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import models.Portal;
import models.PortalLoginConfiguration;
import models.types.LoginMethodType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static models.types.LoginMethodType.*;

public class SocialLoginPortalConfigDTO extends WiFreeDTO {

    @JsonProperty("login-type")
    public final String loginType;
    @JsonProperty("social-login-keys")
    public final SocialLoginKeysDTO socialLoginKeys;

    private final String defaultKeyValue = null;

    public static JsonNode json(Portal model) {
        return new SocialLoginPortalConfigDTO(model).toJson();
    }

    private SocialLoginPortalConfigDTO(Portal portal) {
        Set<PortalLoginConfiguration> socialLoginConfigurations = portal.getLoginConfigurations();
        loginType = "social-login";
        HashMap<LoginMethodType, String> map = new HashMap<>();
        addSocialLoginMethod(socialLoginConfigurations, map, Facebook, Google, Twitter);
        socialLoginKeys = new SocialLoginKeysDTO(
                getKey(map, Google),
                getKey(map, Facebook),
                getKey(map, Twitter)
        );
    }

    private String getKey(HashMap<LoginMethodType, String> map, LoginMethodType loginMethodType) {
        return map.getOrDefault(loginMethodType, defaultKeyValue);
    }

    private void addSocialLoginMethod(Set<PortalLoginConfiguration> socialLoginConfigurations,
                                      HashMap<LoginMethodType, String> map,
                                      LoginMethodType... loginMethods) {
        socialLoginConfigurations.stream()
                .filter(p -> Arrays.asList(loginMethods).contains(p.getLoginMethod()))
                .forEach(p -> map.put(p.getLoginMethod(), "TODO")); // TODO
    }

    public static class SocialLoginKeysDTO extends WiFreeDTO {
        public final String google;
        public final String facebook;
        public final String twitter;

        SocialLoginKeysDTO(String google, String facebook, String twitter) {
            this.google = google;
            this.facebook = facebook;
            this.twitter = twitter;
        }
    }
}
