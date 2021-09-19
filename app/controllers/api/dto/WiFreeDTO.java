package controllers.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

abstract class WiFreeDTO {

    public JsonNode toJson() {
        return Json.toJson(this);
    }

}
