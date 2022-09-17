package models;

import io.ebean.annotation.DbArray;
import models.types.DayOfTheWeek;
import models.types.Gender;
import models.types.TimePeriodType;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class AnalyticsQueryFilter extends BaseModel {

    @ManyToOne(optional = false)
    private Portal portal;

    @NotNull
    private String name;

    private String description;

    private TimePeriodType timePeriodType;    // RELATIVE, ABSOLUTE

    private String timePeriod;                // LAST_DAY, LAST_WEEK... OR 2018-06-12T00:08:30Z 201N8-06-15T20:08:30Z

    private Gender gender;                    // enum values

    private String ageRange;                // n-N

    private String visitsAmount;            // n-N

    private String visitorAddress;            // anything

    @DbArray
    private List<DayOfTheWeek> daysOfTheWeek;    // MONDAY, TUESDAY... comma separated

    private String hours;                    // 18:30-21:30


    public AnalyticsQueryFilter() {
    }

    public TimePeriodType getTimePeriodType() {
        return timePeriodType;
    }

    public void setTimePeriodType(TimePeriodType timePeriodType) {
        this.timePeriodType = timePeriodType;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getVisitsAmount() {
        return visitsAmount;
    }

    public void setVisitsAmount(String visitsAmount) {
        this.visitsAmount = visitsAmount;
    }

    public String getVisitorAddress() {
        return visitorAddress;
    }

    public void setVisitorAddress(String visitorAddress) {
        this.visitorAddress = visitorAddress;
    }

    public List<DayOfTheWeek> getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    public void setDaysOfTheWeek(List<DayOfTheWeek> daysOfTheWeek) {
        this.daysOfTheWeek = daysOfTheWeek;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toLogString() {
        return null;
    }

}
