package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MansuriTopAppBar
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxBlack
import com.example.ui.theme.PureWhite

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Privacy Policy",
                subtitle = "Mansuri Paints & Services Safety Policy",
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
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Privacy & Data Protection Guarantees",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = CardBorderDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        PolicySection(
                            title = "1. Information Collection",
                            content = "We collect customer details including name, contact number, delivery address, house dimensions, wall photos, and location coordinates solely for delivering painting, putty, and waterproofing services."
                        )

                        PolicySection(
                            title = "2. Firebase & Cloud Encryption",
                            content = "All authentication tokens, OTPs, booking records, and uploaded room photos are encrypted in transit and at rest using Google Firebase Authentication, Firestore Security Rules, and Cloud Storage."
                        )

                        PolicySection(
                            title = "3. Payment Confidentiality",
                            content = "Payment transactions processed through Razorpay or UPI are securely tokenized. Mansuri Paints never stores raw credit card, debit card, or banking PIN credentials."
                        )

                        PolicySection(
                            title = "4. Third-Party Sharing",
                            content = "We do not sell, rent, or trade your personal data. Address details are shared strictly with verified Mansuri Master Painters assigned to your job for navigation purposes."
                        )

                        PolicySection(
                            title = "5. User Rights & Contact",
                            content = "You may request account deletion, photo deletion, or data copy anytime by contacting our privacy compliance team via WhatsApp or phone at +91 78430 99068."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TermsConditionsScreen(
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Terms & Conditions",
                subtitle = "Service Guarantees & Cancellation Policy",
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
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Mansuri Service Terms & Warranties",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = CardBorderDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        PolicySection(
                            title = "1. Quotations & Area Measurements",
                            content = "Estimates generated via the app calculator are based on user inputs. Final billing is calculated after precise laser measurement on site before work commencement."
                        )

                        PolicySection(
                            title = "2. Warranty Protection",
                            content = "Royal Paint and Waterproofing packages include 5-Year and 10-Year warranties against peeling, fading, and seepage under normal residential conditions."
                        )

                        PolicySection(
                            title = "3. Cancellation & Refunds",
                            content = "Free cancellation is permitted up to 2 hours prior to scheduled painter arrival. Full refunds for Razorpay/UPI prepayments are processed within 24-48 hours."
                        )

                        PolicySection(
                            title = "4. Site Safety & Masking",
                            content = "Our painters provide 100% masking sheets for furniture and floors. Homeowners are advised to secure personal valuables prior to work initiation."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = GoldMetallic
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = PureWhite.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )
    }
}
