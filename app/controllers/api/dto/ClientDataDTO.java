package controllers.api.dto;

public class ClientDataDTO extends WiFreeDTO {

    public String clientId;
    public String name;
    public String description;

    public ClientDataDTO() {
    }

    public ClientDataDTO(String clientId, String name, String description) {
        this.clientId = clientId;
        this.name = name;
        this.description = description;
    }
}
