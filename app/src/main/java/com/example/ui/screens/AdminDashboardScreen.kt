package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.BookingEntity
import com.example.data.models.PaymentConfig
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.components.MansuriTopAppBar
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxBlack
import com.example.ui.theme.PureWhite

@Composable
fun AdminDashboardScreen(
    bookings: List<BookingEntity>,
    paymentConfig: PaymentConfig = PaymentConfig(),
    onUpdateStatus: (id: String, newStatus: String) -> Unit,
    onDeleteBooking: (id: String) -> Unit = {},
    onGeneratePdf: (BookingEntity) -> Unit = {},
    onSavePaymentSettings: (upiId: String, qrCodeUri: Uri?) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    onWhatsAppClick: (phone: String) -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Bookings", "Customers", "Payments", "Payment Settings", "Reports")

    var searchQuery by remember { mutableStateOf("") }
    var painterModalBooking by remember { mutableStateOf<BookingEntity?>(null) }
    var blockedCustomers by remember { mutableStateOf(setOf<String>()) }

    // Analytics calculations
    val totalRevenue = bookings.sumOf { it.totalAmount }
    val pendingJobs = bookings.count { it.status in listOf("Pending", "Requested") }
    val completedJobs = bookings.count { it.status == "Completed" || it.status == "Work Completed" }
    val cancelledJobs = bookings.count { it.status == "Cancelled" }
    val uniqueCustomers = bookings.map { it.phone }.distinct()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Admin Control Panel",
                subtitle = "Revenue • Dispatch • Customers • Reports",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = { onWhatsAppClick("+917843099068") }
            )

            // Top Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardDark,
                contentColor = GoldPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) GoldPrimary else PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> AdminOverviewTab(
                        totalBookings = bookings.size,
                        pendingJobs = pendingJobs,
                        completedJobs = completedJobs,
                        cancelledJobs = cancelledJobs,
                        totalRevenue = totalRevenue,
                        activeCustomers = uniqueCustomers.size,
                        recentBookings = bookings.take(4),
                        onNavigateToBookings = { selectedTab = 1 }
                    )

                    1 -> AdminBookingsTab(
                        bookings = bookings,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onUpdateStatus = onUpdateStatus,
                        onDeleteBooking = onDeleteBooking,
                        onAssignPainter = { painterModalBooking = it },
                        onGeneratePdf = onGeneratePdf,
                        onCall = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        },
                        onWhatsApp = { phone -> onWhatsAppClick(phone) },
                        onOpenMaps = { address ->
                            val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}"))
                                context.startActivity(webIntent)
                            }
                        }
                    )

                    2 -> AdminCustomersTab(
                        bookings = bookings,
                        blockedCustomers = blockedCustomers,
                        onToggleBlock = { phone ->
                            blockedCustomers = if (blockedCustomers.contains(phone)) {
                                blockedCustomers - phone
                            } else {
                                blockedCustomers + phone
                            }
                        },
                        onWhatsApp = { phone -> onWhatsAppClick(phone) }
                    )

                    3 -> AdminPaymentsTab(
                        bookings = bookings,
                        onUpdatePaymentStatus = { id, status ->
                            onUpdateStatus(id, "Payment Status: $status")
                        }
                    )

                    4 -> AdminPaymentSettingsTab(
                        paymentConfig = paymentConfig,
                        onSavePaymentSettings = onSavePaymentSettings
                    )

                    5 -> AdminReportsTab(bookings = bookings)
                }
            }
        }

        // Assign Painter Dialog
        painterModalBooking?.let { booking ->
            var selectedPainter by remember { mutableStateOf("Usman Mansuri (Master Painter)") }
            AlertDialog(
                onDismissRequest = { painterModalBooking = null },
                containerColor = CardDark,
                title = { Text("Assign Painter for #${booking.id}", color = GoldPrimary) },
                text = {
                    Column {
                        Text("Customer: ${booking.customerName}", color = PureWhite)
                        Text("Address: ${booking.address}", color = PureWhite.copy(alpha = 0.7f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select Painter:", color = GoldMetallic, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf(
                            "Usman Mansuri (Master Painter)",
                            "Rashid Khan (Royal Paint Specialist)",
                            "Imran Shaikh (Texture & Putty Expert)",
                            "Suresh Patel (Waterproofing Senior)"
                        ).forEach { painter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPainter = painter }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (selectedPainter == painter) GoldPrimary else PureWhite.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = painter,
                                    color = if (selectedPainter == painter) GoldPrimary else PureWhite,
                                    fontWeight = if (selectedPainter == painter) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    LuxuryGoldButton(
                        text = "ASSIGN PAINTER",
                        onClick = {
                            onUpdateStatus(booking.id, "Painter Assigned")
                            painterModalBooking = null
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { painterModalBooking = null }) {
                        Text("CANCEL", color = PureWhite)
                    }
                }
            )
        }
    }
}

@Composable
private fun AdminOverviewTab(
    totalBookings: Int,
    pendingJobs: Int,
    completedJobs: Int,
    cancelledJobs: Int,
    totalRevenue: Double,
    activeCustomers: Int,
    recentBookings: List<BookingEntity>,
    onNavigateToBookings: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Business Overview & Revenue",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard("Total Bookings", "$totalBookings", Modifier.weight(1f))
                    KpiCard("Pending Jobs", "$pendingJobs", Modifier.weight(1f))
                    KpiCard("Completed", "$completedJobs", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard("Total Revenue", "₹${totalRevenue.toInt()}", Modifier.weight(1.5f))
                    KpiCard("Active Clients", "$activeCustomers", Modifier.weight(1f))
                    KpiCard("Cancelled", "$cancelledJobs", Modifier.weight(1f))
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Booking Activity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldMetallic
                )
                TextButton(onClick = onNavigateToBookings) {
                    Text("View All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(recentBookings) { booking ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Job #${booking.id} • ${booking.customerName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                        Text(text = "${booking.serviceName} • ₹${booking.totalAmount.toInt()}", style = MaterialTheme.typography.bodySmall, color = GoldPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .background(GoldContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = booking.status, style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminBookingsTab(
    bookings: List<BookingEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    onDeleteBooking: (String) -> Unit,
    onAssignPainter: (BookingEntity) -> Unit,
    onGeneratePdf: (BookingEntity) -> Unit,
    onCall: (String) -> Unit,
    onWhatsApp: (String) -> Unit,
    onOpenMaps: (String) -> Unit
) {
    val filteredBookings = bookings.filter {
        it.customerName.contains(searchQuery, true) ||
                it.phone.contains(searchQuery) ||
                it.id.contains(searchQuery, true) ||
                it.serviceName.contains(searchQuery, true)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by Customer, Phone, ID, or Service...", color = PureWhite.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorderDark
                )
            )
        }

        items(filteredBookings) { booking ->
            AdminBookingDetailCard(
                booking = booking,
                onUpdateStatus = { st -> onUpdateStatus(booking.id, st) },
                onDelete = { onDeleteBooking(booking.id) },
                onAssignPainter = { onAssignPainter(booking) },
                onGeneratePdf = { onGeneratePdf(booking) },
                onCall = { onCall(booking.phone) },
                onWhatsApp = { onWhatsApp(booking.phone) },
                onOpenMaps = { onOpenMaps(booking.address) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminBookingDetailCard(
    booking: BookingEntity,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit,
    onAssignPainter: () -> Unit,
    onGeneratePdf: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onOpenMaps: () -> Unit
) {
    var expandedStatus by remember { mutableStateOf(false) }
    val statuses = listOf(
        "Pending",
        "Accepted",
        "Painter Assigned",
        "On The Way",
        "Work Started",
        "Work Completed",
        "Cancelled"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Booking #${booking.id}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldMetallic)
                    Text(text = "${booking.customerName} (${booking.phone})", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCall, modifier = Modifier.background(GoldPrimary, RoundedCornerShape(8.dp)).size(36.dp)) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = OnyxBlack, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onWhatsApp, modifier = Modifier.background(Color(0xFF25D366), RoundedCornerShape(8.dp)).size(36.dp)) {
                        Icon(Icons.Default.FormatPaint, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onOpenMaps, modifier = Modifier.background(GoldContainer, RoundedCornerShape(8.dp)).size(36.dp)) {
                        Icon(Icons.Default.Directions, contentDescription = "Maps", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${booking.serviceName} (${booking.sqFt.toInt()} sq ft) • Total: ₹${booking.totalAmount.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = GoldPrimary
            )
            Text(text = "Address: ${booking.address}", style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.8f))
            Text(text = "Date/Slot: ${booking.bookingDate} (${booking.timeSlot})", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.6f))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LuxuryOutlinedButton(
                    text = "Assign Painter",
                    onClick = onAssignPainter,
                    modifier = Modifier.weight(1f)
                )
                LuxuryOutlinedButton(
                    text = "PDF Quote",
                    onClick = onGeneratePdf,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PictureAsPdf
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = "Status: ${booking.status}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorderDark
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false },
                    modifier = Modifier.background(CardDark)
                ) {
                    statuses.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st, color = PureWhite) },
                            onClick = {
                                onUpdateStatus(st)
                                expandedStatus = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onUpdateStatus("Accepted") }) {
                    Text("ACCEPT", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { onUpdateStatus("Cancelled") }) {
                    Text("REJECT", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DELETE", color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
private fun AdminCustomersTab(
    bookings: List<BookingEntity>,
    blockedCustomers: Set<String>,
    onToggleBlock: (phone: String) -> Unit,
    onWhatsApp: (phone: String) -> Unit
) {
    val customers = bookings.groupBy { it.phone }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Registered Customer Database", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
        }

        items(customers.keys.toList()) { phone ->
            val custBookings = customers[phone] ?: emptyList()
            val sampleCust = custBookings.first()
            val isBlocked = blockedCustomers.contains(phone)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = sampleCust.customerName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                        Text(text = "Phone: $phone", style = MaterialTheme.typography.bodySmall, color = GoldMetallic)
                        Text(text = "Total Orders: ${custBookings.size} • Address: ${sampleCust.address}", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.7f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { onWhatsApp(phone) },
                            modifier = Modifier.background(Color(0xFF25D366), RoundedCornerShape(8.dp)).size(36.dp)
                        ) {
                            Icon(Icons.Default.FormatPaint, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        Button(
                            onClick = { onToggleBlock(phone) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isBlocked) Color(0xFF4CAF50) else Color(0xFFE53935)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isBlocked) "UNBLOCK" else "BLOCK", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPaymentsTab(
    bookings: List<BookingEntity>,
    onUpdatePaymentStatus: (String, String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Payment Transactions (Razorpay, UPI, Cash)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
        }

        items(bookings) { booking ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Order #${booking.id}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldMetallic)
                        Text(text = "₹${booking.totalAmount.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = "Customer: ${booking.customerName} (${booking.phone})", style = MaterialTheme.typography.bodySmall, color = PureWhite)
                    Text(text = "Method: ${booking.paymentMethod} • Status: ${booking.paymentStatus}", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.7f))

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { onUpdatePaymentStatus(booking.id, "Paid") }) {
                            Text("Mark Paid", color = Color(0xFF4CAF50))
                        }
                        TextButton(onClick = { onUpdatePaymentStatus(booking.id, "Refunded") }) {
                            Text("Mark Refunded", color = Color(0xFFE53935))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminReportsTab(bookings: List<BookingEntity>) {
    val totalRev = bookings.sumOf { it.totalAmount }
    val topService = bookings.groupBy { it.serviceName }.maxByOrNull { it.value.size }?.key ?: "Royal Paint"

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Executive Revenue & Analytics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Daily Revenue: ₹${(totalRev / 30).toInt()}", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                    Text("Weekly Revenue: ₹${(totalRev / 4).toInt()}", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                    Text("Monthly Revenue: ₹${totalRev.toInt()}", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Most Ordered Service: $topService", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GoldMetallic)
                }
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
        }
    }
}

@Composable
private fun AdminPaymentSettingsTab(
    paymentConfig: PaymentConfig,
    onSavePaymentSettings: (upiId: String, qrCodeUri: Uri?) -> Unit
) {
    val context = LocalContext.current
    var upiInput by remember(paymentConfig.upiId) { mutableStateOf(paymentConfig.upiId) }
    var selectedQrUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val qrImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedQrUri = uri
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Admin Payment Settings (Firebase Realtime)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Configure UPI ID & UPI QR Code image. Changes sync live to Firebase Firestore (settings/payment_settings) & Firebase Storage (payment_qrs/). Customers will immediately see updated payment details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PureWhite
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Configure Official Admin UPI ID",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldMetallic
                    )

                    OutlinedTextField(
                        value = upiInput,
                        onValueChange = { upiInput = it },
                        label = { Text("Admin UPI ID (e.g. 7843099068@upi)", color = PureWhite.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorderDark
                        )
                    )

                    HorizontalDivider(color = CardBorderDark)

                    Text(
                        text = "2. Upload / Update UPI QR Code Image",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldMetallic
                    )

                    Text(
                        text = "Current Active QR Code:",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite.copy(alpha = 0.8f)
                    )

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(2.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayQr = selectedQrUri?.toString() ?: paymentConfig.qrCodeUrl.ifEmpty {
                            "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=$upiInput&pn=Mansuri%20Paints"
                        }

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(displayQr)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Active UPI QR Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LuxuryOutlinedButton(
                            text = if (selectedQrUri == null) "Select New QR Image" else "Change Image",
                            onClick = { qrImagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.QrCode2
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LuxuryGoldButton(
                        text = if (isSaving) "SAVING TO FIREBASE..." else "SAVE TO FIREBASE & STORAGE",
                        onClick = {
                            if (upiInput.isBlank()) {
                                Toast.makeText(context, "Please enter a valid UPI ID", Toast.LENGTH_SHORT).show()
                            } else {
                                isSaving = true
                                onSavePaymentSettings(upiInput, selectedQrUri)
                                isSaving = false
                            }
                        },
                        icon = Icons.Default.Check
                    )
                }
            }
        }
    }
}
