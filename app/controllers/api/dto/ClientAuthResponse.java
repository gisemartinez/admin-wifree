package controllers.api.dto;

public class ClientAuthResponse extends WiFreeDTO {

    public AuthDataDTO authData;
    public ClientDataDTO clientData;

    public ClientAuthResponse() {}

    public ClientAuthResponse(AuthDataDTO authData, ClientDataDTO clientData) {
        this.authData = authData;
        this.clientData = clientData;
    }

}
