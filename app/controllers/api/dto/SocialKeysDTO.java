package controllers.api.dto;

import models.types.SocialKeys;

public class SocialKeysDTO extends WiFreeDTO {

    public String provider;
    public String clientId;
    public String secret;

    public SocialKeysDTO() {}

    public SocialKeysDTO(String provider, String clientId, String secret) {
        this.provider = provider;
        this.clientId = clientId;
        this.secret = secret;
    }

    public static SocialKeysDTO fromDomain(SocialKeys socialKeys) {
        return new SocialKeysDTO(socialKeys.getProvider(), socialKeys.getClientId(), socialKeys.getSecret());
    }
}
