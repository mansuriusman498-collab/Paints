package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.local.ReviewDao
import com.example.data.local.ReviewEntity
import com.example.data.local.RoomPhotoDao
import com.example.data.local.RoomPhotoEntity
import com.example.data.models.DefaultServices
import com.example.data.models.PaintCostEstimate
import com.example.data.models.PaintService
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MansuriRepository(
    private val bookingDao: BookingDao,
    private val roomPhotoDao: RoomPhotoDao,
    private val reviewDao: ReviewDao
) {
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val allPhotos: Flow<List<RoomPhotoEntity>> = roomPhotoDao.getAllPhotos()
    val allReviews: Flow<List<ReviewEntity>> = reviewDao.getAllReviews()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            name = "Mansuri Client",
            phone = "+91 98765 43210",
            email = "mansuriusman498@gmail.com",
            address = "Flat 402, Golden Heights, Station Road",
            isLoggedIn = true,
            isAdmin = false
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser

    fun getServices(): List<PaintService> = DefaultServices.list

    fun calculateCost(
        areaSqFt: Double,
        roomsCount: Int,
        serviceTitle: String,
        customRate: Double? = null
    ): PaintCostEstimate {
        val service = DefaultServices.list.find { it.title == serviceTitle }
        val rate = customRate ?: service?.pricePerSqFt ?: 20.0
        val baseCost = areaSqFt * rate
        val materialCost = baseCost * 0.65
        val laborCost = baseCost * 0.35
        val litersNeeded = (areaSqFt / 100.0) * 1.5
        val daysEstimated = (areaSqFt / 400.0).toInt().coerceAtLeast(1)

        return PaintCostEstimate(
            areaSqFt = areaSqFt,
            roomsCount = roomsCount,
            serviceTitle = serviceTitle,
            ratePerSqFt = rate,
            paintMaterialCost = materialCost,
            laborCost = laborCost,
            totalCost = baseCost,
            estimatedLiters = litersNeeded,
            estimatedDays = daysEstimated
        )
    }

    suspend fun createBooking(
        customerName: String,
        phone: String,
        serviceName: String,
        sqFt: Double,
        totalAmount: Double,
        bookingDate: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentStatus: String
    ): String {
        val bookingId = "MP-" + (1000..9999).random()
        val entity = BookingEntity(
            id = bookingId,
            customerName = customerName.ifEmpty { _currentUser.value.name },
            phone = phone.ifEmpty { _currentUser.value.phone },
            serviceName = serviceName,
            sqFt = sqFt,
            totalAmount = totalAmount,
            bookingDate = bookingDate,
            timeSlot = timeSlot,
            address = address.ifEmpty { _currentUser.value.address },
            notes = notes,
            status = "Requested",
            paymentStatus = paymentStatus,
            painterName = "Usman Mansuri & Master Team",
            painterPhone = "+91 78430 99068"
        )
        bookingDao.insertBooking(entity)
        return bookingId
    }

    suspend fun updateBookingStatus(id: String, status: String) {
        bookingDao.updateStatus(id, status)
    }

    suspend fun addRoomPhoto(roomLabel: String, photoPath: String, aiNotes: String) {
        val photo = RoomPhotoEntity(
            id = UUID.randomUUID().toString(),
            roomLabel = roomLabel,
            photoPath = photoPath,
            aiInspectionNotes = aiNotes
        )
        roomPhotoDao.insertPhoto(photo)
    }

    suspend fun deleteRoomPhoto(id: String) {
        roomPhotoDao.deletePhoto(id)
    }

    suspend fun submitReview(userName: String, rating: Float, comment: String, serviceName: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        val review = ReviewEntity(
            id = UUID.randomUUID().toString(),
            userName = userName.ifEmpty { "Verified Customer" },
            rating = rating,
            comment = comment,
            date = dateStr,
            serviceName = serviceName
        )
        reviewDao.insertReview(review)
    }

    fun login(phone: String, name: String = "Valued Customer") {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            phone = phone,
            isLoggedIn = true,
            isGoogleUser = false
        )
    }

    fun loginWithGoogle(email: String, name: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            email = email,
            isLoggedIn = true,
            isGoogleUser = true
        )
    }

    fun logout() {
        _currentUser.value = _currentUser.value.copy(isLoggedIn = false, isAdmin = false)
    }

    fun setAdminUser(email: String, name: String) {
        _currentUser.value = _currentUser.value.copy(
            email = email,
            name = name,
            isLoggedIn = true,
            isAdmin = true
        )
    }

    fun setAdminMode(isAdmin: Boolean) {
        _currentUser.value = _currentUser.value.copy(isAdmin = isAdmin)
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seed initial sample reviews and sample booking if DB is fresh
        val initialReviews = listOf(
            ReviewEntity("r1", "Aamir Khan", 5.0f, "Mansuri Paints transformed our 3BHK flat with Royal Paint. Outstanding finish and zero mess!", "14 Jul 2026", "Royal Paint"),
            ReviewEntity("r2", "Priya Sharma", 5.0f, "Punctual painters, excellent waterproofing solution, and instant PDF quote!", "22 Jun 2026", "Waterproofing"),
            ReviewEntity("r3", "Rajesh Gupta", 4.8f, "Great price for Plastic Paint (₹20/sq ft). Finished 2 days ahead of schedule.", "05 Jun 2026", "Plastic Paint")
        )
        for (r in initialReviews) {
            reviewDao.insertReview(r)
        }

        val sampleBooking = BookingEntity(
            id = "MP-8821",
            customerName = "Mansuri Client",
            phone = "+91 98765 43210",
            serviceName = "Royal Paint",
            sqFt = 850.0,
            totalAmount = 22950.0,
            bookingDate = "2026-08-05",
            timeSlot = "10:00 AM - 01:00 PM",
            address = "Flat 402, Golden Heights, Station Road",
            notes = "Require gold metallic texture on living room wall.",
            status = "Painter Assigned",
            paymentStatus = "Paid via Razorpay",
            painterName = "Usman Mansuri (Master Painter)",
            painterPhone = "+91 78430 99068"
        )
        bookingDao.insertBooking(sampleBooking)
    }
}
