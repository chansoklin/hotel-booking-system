package com.hotel.bookingsystem.repository;

import com.hotel.bookingsystem.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);
    
    List<Room> findByTypeAndStatus(String type, String status);
    
    List<Room> findByPricePerNightBetween(double minPrice, double maxPrice);
    
    @Query("SELECT r FROM Room r WHERE r.type = :type AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByType(@Param("type") String type);
    
    @Query("SELECT r FROM Room r WHERE r.pricePerNight <= :maxPrice AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByMaxPrice(@Param("maxPrice") double maxPrice);
}
