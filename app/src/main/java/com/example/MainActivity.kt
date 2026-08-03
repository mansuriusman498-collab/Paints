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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaintCostEstimate
import com.example.data.models.RazorpayPaymentState
import com.example.ui.MainViewModel
import com.example.ui.components.LuxuryGoldButton
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
import com.example.ui.screens.CustomerPaymentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.PainterDashboardScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TermsConditionsScreen
import com.example.ui.screens.UploadPhotosScreen
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MansuriPaintsTheme
import com.example.ui.theme.PureWhite

class MainActivity : ComponentActivity(), com.razorpay.PaymentResultWithDataListener {
  private var mainViewModel: MainViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    try {
      com.razorpay.Checkout.preload(applicationContext)
    } catch (e: Exception) {
      // Preload fallback
    }
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      mainViewModel = viewModel
      val isDarkTheme by viewModel.isDarkTheme.collectAsState()

      MansuriPaintsTheme(darkTheme = isDarkTheme) {
        MansuriAppContent(viewModel = viewModel, activity = this)
      }
    }
  }

  override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: com.razorpay.PaymentData?) {
    val paymentId = razorpayPaymentId ?: paymentData?.paymentId ?: "pay_${System.currentTimeMillis()}"
    val orderId = paymentData?.orderId ?: ""
    val signature = paymentData?.signature ?: ""
    mainViewModel?.onRazorpayPaymentSuccess(paymentId, orderId, signature)
  }

  override fun onPaymentError(code: Int, response: String?, paymentData: com.razorpay.PaymentData?) {
    val msg = response ?: "Payment cancelled or failed"
    mainViewModel?.onRazorpayPaymentFailed(code, msg)
  }
}

@Composable
fun MansuriAppContent(viewModel: MainViewModel, activity: ComponentActivity) {
  val context = LocalContext.current
  val activeScreen by viewModel.activeScreen.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val bookings by viewModel.bookings.collectAsState()
  val roomPhotos by viewModel.roomPhotos.collectAsState()
  val reviews by viewModel.reviews.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()
  val pdfModalEstimate by viewModel.pdfQuotationModal.collectAsState()
  val selectedService by viewModel.selectedService.collectAsState()
  val currentEstimate by viewModel.currentEstimate.collectAsState()
  val paymentConfig by viewModel.paymentConfig.collectAsState()
  val isDarkTheme by viewModel.isDarkTheme.collectAsState()
  val razorpayState by viewModel.razorpayPaymentState.collectAsState()

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
                viewModel.navigateTo("login")
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
              onConfirmBooking = { name, phone, serviceName, propertyType, bedrooms, hall, kitchen, bathroom, balcony, sqFt, photosJson, bookingType, siteVisitFee, totalAmount, advancePct, date, timeSlot, address, notes, payMethod, payStatus ->
                viewModel.createBooking(
                    customerName = name,
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
                    siteVisitFee = siteVisitFee,
                    totalAmount = totalAmount,
                    advancePct = advancePct,
                    bookingDate = date,
                    timeSlot = timeSlot,
                    address = address,
                    notes = notes,
                    paymentMethod = payMethod,
                    paymentStatusOverride = payStatus
                ) {
                  viewModel.navigateTo("order_tracking")
                }
              },
              onStartRazorpayPayment = { req ->
                viewModel.startRazorpayCheckout(activity, req)
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
                onNavigateToPayment = { viewModel.navigateTo("customer_payment") },
                onBack = { viewModel.navigateTo("my_bookings") },
                onWhatsAppClick = { bId -> viewModel.openWhatsApp(context, "Inquiry about booking #$bId") },
                onCallClick = { viewModel.makeCall(context) }
            )
          }

          "customer_payment" -> {
            val sBooking by viewModel.selectedBooking.collectAsState()
            CustomerPaymentScreen(
                booking = sBooking ?: activeBooking,
                paymentConfig = paymentConfig,
                onUploadScreenshot = { bId, screenshotUri ->
                  viewModel.uploadPaymentScreenshot(bId, screenshotUri, context) {
                    viewModel.navigateTo("order_tracking")
                  }
                },
                onStartRazorpayPayment = { req ->
                  viewModel.startRazorpayCheckout(activity, req)
                },
                onBack = { viewModel.navigateTo("booking_details") },
                onWhatsAppClick = { viewModel.openWhatsApp(context, "UPI Payment inquiry") },
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
              val employees by viewModel.employees.collectAsState()
              AdminDashboardScreen(
                  bookings = bookings,
                  employees = employees,
                  paymentConfig = paymentConfig,
                  onUpdateStatus = { id, newStatus -> viewModel.updateBookingStatus(id, newStatus) },
                  onDeleteBooking = { id -> viewModel.deleteBooking(id) },
                  onGeneratePdf = { b ->
                    val est = PaintCostEstimate(
                        areaSqFt = b.sqFt,
                        roomsCount = b.bedrooms,
                        serviceTitle = b.serviceName,
                        ratePerSqFt = b.totalAmount / (b.sqFt.coerceAtLeast(1.0)),
                        paintMaterialCost = b.totalAmount * 0.65,
                        laborCost = b.totalAmount * 0.35,
                        totalCost = b.totalAmount,
                        estimatedLiters = (b.sqFt / 100.0) * 1.5,
                        estimatedDays = (b.sqFt / 400.0).toInt().coerceAtLeast(1)
                    )
                    viewModel.showPdfQuotation(est)
                  },
                  onSavePaymentSettings = { upiId, qrUri ->
                    viewModel.savePaymentSettings(upiId, qrUri, context)
                  },
                  onAddEmployee = { name, desig, phone, email, pin ->
                    viewModel.addEmployee(name, desig, phone, email, pin)
                  },
                  onUpdateEmployee = { emp ->
                    viewModel.updateEmployee(emp)
                  },
                  onDeleteEmployee = { empId ->
                    viewModel.deleteEmployee(empId)
                  },
                  onAssignEmployeeToBooking = { bId, emp ->
                    viewModel.assignEmployeeToBooking(bId, emp)
                  },
                  onLoginAsEmployee = { emp ->
                    viewModel.loginAsEmployee(emp)
                  },
                  onBack = { viewModel.navigateTo("profile") },
                  onWhatsAppClick = { phone -> viewModel.openWhatsApp(context, "Hello from Mansuri Admin") },
                  onCallClick = { viewModel.makeCall(context) }
              )
            }
          }

          "painter_panel" -> PainterDashboardScreen(
              bookings = bookings,
              painterName = userProfile.name,
              onUpdateStatus = { id, st -> viewModel.updateBookingStatus(id, st) },
              onUploadBeforePhoto = { id, path -> viewModel.uploadPhoto("Before Painting", path, "Pre-work wall inspection photo") },
              onUploadAfterPhoto = { id, path -> viewModel.uploadPhoto("After Painting", path, "Post-work completed photo") },
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { phone -> viewModel.openWhatsApp(context, "Hello customer") },
              onCallClick = { phone -> viewModel.makeCall(context) }
          )

          "notifications" -> NotificationsScreen(
              notifications = notifications,
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "privacy_policy" -> PrivacyPolicyScreen(
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

          "terms_conditions" -> TermsConditionsScreen(
              onBack = { viewModel.navigateTo("profile") },
              onWhatsAppClick = { viewModel.openWhatsApp(context) },
              onCallClick = { viewModel.makeCall(context) }
          )

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

      // Razorpay Payment Status Overlay Dialogs
      when (val state = razorpayState) {
        is RazorpayPaymentState.Failed -> {
          AlertDialog(
              onDismissRequest = { viewModel.resetRazorpayState() },
              containerColor = CardDark,
              title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFE53935))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Payment Failed or Cancelled", color = Color(0xFFE53935), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
              },
              text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text(text = state.message, color = PureWhite, style = MaterialTheme.typography.bodyMedium)
                  Text(text = "• No booking was created.\n• Your account/money is safe.\n• You can retry payment below.", color = PureWhite.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                }
              },
              confirmButton = {
                LuxuryGoldButton(
                    text = "RETRY PAYMENT",
                    onClick = {
                      viewModel.retryRazorpayCheckout(activity)
                    }
                )
              },
              dismissButton = {
                TextButton(onClick = { viewModel.resetRazorpayState() }) {
                  Text("CANCEL", color = PureWhite)
                }
              }
          )
        }

        is RazorpayPaymentState.Success -> {
          AlertDialog(
              onDismissRequest = {
                viewModel.resetRazorpayState()
                viewModel.navigateTo("order_tracking")
              },
              containerColor = CardDark,
              title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Advance Payment Received!", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
              },
              text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(text = "20% Advance Payment of ₹${state.amountPaid.toInt()} Received Successfully via Razorpay!", color = PureWhite, fontWeight = FontWeight.Bold)
                  Text(text = "Booking ID: #${state.bookingId}", color = GoldMetallic, fontWeight = FontWeight.Bold)
                  Text(text = "Razorpay Payment ID: ${state.razorpayPaymentId}", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                  if (state.orderId.isNotEmpty()) {
                    Text(text = "Razorpay Order ID: ${state.orderId}", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                  }
                  Text(text = "Status: Pending Admin Approval", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                }
              },
              confirmButton = {
                LuxuryGoldButton(
                    text = "VIEW ORDER TRACKING",
                    onClick = {
                      viewModel.resetRazorpayState()
                      viewModel.navigateTo("order_tracking")
                    }
                )
              }
          )
        }

        else -> {}
      }
    }
  }
}
