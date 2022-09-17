package models.types;

import io.ebean.annotation.EnumValue;

public enum DayOfTheWeek {

    @EnumValue("SUNDAY") SUNDAY,
    @EnumValue("MONDAY") MONDAY,
    @EnumValue("TUESDAY") TUESDAY,
    @EnumValue("WEDNESDAY") WEDNESDAY,
    @EnumValue("THURSDAY") THURSDAY,
    @EnumValue("FRIDAY") FRIDAY,
    @EnumValue("SATURDAY") SATURDAY,
    @EnumValue("ALL") ALL,
    @EnumValue("NONE") NONE,
    @EnumValue("ERROR") ERROR

}
