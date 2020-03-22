package ca.concordia.ginacody.comp6231.demsha.server.dao;

import ca.concordia.ginacody.comp6231.demsha.common.exception.EventManagementServiceException;
import ca.concordia.ginacody.comp6231.demsha.server.config.Configuration;
import ca.concordia.ginacody.comp6231.demsha.server.vo.EventVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BookingDAO {

    /**
     *
     *
     */
    private static Object mutex = new Object();

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDAO.class);

    /**
     *
     */
    public BookingDAO() {
    }


    /**
     *
     * @param customerID
     * @param eventVO
     * @return
     * @throws EventManagementServiceException
     */
    public String addBooking(String customerID, EventVO eventVO) throws EventManagementServiceException {
        synchronized(BookingDAO.mutex) {
            Database.getInstance().getEvents().computeIfAbsent(eventVO.getEventType(), eventType -> {
                LOGGER.error("No {} Events exist, nothing will be booked", eventType);
                throw new EventManagementServiceException(String.format("Error: No %s Events exist, nothing will be booked", eventType));
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfAbsent(eventVO.getId(), eventId -> {
                    LOGGER.error("Event {} does not exit, nothing will be booked", eventId);
                    throw new EventManagementServiceException(String.format("Error: Event %s does not exit, nothing will be booked", eventId));
                });
                return map;
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfPresent(eventVO.getId(), (eventId, eventVO1) -> {

                    LOGGER.info("Check if customer already made a booking in this event {} type {}", eventId, eventVO1.getEventType());
                    Database.getInstance().getEventRecords().computeIfPresent(eventVO1, (eventVO2, list) -> {
                        if(customerID.length()>8 && list.contains(customerID.substring(0, 8)) ) {
                            throw new EventManagementServiceException(String.format("Error: Customer %s is already booked for %s", customerID.substring(0, 7), eventId));
                        }
                        if(list.contains(customerID)) {
                            throw new EventManagementServiceException(String.format("Error: Customer %s is already booked for %s", customerID, eventId));
                        }
                        return list;
                    });

                    LOGGER.info("Check if there is capacity to book event {}", eventId);
                    int spaces = eventVO1.getCapacity() - eventVO1.getNumberOfAttendees();
                    if(spaces > 0) {
                        LOGGER.info("{} spaces are available Booking in progress", spaces);
                        eventVO1.setNumberOfAttendees(eventVO1.getNumberOfAttendees()+1);

                        Database.getInstance().getUserRecords().putIfAbsent(customerID, new ArrayList<>());
                        Database.getInstance().getUserRecords().computeIfPresent(customerID, (customerID0, list) -> {
                            list.add(eventVO1);
                            return list;
                        });

                        Database.getInstance().getEventRecords().putIfAbsent(eventVO1, new ArrayList<>());
                        Database.getInstance().getEventRecords().computeIfPresent(eventVO1, (eventVO2, list) -> {
                            list.add(customerID);
                            return list;
                        });

                    } else {
                        throw new EventManagementServiceException(String.format("Error: Event %s is full, nothing will be booked", eventId));
                    }
                    return eventVO1;
                });
                return map;
            });
        }
        return String.format("Success: Event %s booked for Customer %s successfully", eventVO.getId(), customerID);
    }

    /**
     *
     * @param customerID
     * @param eventVO
     * @return
     * @throws EventManagementServiceException
     */
    public String removeBooking(String customerID, EventVO eventVO) throws EventManagementServiceException {
        synchronized(BookingDAO.mutex) {
            Database.getInstance().getUserRecords().computeIfAbsent(customerID, s -> {
                LOGGER.error("No bookings for {} on {} database", s, Configuration.SERVER_LOCATION);
                throw new EventManagementServiceException(String.format("Error: No bookings for %s on %s database", s, Configuration.SERVER_LOCATION));
            });
            Database.getInstance().getEvents().computeIfAbsent(eventVO.getEventType(), eventType -> {
                LOGGER.error("No {} Events exist, nothing will be canceled", eventType);
                throw new EventManagementServiceException(String.format("Error: No %s Events exist, nothing will be canceled", eventType));
            });
            Database.getInstance().getEvents().computeIfPresent(eventVO.getEventType(), (type, map) -> {
                map.computeIfPresent(eventVO.getId(), (eventId, eventVO1) -> {
                    LOGGER.info("Check if customer already made a booking in this event {} type {}", eventId, eventVO1.getEventType());
                    Database.getInstance().getEventRecords().computeIfPresent(eventVO1, (eventVO2, list) -> {
                        if(list.contains(customerID)) {
                            list.remove(customerID);
                            Database.getInstance().getUserRecords().get(customerID).remove(eventVO);
                        } else {
                            throw new EventManagementServiceException(String.format("Error: Event %s %s is not booked for customer %s", eventVO.getId(), eventVO.getEventType(), customerID));
                        }
                        return list;
                    });
                    eventVO1.setNumberOfAttendees(eventVO1.getNumberOfAttendees()-1);
                    return eventVO1;
                });
                return map;
            });
        }
        return String.format("Success: Event %s is canceled for Customer %s successfully", eventVO.getId(), customerID);
    }

    /**
     *
     * @param customerID
     * @return
     * @throws EventManagementServiceException
     */
    public String selectAllBookings(String customerID) throws EventManagementServiceException {
        StringBuilder stringBuilder = new StringBuilder("Success:");
        Database.getInstance().getUserRecords().computeIfAbsent(customerID, s -> {
            throw new EventManagementServiceException(String.format("Error: No bookings for %s on %s database", s, Configuration.SERVER_LOCATION));
        });
        Database.getInstance().getUserRecords().computeIfPresent(customerID, (s, eventVOS) -> {
            if(eventVOS.size() == 0) {
                throw new EventManagementServiceException(String.format("Error: No bookings for %s on %s database", s, Configuration.SERVER_LOCATION));
            }
            eventVOS.stream().sorted(Comparator.comparing(EventVO::getDate)).forEach(eventVO -> {
                stringBuilder.append(String.format("%s %s on %s at %s is confirmed$$", eventVO.getEventType(), eventVO.getId(), eventVO.getDate(), eventVO.getEventTimeSlot()));
            });
            return eventVOS;
        });
        return stringBuilder.toString();
    }

    /**
     *
     * @param customerID
     * @param eventVO
     * @return
     */
    public int countBookingInSameWeek(String customerID, EventVO eventVO) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Database.getInstance().getUserRecords().computeIfPresent(customerID, (s, eventVOS) -> {
            atomicInteger.set(eventVOS.stream().filter(eventVO1 -> eventVO.getWeekIndex() == eventVO1.getWeekIndex()).collect(Collectors.toList()).size());
            return eventVOS;
        });
        return atomicInteger.get();
    }

    /**
     *
     * @param trxNumber
     */
    public String commit(String trxNumber) {
        StringBuilder stringBuilder = new StringBuilder("Success:");
        Optional<EventVO> optional = Optional.ofNullable(Database.getInstance().getUserRecords().get(trxNumber).get(0));
        optional.ifPresent(eventVO -> {
            Database.getInstance().getEventRecords().get(eventVO).remove(trxNumber);
            Database.getInstance().getEventRecords().get(eventVO).add(trxNumber.substring(0,8));
            Database.getInstance().getUserRecords().remove(trxNumber);
            Database.getInstance().getUserRecords().computeIfPresent(trxNumber.substring(0,8), (s, eventVOS) -> {
                eventVOS.add(eventVO);
                return eventVOS;
            });
            Database.getInstance().getUserRecords().computeIfAbsent(trxNumber.substring(0,8), s -> {
                return new ArrayList<>();
            }).add(eventVO);
        });
        return stringBuilder.toString();
    }
}