package ca.concordia.ginacody.comp6231.demsha.server.vo;


import ca.concordia.ginacody.comp6231.demsha.common.enums.EventTimeSlot;
import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.regex.Pattern;

public class EventVO implements Serializable {

    /**
     *
     */
    private String id;

    /**
     *
     */
    private EventType eventType;

    /**
     *
     */
    private EventTimeSlot eventTimeSlot;

    /**
     *
     */
    private Date date;

    /**
     *
     */
    private int capacity;


    /**
     *
     */
    private int numberOfAttendees;

    /**
     *
     */
    private int weekIndex;

    /**
     *
     */
    private int dayIndex;

    /**
     *
     * @param id
     * @param eventType
     */
    public EventVO(String id, EventType eventType) {
        this(id, eventType, 0);
    }

    /**
     *
     */
    public EventVO(String id, EventType eventType, int capacity) {
        this.setId(id);
        this.setEventType(eventType);
        this.setCapacity(capacity);
        this.setNumberOfAttendees(0);
        char timeSlot = id.charAt(3);
        this.setEventTimeSlot(EventTimeSlot.get(Character.toString(timeSlot)));
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy");
            this.setDate(simpleDateFormat.parse(id.substring(4)));
        } catch (ParseException e) {
            throw new EventManagementServiceException(e.getMessage());
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(1);
        calendar.setTime(this.getDate());
        this.setWeekIndex(calendar.get(GregorianCalendar.WEEK_OF_YEAR));
        this.setDayIndex(calendar.get(GregorianCalendar.DAY_OF_YEAR));
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @param eventType
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     *
     * @param eventTimeSlot
     */
    public void setEventTimeSlot(EventTimeSlot eventTimeSlot) {
        this.eventTimeSlot = eventTimeSlot;
    }

    /**
     *
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     *
     * @param capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     *
     * @param numberOfAttendees
     */
    public void setNumberOfAttendees(int numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    /**
     *
     * @param weekIndex
     */
    public void setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
    }

    /**
     *
     * @param dayIndex
     */
    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    /**
     *
     * @return
     */
    public int getDayIndex() {
        return dayIndex;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     *
     * @return
     */
    public EventTimeSlot getEventTimeSlot() {
        return eventTimeSlot;
    }

    /**
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     *
     * @return
     */
    public int getNumberOfAttendees() {
        return numberOfAttendees;
    }

    /**
     *
     * @return
     */
    public int getWeekIndex() {
        return weekIndex;
    }

    @Override
    public String toString() {
        return "Event {" + "id='" + id + '\'' + ", eventType=" + eventType + ", capacity=" + capacity + ", numberOfAttendees=" + numberOfAttendees + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVO)) return false;
        EventVO eventVO = (EventVO) o;
        return  getWeekIndex() == eventVO.getWeekIndex() &&
                getDayIndex() == eventVO.getDayIndex() &&
                Objects.equals(getId(), eventVO.getId()) &&
                getEventType() == eventVO.getEventType() &&
                getEventTimeSlot() == eventVO.getEventTimeSlot() &&
                Objects.equals(getDate(), eventVO.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEventType(), getEventTimeSlot(), getDate(), getWeekIndex(), getDayIndex());
    }
}