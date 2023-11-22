package lux.fm.bookingservice.service;

public interface SchedulingService {
    void checkExpiredBookings();

    void checkBookingLifeTime();
}
