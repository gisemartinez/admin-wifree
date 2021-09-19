package controllers.api.dto;

public class ClientDataDTO extends WiFreeDTO {

    public String clientId;
    public String name;

    public ClientDataDTO() {}

    public ClientDataDTO(String clientId, String name) {
        this.clientId = clientId;
        this.name = name;
    }
}
