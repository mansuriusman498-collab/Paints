package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.MansuriTopAppBar
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxBlack
import com.example.ui.theme.PureWhite

@Composable
fun OrderTrackingScreen(
    booking: BookingEntity?,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val activeBooking = booking ?: BookingEntity(
        id = "MP-8821",
        customerName = "Mansuri Client",
        phone = "+91 98765 43210",
        serviceName = "Royal Paint",
        sqFt = 850.0,
        totalAmount = 22950.0,
        bookingDate = "2026-08-05",
        timeSlot = "10:00 AM - 01:00 PM",
        address = "Flat 402, Golden Heights, Station Road",
        notes = "Royal metallic sheen finish on living room wall",
        status = "Painter Assigned",
        paymentStatus = "Paid via Razorpay",
        painterName = "Usman Mansuri & Master Team",
        painterPhone = "+91 78430 99068"
    )

    val steps = listOf(
        "Requested",
        "Confirmed",
        "Painter Assigned",
        "Material Delivered",
        "Painting In Progress",
        "Quality Inspection",
        "Completed"
    )

    val currentStepIndex = steps.indexOf(activeBooking.status).let { if (it < 0) 2 else it }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Live Order Tracking",
                subtitle = "Booking #${activeBooking.id}",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = onWhatsAppClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeBooking.serviceName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PureWhite
                                )
                                Text(
                                    text = "${activeBooking.sqFt.toInt()} sq ft • ${activeBooking.bookingDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldMetallic
                                )
                            }
                            Text(
                                text = "₹${activeBooking.totalAmount.toInt()}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = GoldPrimary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Payment Status:", style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.8f))
                            Text(text = activeBooking.paymentStatus, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                        }
                    }
                }

                // Assigned Painter Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = OnyxBlack)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeBooking.painterName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PureWhite
                                )
                                Text(
                                    text = activeBooking.painterPhone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldPrimary
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onWhatsAppClick,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF25D366), CircleShape)
                            ) {
                                Icon(Icons.Default.FormatPaint, contentDescription = "WhatsApp", tint = Color.White)
                            }
                            IconButton(
                                onClick = onCallClick,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(GoldPrimary, CircleShape)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = OnyxBlack)
                            }
                        }
                    }
                }

                // Step-by-Step Progress Timeline
                Text(
                    text = "Project Timeline",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        steps.forEachIndexed { index, stepName ->
                            val isCompleted = index <= currentStepIndex
                            val isCurrent = index == currentStepIndex

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (isCompleted) GoldPrimary else CardBorderDark,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = OnyxBlack,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PureWhite.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = stepName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isCurrent) GoldPrimary else if (isCompleted) PureWhite else PureWhite.copy(alpha = 0.5f)
                                    )
                                    if (isCurrent) {
                                        Text(
                                            text = "Active Status - In Progress",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldMetallic
                                        )
                                    }
                                }
                            }

                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 13.dp)
                                        .width(2.dp)
                                        .height(16.dp)
                                        .background(if (index < currentStepIndex) GoldPrimary else CardBorderDark)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
