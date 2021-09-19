package controllers.api.dto;

public class AuthDataDTO extends WiFreeDTO {

    public String uniqueId;
    public String loginType;
    public LoginTypeOptionsDTO loginTypeOptions;
    public String client_id;

    public AuthDataDTO() {}

    public AuthDataDTO(String uniqueId, String loginType, LoginTypeOptionsDTO loginTypeOptions, String client_id) {
        this.uniqueId = uniqueId;
        this.loginType = loginType;
        this.loginTypeOptions = loginTypeOptions;
        this.client_id = client_id;
    }
}
