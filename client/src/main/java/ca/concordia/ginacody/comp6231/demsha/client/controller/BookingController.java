package ca.concordia.ginacody.comp6231.demsha.client.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ca.concordia.ginacody.comp6231.demsha.client.cache.Session;
import ca.concordia.ginacody.comp6231.demsha.client.cache.UserType;
import ca.concordia.ginacody.comp6231.demsha.client.corba.EventManagementServiceCorbaBean;
import ca.concordia.ginacody.comp6231.demsha.client.rmi.EventManagementServiceFactoryBean;
import ca.concordia.ginacody.comp6231.demsha.client.shell.ShellHelper;
import ca.concordia.ginacody.comp6231.demsha.common.enums.EventTimeSlot;
import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;

@ShellComponent
public class BookingController {

    /**
     *
     */
    private static Logger log = LoggerFactory.getLogger(BookingController.class);

    /**
     *
     */
    private ObjectProvider<EventManagementServiceFactoryBean> eventManagementServiceFactoryBeanProvider;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private Session session;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    EventManagementServiceCorbaBean eventManagementServiceCorbaBean;

    @Autowired
    public void UsingMyPrototype(ObjectProvider<EventManagementServiceFactoryBean> eventManagementServiceFactoryBeanProvider) {
        this.eventManagementServiceFactoryBeanProvider = eventManagementServiceFactoryBeanProvider;
    }

    @Value("${dems.pattern.username}")
    private String usernamePattern;

    @Value("${dems.pattern.eventid}")
    private String eventIDPattern;

    @Value("${dems.pattern.eventdate}")
    private String eventDatePattern;


    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @return
     */
    @ShellMethod("Book Event")
    public String bookEvent(@ShellOption(value = {"-customerid"}) String customerID,
                            @ShellOption(value = {"-eventid"}) String eventID,
                            @ShellOption(value = {"-type"}) String eventType
    ) {

        log.debug("inside bookEvent, customerID {}, eventID {}, eventType {}", customerID, eventID, eventType);
        log.debug("checking if there is a logged in user {}", session.isActive());
        if (!session.isActive()) {
            return shellHelper.getErrorMessage("No Logged in user, Please login");
        }

        log.debug("checking if session user {} is a Manager or a Customer", session.getUserName());
        if (!UserType.EVENT_MANAGER.equals(session.getUserType()) && !UserType.CUSTOMER.equals(session.getUserType())) {
            String msg = String.format("User %s is not authorized to perform this action", session.getUserName());
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s] eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID to be in valid format {} {}", usernamePattern, customerID);
        Pattern pattern = Pattern.compile(usernamePattern);
        if (!pattern.matcher(customerID).matches()) {
            String msg = "Invalid customerID";
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID is not a manager {}", customerID);
        if (UserType.EVENT_MANAGER.equals(UserType.get(Character.toString(customerID.charAt(3))))) {
            String msg = "Invalid customerID, Manager cannot book or cancel events for themselves";
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking eventID to be in valid format {} {}", eventIDPattern, eventID);
        pattern = Pattern.compile(eventIDPattern);
        if (!pattern.matcher(eventID).matches()) {
            String msg = "Invalid eventID";
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s],  error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking event type {}", eventType);
        if (EventType.get(eventType) == null) {
            String msg = String.format("Invalid event type %s", eventType);
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s],  error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String dateStr = eventID.substring(4);
        log.debug("checking event Date {}", dateStr);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(eventDatePattern);
            simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            String msg = String.format("Invalid event Date %s", dateStr);
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        char timeSlot = eventID.charAt(3);
        log.debug("checking event Time Slot {}", timeSlot);
        if (EventTimeSlot.get(Character.toString(timeSlot)) == null) {
            String msg = String.format("Invalid Event time slot %s", timeSlot);
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String result = null;
        try {
            dems.EventManagementServiceImpl eventManagementServiceImpl = eventManagementServiceCorbaBean.locateObject("frontend");
            result = shellHelper.getSuccessMessage(eventManagementServiceImpl.bookEvent(customerID, eventID, eventType));

        } catch (EventManagementServiceException e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }  catch (BeansException e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (Exception e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }
        session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], eventID [%s], eventType [%s], result [%s]", customerID, eventID, eventType, result));
        return result;
    }

    /**
     *
     * @param customerID
     * @return
     */
    @ShellMethod("List Bookings")
    public String listBookings(@ShellOption(value = {"-customerid"}) String customerID) {

        log.debug("inside listBookings, customerID {}", customerID);
        log.debug("checking if there is a logged in user {}", session.isActive());
        if (!session.isActive()) {
            return shellHelper.getErrorMessage("No Logged in user, Please login");
        }

        log.debug("checking if session user {} is a Manager or a Customer", session.getUserName());
        if (!UserType.EVENT_MANAGER.equals(session.getUserType()) && !UserType.CUSTOMER.equals(session.getUserType())) {
            String msg = String.format("User %s is not authorized to perform this action", session.getUserName());
            session.getUserActivityLogger().log(String.format("action [listBookings], param customerID [%s], error [%s]", customerID, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID to be in valid format {} {}", usernamePattern, customerID);
        Pattern pattern = Pattern.compile(usernamePattern);
        if (!pattern.matcher(customerID).matches()) {
            String msg = "Invalid customerID";
            session.getUserActivityLogger().log(String.format("action [listBookings], param customerID [%s], error [%s]", customerID, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID is not a manager {}", customerID);
        if (UserType.EVENT_MANAGER.equals(UserType.get(Character.toString(customerID.charAt(3))))) {
            String msg = "Invalid customerID, Manager cannot book an event";
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], error [%s]", customerID,  msg));
            return shellHelper.getErrorMessage(msg);
        }

        String result = null;
        try {
            dems.EventManagementServiceImpl eventManagementServiceImpl = eventManagementServiceCorbaBean.locateObject("frontend");
            result = shellHelper.getSuccessMessage(eventManagementServiceImpl.getBookingSchedule(customerID));
        } catch (EventManagementServiceException e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], error [%s]", customerID, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (BeansException e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], error [%s]", customerID, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (Exception e) {
            session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], error [%s]", customerID, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }
        session.getUserActivityLogger().log(String.format("action [bookEvent], param customerID [%s], result [%s]", customerID, result));
        return result;
    }

    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @return
     */
    @ShellMethod("Cancel Event")
    public String cancelEvent(@ShellOption(value = {"-customerid"}) String customerID,
                            @ShellOption(value = {"-eventid"}) String eventID,
                            @ShellOption(value = {"-type"}) String eventType
    ) {

        log.debug("inside cancelEvent, customerID {}, eventID {}, eventType {}", customerID, eventID, eventType);
        log.debug("checking if there is a logged in user {}", session.isActive());
        if (!session.isActive()) {
            return shellHelper.getErrorMessage("No Logged in user, Please login");
        }

        log.debug("checking if session user {} is a Manager or a Customer", session.getUserName());
        if (!UserType.EVENT_MANAGER.equals(session.getUserType()) && !UserType.CUSTOMER.equals(session.getUserType())) {
            String msg = String.format("User %s is not authorized to perform this action", session.getUserName());
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s] eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID to be in valid format {} {}", usernamePattern, customerID);
        Pattern pattern = Pattern.compile(usernamePattern);
        if (!pattern.matcher(customerID).matches()) {
            String msg = "Invalid customerID";
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID is not a manager {}", customerID);
        if (UserType.EVENT_MANAGER.equals(UserType.get(Character.toString(customerID.charAt(3))))) {
            String msg = "Invalid customerID, Manager cannot book or cancel events for themselves";
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking eventID to be in valid format {} {}", eventIDPattern, eventID);
        pattern = Pattern.compile(eventIDPattern);
        if (!pattern.matcher(eventID).matches()) {
            String msg = "Invalid eventID";
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s],  error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking event type {}", eventType);
        if (EventType.get(eventType) == null) {
            String msg = String.format("Invalid event type %s", eventType);
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s],  error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String dateStr = eventID.substring(4);
        log.debug("checking event Date {}", dateStr);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(eventDatePattern);
            simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            String msg = String.format("Invalid event Date %s", dateStr);
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        char timeSlot = eventID.charAt(3);
        log.debug("checking event Time Slot {}", timeSlot);
        if (EventTimeSlot.get(Character.toString(timeSlot)) == null) {
            String msg = String.format("Invalid Event time slot %s", timeSlot);
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String result = null;
        try {
            dems.EventManagementServiceImpl eventManagementServiceImpl = eventManagementServiceCorbaBean.locateObject("frontend");
            result = shellHelper.getSuccessMessage(eventManagementServiceImpl.cancelEvent(customerID, eventID, eventType));
        } catch (EventManagementServiceException e) {
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (BeansException e) {
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (Exception e) {
            session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], error [%s]", customerID, eventID, eventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }
        session.getUserActivityLogger().log(String.format("action [cancelEvent], param customerID [%s], eventID [%s], eventType [%s], result [%s]", customerID, eventID, eventType, result));
        return result;
    }


    /**
     *
     * @param customerID
     * @param eventID
     * @param eventType
     * @param oldEventID
     * @param oldEventType
     * @return
     */
    @ShellMethod("Swap Event")
    public String swapEvent(@ShellOption(value = {"-customerid"}) String customerID,
                            @ShellOption(value = {"-eventid"}) String eventID,
                            @ShellOption(value = {"-type"}) String eventType,
                            @ShellOption(value = {"-oldeventid"}) String oldEventID,
                            @ShellOption(value = {"-oldtype"}) String oldEventType
    ) {
        log.debug("inside swapEvent, customerID {}, eventID {}, eventType {}, oldEventID {}, oldEeventType{}", customerID, eventID, eventType, oldEventID, oldEventType);
        String debugErrorMessage = "action [swapEvent], param customerID [%s], eventID [%s], eventType [%s], eventID [%s], oldEventType [%s], error [%s]";

        log.debug("checking if there is a logged in user {}", session.isActive());
        if (!session.isActive()) {
            return shellHelper.getErrorMessage("No Logged in user, Please login");
        }

        log.debug("checking if session user {} is a Manager or a Customer", session.getUserName());
        if (!UserType.EVENT_MANAGER.equals(session.getUserType()) && !UserType.CUSTOMER.equals(session.getUserType())) {
            String msg = String.format("User %s is not authorized to perform this action", session.getUserName());
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID to be in valid format {} {}", usernamePattern, customerID);
        Pattern pattern = Pattern.compile(usernamePattern);
        if (!pattern.matcher(customerID).matches()) {
            String msg = "Invalid customerID";
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking customerID is not a manager {}", customerID);
        if (UserType.EVENT_MANAGER.equals(UserType.get(Character.toString(customerID.charAt(3))))) {
            String msg = "Invalid customerID, Manager cannot book or cancel events for themselves";
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking eventID to be in valid format {} {}", eventIDPattern, eventID);
        pattern = Pattern.compile(eventIDPattern);
        if (!pattern.matcher(eventID).matches()) {
            String msg = "Invalid eventID";
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking event type {}", eventType);
        if (EventType.get(eventType) == null) {
            String msg = String.format("Invalid event type %s", eventType);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String dateStr = eventID.substring(4);
        log.debug("checking event Date {}", dateStr);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(eventDatePattern);
            simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            String msg = String.format("Invalid event Date %s", dateStr);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        char timeSlot = eventID.charAt(3);
        log.debug("checking event Time Slot {}", timeSlot);
        if (EventTimeSlot.get(Character.toString(timeSlot)) == null) {
            String msg = String.format("Invalid Event time slot %s", timeSlot);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking oldEventID to be in valid format {} {}", eventIDPattern, oldEventID);
        if (!pattern.matcher(oldEventID).matches()) {
            String msg = String.format("Invalid oldEventID %s", oldEventID);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        log.debug("checking old event type {}", oldEventType);
        if (EventType.get(oldEventType) == null) {
            String msg = String.format("Invalid old event type %s", oldEventType);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        dateStr = oldEventID.substring(4);
        log.debug("checking old event Date {}", dateStr);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(eventDatePattern);
            simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            String msg = String.format("Invalid old event Date %s", dateStr);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        timeSlot = eventID.charAt(3);
        log.debug("checking old event Time Slot {}", timeSlot);
        if (EventTimeSlot.get(Character.toString(timeSlot)) == null) {
            String msg = String.format("Invalid Old Event time slot %s", timeSlot);
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, msg));
            return shellHelper.getErrorMessage(msg);
        }

        String result = null;
        try {
            dems.EventManagementServiceImpl eventManagementServiceImpl = eventManagementServiceCorbaBean.locateObject("frontend");
            result = shellHelper.getSuccessMessage(eventManagementServiceImpl.swapEvent(customerID, eventID, eventType, oldEventID, oldEventType));
        } catch (EventManagementServiceException e) {
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }  catch (BeansException e) {
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        } catch (Exception e) {
            session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, e.getMessage()));
            return shellHelper.getErrorMessage(e.getMessage());
        }
        debugErrorMessage.replace("error", "result");
        session.getUserActivityLogger().log(String.format(debugErrorMessage, customerID, eventID, eventType, oldEventID, oldEventType, result));
        return result;
    }

}