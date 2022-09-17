package models.types;

import io.ebean.annotation.EnumValue;

public enum TimePeriodType {

    @EnumValue("ABSOLUTE") ABSOLUTE,
    @EnumValue("RELATIVE") RELATIVE,
    @EnumValue("NONE") NONE,
    @EnumValue("ERROR") ERROR

}
