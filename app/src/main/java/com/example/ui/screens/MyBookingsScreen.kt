package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Room
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.BookingEntity
import com.example.data.models.PaintCostEstimate
import com.example.ui.components.LuxuryCard
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
fun MyBookingsScreen(
    bookings: List<BookingEntity>,
    onTrackBooking: (BookingEntity) -> Unit,
    onShowPdfQuotation: (PaintCostEstimate) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: (bookingId: String) -> Unit,
    onCallClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Active Bookings", "Completed Orders")

    val activeList = bookings.filter { it.status != "Completed" }
    val completedList = bookings.filter { it.status == "Completed" }

    val currentDisplayList = if (selectedTabIndex == 0) activeList else completedList

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "My Bookings & Orders",
                subtitle = "Manage Appointments & Track Quotations",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = { onWhatsAppClick("General Inquiry") }
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CardDark,
                contentColor = GoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = GoldPrimary
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) GoldPrimary else PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            if (currentDisplayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LuxuryCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (selectedTabIndex == 0) "No Active Bookings" else "No Completed Orders",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Book a painter or calculate your wall paint cost to get started.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(currentDisplayList) { booking ->
                        BookingCardItem(
                            booking = booking,
                            onTrack = { onTrackBooking(booking) },
                            onPdf = {
                                val est = PaintCostEstimate(
                                    areaSqFt = booking.sqFt,
                                    roomsCount = (booking.sqFt / 400.0).toInt().coerceAtLeast(1),
                                    serviceTitle = booking.serviceName,
                                    ratePerSqFt = booking.totalAmount / booking.sqFt.coerceAtLeast(1.0),
                                    paintMaterialCost = booking.totalAmount * 0.65,
                                    laborCost = booking.totalAmount * 0.35,
                                    totalCost = booking.totalAmount,
                                    estimatedLiters = (booking.sqFt / 100.0) * 1.5,
                                    estimatedDays = (booking.sqFt / 400.0).toInt().coerceAtLeast(1)
                                )
                                onShowPdfQuotation(est)
                            },
                            onWhatsApp = { onWhatsAppClick(booking.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingCardItem(
    booking: BookingEntity,
    onTrack: () -> Unit,
    onPdf: () -> Unit,
    onWhatsApp: () -> Unit
) {
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
                Text(
                    text = "Booking #${booking.id}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GoldMetallic
                )

                Surface(
                    color = GoldPrimary,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnyxBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = booking.serviceName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PureWhite
            )

            Text(
                text = "${booking.sqFt.toInt()} sq ft • Preferred Date: ${booking.bookingDate}",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = CardBorderDark)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Amount:", style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.7f))
                Text(text = "₹${booking.totalAmount.toInt()} (${booking.paymentStatus})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LuxuryOutlinedButton(
                    text = "PDF Quote",
                    onClick = onPdf,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PictureAsPdf
                )

                LuxuryGoldButton(
                    text = "Track Progress",
                    onClick = onTrack,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
