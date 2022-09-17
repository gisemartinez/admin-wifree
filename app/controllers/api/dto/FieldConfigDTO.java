package controllers.api.dto;

import models.FieldConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FieldConfigDTO extends WiFreeDTO {

    public String key;
    public String label;
    public Integer order;
    public Boolean required;
    public Integer maximum;
    public List<OptionDTO> otherOptions;

    public FieldConfigDTO() {
    }

    public FieldConfigDTO(String key, String label, Integer order, Boolean required, Integer maximum, List<OptionDTO> otherOptions) {
        this.key = key;
        this.label = label;
        this.order = order;
        this.required = required;
        this.maximum = maximum;
        this.otherOptions = otherOptions;
    }

    public static FieldConfigDTO fromDomain(FieldConfig config, String type) {
        if ("rating".equals(type))
            return new FieldConfigDTO(config.getKey(), config.getLabel(), config.getOrder(),
                    config.getRequired(), config.getMaximum(), Collections.singletonList(OptionDTO.fromRating(config)));
        else {
            List<OptionDTO> otherOptions = new ArrayList<>();
            if (config.getOtherOptions() != null) {
                otherOptions = config.getOtherOptions().stream().map(OptionDTO::fromDomain).collect(Collectors.toList());
            }
            return new FieldConfigDTO(config.getKey(), config.getLabel(), config.getOrder(), config.getRequired(), config.getMaximum(), otherOptions);
        }
    }
}
