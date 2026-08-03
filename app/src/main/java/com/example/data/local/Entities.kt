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
    val propertyType: String = "House", // House, Flat, Shop, Office, Villa
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
    
    // Booking Type & Site Visit Details
    val bookingType: String = "Direct Booking", // "Direct Booking" or "Request Site Visit"
    val siteVisitFee: Double = 200.0,
    val siteVisitFeePaid: Boolean = false,
    val isSiteVisitWaived: Boolean = false,
    val siteVisitDate: String = "",
    
    // Financial & Advance Payment Breakdown
    val totalAmount: Double, // Estimated or final quotation cost
    val finalQuotationAmount: Double = 0.0, // Set by Admin after site visit
    val quotationStatus: String = "Not Generated", // "Not Generated", "Sent", "Accepted", "Rejected"
    val advancePercentage: Double = 20.0, // Default 20%
    val advanceAmount: Double = 0.0, // Calculated 20% advance expected
    val advancePaidAmount: Double = 0.0, // Actual advance paid
    val remainingAmount: Double = 0.0, // Remaining balance (Total - Advance Paid)
    
    // Schedules & Address
    val bookingDate: String,
    val timeSlot: String,
    val address: String,
    val latitude: Double = 26.8228,
    val longitude: Double = 75.8648,
    val notes: String = "",
    
    // Core Workflow Status
    val status: String = "Pending Admin Approval", 
    // Values: "Pending Admin Approval", "Site Visit Requested", "Site Visit Scheduled", "Quotation Sent", "Quotation Accepted", "Quotation Rejected", "Booking Approved", "Work Started", "Work Completed", "Cancelled", "Rejected"
    
    val paymentStatus: String = "Pending Advance", 
    // Values: "Pending Advance", "Advance Paid", "Cash Payment Pending", "Fully Paid"
    
    val paymentMethod: String = "Online UPI", // "Online UPI", "Razorpay", "Cash at Site", "GPay", "PhonePe", "Cards"
    
    // Staff & Reviews
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
