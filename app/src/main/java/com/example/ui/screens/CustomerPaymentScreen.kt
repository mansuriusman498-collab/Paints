package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
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
fun CustomerPaymentScreen(
    booking: BookingEntity?,
    paymentConfig: PaymentConfig,
    onUploadScreenshot: (bookingId: String, screenshotUri: Uri) -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedScreenshotUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedScreenshotUri = uri
        }
    }

    val amount = booking?.totalAmount ?: 0.0
    val upiId = paymentConfig.upiId.ifEmpty { "mansuripaints@upi" }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "UPI Payment & Verification",
                subtitle = "Dynamic Firebase UPI QR & Instant Verification",
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
                // 1. Order / Amount Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (booking != null) "Order #${booking.id} • ${booking.serviceName}" else "Mansuri Painting Services",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Amount Payable: ₹${amount.toInt()}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = GoldPrimary
                        )
                        if (booking != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Client: ${booking.customerName} (${booking.phone})",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldMetallic
                            )
                        }
                    }
                }

                // 2. Dynamic Firebase QR Code & UPI ID Card
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan QR Code to Pay",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // QR Image Container (Loaded Dynamically from Firebase)
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(2.dp, GoldPrimary, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val qrUrl = paymentConfig.qrCodeUrl.ifEmpty {
                                "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=upi://pay?pa=$upiId&pn=Mansuri%20Paints&am=${amount.toInt()}&cu=INR"
                            }

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(qrUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Firebase UPI QR Code",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // UPI ID Text Display
                        Text(
                            text = "Official Admin UPI ID:",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = upiId,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pay via UPI & Copy Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LuxuryGoldButton(
                                text = "PAY VIA UPI",
                                onClick = {
                                    val upiUri = Uri.parse("upi://pay?pa=$upiId&pn=Mansuri%20Paints&am=${amount.toInt()}&cu=INR")
                                    val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                    try {
                                        context.startActivity(Intent.createChooser(intent, "Pay with UPI"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No UPI app found. Please copy UPI ID to pay manually.", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.weight(1.2f),
                                icon = Icons.Default.Payment
                            )

                            LuxuryOutlinedButton(
                                text = "COPY UPI ID",
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Mansuri UPI ID", upiId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ContentCopy
                            )
                        }
                    }
                }

                // 3. Upload Payment Screenshot Section
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
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Upload Payment Screenshot",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Attach screenshot or UTR reference receipt after paying via Google Pay, PhonePe, Paytm or BHIM.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PureWhite.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (selectedScreenshotUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OnyxBlack)
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = selectedScreenshotUri,
                                    contentDescription = "Payment Screenshot Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LuxuryOutlinedButton(
                                text = if (selectedScreenshotUri == null) "Select Screenshot" else "Change Image",
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Image
                            )

                            if (selectedScreenshotUri != null) {
                                LuxuryGoldButton(
                                    text = if (isUploading) "UPLOADING..." else "SUBMIT PROOF",
                                    onClick = {
                                        val bId = booking?.id ?: "MP-${(1000..9999).random()}"
                                        isUploading = true
                                        onUploadScreenshot(bId, selectedScreenshotUri!!)
                                    },
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.UploadFile
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
