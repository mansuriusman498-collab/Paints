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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
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
    onUpdateStatus: (id: String, newStatus: String) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: (phone: String) -> Unit,
    onCallClick: () -> Unit
) {
    val totalRevenue = bookings.sumOf { it.totalAmount }
    val activeCount = bookings.count { it.status != "Completed" }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Admin Control Panel",
                subtitle = "Manage Bookings & Painter Dispatch",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = { onWhatsAppClick("+917843099068") }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Analytics Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(
                            title = "Total Orders",
                            value = "${bookings.size}",
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Active Jobs",
                            value = "$activeCount",
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            title = "Revenue",
                            value = "₹${(totalRevenue / 1000).toInt()}k",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Manage Customer Bookings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                }

                items(bookings) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onUpdateStatus = { newStatus -> onUpdateStatus(booking.id, newStatus) },
                        onWhatsApp = { onWhatsAppClick(booking.phone) }
                    )
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
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminBookingCard(
    booking: BookingEntity,
    onUpdateStatus: (String) -> Unit,
    onWhatsApp: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf(
        "Requested",
        "Confirmed",
        "Painter Assigned",
        "Material Delivered",
        "Painting In Progress",
        "Quality Inspection",
        "Completed"
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
                    Text(text = "${booking.customerName} • ${booking.phone}", style = MaterialTheme.typography.bodySmall, color = PureWhite)
                }

                IconButton(
                    onClick = onWhatsApp,
                    modifier = Modifier.background(Color(0xFF25D366), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.FormatPaint, contentDescription = "WhatsApp Customer", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${booking.serviceName} (${booking.sqFt.toInt()} sq ft)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
            Text(text = "Address: ${booking.address}", style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Update Status:", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)

            Spacer(modifier = Modifier.height(4.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = booking.status,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorderDark
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statuses.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st) },
                            onClick = {
                                onUpdateStatus(st)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
