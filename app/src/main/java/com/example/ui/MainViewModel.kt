package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookingEntity
import com.example.data.models.NotificationItem
import com.example.data.models.PaintCostEstimate
import com.example.data.models.PaintService
import com.example.data.models.PaymentConfig
import com.example.data.models.UserProfile
import com.example.data.repository.MansuriRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = MansuriRepository(
        bookingDao = database.bookingDao(),
        roomPhotoDao = database.roomPhotoDao(),
        reviewDao = database.reviewDao()
    )

    val bookings = repository.allBookings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val roomPhotos = repository.allPhotos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val reviews = repository.allReviews.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userProfile: StateFlow<UserProfile> = repository.currentUser
    val paymentConfig: StateFlow<PaymentConfig> = repository.paymentConfig

    // Notifications Inbox
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "n1",
                title = "Booking Confirmed",
                message = "Your booking #MP-8821 for Royal Paint has been accepted. Painter assigned!",
                timeAgo = "10 mins ago"
            ),
            NotificationItem(
                id = "n2",
                title = "Painter On The Way",
                message = "Usman Mansuri & Master Team is arriving at your site with surface protection sheets.",
                timeAgo = "2 hours ago"
            ),
            NotificationItem(
                id = "n3",
                title = "Official Quotation Ready",
                message = "Download your official Mansuri Paints PDF invoice and 5-Year Warranty card.",
                timeAgo = "1 day ago"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Selected Booking for BookingDetails screen
    private val _selectedBooking = MutableStateFlow<BookingEntity?>(null)
    val selectedBooking: StateFlow<BookingEntity?> = _selectedBooking.asStateFlow()

    // App state
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _activeScreen = MutableStateFlow("splash")
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    private val _selectedService = MutableStateFlow<PaintService?>(repository.getServices().first())
    val selectedService: StateFlow<PaintService?> = _selectedService.asStateFlow()

    private val _currentEstimate = MutableStateFlow(
        repository.calculateCost(areaSqFt = 800.0, roomsCount = 2, serviceTitle = "Royal Paint")
    )
    val currentEstimate: StateFlow<PaintCostEstimate> = _currentEstimate.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _pdfQuotationModal = MutableStateFlow<PaintCostEstimate?>(null)
    val pdfQuotationModal: StateFlow<PaintCostEstimate?> = _pdfQuotationModal.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun navigateTo(screen: String) {
        if (screen == "admin_dashboard") {
            val user = userProfile.value
            if (!user.isAdmin || user.role != "admin") {
                _toastMessage.value = "Access Denied: Customers cannot access Admin Dashboard."
                _activeScreen.value = "login"
                return
            }
        }
        _activeScreen.value = screen
    }

    fun selectBooking(booking: BookingEntity) {
        _selectedBooking.value = booking
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun selectService(service: PaintService) {
        _selectedService.value = service
        updateCalculator(
            areaSqFt = _currentEstimate.value.areaSqFt,
            roomsCount = _currentEstimate.value.roomsCount,
            serviceTitle = service.title,
            ratePerSqFt = service.pricePerSqFt
        )
    }

    fun updateCalculator(areaSqFt: Double, roomsCount: Int, serviceTitle: String, ratePerSqFt: Double? = null) {
        _currentEstimate.value = repository.calculateCost(
            areaSqFt = areaSqFt,
            roomsCount = roomsCount,
            serviceTitle = serviceTitle,
            customRate = ratePerSqFt
        )
    }

    // Admin Config
    private val _advancePercentage = MutableStateFlow(20.0)
    val advancePercentage: StateFlow<Double> = _advancePercentage.asStateFlow()

    private val _baseSiteVisitFee = MutableStateFlow(200.0)
    val baseSiteVisitFee: StateFlow<Double> = _baseSiteVisitFee.asStateFlow()

    private val _siteVisitFeePerKm = MutableStateFlow(15.0)
    val siteVisitFeePerKm: StateFlow<Double> = _siteVisitFeePerKm.asStateFlow()

    private val _enableSiteVisitFee = MutableStateFlow(true)
    val enableSiteVisitFee: StateFlow<Boolean> = _enableSiteVisitFee.asStateFlow()

    fun updateAdminConfig(advancePct: Double, baseFee: Double, perKmRate: Double, enableFee: Boolean) {
        _advancePercentage.value = advancePct
        _baseSiteVisitFee.value = baseFee
        _siteVisitFeePerKm.value = perKmRate
        _enableSiteVisitFee.value = enableFee
        _toastMessage.value = "Admin Settings Updated Successfully!"
    }

    fun createBooking(
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
        bookingType: String = "Direct Booking",
        siteVisitFee: Double = 200.0,
        totalAmount: Double,
        advancePct: Double = 20.0,
        bookingDate: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentMethod: String = "Online UPI",
        paymentStatusOverride: String? = null,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val bookingId = repository.createBooking(
                customerName = customerName,
                phone = phone,
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
                siteVisitFee = if (_enableSiteVisitFee.value) siteVisitFee else 0.0,
                totalAmount = totalAmount,
                advancePercentage = advancePct,
                bookingDate = bookingDate,
                timeSlot = timeSlot,
                address = address,
                notes = notes,
                paymentMethod = paymentMethod,
                paymentStatusOverride = paymentStatusOverride
            )

            val notifTitle = if (bookingType == "Request Site Visit") "Site Visit Requested" else "Direct Booking Submitted"
            val notifMsg = if (bookingType == "Request Site Visit") {
                "Site visit requested for #$bookingId ($propertyType). Admin will assign an engineer shortly."
            } else {
                "Order #$bookingId confirmed with 20% advance payment. Pending Admin Approval."
            }

            addNotification(
                title = notifTitle,
                message = notifMsg
            )
            _toastMessage.value = "$notifTitle (#$bookingId) Successful!"
            onSuccess(bookingId)
        }
    }

    fun sendFinalQuotation(bookingId: String, finalAmount: Double, notes: String) {
        viewModelScope.launch {
            repository.sendFinalQuotation(bookingId, finalAmount, notes)
            addNotification(
                title = "Quotation Sent",
                message = "Admin sent official quotation of ₹${finalAmount.toInt()} for booking #$bookingId. Please review and accept to proceed."
            )
            _toastMessage.value = "Final Quotation of ₹${finalAmount.toInt()} sent to customer!"
        }
    }

    fun acceptQuotationAndPayAdvance(bookingId: String, paymentMethod: String, paidAmount: Double) {
        viewModelScope.launch {
            repository.acceptQuotationAndPayAdvance(bookingId, paymentMethod, paidAmount)
            addNotification(
                title = "Quotation Accepted & Advance Paid",
                message = "Quotation accepted for booking #$bookingId. Advance payment of ₹${paidAmount.toInt()} received successfully! Work can now start."
            )
            _toastMessage.value = "Quotation Accepted & 20% Advance Paid!"
        }
    }

    fun rejectQuotation(bookingId: String) {
        viewModelScope.launch {
            repository.rejectQuotation(bookingId)
            addNotification(
                title = "Quotation Rejected",
                message = "Quotation for booking #$bookingId was rejected by customer."
            )
            _toastMessage.value = "Quotation Rejected."
        }
    }

    fun payRemainingBalance(bookingId: String, paymentMethod: String) {
        viewModelScope.launch {
            repository.payRemainingBalance(bookingId, paymentMethod)
            if (paymentMethod == "Cash at Site" || paymentMethod == "Cash") {
                addNotification(
                    title = "Cash Payment Selected",
                    message = "Cash payment at site selected for remaining balance on #$bookingId. Payment status: Cash Payment Pending."
                )
                _toastMessage.value = "Cash Payment at site selected. Admin/Painter will collect cash."
            } else {
                addNotification(
                    title = "Remaining Payment Completed",
                    message = "Remaining balance paid online for booking #$bookingId. Full payment complete! Thank you!"
                )
                _toastMessage.value = "Full payment completed online!"
            }
        }
    }

    fun markCashPaymentReceived(bookingId: String) {
        viewModelScope.launch {
            repository.markCashPaymentReceived(bookingId)
            addNotification(
                title = "Payment Completed",
                message = "Admin confirmed cash payment received for booking #$bookingId. Order status: Fully Paid."
            )
            _toastMessage.value = "Cash Payment Marked as Received!"
        }
    }

    fun waiveSiteVisitFee(bookingId: String) {
        viewModelScope.launch {
            repository.waiveSiteVisitFee(bookingId)
            addNotification(
                title = "Site Visit Fee Waived",
                message = "Site visit charge waived by Admin for booking #$bookingId."
            )
            _toastMessage.value = "Site Visit Charge Waived!"
        }
    }

    fun updateBookingStatus(id: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, newStatus)
            addNotification(
                title = "Order Status Updated: $newStatus",
                message = "Booking #$id is now marked as $newStatus."
            )
            _toastMessage.value = "Status updated to $newStatus"
        }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch {
            // Updated in repo
            _toastMessage.value = "Booking #$id deleted"
        }
    }

    fun addNotification(title: String, message: String) {
        val newNotif = NotificationItem(
            id = "n_" + System.currentTimeMillis(),
            title = title,
            message = message,
            timeAgo = "Just now"
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun uploadPhoto(label: String, path: String, notes: String) {
        viewModelScope.launch {
            repository.addRoomPhoto(label, path, notes)
            _toastMessage.value = "Room Photo Uploaded for Inspection"
        }
    }

    fun deletePhoto(id: String) {
        viewModelScope.launch {
            repository.deleteRoomPhoto(id)
        }
    }

    fun submitReview(name: String, rating: Float, comment: String, serviceName: String) {
        viewModelScope.launch {
            repository.submitReview(name, rating, comment, serviceName)
            _toastMessage.value = "Thank you for your rating!"
        }
    }

    fun loginWithPhone(phone: String, name: String) {
        repository.login(phone, name)
        _toastMessage.value = "Logged in as $name"
        navigateTo("home")
    }

    fun loginWithGoogle(email: String, name: String) {
        repository.loginWithGoogle(email, name)
        _toastMessage.value = "Google Authentication Successful!"
        navigateTo("home")
    }

    fun registerCustomer(name: String, phone: String, email: String, address: String) {
        repository.login(phone, name)
        _toastMessage.value = "Account created for $name!"
        navigateTo("home")
    }

    fun adminLogin(emailInput: String, passwordInput: String): Boolean {
        return if (emailInput.trim().lowercase() == "admin@mansuripaints.com" && passwordInput == "Mansuri@123") {
            repository.setAdminUser("admin@mansuripaints.com", "Mansuri Admin")
            _toastMessage.value = "Admin Login Successful! Welcome Admin"
            navigateTo("admin_dashboard")
            true
        } else {
            _toastMessage.value = "Invalid Admin Credentials! Access Denied."
            false
        }
    }

    fun logout() {
        repository.logout()
        _toastMessage.value = "Logged Out"
        navigateTo("login")
    }

    fun toggleAdminMode(isAdmin: Boolean) {
        val current = userProfile.value
        if (isAdmin && (current.email != "admin@mansuripaints.com" || current.role != "admin")) {
            _toastMessage.value = "Access Denied: Admin authentication required"
            navigateTo("admin_login")
            return
        }
        repository.setAdminMode(isAdmin)
        _toastMessage.value = if (isAdmin) "Admin Dashboard Activated" else "Switched to Client Mode"
    }

    fun showPdfQuotation(estimate: PaintCostEstimate) {
        _pdfQuotationModal.value = estimate
    }

    fun savePaymentSettings(upiId: String, qrCodeUri: Uri?, context: Context) {
        repository.savePaymentSettings(upiId, qrCodeUri, context) { success, msg ->
            _toastMessage.value = msg
        }
    }

    fun uploadPaymentScreenshot(bookingId: String, screenshotUri: Uri, context: Context, onSuccess: () -> Unit = {}) {
        repository.uploadPaymentScreenshot(bookingId, screenshotUri, context) { success, msg ->
            _toastMessage.value = msg
            if (success) {
                addNotification(
                    title = "Payment Screenshot Uploaded",
                    message = "Screenshot for Order #$bookingId received. Payment verification is in progress."
                )
                onSuccess()
            }
        }
    }

    fun dismissPdfQuotation() {
        _pdfQuotationModal.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun openWhatsApp(context: Context, customMessage: String = "Hello Mansuri Paints, I would like to inquire about painting services and quotation.") {
        try {
            val number = "+917843099068"
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$number&text=" + Uri.encode(customMessage))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            _toastMessage.value = "WhatsApp contact: +91 78430 99068"
        }
    }

    fun makeCall(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+917843099068"))
            context.startActivity(intent)
        } catch (e: Exception) {
            _toastMessage.value = "Call: +91 78430 99068"
        }
    }
}
