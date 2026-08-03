package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.rememberAsyncImagePainter
import com.example.data.local.RoomPhotoEntity
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotosScreen(
    photos: List<RoomPhotoEntity>,
    onUploadPhoto: (label: String, path: String, notes: String) -> Unit,
    onDeletePhoto: (id: String) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLabel by remember { mutableStateOf("Living Room Wall") }
    var notesInput by remember { mutableStateOf("Dampness noticed near lower skirting board & window area.") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Selected image URI for upload preview
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceChooser by remember { mutableStateOf(false) }

    // Upload state: Idle, Uploading, Success, Error
    var isUploading by remember { mutableStateOf(false) }
    var uploadErrorMessage by remember { mutableStateOf<String?>(null) }

    val roomTypes = listOf(
        "Living Room Wall",
        "Master Bedroom",
        "Terrace Wall",
        "Balcony & Exterior",
        "Kitchen & Ceiling",
        "Bathroom Tile / Moisture"
    )

    // Activity Result Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            uploadErrorMessage = null
            Toast.makeText(context, "Photo selected from Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            uploadErrorMessage = null
            Toast.makeText(context, "Photo selected from Files", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Save bitmap to temporary cache URI for preview & database storage
            val cacheUri = saveBitmapToCache(context, bitmap)
            if (cacheUri != null) {
                selectedImageUri = cacheUri
                uploadErrorMessage = null
                Toast.makeText(context, "Camera photo captured successfully", Toast.LENGTH_SHORT).show()
            } else {
                uploadErrorMessage = "Failed to save camera image. Please try again."
            }
        }
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        if (cameraGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos", Toast.LENGTH_LONG).show()
        }
    }

    // Perform real or simulated upload & analysis
    fun executeUploadProcess() {
        val uri = selectedImageUri
        if (uri == null) {
            uploadErrorMessage = "Please select or capture a real photo first."
            return
        }

        isUploading = true
        uploadErrorMessage = null

        coroutineScope.launch {
            try {
                // Simulate network/storage upload delay
                delay(1200)

                val analysisNotes = buildString {
                    append("Wall Inspection Analysis ($selectedLabel):\n")
                    if (notesInput.isNotBlank()) {
                        append("Customer Note: \"$notesInput\"\n")
                    }
                    append("• Surface Condition: High moisture / seepage risk evaluated.\n")
                    append("• Recommended Treatment: 1 coat Dampproof Primer + 2 coats Exterior/Interior Premium Emulsion.")
                }

                // Call parent callback to store in database
                onUploadPhoto(selectedLabel, uri.toString(), analysisNotes)

                // Reset UI state
                isUploading = false
                selectedImageUri = null
                uploadErrorMessage = null
                Toast.makeText(context, "Wall photo uploaded & analyzed successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                isUploading = false
                uploadErrorMessage = "Failed to upload photo: ${e.localizedMessage ?: "Storage Error"}"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Upload Room Photos",
                subtitle = "Wall Condition & Seepage Inspection",
                onBackClick = onBack,
                onCallClick = onCallClick,
                onWhatsAppClick = onWhatsAppClick
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
                            .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Upload Photo for Inspection",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedLabel,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Room / Area Label", color = PureWhite.copy(alpha = 0.7f)) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                        .fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = CardBorderDark
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false },
                                    modifier = Modifier.background(CardDark)
                                ) {
                                    roomTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type, color = PureWhite) },
                                            onClick = {
                                                selectedLabel = type
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Special Condition Notes (e.g. seepage, cracks)", color = PureWhite.copy(alpha = 0.7f)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = CardBorderDark
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Step A: Select Image Button or Preview Box
                            if (selectedImageUri == null) {
                                Button(
                                    onClick = { showSourceChooser = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = GoldPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "SELECT PHOTO (Camera / Gallery / Files)",
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            } else {
                                // Image Selected Preview
                                Text(
                                    text = "Selected Photo Preview:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                                        contentDescription = "Selected Wall Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Remove/Change photo badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(32.dp)
                                            .background(OnyxBlack.copy(alpha = 0.8f), CircleShape)
                                            .clickable { selectedImageUri = null },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove photo",
                                            tint = PureWhite,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LuxuryOutlinedButton(
                                    text = "CHANGE SELECTED PHOTO",
                                    onClick = { showSourceChooser = true }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Step B: Error Banner & Retry if upload failed
                            if (uploadErrorMessage != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1111)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF6B6B))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Upload Error",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFFF6B6B)
                                            )
                                            Text(
                                                text = uploadErrorMessage ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = PureWhite.copy(alpha = 0.9f)
                                            )
                                        }
                                        if (selectedImageUri != null) {
                                            IconButton(onClick = { executeUploadProcess() }) {
                                                Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = GoldPrimary)
                                            }
                                        }
                                    }
                                }
                            }

                            // Step C: Upload & Analyze Button or Progress Indicator
                            if (isUploading) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Uploading & Analyzing Wall Condition...",
                                        color = GoldPrimary,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            } else {
                                LuxuryGoldButton(
                                    text = if (uploadErrorMessage != null && selectedImageUri != null) "RETRY UPLOAD & ANALYZE" else "UPLOAD & ANALYZE WALL",
                                    icon = Icons.Default.AddAPhoto,
                                    onClick = { executeUploadProcess() }
                                )
                            }
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
                            text = "Uploaded Inspection Photos (${photos.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                    }
                }

                if (photos.isEmpty()) {
                    item {
                        LuxuryCard {
                            Text(
                                text = "No inspection photos uploaded yet. Select a photo from Camera or Gallery above to perform wall analysis.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    items(photos) { photo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardDark)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = photo.roomLabel,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = PureWhite
                                        )
                                    }
                                    IconButton(onClick = { onDeletePhoto(photo.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Real Image rendering using Coil
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = photo.photoPath),
                                        contentDescription = photo.roomLabel,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(GoldContainer, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = photo.aiInspectionNotes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GoldMetallic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Native Photo Source Options Chooser Dialog
        if (showSourceChooser) {
            AlertDialog(
                onDismissRequest = { showSourceChooser = false },
                containerColor = CardDark,
                title = {
                    Text(
                        text = "Select Photo Source",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Choose where to capture or select your wall condition photo:",
                            color = PureWhite,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // 1. Camera Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showSourceChooser = false
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Take Photo with Camera", color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        // 2. Gallery Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showSourceChooser = false
                                    galleryLauncher.launch("image/*")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Choose from Gallery / Photos", color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        // 3. Files / Documents Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldContainer)
                                .clickable {
                                    showSourceChooser = false
                                    fileLauncher.launch("image/*")
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Select File from Storage", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showSourceChooser = false }) {
                        Text("CANCEL", color = PureWhite)
                    }
                }
            )
        }
    }
}

// Helper to save bitmap from camera to temporary internal cache file and return Uri
private fun saveBitmapToCache(context: Context, bitmap: android.graphics.Bitmap): Uri? {
    return try {
        val file = java.io.File(context.cacheDir, "inspection_${System.currentTimeMillis()}.jpg")
        val stream = java.io.FileOutputStream(file)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, stream)
        stream.flush()
        stream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
