package com.hotel.bookingsystem.repository;

import com.hotel.bookingsystem.model.entity.Booking;
import com.hotel.bookingsystem.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByRoomId(Long roomId);
    
    @Query("SELECT b FROM Booking b WHERE b.room = :room AND " +
           "((b.checkIn BETWEEN :checkIn AND :checkOut) OR " +
           "(b.checkOut BETWEEN :checkIn AND :checkOut) OR " +
           "(:checkIn BETWEEN b.checkIn AND b.checkOut))")
    List<Booking> findConflictingBookings(@Param("room") Room room,
                                          @Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut);
}
