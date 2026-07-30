package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PainterDashboardScreen(
    bookings: List<BookingEntity>,
    painterName: String = "Mansuri Master Painter",
    onUpdateStatus: (id: String, status: String) -> Unit,
    onUploadBeforePhoto: (bookingId: String, photoPath: String) -> Unit,
    onUploadAfterPhoto: (bookingId: String, photoPath: String) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: (phone: String) -> Unit,
    onCallClick: (phone: String) -> Unit
) {
    val context = LocalContext.current
    val assignedJobs = bookings.filter { 
        it.painterName.contains("Mansuri", ignoreCase = true) || it.status in listOf("Painter Assigned", "On The Way", "Work Started") 
    }.ifEmpty { bookings }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Painter Workstation",
                subtitle = "Assigned Jobs • GPS Directions • Work Logs",
                onBackClick = onBack,
                onCallClick = { onCallClick("+917843099068") },
                onWhatsAppClick = { onWhatsAppClick("+917843099068") }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column {
                                Text(
                                    text = "Welcome, $painterName",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PureWhite
                                )
                                Text(
                                    text = "${assignedJobs.size} Active Job(s) Assigned Today",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldMetallic
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Assigned Painting Assignments",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                }

                items(assignedJobs) { booking ->
                    PainterJobCard(
                        booking = booking,
                        onUpdateStatus = { newStatus -> onUpdateStatus(booking.id, newStatus) },
                        onOpenMaps = {
                            val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(booking.address)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(booking.address)}"))
                                context.startActivity(webIntent)
                            }
                        },
                        onCall = { onCallClick(booking.phone) },
                        onWhatsApp = { onWhatsAppClick(booking.phone) },
                        onUploadBeforePhoto = { path -> onUploadBeforePhoto(booking.id, path) },
                        onUploadAfterPhoto = { path -> onUploadAfterPhoto(booking.id, path) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PainterJobCard(
    booking: BookingEntity,
    onUpdateStatus: (String) -> Unit,
    onOpenMaps: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onUploadBeforePhoto: (String) -> Unit,
    onUploadAfterPhoto: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var photoInput by remember { mutableStateOf("") }
    var beforePhotoUploaded by remember { mutableStateOf(false) }
    var afterPhotoUploaded by remember { mutableStateOf(false) }
    var progressSlider by remember { mutableFloatStateOf(if (booking.status == "Completed") 100f else if (booking.status == "Work Started") 50f else 10f) }

    val statusOptions = listOf(
        "Painter Assigned",
        "On The Way",
        "Work Started",
        "Work Completed"
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Job #${booking.id}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldMetallic
                    )
                    Text(
                        text = "Customer: ${booking.customerName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier.background(GoldPrimary, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call Customer", tint = OnyxBlack)
                    }
                    IconButton(
                        onClick = onWhatsApp,
                        modifier = Modifier.background(Color(0xFF25D366), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.FormatPaint, contentDescription = "WhatsApp", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Service: ${booking.serviceName} (${booking.sqFt.toInt()} sq ft)",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = GoldPrimary
            )
            Text(
                text = "Address: ${booking.address}",
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.8f)
            )
            Text(
                text = "Scheduled: ${booking.bookingDate} at ${booking.timeSlot}",
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onOpenMaps,
                colors = ButtonDefaults.buttonColors(containerColor = GoldContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Directions, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("NAVIGATE WITH GOOGLE MAPS", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorderDark)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Update Work Progress:",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary
            )

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
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorderDark
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(CardDark)
                ) {
                    statusOptions.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st, color = PureWhite) },
                            onClick = {
                                onUpdateStatus(st)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Completion Progress (${progressSlider.toInt()}%):",
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.7f)
            )

            Slider(
                value = progressSlider,
                onValueChange = { progressSlider = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = GoldPrimary,
                    activeTrackColor = GoldPrimary,
                    inactiveTrackColor = CardBorderDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LuxuryOutlinedButton(
                    text = if (beforePhotoUploaded) "Before Photo ✓" else "Upload Before Photo",
                    onClick = {
                        beforePhotoUploaded = true
                        onUploadBeforePhoto("before_photo_${booking.id}.jpg")
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AddPhotoAlternate
                )

                LuxuryOutlinedButton(
                    text = if (afterPhotoUploaded) "After Photo ✓" else "Upload After Photo",
                    onClick = {
                        afterPhotoUploaded = true
                        onUploadAfterPhoto("after_photo_${booking.id}.jpg")
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (booking.status != "Work Completed") {
                LuxuryGoldButton(
                    text = "MARK WORK COMPLETED",
                    onClick = { onUpdateStatus("Work Completed") }
                )
            }
        }
    }
}
