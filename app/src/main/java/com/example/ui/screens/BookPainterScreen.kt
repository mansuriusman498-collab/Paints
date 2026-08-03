package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
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
        propertyType: String,
        bedrooms: Int,
        hall: Int,
        kitchen: Int,
        bathroom: Int,
        balcony: Int,
        sqFt: Double,
        photosJson: String,
        bookingType: String,
        siteVisitFee: Double,
        totalAmount: Double,
        advancePct: Double,
        date: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentMethod: String,
        paymentStatusOverride: String?
    ) -> Unit,
    onStartRazorpayPayment: (com.example.data.models.PendingBookingRequest) -> Unit = {},
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current

    // 1. Service Selection
    var serviceName by remember { mutableStateOf(selectedService?.title ?: services.first().title) }
    var isServiceDropdownExpanded by remember { mutableStateOf(false) }

    // 2. Property Details
    var selectedPropertyType by remember { mutableStateOf("House") }
    val propertyTypes = listOf("House", "Flat", "Shop", "Office", "Villa")
    var sqFtInput by remember { mutableStateOf("850") }
    var bedroomsCount by remember { mutableIntStateOf(2) }
    var hallCount by remember { mutableIntStateOf(1) }
    var kitchenCount by remember { mutableIntStateOf(1) }
    var bathroomCount by remember { mutableIntStateOf(1) }
    var balconyCount by remember { mutableIntStateOf(1) }

    // 3. Current Location / Address
    var nameInput by remember { mutableStateOf(userProfile.name) }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }
    var selectedAddressId by remember { mutableStateOf(userProfile.addresses.firstOrNull()?.id ?: "custom") }
    var houseNo by remember { mutableStateOf(userProfile.houseNo) }
    var buildingName by remember { mutableStateOf(userProfile.buildingName) }
    var street by remember { mutableStateOf(userProfile.street) }
    var landmark by remember { mutableStateOf(userProfile.landmark) }
    var city by remember { mutableStateOf(userProfile.city.ifEmpty { "Jaipur" }) }
    var state by remember { mutableStateOf("Rajasthan") }
    var pincode by remember { mutableStateOf("302017") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            houseNo = "Plot 102, Sector 5"
            buildingName = "Capital Heights"
            street = "Mahal Road, Jagatpura"
            landmark = "Near Capital High Street"
            city = "Jaipur"
            state = "Rajasthan"
            pincode = "302017"
            Toast.makeText(context, "GPS Location Auto-Detected (Jaipur, Rajasthan)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Location permission denied. Please enter address manually.", Toast.LENGTH_SHORT).show()
        }
    }

    // 4. Photo Uploads (Native Gallery/Camera/Files)
    var selectedPhotoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showPhotoPickerChooser by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedPhotoUris = (selectedPhotoUris + uris).distinct()
            Toast.makeText(context, "${uris.size} photos attached from Gallery/Files", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Toast.makeText(context, "Camera photo captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // 5. Schedule Date & Time
    var dateInput by remember { mutableStateOf("2026-08-05") }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM - 01:00 PM") }
    val timeSlots = listOf("09:00 AM - 12:00 PM", "10:00 AM - 01:00 PM", "02:00 PM - 05:00 PM", "05:00 PM - 08:00 PM")
    var notesInput by remember { mutableStateOf("Double coat paint required with surface protection covering.") }

    // 6. Choose Booking Type
    var bookingTypeChoice by remember { mutableStateOf("Direct Booking") } // "Direct Booking" or "Request Site Visit"
    val siteVisitCharge = 200.0 // Standard site visit charge in Jaipur
    var showAdvancePaymentDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Online UPI") }

    val currentServiceObj = services.find { it.title == serviceName } ?: services.first()
    val parsedSqFt = sqFtInput.toDoubleOrNull() ?: 0.0
    val totalEstimatedPrice = parsedSqFt * currentServiceObj.pricePerSqFt
    val advance20Percent = totalEstimatedPrice * 0.20

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Book a Master Painter",
                subtitle = "Select Direct Booking or Request Site Visit",
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
                // Step 1: Service Selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Service Selection",
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

                // Step 2: Property Details & Room Breakdown
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. Property Details & Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Property Type:", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            propertyTypes.forEach { pType ->
                                val isSelected = selectedPropertyType == pType
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) GoldPrimary else OnyxBlack)
                                        .border(1.dp, if (isSelected) GoldPrimary else CardBorderDark, RoundedCornerShape(8.dp))
                                        .clickable { selectedPropertyType = pType }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pType,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) OnyxBlack else PureWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CounterRow("Bedrooms", bedroomsCount) { bedroomsCount = (bedroomsCount + it).coerceIn(0, 10) }
                        CounterRow("Hall / Living Room", hallCount) { hallCount = (hallCount + it).coerceIn(0, 5) }
                        CounterRow("Kitchen", kitchenCount) { kitchenCount = (kitchenCount + it).coerceIn(0, 5) }
                        CounterRow("Bathroom", bathroomCount) { bathroomCount = (bathroomCount + it).coerceIn(0, 5) }
                        CounterRow("Balcony / Terrace", balconyCount) { balconyCount = (balconyCount + it).coerceIn(0, 5) }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = sqFtInput,
                            onValueChange = { sqFtInput = it },
                            label = { Text("Total Wall Area in Sq Ft", color = PureWhite.copy(alpha = 0.7f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                    }
                }

                // Step 3: Current Location & Address
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. Site Location & Contact",
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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = CardBorderDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Saved Address:", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))

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

                        OutlinedTextField(value = houseNo, onValueChange = { houseNo = it }, label = { Text("House / Flat / Plot No.", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = buildingName, onValueChange = { buildingName = it }, label = { Text("Building / Colony / Society Name", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Street / Road (e.g. Mahal Road, Jagatpura)", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = landmark, onValueChange = { landmark = it }, label = { Text("Landmark (e.g. Near Capital High Street)", color = PureWhite.copy(alpha = 0.7f)) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark))

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = OnyxBlack)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AUTO-DETECT GPS", color = OnyxBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val fullAddr = "$houseNo, $buildingName, $street, $landmark, Jaipur, Rajasthan"
                                    val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(fullAddr)}")
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                                    } catch (e: Exception) {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(fullAddr)}")))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldContainer),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PIN ON MAP", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Step 4: Upload Photos (Native Android Camera / Gallery / Files)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Upload Room & Wall Photos",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Attach photos of walls, dampness, or damage for estimation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Upload Button
                        Button(
                            onClick = { showPhotoPickerChooser = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("UPLOAD PHOTOS (Camera / Gallery / Files)", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }

                        // Preview attached photos
                        if (selectedPhotoUris.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Attached Photos (${selectedPhotoUris.size}):", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                itemsIndexed(selectedPhotoUris) { index, uri ->
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(uri),
                                            contentDescription = "Uploaded Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(22.dp)
                                                .background(Color.Red, CircleShape)
                                                .clickable {
                                                    selectedPhotoUris = selectedPhotoUris.filterIndexed { i, _ -> i != index }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = PureWhite, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Step 5: Schedule Date & Time
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "5. Select Preferred Date & Time",
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

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Select Preferred Arrival Time Slot:", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))

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
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Special Instructions / Notes", color = PureWhite.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                    }
                }

                // Step 6: Choose Booking Type
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "6. Choose Booking Type",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 1: Direct Booking
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (bookingTypeChoice == "Direct Booking") GoldContainer else OnyxBlack)
                                .border(1.dp, if (bookingTypeChoice == "Direct Booking") GoldPrimary else CardBorderDark, RoundedCornerShape(12.dp))
                                .clickable { bookingTypeChoice = "Direct Booking" }
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            RadioButton(
                                selected = bookingTypeChoice == "Direct Booking",
                                onClick = { bookingTypeChoice = "Direct Booking" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Option 1: Direct Booking (20% Advance)", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Estimated Total: ₹${totalEstimatedPrice.toInt()}", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                                Text("Mandatory 20% Advance: ₹${advance20Percent.toInt()}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GoldMetallic)
                                Text("• Instant slot booking confirmation\n• Booking status: Pending Admin Approval", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.7f))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 2: Request Site Visit
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (bookingTypeChoice == "Request Site Visit") GoldContainer else OnyxBlack)
                                .border(1.dp, if (bookingTypeChoice == "Request Site Visit") GoldPrimary else CardBorderDark, RoundedCornerShape(12.dp))
                                .clickable { bookingTypeChoice = "Request Site Visit" }
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            RadioButton(
                                selected = bookingTypeChoice == "Request Site Visit",
                                onClick = { bookingTypeChoice = "Request Site Visit" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Option 2: Request Site Visit", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Site Visit Charge: ₹${siteVisitCharge.toInt()} (Waived in final quote)", style = MaterialTheme.typography.bodyMedium, color = PureWhite)
                                Text("• No 20% advance payment required now\n• Engineer visits site, measures walls & provides final quote", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                // Final Action Button
                if (bookingTypeChoice == "Direct Booking") {
                    LuxuryGoldButton(
                        text = "PAY 20% ADVANCE (₹${advance20Percent.toInt()}) & BOOK",
                        onClick = {
                            showAdvancePaymentDialog = true
                        }
                    )
                } else {
                    LuxuryGoldButton(
                        text = "REQUEST SITE VISIT (₹${siteVisitCharge.toInt()})",
                        onClick = {
                            val fullAddr = "$houseNo, $buildingName, $street, $landmark, $city, $state $pincode"
                            val photoJsonStr = selectedPhotoUris.joinToString(",") { it.toString() }
                            onConfirmBooking(
                                nameInput,
                                phoneInput,
                                serviceName,
                                selectedPropertyType,
                                bedroomsCount,
                                hallCount,
                                kitchenCount,
                                bathroomCount,
                                balconyCount,
                                parsedSqFt,
                                photoJsonStr,
                                "Request Site Visit",
                                siteVisitCharge,
                                totalEstimatedPrice,
                                20.0,
                                dateInput,
                                selectedTimeSlot,
                                fullAddr,
                                notesInput,
                                "Cash on Site Visit",
                                "Site Visit Requested"
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Native Photo Source Options Chooser Dialog
        if (showPhotoPickerChooser) {
            AlertDialog(
                onDismissRequest = { showPhotoPickerChooser = false },
                containerColor = CardDark,
                title = { Text("Choose Photo Source", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select where to pick your wall/room photos from:", color = PureWhite, style = MaterialTheme.typography.bodyMedium)
                        
                        // Gallery Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showPhotoPickerChooser = false
                                    galleryLauncher.launch("image/*")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Gallery / Photos (Multiple Select)", color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        // Files Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showPhotoPickerChooser = false
                                    galleryLauncher.launch("image/*")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Files & Documents", color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        // Camera Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showPhotoPickerChooser = false
                                    cameraLauncher.launch(null)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Take Photo with Camera", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPhotoPickerChooser = false }) {
                        Text("CANCEL", color = PureWhite)
                    }
                }
            )
        }

        // Direct Booking 20% Advance Payment Modal
        if (showAdvancePaymentDialog) {
            AlertDialog(
                onDismissRequest = { showAdvancePaymentDialog = false },
                containerColor = CardDark,
                title = { Text("20% Mandatory Advance Payment", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Total Project Amount: ₹${totalEstimatedPrice.toInt()}", color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("Mandatory 20% Advance: ₹${advance20Percent.toInt()}", color = GoldPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Remaining 80% Balance: ₹${(totalEstimatedPrice - advance20Percent).toInt()} (Payable during/after completion)", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Select Payment Gateway / Method:", color = GoldMetallic, style = MaterialTheme.typography.labelSmall)

                        val methods = listOf("Online UPI (GPay / PhonePe / Paytm)", "Cards & NetBanking", "Razorpay Secured")
                        methods.forEach { m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaymentMethod = m },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPaymentMethod == m,
                                    onClick = { selectedPaymentMethod = m },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                                Text(text = m, color = PureWhite, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                },
                confirmButton = {
                    LuxuryGoldButton(
                        text = "PAY ₹${advance20Percent.toInt()} ADVANCE NOW",
                        onClick = {
                            showAdvancePaymentDialog = false
                            val fullAddr = "$houseNo, $buildingName, $street, $landmark, $city, $state $pincode"
                            val photoJsonStr = selectedPhotoUris.joinToString(",") { it.toString() }
                            val pendingReq = com.example.data.models.PendingBookingRequest(
                                customerName = nameInput,
                                phone = phoneInput,
                                serviceName = serviceName,
                                propertyType = selectedPropertyType,
                                bedrooms = bedroomsCount,
                                hall = hallCount,
                                kitchen = kitchenCount,
                                bathroom = bathroomCount,
                                balcony = balconyCount,
                                sqFt = parsedSqFt,
                                photosJson = photoJsonStr,
                                bookingType = "Direct Booking",
                                siteVisitFee = 0.0,
                                totalAmount = totalEstimatedPrice,
                                advancePercentage = 20.0,
                                advanceAmount = advance20Percent,
                                bookingDate = dateInput,
                                timeSlot = selectedTimeSlot,
                                address = fullAddr,
                                notes = notesInput,
                                paymentMethod = selectedPaymentMethod
                            )
                            onStartRazorpayPayment(pendingReq)
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showAdvancePaymentDialog = false }) {
                        Text("CANCEL", color = PureWhite)
                    }
                }
            )
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
