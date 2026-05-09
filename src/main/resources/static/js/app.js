let currentUser = null;

const API_URL = 'http://localhost:8080/api';

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadHotels();
});

function showLogin() {
    document.getElementById('loginModal').style.display = 'block';
}

function showRegister() {
    document.getElementById('registerModal').style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

async function login() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    const response = await fetch(`${API_URL}/users`);
    const users = await response.json();
    const user = users.find(u => u.email === email);
    
    if (user) {
        currentUser = user;
        localStorage.setItem('user', JSON.stringify(user));
        document.getElementById('authButtons').style.display = 'none';
        document.getElementById('userInfo').style.display = 'flex';
        document.getElementById('userName').innerText = `Welcome, ${user.name}`;
        
        if (user.role === 'ADMIN') {
            document.getElementById('adminTab').style.display = 'block';
        }
        
        closeModal('loginModal');
        showTab('hotels');
        loadHotels();
    } else {
        alert('Login failed. Please register first.');
    }
}

function logout() {
    currentUser = null;
    localStorage.removeItem('user');
    document.getElementById('authButtons').style.display = 'flex';
    document.getElementById('userInfo').style.display = 'none';
    document.getElementById('adminTab').style.display = 'none';
    showTab('hotels');
}

function checkAuth() {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        document.getElementById('authButtons').style.display = 'none';
        document.getElementById('userInfo').style.display = 'flex';
        document.getElementById('userName').innerText = `Welcome, ${currentUser.name}`;
        
        if (currentUser.role === 'ADMIN') {
            document.getElementById('adminTab').style.display = 'block';
        }
    }
}

async function register() {
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;
    
    const response = await fetch(`${API_URL}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password, role })
    });
    
    if (response.ok) {
        alert('Registration successful! Please login.');
        closeModal('registerModal');
        showLogin();
        document.getElementById('regName').value = '';
        document.getElementById('regEmail').value = '';
        document.getElementById('regPassword').value = '';
    } else {
        alert('Registration failed!');
    }
}

function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    document.getElementById(`${tabName}Tab`).classList.add('active');
    event.target.classList.add('active');
    
    if (tabName === 'hotels') loadHotels();
    if (tabName === 'bookings' && currentUser) loadUserBookings();
    if (tabName === 'admin' && currentUser?.role === 'ADMIN') loadAllBookings();
}

async function loadHotels() {
    const response = await fetch(`${API_URL}/hotels`);
    const hotels = await response.json();
    
    const hotelsList = document.getElementById('hotelsList');
    if (hotels.length === 0) {
        hotelsList.innerHTML = '<p>No hotels yet. Admin can add hotels.</p>';
    } else {
        hotelsList.innerHTML = hotels.map(hotel => `
            <div class="hotel-card">
                <h3>${hotel.name}</h3>
                <p>📍 ${hotel.location}</p>
                <p>${hotel.description || 'No description'}</p>
                <button onclick="showRooms(${hotel.id})" class="btn btn-primary">View Rooms</button>
                ${currentUser?.role === 'ADMIN' ? `
                    <button onclick="deleteHotel(${hotel.id})" class="btn btn-danger">Delete</button>
                ` : ''}
            </div>
        `).join('');
    }
    
    if (currentUser?.role === 'ADMIN') {
        document.getElementById('addHotelCard').style.display = 'block';
    }
}

async function showRooms(hotelId) {
    const response = await fetch(`${API_URL}/hotels/${hotelId}/rooms`);
    const rooms = await response.json();
    
    if (rooms.length === 0) {
        alert('No rooms available in this hotel yet.');
    } else {
        alert(rooms.map(room => 
            `Room ${room.roomNumber}: ${room.type} - $${room.pricePerNight}/night (${room.status})`
        ).join('\n'));
    }
}

async function createHotel() {
    const name = document.getElementById('hotelName').value;
    const location = document.getElementById('hotelLocation').value;
    const description = document.getElementById('hotelDescription').value;
    
    if (!name || !location) {
        alert('Please fill hotel name and location');
        return;
    }
    
    const response = await fetch(`${API_URL}/hotels`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, location, description })
    });
    
    if (response.ok) {
        alert('Hotel created successfully!');
        loadHotels();
        document.getElementById('hotelName').value = '';
        document.getElementById('hotelLocation').value = '';
        document.getElementById('hotelDescription').value = '';
    } else {
        alert('Failed to create hotel');
    }
}

async function deleteHotel(hotelId) {
    if (confirm('Delete this hotel?')) {
        await fetch(`${API_URL}/hotels/${hotelId}`, { method: 'DELETE' });
        loadHotels();
    }
}

async function searchRooms() {
    const checkIn = document.getElementById('searchCheckIn').value;
    const checkOut = document.getElementById('searchCheckOut').value;
    const type = document.getElementById('searchType').value;
    const maxPrice = document.getElementById('searchMaxPrice').value;
    
    let url = `${API_URL}/rooms`;
    if (type) {
        url = `${API_URL}/rooms/search/by-type?type=${type}`;
    }
    
    const response = await fetch(url);
    const rooms = await response.json();
    
    let filteredRooms = rooms;
    if (maxPrice) {
        filteredRooms = rooms.filter(r => r.pricePerNight <= maxPrice);
    }
    
    const results = document.getElementById('searchResults');
    if (filteredRooms.length === 0) {
        results.innerHTML = '<p>No rooms found matching your criteria.</p>';
    } else {
        results.innerHTML = `
            <h3>Available Rooms (${filteredRooms.length})</h3>
            ${filteredRooms.map(room => `
                <div class="room-card">
                    <p><strong>Room ${room.roomNumber}</strong> - ${room.type}</p>
                    <p>Price: $${room.pricePerNight}/night</p>
                    <p>Status: ${room.status}</p>
                    ${currentUser ? `
                        <button onclick="bookRoom(${room.id})" class="btn btn-success">Book Now</button>
                    : '<p>Please login to book</p>'}
                </div>
            `).join('')}
        `;
    }
}

async function bookRoom(roomId) {
    const checkIn = document.getElementById('searchCheckIn').value;
    const checkOut = document.getElementById('searchCheckOut').value;
    
    if (!checkIn || !checkOut) {
        alert('Please select check-in and check-out dates');
        return;
    }
    
    const response = await fetch(`${API_URL}/bookings`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: currentUser.id,
            roomId: roomId,
            checkIn: checkIn,
            checkOut: checkOut
        })
    });
    
    if (response.ok) {
        const booking = await response.json();
        alert(`Booking successful! Total: $${booking.totalPrice}`);
        showTab('bookings');
        loadUserBookings();
    } else {
        const error = await response.text();
        alert('Booking failed: ' + error);
    }
}

async function loadUserBookings() {
    const response = await fetch(`${API_URL}/bookings/user/${currentUser.id}`);
    const bookings = await response.json();
    
    const bookingsList = document.getElementById('bookingsList');
    if (bookings.length === 0) {
        bookingsList.innerHTML = '<p>No bookings yet. Search and book a room!</p>';
    } else {
        bookingsList.innerHTML = bookings.map(booking => `
            <div class="booking-card">
                <p><strong>${booking.hotelName}</strong> - Room ${booking.roomNumber} (${booking.roomType})</p>
                <p>📅 ${booking.checkIn} to ${booking.checkOut}</p>
                <p>💰 Total: $${booking.totalPrice}</p>
                <p>Status: <strong>${booking.status}</strong></p>
                ${booking.status === 'CONFIRMED' ? `
                    <button onclick="cancelBooking(${booking.id})" class="btn btn-danger">Cancel Booking</button>
                ` : ''}
            </div>
        `).join('');
    }
}

async function loadAllBookings() {
    const response = await fetch(`${API_URL}/bookings`);
    const bookings = await response.json();
    
    const allBookingsList = document.getElementById('allBookingsList');
    if (bookings.length === 0) {
        allBookingsList.innerHTML = '<p>No bookings yet.</p>';
    } else {
        allBookingsList.innerHTML = bookings.map(booking => `
            <div class="booking-card">
                <p><strong>${booking.userName}</strong> booked ${booking.hotelName}</p>
                <p>Room ${booking.roomNumber} (${booking.roomType})</p>
                <p>📅 ${booking.checkIn} to ${booking.checkOut}</p>
                <p>💰 Total: $${booking.totalPrice}</p>
                <p>Status: ${booking.status}</p>
            </div>
        `).join('');
    }
}

async function cancelBooking(bookingId) {
    if (confirm('Cancel this booking?')) {
        await fetch(`${API_URL}/bookings/${bookingId}`, { method: 'DELETE' });
        loadUserBookings();
        alert('Booking cancelled successfully!');
    }
}
