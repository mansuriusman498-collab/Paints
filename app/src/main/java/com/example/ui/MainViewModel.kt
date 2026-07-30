package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.models.PaintCostEstimate
import com.example.data.models.PaintService
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
        _activeScreen.value = screen
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

    fun createBooking(
        customerName: String,
        phone: String,
        serviceName: String,
        sqFt: Double,
        totalAmount: Double,
        bookingDate: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentStatus: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val bookingId = repository.createBooking(
                customerName = customerName,
                phone = phone,
                serviceName = serviceName,
                sqFt = sqFt,
                totalAmount = totalAmount,
                bookingDate = bookingDate,
                timeSlot = timeSlot,
                address = address,
                notes = notes,
                paymentStatus = paymentStatus
            )
            _toastMessage.value = "Booking #$bookingId Confirmed Successfully!"
            onSuccess(bookingId)
        }
    }

    fun updateBookingStatus(id: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, newStatus)
            _toastMessage.value = "Status updated to $newStatus"
        }
    }

    fun uploadPhoto(label: String, path: String, notes: String) {
        viewModelScope.launch {
            repository.addRoomPhoto(label, path, notes)
            _toastMessage.value = "Room Photo Uploaded for AI Inspection"
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

    fun logout() {
        repository.logout()
        _toastMessage.value = "Logged Out"
        navigateTo("login")
    }

    fun toggleAdminMode(isAdmin: Boolean) {
        repository.setAdminMode(isAdmin)
        _toastMessage.value = if (isAdmin) "Admin Dashboard Activated" else "Switched to Client Mode"
    }

    fun showPdfQuotation(estimate: PaintCostEstimate) {
        _pdfQuotationModal.value = estimate
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
