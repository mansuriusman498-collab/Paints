package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaintService
import com.example.data.models.UserProfile
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
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
fun BookPainterScreen(
    services: List<PaintService>,
    selectedService: PaintService?,
    userProfile: UserProfile,
    onConfirmBooking: (
        name: String,
        phone: String,
        serviceName: String,
        sqFt: Double,
        totalAmount: Double,
        date: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentStatus: String
    ) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    var serviceName by remember { mutableStateOf(selectedService?.title ?: services.first().title) }
    var sqFtInput by remember { mutableStateOf("800") }
    var nameInput by remember { mutableStateOf(userProfile.name) }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }
    var addressInput by remember { mutableStateOf(userProfile.address) }
    var dateInput by remember { mutableStateOf("2026-08-05") }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM - 01:00 PM") }
    var selectedPaymentMethod by remember { mutableStateOf("Razorpay") } // Razorpay or Cash
    var notesInput by remember { mutableStateOf("Please bring surface cover sheet for living room sofa.") }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    val currentServiceObj = services.find { it.title == serviceName } ?: services.first()
    val parsedSqFt = sqFtInput.toDoubleOrNull() ?: 0.0
    val totalEstimatedPrice = parsedSqFt * currentServiceObj.pricePerSqFt

    val timeSlots = listOf("09:00 AM - 12:00 PM", "10:00 AM - 01:00 PM", "02:00 PM - 05:00 PM")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Book a Master Painter",
                subtitle = "Urban Company Quality • On-Time Guarantee",
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Service Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = serviceName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Service", color = PureWhite.copy(alpha = 0.7f)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = CardBorderDark
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                services.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.title} (₹${s.pricePerSqFt.toInt()}/sq ft)") },
                                        onClick = {
                                            serviceName = s.title
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sqFtInput,
                            onValueChange = { sqFtInput = it },
                            label = { Text("Total Wall Area (Sq Ft)", color = PureWhite.copy(alpha = 0.7f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorderDark
                            )
                        )
                    }
                }

                // Customer Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Customer & Location Info",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Customer Name", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Contact Phone", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            label = { Text("Service Address", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                    }
                }

                // Date & Time Slot
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Date & Arrival Time",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text("Preferred Date (YYYY-MM-DD)", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select Time Slot:",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        timeSlots.forEach { slot ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTimeSlot = slot },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedTimeSlot == slot,
                                    onClick = { selectedTimeSlot = slot },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                                Text(text = slot, style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                            }
                        }
                    }
                }

                // Payment Method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Option",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "Razorpay" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == "Razorpay",
                                onClick = { selectedPaymentMethod = "Razorpay" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Online Payment via Razorpay", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                Text("Instant Receipt & Cashbacks", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.6f))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = "Cash" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == "Cash",
                                onClick = { selectedPaymentMethod = "Cash" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Pay Cash After Completion", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                Text("Pay after inspecting final quality", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // Total Summary Card
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
                            Text(text = "TOTAL BOOKING VALUE:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                            Text(text = "₹${totalEstimatedPrice.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = GoldPrimary)
                        }
                        Text(text = "*Includes 100% masking protection & deep cleaning post job", style = MaterialTheme.typography.labelSmall, color = GoldMetallic)
                    }
                }

                LuxuryGoldButton(
                    text = "CONFIRM & BOOK PAINTER",
                    onClick = {
                        val finalPayStatus = if (selectedPaymentMethod == "Razorpay") "Paid via Razorpay" else "Cash on Completion"
                        onConfirmBooking(
                            nameInput,
                            phoneInput,
                            serviceName,
                            parsedSqFt,
                            totalEstimatedPrice,
                            dateInput,
                            selectedTimeSlot,
                            addressInput,
                            notesInput,
                            finalPayStatus
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
