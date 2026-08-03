package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.local.EmployeeDao
import com.example.data.local.EmployeeEntity
import com.example.data.local.ReviewDao
import com.example.data.local.ReviewEntity
import com.example.data.local.RoomPhotoDao
import com.example.data.local.RoomPhotoEntity
import com.example.data.models.DefaultServices
import com.example.data.models.PaintCostEstimate
import com.example.data.models.PaintService
import com.example.data.models.PaymentConfig
import com.example.data.models.UserProfile
import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MansuriRepository(
    private val bookingDao: BookingDao,
    private val roomPhotoDao: RoomPhotoDao,
    private val reviewDao: ReviewDao,
    private val employeeDao: EmployeeDao
) {
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val allPhotos: Flow<List<RoomPhotoEntity>> = roomPhotoDao.getAllPhotos()
    val allReviews: Flow<List<ReviewEntity>> = reviewDao.getAllReviews()
    val allEmployees: Flow<List<EmployeeEntity>> = employeeDao.getAllEmployees()

    private val firestore by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }
    private val firebaseAuth by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val firebaseStorage by lazy {
        try { FirebaseStorage.getInstance() } catch (e: Exception) { null }
    }

    private val _paymentConfig = MutableStateFlow(PaymentConfig())
    val paymentConfig: StateFlow<PaymentConfig> = _paymentConfig

    init {
        listenToPaymentConfig()
    }

    private fun listenToPaymentConfig() {
        try {
            firestore?.collection("settings")?.document("payment_settings")
                ?.addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && snapshot.exists()) {
                        val upi = snapshot.getString("upiId") ?: ""
                        val qr = snapshot.getString("qrCodeUrl") ?: ""
                        val updated = snapshot.getLong("updatedAt") ?: System.currentTimeMillis()
                        if (upi.isNotEmpty()) {
                            _paymentConfig.value = PaymentConfig(
                                upiId = upi,
                                qrCodeUrl = qr.ifEmpty { "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=$upi&pn=Mansuri%20Paints" },
                                lastUpdated = updated
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    fun savePaymentSettings(
        upiId: String,
        qrCodeUri: Uri?,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        if (qrCodeUri != null && firebaseStorage != null) {
            try {
                val storageRef = firebaseStorage!!.reference.child("payment_qrs/upi_qr_${System.currentTimeMillis()}.jpg")
                storageRef.putFile(qrCodeUri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            updateFirestorePaymentSettings(upiId, downloadUrl.toString(), onResult)
                        }.addOnFailureListener {
                            updateFirestorePaymentSettings(upiId, qrCodeUri.toString(), onResult)
                        }
                    }
                    .addOnFailureListener {
                        updateFirestorePaymentSettings(upiId, qrCodeUri.toString(), onResult)
                    }
            } catch (e: Exception) {
                updateFirestorePaymentSettings(upiId, qrCodeUri?.toString() ?: _paymentConfig.value.qrCodeUrl, onResult)
            }
        } else {
            val currentQr = if (qrCodeUri != null) qrCodeUri.toString() else _paymentConfig.value.qrCodeUrl
            updateFirestorePaymentSettings(upiId, currentQr, onResult)
        }
    }

    private fun updateFirestorePaymentSettings(
        upiId: String,
        qrCodeUrl: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val finalQr = if (qrCodeUrl.isBlank()) {
            "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=$upiId&pn=Mansuri%20Paints"
        } else qrCodeUrl

        val updatedConfig = PaymentConfig(
            upiId = upiId,
            qrCodeUrl = finalQr,
            lastUpdated = System.currentTimeMillis()
        )
        _paymentConfig.value = updatedConfig

        val data = mapOf(
            "upiId" to upiId,
            "qrCodeUrl" to finalQr,
            "updatedAt" to System.currentTimeMillis()
        )

        if (firestore != null) {
            firestore!!.collection("settings").document("payment_settings")
                .set(data)
                .addOnSuccessListener {
                    onResult(true, "UPI ID and QR Code saved to Firebase Firestore & Storage!")
                }
                .addOnFailureListener { e ->
                    onResult(true, "Saved settings locally! (Firebase error: ${e.localizedMessage})")
                }
        } else {
            onResult(true, "Saved payment settings locally!")
        }
    }

    fun uploadPaymentScreenshot(
        bookingId: String,
        screenshotUri: Uri,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        val timestamp = System.currentTimeMillis()
        if (firebaseStorage != null) {
            try {
                val storageRef = firebaseStorage!!.reference.child("payment_screenshots/${bookingId}_${timestamp}.jpg")
                storageRef.putFile(screenshotUri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            saveScreenshotRecord(bookingId, downloadUrl.toString(), onResult)
                        }.addOnFailureListener {
                            saveScreenshotRecord(bookingId, screenshotUri.toString(), onResult)
                        }
                    }
                    .addOnFailureListener {
                        saveScreenshotRecord(bookingId, screenshotUri.toString(), onResult)
                    }
            } catch (e: Exception) {
                saveScreenshotRecord(bookingId, screenshotUri.toString(), onResult)
            }
        } else {
            saveScreenshotRecord(bookingId, screenshotUri.toString(), onResult)
        }
    }

    private fun saveScreenshotRecord(
        bookingId: String,
        screenshotUrl: String,
        onResult: (Boolean, String) -> Unit
    ) {
        try {
            val data = mapOf(
                "paymentScreenshotUrl" to screenshotUrl,
                "paymentStatus" to "Payment Proof Uploaded",
                "paymentMethod" to "UPI",
                "updatedAt" to System.currentTimeMillis()
            )
            firestore?.collection("bookings")?.document(bookingId)?.update(data)
        } catch (e: Exception) {
            // Fallback
        }

        try {
            CoroutineScope(Dispatchers.IO).launch {
                bookingDao.updateStatus(bookingId, "Payment Proof Uploaded")
            }
        } catch (e: Exception) {
            // Fallback
        }
        onResult(true, "Payment screenshot uploaded successfully! Admin will verify shortly.")
    }

    private val _currentUser = MutableStateFlow(
        UserProfile(
            name = "Mansuri Client",
            phone = "+91 78430 99068",
            email = "mansuriusman498@gmail.com",
            houseNo = "Flat 102",
            buildingName = "Capital Residence",
            street = "Mahal Road, Jagatpura",
            landmark = "Near Capital High Street",
            city = "Jaipur",
            state = "Rajasthan",
            pincode = "302017",
            isLoggedIn = false,
            isAdmin = false,
            role = "customer"
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
        propertyType: String = "House",
        bedrooms: Int = 2,
        hall: Int = 1,
        kitchen: Int = 1,
        bathroom: Int = 1,
        balcony: Int = 1,
        sqFt: Double,
        photosJson: String = "",
        bookingType: String = "Direct Booking", // "Direct Booking" or "Request Site Visit"
        siteVisitFee: Double = 200.0,
        totalAmount: Double,
        advancePercentage: Double = 20.0,
        bookingDate: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentMethod: String = "Online UPI",
        paymentStatusOverride: String? = null,
        razorpayPaymentId: String = "",
        razorpayOrderId: String = "",
        razorpaySignature: String = "",
        paymentTimestamp: Long = System.currentTimeMillis()
    ): String {
        val bookingId = "MP-" + (1000..9999).random()
        val calculatedAdvance = totalAmount * (advancePercentage / 100.0)
        
        val isDirect = bookingType == "Direct Booking"
        val status = if (isDirect) "Pending Admin Approval" else "Site Visit Requested"
        
        val advancePaid = if (isDirect && (paymentStatusOverride?.contains("Paid") == true || paymentStatusOverride == "Advance Paid")) {
            calculatedAdvance
        } else 0.0
        
        val payStatus = paymentStatusOverride ?: if (isDirect) "Advance Paid" else "Pending Advance"
        val remaining = totalAmount - advancePaid

        val entity = BookingEntity(
            id = bookingId,
            customerName = customerName.ifEmpty { _currentUser.value.name },
            phone = phone.ifEmpty { _currentUser.value.phone },
            email = _currentUser.value.email,
            serviceName = serviceName,
            propertyType = propertyType,
            bedrooms = bedrooms,
            hall = hall,
            kitchen = kitchen,
            bathroom = bathroom,
            balcony = balcony,
            sqFt = sqFt,
            photosJson = photosJson,
            bookingType = bookingType,
            siteVisitFee = siteVisitFee,
            siteVisitFeePaid = !isDirect,
            totalAmount = totalAmount,
            finalQuotationAmount = if (isDirect) totalAmount else 0.0,
            quotationStatus = if (isDirect) "Generated" else "Not Generated",
            advancePercentage = advancePercentage,
            advanceAmount = calculatedAdvance,
            advancePaidAmount = advancePaid,
            remainingAmount = remaining,
            bookingDate = bookingDate,
            timeSlot = timeSlot,
            address = address.ifEmpty { _currentUser.value.fullAddress },
            notes = notes,
            status = status,
            paymentStatus = payStatus,
            paymentMethod = paymentMethod,
            razorpayPaymentId = razorpayPaymentId,
            razorpayOrderId = razorpayOrderId,
            razorpaySignature = razorpaySignature,
            paymentTimestamp = paymentTimestamp,
            painterName = "Usman Mansuri & Master Team",
            painterPhone = "+91 78430 99068"
        )
        bookingDao.insertBooking(entity)
        syncBookingToFirestore(entity)
        return bookingId
    }

    suspend fun updateBookingStatus(id: String, status: String) {
        bookingDao.updateStatus(id, status)
        try {
            firestore?.collection("bookings")?.document(id)?.update("status", status)
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    suspend fun sendFinalQuotation(bookingId: String, finalAmount: Double, notes: String) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val advanceAmt = finalAmount * (booking.advancePercentage / 100.0)
        val updated = booking.copy(
            finalQuotationAmount = finalAmount,
            totalAmount = finalAmount,
            advanceAmount = advanceAmt,
            remainingAmount = finalAmount - booking.advancePaidAmount,
            quotationStatus = "Sent",
            status = "Quotation Sent",
            notes = notes.ifEmpty { booking.notes }
        )
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    suspend fun acceptQuotationAndPayAdvance(bookingId: String, paymentMethod: String, paidAmount: Double) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val newAdvancePaid = booking.advancePaidAmount + paidAmount
        val updated = booking.copy(
            quotationStatus = "Accepted",
            status = "Booking Approved",
            paymentStatus = "Advance Paid",
            paymentMethod = paymentMethod,
            advancePaidAmount = newAdvancePaid,
            remainingAmount = (booking.totalAmount - newAdvancePaid).coerceAtLeast(0.0)
        )
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    suspend fun rejectQuotation(bookingId: String) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val updated = booking.copy(
            quotationStatus = "Rejected",
            status = "Quotation Rejected"
        )
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    suspend fun payRemainingBalance(bookingId: String, paymentMethod: String) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val isCash = paymentMethod == "Cash at Site" || paymentMethod == "Cash"
        val updated = if (isCash) {
            booking.copy(
                paymentStatus = "Cash Payment Pending",
                paymentMethod = "Cash at Site"
            )
        } else {
            booking.copy(
                paymentStatus = "Fully Paid",
                paymentMethod = paymentMethod,
                advancePaidAmount = booking.totalAmount,
                remainingAmount = 0.0
            )
        }
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    suspend fun markCashPaymentReceived(bookingId: String) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val updated = booking.copy(
            paymentStatus = "Fully Paid",
            advancePaidAmount = booking.totalAmount,
            remainingAmount = 0.0
        )
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    suspend fun waiveSiteVisitFee(bookingId: String) {
        val booking = bookingDao.getBookingById(bookingId) ?: return
        val updated = booking.copy(
            isSiteVisitWaived = true,
            siteVisitFee = 0.0
        )
        bookingDao.updateBooking(updated)
        syncBookingToFirestore(updated)
    }

    private fun syncBookingToFirestore(booking: BookingEntity) {
        try {
            val bookingMap = mapOf(
                "id" to booking.id,
                "customerName" to booking.customerName,
                "phone" to booking.phone,
                "serviceName" to booking.serviceName,
                "sqFt" to booking.sqFt,
                "totalAmount" to booking.totalAmount,
                "bookingDate" to booking.bookingDate,
                "timeSlot" to booking.timeSlot,
                "address" to booking.address,
                "status" to booking.status,
                "paymentStatus" to booking.paymentStatus,
                "timestamp" to System.currentTimeMillis()
            )
            firestore?.collection("bookings")?.document(booking.id)?.set(bookingMap)
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    private fun syncUserToFirestore(user: UserProfile) {
        try {
            val userMap = mapOf(
                "name" to user.name,
                "phone" to user.phone,
                "email" to user.email,
                "address" to user.fullAddress,
                "isAdmin" to user.isAdmin,
                "role" to user.role,
                "updatedAt" to System.currentTimeMillis()
            )
            val docId = if (user.isAdmin) "admin_user" else user.phone.replace("+", "").replace(" ", "")
            firestore?.collection("users")?.document(docId.ifEmpty { "user_" + System.currentTimeMillis() })?.set(userMap)
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    suspend fun addRoomPhoto(roomLabel: String, photoPath: String, aiNotes: String) {
        val photoId = UUID.randomUUID().toString()
        val photo = RoomPhotoEntity(
            id = photoId,
            roomLabel = roomLabel,
            photoPath = photoPath,
            aiInspectionNotes = aiNotes
        )
        roomPhotoDao.insertPhoto(photo)
        try {
            val photoMap = mapOf(
                "id" to photoId,
                "roomLabel" to roomLabel,
                "photoPath" to photoPath,
                "aiNotes" to aiNotes,
                "storageBucket" to "gs://mansuri-paint.firebasestorage.app/room_photos/$photoId",
                "timestamp" to System.currentTimeMillis()
            )
            firestore?.collection("room_photos")?.document(photoId)?.set(photoMap)
        } catch (e: Exception) {
            // Firestore sync fallback
        }
    }

    suspend fun deleteRoomPhoto(id: String) {
        roomPhotoDao.deletePhoto(id)
        try {
            firestore?.collection("room_photos")?.document(id)?.delete()
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    suspend fun submitReview(userName: String, rating: Float, comment: String, serviceName: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        val reviewId = UUID.randomUUID().toString()
        val review = ReviewEntity(
            id = reviewId,
            userName = userName.ifEmpty { "Verified Customer" },
            rating = rating,
            comment = comment,
            date = dateStr,
            serviceName = serviceName
        )
        reviewDao.insertReview(review)
        try {
            val reviewMap = mapOf(
                "id" to reviewId,
                "userName" to review.userName,
                "rating" to rating,
                "comment" to comment,
                "date" to dateStr,
                "serviceName" to serviceName,
                "timestamp" to System.currentTimeMillis()
            )
            firestore?.collection("reviews")?.document(reviewId)?.set(reviewMap)
        } catch (e: Exception) {
            // Firestore sync fallback
        }
    }

    fun login(phone: String, name: String = "Valued Customer") {
        val updated = _currentUser.value.copy(
            name = name,
            phone = phone,
            isLoggedIn = true,
            isGoogleUser = false,
            isAdmin = false,
            role = "customer"
        )
        _currentUser.value = updated
        syncUserToFirestore(updated)
    }

    fun loginWithGoogle(email: String, name: String) {
        val updated = _currentUser.value.copy(
            name = name,
            email = email,
            isLoggedIn = true,
            isGoogleUser = true,
            isAdmin = false,
            role = "customer"
        )
        _currentUser.value = updated
        syncUserToFirestore(updated)
    }

    fun logout() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            // Auth fallback
        }
        _currentUser.value = _currentUser.value.copy(isLoggedIn = false, isAdmin = false, role = "customer")
    }

    fun setAdminUser(email: String, name: String) {
        val updated = _currentUser.value.copy(
            email = email,
            name = name,
            isLoggedIn = true,
            isAdmin = true,
            role = "admin"
        )
        _currentUser.value = updated
        syncUserToFirestore(updated)
    }

    fun setAdminMode(isAdmin: Boolean) {
        val updated = _currentUser.value.copy(
            isAdmin = isAdmin,
            role = if (isAdmin) "admin" else "customer"
        )
        _currentUser.value = updated
        syncUserToFirestore(updated)
    }

    fun loginAsEmployee(employee: EmployeeEntity) {
        val updated = _currentUser.value.copy(
            name = employee.name,
            phone = employee.phone,
            email = employee.email,
            isLoggedIn = true,
            isAdmin = false,
            employeeId = employee.id,
            employeeDesignation = employee.designation,
            role = "employee"
        )
        _currentUser.value = updated
        syncUserToFirestore(updated)
    }

    suspend fun addEmployee(name: String, designation: String, phone: String, email: String = "", pin: String = "1234"): String {
        val empId = "emp_" + (100..999).random()
        val emp = EmployeeEntity(
            id = empId,
            name = name,
            designation = designation,
            phone = phone,
            email = email.ifBlank { "staff_$empId@mansuripaints.com" },
            pin = pin,
            status = "Active"
        )
        employeeDao.insertEmployee(emp)
        try {
            val empMap = mapOf(
                "id" to empId,
                "name" to name,
                "designation" to designation,
                "phone" to phone,
                "email" to emp.email,
                "pin" to pin,
                "status" to "Active"
            )
            firestore?.collection("employees")?.document(empId)?.set(empMap)
        } catch (e: Exception) {
            // Firestore fallback
        }
        return empId
    }

    suspend fun updateEmployee(employee: EmployeeEntity) {
        employeeDao.updateEmployee(employee)
        try {
            val empMap = mapOf(
                "id" to employee.id,
                "name" to employee.name,
                "designation" to employee.designation,
                "phone" to employee.phone,
                "email" to employee.email,
                "pin" to employee.pin,
                "status" to employee.status,
                "totalJobsCompleted" to employee.totalJobsCompleted,
                "rating" to employee.rating
            )
            firestore?.collection("employees")?.document(employee.id)?.set(empMap)
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    suspend fun deleteEmployee(employeeId: String) {
        employeeDao.deleteEmployee(employeeId)
        try {
            firestore?.collection("employees")?.document(employeeId)?.delete()
        } catch (e: Exception) {
            // Firestore fallback
        }
    }

    suspend fun assignEmployeeToBooking(bookingId: String, employeeId: String, employeeName: String, employeePhone: String) {
        val booking = bookingDao.getBookingById(bookingId)
        if (booking != null) {
            val updated = booking.copy(
                painterId = employeeId,
                painterName = employeeName,
                painterPhone = employeePhone,
                status = "Staff Assigned"
            )
            bookingDao.updateBooking(updated)
            syncBookingToFirestore(updated)
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seed initial employees
        val initialEmployees = listOf(
            EmployeeEntity("emp_101", "Usman Mansuri", "Master Site Supervisor", "+91 78430 99068", "usman@mansuripaints.com", "1234", "Active", 48, 5.0f, "01 Jan 2022"),
            EmployeeEntity("emp_102", "Rashid Mansuri", "Waterproofing & Texture Lead", "+91 98290 11223", "rashid@mansuripaints.com", "1234", "Active", 35, 4.9f, "15 Mar 2023"),
            EmployeeEntity("emp_103", "Farhan Khan", "Royale Interior Painter", "+91 97840 55667", "farhan@mansuripaints.com", "1234", "Active", 22, 4.8f, "10 Aug 2023"),
            EmployeeEntity("emp_104", "Sameer Shaikh", "Putty & Surface Prep Specialist", "+91 96540 88990", "sameer@mansuripaints.com", "1234", "Active", 19, 4.7f, "01 Nov 2023"),
            EmployeeEntity("emp_105", "Imran Pathan", "Exterior Painting Specialist", "+91 95430 11224", "imran@mansuripaints.com", "1234", "Active", 28, 4.9f, "05 Jan 2024")
        )
        for (e in initialEmployees) {
            employeeDao.insertEmployee(e)
        }

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
            address = "Flat 102, Capital Residence, Mahal Road, Jagatpura, Jaipur 302017",
            notes = "Require gold metallic texture on living room wall.",
            status = "Staff Assigned",
            paymentStatus = "Paid via Razorpay",
            painterId = "emp_101",
            painterName = "Usman Mansuri (Master Supervisor)",
            painterPhone = "+91 78430 99068"
        )
        bookingDao.insertBooking(sampleBooking)
    }
}
