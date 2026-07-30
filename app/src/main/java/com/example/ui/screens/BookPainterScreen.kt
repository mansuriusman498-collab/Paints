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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VideoLibrary
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CustomerAddress
import com.example.data.models.PaintService
import com.example.data.models.UserProfile
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
    val context = LocalContext.current

    var serviceName by remember { mutableStateOf(selectedService?.title ?: services.first().title) }
    var sqFtInput by remember { mutableStateOf("850") }
    var bedroomsCount by remember { mutableIntStateOf(2) }
    var hallCount by remember { mutableIntStateOf(1) }
    var kitchenCount by remember { mutableIntStateOf(1) }
    var bathroomCount by remember { mutableIntStateOf(1) }
    var balconyCount by remember { mutableIntStateOf(1) }

    var nameInput by remember { mutableStateOf(userProfile.name) }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }
    var emailInput by remember { mutableStateOf(userProfile.email) }

    // Address Fields
    var selectedAddressId by remember { mutableStateOf(userProfile.addresses.firstOrNull()?.id ?: "custom") }
    var houseNo by remember { mutableStateOf(userProfile.houseNo) }
    var buildingName by remember { mutableStateOf(userProfile.buildingName) }
    var street by remember { mutableStateOf(userProfile.street) }
    var landmark by remember { mutableStateOf(userProfile.landmark) }
    var city by remember { mutableStateOf(userProfile.city) }
    var state by remember { mutableStateOf(userProfile.state) }
    var pincode by remember { mutableStateOf(userProfile.pincode) }

    var dateInput by remember { mutableStateOf("2026-08-05") }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM - 01:00 PM") }
    var budgetInput by remember { mutableStateOf("25000") }
    var descriptionInput by remember { mutableStateOf("Require double coat paint with surface masking for furniture.") }
    var photoCount by remember { mutableIntStateOf(3) }
    var videoUrlInput by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Razorpay") }

    var isServiceDropdownExpanded by remember { mutableStateOf(false) }
    var isAddressDropdownExpanded by remember { mutableStateOf(false) }

    val currentServiceObj = services.find { it.title == serviceName } ?: services.first()
    val parsedSqFt = sqFtInput.toDoubleOrNull() ?: 0.0
    val totalEstimatedPrice = parsedSqFt * currentServiceObj.pricePerSqFt
    val timeSlots = listOf("09:00 AM - 12:00 PM", "10:00 AM - 01:00 PM", "02:00 PM - 05:00 PM", "05:00 PM - 08:00 PM")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Book a Master Painter",
                subtitle = "Urban Company Standard • On-Time Guarantee",
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
                // 1. Service Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Select Paint Service",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = isServiceDropdownExpanded,
                            onExpandedChange = { isServiceDropdownExpanded = !isServiceDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = "$serviceName (₹${currentServiceObj.pricePerSqFt.toInt()}/sq ft)",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServiceDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = CardBorderDark
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = isServiceDropdownExpanded,
                                onDismissRequest = { isServiceDropdownExpanded = false },
                                modifier = Modifier.background(CardDark)
                            ) {
                                services.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.title} (₹${s.pricePerSqFt.toInt()}/sq ft)", color = PureWhite) },
                                        onClick = {
                                            serviceName = s.title
                                            isServiceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Room Configuration & Area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. Property & Room Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CounterRow("Bedrooms", bedroomsCount) { bedroomsCount = (bedroomsCount + it).coerceIn(0, 10) }
                        CounterRow("Hall / Living Room", hallCount) { hallCount = (hallCount + it).coerceIn(0, 5) }
                        CounterRow("Kitchen", kitchenCount) { kitchenCount = (kitchenCount + it).coerceIn(0, 5) }
                        CounterRow("Bathroom", bathroomCount) { bathroomCount = (bathroomCount + it).coerceIn(0, 5) }
                        CounterRow("Balcony / Terrace", balconyCount) { balconyCount = (balconyCount + it).coerceIn(0, 5) }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sqFtInput,
                            onValueChange = { sqFtInput = it },
                            label = { Text("Total Wall Area (Sq Ft)", color = PureWhite.copy(alpha = 0.7f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                    }
                }

                // 3. Customer & Multiple Address Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. Customer & Site Address",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Mobile Number", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = CardBorderDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Saved Address:", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))

                        Spacer(modifier = Modifier.height(6.dp))

                        userProfile.addresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAddressId = addr.id
                                        houseNo = addr.houseNo
                                        buildingName = addr.buildingName
                                        street = addr.street
                                        landmark = addr.landmark
                                        city = addr.city
                                        state = addr.state
                                        pincode = addr.pincode
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedAddressId == addr.id,
                                    onClick = {
                                        selectedAddressId = addr.id
                                        houseNo = addr.houseNo
                                        buildingName = addr.buildingName
                                        street = addr.street
                                        landmark = addr.landmark
                                        city = addr.city
                                        state = addr.state
                                        pincode = addr.pincode
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                                Column {
                                    Text(text = "${addr.label}: ${addr.houseNo}, ${addr.buildingName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                    Text(text = "${addr.street}, ${addr.city} ${addr.pincode}", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.6f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(value = houseNo, onValueChange = { houseNo = it }, label = { Text("House / Flat No.", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = buildingName, onValueChange = { buildingName = it }, label = { Text("Building / Society Name", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street / Area", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val fullAddr = "$houseNo, $buildingName, $street, $landmark, $city, $state $pincode"
                                val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(fullAddr)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                context.startActivity(mapIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SELECT GOOGLE MAPS LIVE LOCATION", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                // 4. Photos & Video Attachment
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Upload Wall Photos & Video",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Photos Attached ($photoCount/20):", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                            Row {
                                IconButton(onClick = { photoCount = (photoCount + 1).coerceAtMost(20) }) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = GoldPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = videoUrlInput,
                            onValueChange = { videoUrlInput = it },
                            label = { Text("Upload Video Link (Optional)", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                    }
                }

                // 5. Date, Time Slot & Budget
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "5. Schedule & Budget",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text("Preferred Start Date (YYYY-MM-DD)", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Arrival Time Slot:", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))

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
                                Text(text = slot, style = MaterialTheme.typography.bodySmall, color = PureWhite)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            label = { Text("Target Budget (₹)", color = PureWhite.copy(alpha = 0.7f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
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
                            Text(text = "TOTAL ESTIMATED AMOUNT:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                            Text(text = "₹${totalEstimatedPrice.toInt()}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = GoldPrimary)
                        }
                        Text(text = "*Includes 100% floor protection masking & post-paint deep cleaning", style = MaterialTheme.typography.labelSmall, color = GoldMetallic)
                    }
                }

                LuxuryGoldButton(
                    text = "CONFIRM & SUBMIT BOOKING",
                    onClick = {
                        val fullAddressStr = "$houseNo, $buildingName, $street, $landmark, $city, $state $pincode"
                        val finalPayStatus = if (selectedPaymentMethod == "Razorpay") "Paid via Razorpay" else "Cash on Completion"
                        onConfirmBooking(
                            nameInput,
                            phoneInput,
                            serviceName,
                            parsedSqFt,
                            totalEstimatedPrice,
                            dateInput,
                            selectedTimeSlot,
                            fullAddressStr,
                            descriptionInput,
                            finalPayStatus
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CounterRow(label: String, count: Int, onDelta: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = PureWhite)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onDelta(-1) },
                modifier = Modifier.background(GoldContainer, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = GoldPrimary)
            }
            Text(
                text = "$count",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = PureWhite,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(
                onClick = { onDelta(1) },
                modifier = Modifier.background(GoldContainer, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GoldPrimary)
            }
        }
    }
}
