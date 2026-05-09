package com.hotel.bookingsystem.service;

import com.hotel.bookingsystem.dto.BookingRequest;
import com.hotel.bookingsystem.dto.BookingResponse;
import com.hotel.bookingsystem.model.entity.Booking;
import com.hotel.bookingsystem.model.entity.Room;
import com.hotel.bookingsystem.model.entity.User;
import com.hotel.bookingsystem.repository.BookingRepository;
import com.hotel.bookingsystem.repository.RoomRepository;
import com.hotel.bookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // Validate dates
        if (request.getCheckIn().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }
        
        if (request.getCheckOut().isBefore(request.getCheckIn())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }
        
        // Get room and user
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if room is available
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new RuntimeException("Room is not available");
        }
        
        // Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            room, request.getCheckIn(), request.getCheckOut());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for these dates");
        }
        
        // Calculate total price
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = nights * room.getPricePerNight();
        
        // Create booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setTotalPrice(totalPrice);
        booking.setStatus("CONFIRMED");
        
        // Update room status
        room.setStatus("BOOKED");
        roomRepository.save(room);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        return convertToResponse(savedBooking);
    }
    
    public List<BookingResponse> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus("CANCELLED");
        
        // Make room available again
        Room room = booking.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        
        bookingRepository.save(booking);
    }
    
    private BookingResponse convertToResponse(Booking booking) {
        return new BookingResponse(
            booking.getId(),
            booking.getUser().getName(),
            booking.getRoom().getHotel().getName(),
            booking.getRoom().getRoomNumber(),
            booking.getRoom().getType(),
            booking.getCheckIn(),
            booking.getCheckOut(),
            booking.getTotalPrice(),
            booking.getStatus()
        );
    }
}
