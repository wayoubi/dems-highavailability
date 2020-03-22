package ca.concordia.ginacody.comp6231.demsha.server.dao;


import ca.concordia.ginacody.comp6231.demsha.common.enums.EventType;
import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.server.vo.EventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EventDAO {

    private static Object mutex = new Object();

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDAO.class);

    /**
     *
     */
    public EventDAO() {
    }

    /**
     * @param eventVO
     * @return
     * @throws EventManagementServiceException
     */
    public String addEvent(EventVO eventVO) throws EventManagementServiceException {
        AtomicBoolean newEvent = new AtomicBoolean(true);
        synchronized (EventDAO.mutex) {
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfPresent(eventVO.getId(), (id, event) -> {
                    newEvent.set(false);
                    if(event.getNumberOfAttendees() > eventVO.getCapacity()) {
                        LOGGER.error("Event {} cannot be updated, new capacity is less than already registered users", event.getId());
                        throw new EventManagementServiceException(String.format("Error: Event %s cannot be updated, new capacity is less than already registered users", eventVO.getId()));
                    } else if(event.getNumberOfAttendees() <= eventVO.getCapacity()){
                        event.setCapacity(eventVO.getCapacity());
                        LOGGER.info("Event {} updated successfully", eventVO.getId());
                    }
                    return event;
                });
                return map;
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.putIfAbsent(eventVO.getId(), eventVO);
                return map;
            });
            Database.getInstance().getEvents().computeIfAbsent(eventVO.getEventType(), eventType -> new HashMap<>()).putIfAbsent(eventVO.getId(), eventVO);
        }
        String result = String.format("Success: Event %s updated successfully", eventVO.getId());
        if (newEvent.get()) {
            LOGGER.info("Event {} added successfully", eventVO.getId());
            result = String.format("Success: Event %s added successfully", eventVO.getId());
        }
        return result;
    }

     /**
     *
     * @param eventVO
     * @return
     * @throws EventManagementServiceException
     */
    public String removeEvent(EventVO eventVO) throws EventManagementServiceException {
        synchronized (EventDAO.mutex) {
            Database.getInstance().getEvents().computeIfAbsent(eventVO.getEventType(), eventType -> {
                LOGGER.error("No {} Events exist, nothing will be removed", eventType);
                throw new EventManagementServiceException(String.format("Error: No %s Events exist, nothing will be removed", eventType));
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfAbsent(eventVO.getId(), eventId -> {
                    LOGGER.error("Event {} does not exit, nothing will be removed", eventId);
                    throw new EventManagementServiceException(String.format("Error: Event %s does not exit, nothing will be removed", eventId));
                });
                return map;
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfPresent(eventVO.getId(), (eventId, eventVO1) -> {
                    Database.getInstance().getEventRecords().computeIfPresent(eventVO1, (eventVO2, list) -> {
                        list.stream().forEach(customerID -> {
                            BookingDAO bookingDAO = new BookingDAO();
                            List<EventVO> tempList =  map.values().stream().sorted(Comparator.comparing(EventVO::getDate)).collect(Collectors.toList());
                            for(EventVO eventVO3 : tempList) {
                                if(eventVO3.getDate().after(eventVO2.getDate())) {
                                    try {
                                        bookingDAO.addBooking(customerID, eventVO3);
                                        break;
                                    } catch (EventManagementServiceException e) {
                                        LOGGER.error(e.getMessage());
                                    }
                                }
                            }
                            Database.getInstance().getUserRecords().computeIfPresent(customerID, (customerID0, eventVOS) -> {
                                eventVOS.remove(eventVO2);
                                return eventVOS;
                            });
                        });
                        return list;
                    });
                    LOGGER.info("Event {} will be removed", eventId);
                    Database.getInstance().getEventRecords().remove(eventVO1);
                    map.remove(eventId);
                    return null;
                });
                return map;
            });
        }
        return String.format("Success: Event %s removed successfully", eventVO.getId());
    }

    /**
     *
     * @param eventType
     * @return
     * @throws EventManagementServiceException
     */
    public String selectAllEvents(EventType eventType) throws EventManagementServiceException {
        LOGGER.info("selectAllEvents {}", eventType);
        Database.getInstance().getEvents().computeIfAbsent(eventType, et -> {
            LOGGER.error("No {} Events exist, nothing will be listed", et);
            throw new EventManagementServiceException(String.format("Error: No %s Events exist, nothing will be listed", et));
        });
        StringBuilder stringBuilder = new StringBuilder("Success:");
        Database.getInstance().getEvents().computeIfPresent(eventType, (eventType1, stringEventVOMap) -> {
            if(stringEventVOMap.values().isEmpty()){
                LOGGER.error("No {} Events exist, nothing will be listed", eventType1);
                throw new EventManagementServiceException(String.format("Error: No %s Events exist, nothing will be listed", eventType1));
            }
            stringEventVOMap.values().stream().sorted(Comparator.comparing(EventVO::getDate)).forEach(eventVO -> {
                stringBuilder.append(String.format("%s %s scheduled on %s at %s - available places [%s]$$",eventVO.getEventType(), eventVO.getId(),eventVO.getDate(), eventVO.getEventTimeSlot(), (eventVO.getCapacity()-eventVO.getNumberOfAttendees())));
            });
            return stringEventVOMap;
        });
        LOGGER.info("{} Events List generated successfully", eventType);
        return stringBuilder.toString();
    }
}