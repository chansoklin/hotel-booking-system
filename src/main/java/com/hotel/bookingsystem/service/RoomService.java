package com.hotel.bookingsystem.service;

import com.hotel.bookingsystem.model.entity.Room;
import com.hotel.bookingsystem.model.entity.Hotel;
import com.hotel.bookingsystem.repository.RoomRepository;
import com.hotel.bookingsystem.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public Room createRoom(Long hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        room.setHotel(hotel);
        room.setStatus("AVAILABLE");
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> searchByType(String type) {
        return roomRepository.findByTypeAndStatus(type, "AVAILABLE");
    }

    public List<Room> searchByPriceRange(double minPrice, double maxPrice) {
        return roomRepository.findByPricePerNightBetween(minPrice, maxPrice);
    }

    public List<Room> searchAvailableByType(String type) {
        return roomRepository.findAvailableRoomsByType(type);
    }

    public List<Room> searchAvailableByMaxPrice(double maxPrice) {
        return roomRepository.findAvailableRoomsByMaxPrice(maxPrice);
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setType(roomDetails.getType());
        room.setPricePerNight(roomDetails.getPricePerNight());
        room.setStatus(roomDetails.getStatus());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
