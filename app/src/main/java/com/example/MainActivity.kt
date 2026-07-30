package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.MansuriBottomNavigation
import com.example.ui.components.PdfQuotationModalDialog
import com.example.ui.components.ToastNotificationBanner
import com.example.ui.screens.AboutUsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.BookPainterScreen
import com.example.ui.screens.BookingDetailsScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ContactUsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.UploadPhotosScreen
import com.example.ui.theme.MansuriPaintsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val isDarkTheme by viewModel.isDarkTheme.collectAsState()

      MansuriPaintsTheme(darkTheme = isDarkTheme) {
        MansuriAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MansuriAppContent(viewModel: MainViewModel) {
  val context = LocalContext.current
  val activeScreen by viewModel.activeScreen.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val bookings by viewModel.bookings.collectAsState()
  val roomPhotos by viewModel.roomPhotos.collectAsState()
  val reviews by viewModel.reviews.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()
  val pdfModalEstimate by viewModel.pdfQuotationModal.collectAsState()
  val selectedService by viewModel.selectedService.collectAsState()
  val currentEstimate by viewModel.currentEstimate.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()

  val showBottomNav = activeScreen in listOf("home", "services", "calculator", "my_bookings", "profile")

  val activeBooking = bookings.firstOrNull { it.status != "Completed" } ?: bookings.firstOrNull()

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
        if (showBottomNav) {
          MansuriBottomNavigation(
              currentScreen = activeScreen,
              onNavigate = { viewModel.navigateTo(it) }
          )
        }
      }
  ) { innerPadding ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
      Crossfade(targetState = activeScreen, label = "ScreenTransition") { screen ->
        when (screen) {
          "splash" -> SplashScreen(
              onSplashComplete = {
                if (userProfile.isLoggedIn) viewModel.navigateTo("home") else viewModel.navigateTo("login")
              }
          )

          "login" -> LoginScreen(
              onLoginPhone = { phone, name -> viewModel.loginWithPhone(phone, name) },
              onLoginGoogle = { email, name -> viewModel.loginWithGoogle(email, name) },
              onNavigateToSignUp = { viewModel.navigateTo("signup") },
              onNavigateToAdminLogin = { viewModel.navigateTo("admin_login") },
              onSkip = { viewModel.navigateTo("home") }
          )

          "signup" -> SignUpScreen(
              onSignUp = { name, phone, email, address ->
                viewModel.registerCustomer(name, phone, email, address)
              },
              onBackToLogin = { viewModel.navigateTo("login") }
          )

          "home" -> HomeScreen(
              userName = userProfile.name,
              services = viewModel.repository.getServices(),
              activeBooking = activeBooking,
              reviews = reviews,
              onNavigate = { viewModel.navigateTo(it) },
              onSelectService = { viewModel.selectService(it) },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "services" -> ServicesScreen(
              services = viewModel.repository.getServices(),
              onSelectServiceForBooking = {
                viewModel.selectService(it)
                viewModel.navigateTo("book_painter")
              },
              onSelectServiceForCalculator = {
                viewModel.selectService(it)
                viewModel.navigateTo("calculator")
              },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "calculator" -> CalculatorScreen(
              services = viewModel.repository.getServices(),
              currentEstimate = currentEstimate,
              onUpdateEstimate = { area, rooms, title, rate ->
                viewModel.updateCalculator(area, rooms, title, rate)
              },
              onShowPdfModal = { estimate -> viewModel.showPdfQuotation(estimate) },
              onBookNow = { viewModel.navigateTo("book_painter") },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "book_painter" -> BookPainterScreen(
              services = viewModel.repository.getServices(),
              selectedService = selectedService,
              userProfile = userProfile,
              onConfirmBooking = { name, phone, serviceName, sqFt, totalAmount, date, timeSlot, address, notes, payStatus ->
                viewModel.createBooking(
                    name, phone, serviceName, sqFt, totalAmount, date, timeSlot, address, notes, payStatus
                ) {
                  viewModel.navigateTo("order_tracking")
                }
              },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "upload_photos" -> UploadPhotosScreen(
              photos = roomPhotos,
              onUploadPhoto = { label, path, notes -> viewModel.uploadPhoto(label, path, notes) },
              onDeletePhoto = { id -> viewModel.deletePhoto(id) },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "order_tracking" -> OrderTrackingScreen(
              booking = activeBooking,
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "my_bookings" -> MyBookingsScreen(
              bookings = bookings,
              onTrackBooking = { booking ->
                viewModel.selectBooking(booking)
                viewModel.navigateTo("booking_details")
              },
              onShowPdfQuotation = { estimate -> viewModel.showPdfQuotation(estimate) },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { bookingId ->
                viewModel.openWhatsApp(context, "Hello Mansuri Paints, I am asking about booking #$bookingId.")
              },
              onCallClick = { viewModel.makeCall(context) }
          )

          "booking_details" -> {
            val sBooking by viewModel.selectedBooking.collectAsState()
            BookingDetailsScreen(
                booking = sBooking ?: activeBooking,
                onShowPdfQuotation = { estimate -> viewModel.showPdfQuotation(estimate) },
                onUploadRoomPhotos = { viewModel.navigateTo("upload_photos") },
                onBack = { viewModel.navigateTo("my_bookings") },
                onWhatsAppClick = { bId -> viewModel.openWhatsApp(context, "Inquiry about booking #$bId") },
                onCallClick = { viewModel.makeCall(context) }
            )
          }

          "profile" -> ProfileScreen(
              userProfile = userProfile,
              isDarkTheme = isDarkTheme,
              onToggleTheme = { viewModel.toggleTheme() },
              onToggleAdminMode = { viewModel.toggleAdminMode(it) },
              onNavigate = { viewModel.navigateTo(it) },
              onLogout = { viewModel.logout() },
              onBack = { viewModel.navigateTo("home") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "admin_login" -> AdminLoginScreen(
              onAdminLogin = { email, pass ->
                viewModel.adminLogin(email, pass)
              },
              onBackToCustomerLogin = { viewModel.navigateTo("login") }
          )

          "admin_dashboard" -> {
            if (!userProfile.isAdmin || userProfile.role != "admin") {
              androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.navigateTo("login")
              }
            } else {
              AdminDashboardScreen(
                  bookings = bookings,
                  onUpdateStatus = { id, newStatus -> viewModel.updateBookingStatus(id, newStatus) },
                  onBack = { viewModel.navigateTo("profile") },
                  onWhatsAppClick = { phone -> viewModel.openWhatsApp(context, "Hello from Mansuri Admin") },
                  onCallClick = { viewModel.makeCall(context) }
              )
            }
          }

          "settings" -> SettingsScreen(
              isDarkTheme = isDarkTheme,
              onToggleTheme = { viewModel.toggleTheme() },
              onNavigate = { viewModel.navigateTo(it) },
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "about_us" -> AboutUsScreen(
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "contact_us" -> ContactUsScreen(
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) },
              onSubmitInquiry = { name, phone, msg ->
                viewModel.openWhatsApp(context, "Inquiry from $name ($phone): $msg")
              }
          )
        }
      }

      // Toast Banner Overlay
      ToastNotificationBanner(
          message = toastMessage,
          onDismiss = { viewModel.clearToast() }
      )

      // PDF Quotation Modal Dialog
      pdfModalEstimate?.let { est ->
        PdfQuotationModalDialog(
            estimate = est,
            customerName = userProfile.name,
            onDismiss = { viewModel.dismissPdfQuotation() },
            onBookNow = { viewModel.navigateTo("book_painter") }
        )
      }
    }
  }
}
