package com.hotel.bookingsystem.dto;

import java.time.LocalDate;

public class BookingResponse {
    private Long id;
    private String userName;
    private String hotelName;
    private String roomNumber;
    private String roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;
    private String status;

    public BookingResponse() {}

    public BookingResponse(Long id, String userName, String hotelName, String roomNumber, 
                          String roomType, LocalDate checkIn, LocalDate checkOut, 
                          double totalPrice, String status) {
        this.id = id;
        this.userName = userName;
        this.hotelName = hotelName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
