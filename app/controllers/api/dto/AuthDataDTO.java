package controllers.api.dto;

import java.util.List;

public class AuthDataDTO extends WiFreeDTO {
    public List<String> loginTypes;
    public List<LoginTypeOptionsDTO> loginTypeOptions;
    public String client_id;

    public AuthDataDTO() {
    }

    public AuthDataDTO(List<String> loginTypes, List<LoginTypeOptionsDTO> loginTypeOptions, String client_id) {
        this.loginTypes = loginTypes;
        this.loginTypeOptions = loginTypeOptions;
        this.client_id = client_id;
    }
}
