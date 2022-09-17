package models.types;

import io.ebean.annotation.EnumValue;

/**
 * Created by jesu on 08/06/17.
 */
public enum Gender {

    @EnumValue("Male") Male,
    @EnumValue("Female") Female,
    @EnumValue("Other") Other,
    @EnumValue("Undefined") Undefined,
    @EnumValue("Error") Error

}
