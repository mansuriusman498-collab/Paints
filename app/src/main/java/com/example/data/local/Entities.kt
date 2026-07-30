package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val phone: String,
    val serviceName: String,
    val sqFt: Double,
    val totalAmount: Double,
    val bookingDate: String,
    val timeSlot: String,
    val address: String,
    val notes: String = "",
    val status: String = "Requested", // Requested, Confirmed, Painter Assigned, Material Delivered, Painting In Progress, Quality Inspection, Completed
    val paymentStatus: String = "Pending", // Pending, Paid via Razorpay, Cash on Completion
    val painterName: String = "Mansuri Master Painter",
    val painterPhone: String = "+91 78430 99068",
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
