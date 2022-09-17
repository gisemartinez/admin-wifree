package controllers.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

abstract class WiFreeDTO {

    private static final ObjectMapper mapper = initializeObjectMapper();

    private static ObjectMapper initializeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    public JsonNode toJson() {
        Json.setObjectMapper(mapper);
        return Json.toJson(this);
    }

}
