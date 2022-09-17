package controllers.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import models.NetworkUser;

// TODO
public class NetworkUserDTO extends WiFreeDTO {

    public final String email;
    public final String gender;

    private NetworkUserDTO(NetworkUser networkUser) {
        this.email = networkUser.getEmail();
        this.gender = networkUser.getGender().toString();
    }

    public static JsonNode json(NetworkUser model) {
        return new NetworkUserDTO(model).toJson();
    }

}
