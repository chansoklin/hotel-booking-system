package com.hotel.bookingsystem.controller;

import com.hotel.bookingsystem.model.entity.Room;
import com.hotel.bookingsystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Create room for specific hotel
    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<Room> createRoom(@PathVariable Long hotelId, @RequestBody Room room) {
        return new ResponseEntity<>(roomService.createRoom(hotelId, room), HttpStatus.CREATED);
    }

    // Get all rooms for specific hotel
    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId));
    }

    // SEARCH APIs
    @GetMapping("/rooms/search/by-type")
    public ResponseEntity<List<Room>> searchByType(@RequestParam String type) {
        return ResponseEntity.ok(roomService.searchByType(type));
    }

    @GetMapping("/rooms/search/by-price")
    public ResponseEntity<List<Room>> searchByPriceRange(
            @RequestParam double min, 
            @RequestParam double max) {
        return ResponseEntity.ok(roomService.searchByPriceRange(min, max));
    }

    @GetMapping("/rooms/search/available-by-type")
    public ResponseEntity<List<Room>> searchAvailableByType(@RequestParam String type) {
        return ResponseEntity.ok(roomService.searchAvailableByType(type));
    }

    @GetMapping("/rooms/search/available-by-max-price")
    public ResponseEntity<List<Room>> searchAvailableByMaxPrice(@RequestParam double maxPrice) {
        return ResponseEntity.ok(roomService.searchAvailableByMaxPrice(maxPrice));
    }

    // Get all rooms
    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // Update room
    @PutMapping("/rooms/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    // Delete room
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
