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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.BookingEntity
import com.example.data.models.PaintCostEstimate
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.components.MansuriTopAppBar
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxBlack
import com.example.ui.theme.PureWhite

@Composable
fun BookingDetailsScreen(
    booking: BookingEntity?,
    onShowPdfQuotation: (PaintCostEstimate) -> Unit,
    onUploadRoomPhotos: () -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: (bookingId: String) -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Booking Details #${booking?.id ?: ""}",
                subtitle = "Live Project Tracker & Quotation",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = { onWhatsAppClick(booking?.id ?: "") }
            )

            if (booking == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No booking selected.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PureWhite
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Badge & ID
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${booking.id}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = GoldMetallic
                                )

                                Box(
                                    modifier = Modifier
                                        .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = booking.status,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = GoldPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = booking.serviceName,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )

                            Text(
                                text = "Covered Area: ${booking.sqFt.toInt()} sq ft",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PureWhite.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount:", style = MaterialTheme.typography.bodyMedium, color = PureWhite.copy(alpha = 0.7f))
                                Text("₹${booking.totalAmount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Status:", style = MaterialTheme.typography.bodyMedium, color = PureWhite.copy(alpha = 0.7f))
                                Text(booking.paymentStatus, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF4CAF50))
                            }
                        }
                    }

                    // Timeline Steps
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Project Timeline",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val steps = listOf("Requested", "Confirmed", "Painter Assigned", "In Progress", "Completed")
                            val currentStepIndex = steps.indexOf(booking.status).coerceAtLeast(1)

                            steps.forEachIndexed { index, stepName ->
                                val isCompleted = index <= currentStepIndex
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isCompleted) GoldPrimary else CardBorderDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OnyxBlack, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = stepName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isCompleted) PureWhite else PureWhite.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }

                    // Assigned Painter Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Assigned Master Painter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(booking.painterName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                    Text(booking.painterPhone, style = MaterialTheme.typography.bodySmall, color = GoldPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(booking.address, style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Action Buttons
                    LuxuryGoldButton(
                        text = "DOWNLOAD QUOTATION PDF",
                        onClick = {
                            val estimate = PaintCostEstimate(
                                areaSqFt = booking.sqFt,
                                roomsCount = (booking.sqFt / 300.0).toInt().coerceAtLeast(1),
                                serviceTitle = booking.serviceName,
                                ratePerSqFt = if (booking.sqFt > 0) booking.totalAmount / booking.sqFt else 25.0,
                                paintMaterialCost = booking.totalAmount * 0.65,
                                laborCost = booking.totalAmount * 0.35,
                                totalCost = booking.totalAmount,
                                estimatedLiters = (booking.sqFt / 100.0) * 1.5,
                                estimatedDays = (booking.sqFt / 400.0).toInt().coerceAtLeast(1)
                            )
                            onShowPdfQuotation(estimate)
                        },
                        icon = Icons.Default.Description
                    )

                    LuxuryOutlinedButton(
                        text = "Upload Room Images for Inspection",
                        onClick = onUploadRoomPhotos,
                        icon = Icons.Default.AddAPhoto
                    )

                    LuxuryOutlinedButton(
                        text = "Inquire on WhatsApp",
                        onClick = { onWhatsAppClick(booking.id) },
                        icon = Icons.Default.FormatPaint
                    )
                }
            }
        }
    }
}
