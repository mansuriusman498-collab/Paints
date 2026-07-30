package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val phone: String,
    val email: String = "customer@mansuripaints.com",
    val serviceName: String,
    val paintType: String = "Royal Paint",
    val bedrooms: Int = 2,
    val hall: Int = 1,
    val kitchen: Int = 1,
    val bathroom: Int = 1,
    val balcony: Int = 1,
    val sqFt: Double,
    val budget: Double = 0.0,
    val description: String = "",
    val photosJson: String = "", // Comma-separated list of uploaded photo URIs/paths
    val videoUrl: String = "",
    val totalAmount: Double,
    val bookingDate: String,
    val timeSlot: String,
    val address: String,
    val latitude: Double = 23.0225,
    val longitude: Double = 72.5714,
    val notes: String = "",
    val status: String = "Pending", // Pending, Accepted, Painter Assigned, On The Way, Work Started, Work Completed, Cancelled
    val paymentStatus: String = "Pending", // Pending, Paid, Refunded
    val paymentMethod: String = "Razorpay", // Razorpay, Cash, UPI
    val quotationStatus: String = "Generated", // Generated, Sent, Approved
    val painterId: String = "p1",
    val painterName: String = "Mansuri Master Painter",
    val painterPhone: String = "+91 78430 99068",
    val beforePhotos: String = "",
    val afterPhotos: String = "",
    val rating: Float = 0f,
    val reviewComment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "room_photos")
data class RoomPhotoEntity(
    @PrimaryKey val id: String,
    val roomLabel: String,
    val photoPath: String,
    val aiInspectionNotes: String,
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val serviceName: String
)
