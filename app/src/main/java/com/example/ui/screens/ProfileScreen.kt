package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.UserProfile
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
fun ProfileScreen(
    userProfile: UserProfile,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onToggleAdminMode: (Boolean) -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current
    var showRateDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Customer Profile & Settings",
                subtitle = "Addresses • Notifications • Security",
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
                // User Identity Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, GoldPrimary, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_mansuri_logo_1785404842036),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                            Text(
                                text = userProfile.phone,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GoldPrimary
                            )
                            Text(
                                text = userProfile.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Saved Multiple Addresses Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Saved Delivery Addresses",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        userProfile.addresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${addr.label}: ${addr.houseNo}, ${addr.buildingName}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = PureWhite
                                    )
                                    Text(
                                        text = "${addr.street}, ${addr.city}, ${addr.state} ${addr.pincode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PureWhite.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                // App Preferences
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "App Display Preferences",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Luxury Black & Gold Theme", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                    Text("High contrast dark theme", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.6f))
                                }
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { onToggleTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = OnyxBlack,
                                    checkedTrackColor = GoldPrimary
                                )
                            )
                        }

                        // Only show Admin Controls if the user is authenticated as an Admin!
                        if (userProfile.isAdmin) {
                            HorizontalDivider(color = CardBorderDark, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldPrimary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Admin Dashboard Active", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = PureWhite)
                                        Text("Logged in as ${userProfile.email}", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                                    }
                                }
                                Switch(
                                    checked = userProfile.isAdmin,
                                    onCheckedChange = { isAdmin ->
                                        onToggleAdminMode(isAdmin)
                                        if (isAdmin) onNavigate("admin_dashboard")
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = OnyxBlack,
                                        checkedTrackColor = GoldPrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LuxuryGoldButton(
                                text = "OPEN ADMIN DASHBOARD",
                                onClick = { onNavigate("admin_dashboard") }
                            )
                        }
                    }
                }

                // Navigation & Information Links
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Platform Navigation & Legal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ProfileLinkItem("My Bookings History", Icons.Default.ListAlt) { onNavigate("my_bookings") }
                        ProfileLinkItem("Painter Workstation Panel", Icons.Default.FormatPaint) { onNavigate("painter_panel") }
                        ProfileLinkItem("Notifications & Inbox", Icons.Default.Notifications) { onNavigate("notifications") }
                        ProfileLinkItem("Room Photos & Inspection", Icons.Default.AddAPhoto) { onNavigate("upload_photos") }
                        ProfileLinkItem("About Mansuri Paints", Icons.Default.Info) { onNavigate("about_us") }
                        ProfileLinkItem("Contact & Studio Location", Icons.Default.Phone) { onNavigate("contact_us") }
                        ProfileLinkItem("Privacy Policy", Icons.Default.PrivacyTip) { onNavigate("privacy_policy") }
                        ProfileLinkItem("Terms & Conditions", Icons.Default.PrivacyTip) { onNavigate("terms_conditions") }

                        ProfileLinkItem("Share App with Friends", Icons.Default.Share) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Book luxury painting services on Mansuri Paints App!")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }

                        ProfileLinkItem("Rate App 5 Stars", Icons.Default.Star) { showRateDialog = true }
                        ProfileLinkItem("Check for App Updates", Icons.Default.SystemUpdate) { showUpdateDialog = true }
                    }
                }

                LuxuryOutlinedButton(
                    text = "Log Out",
                    onClick = onLogout,
                    icon = Icons.Default.Logout
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Rate App Dialog
        if (showRateDialog) {
            AlertDialog(
                onDismissRequest = { showRateDialog = false },
                containerColor = CardDark,
                title = { Text("Rate Mansuri Paints", color = GoldPrimary) },
                text = { Text("Enjoying our premium painting service? Please give us a 5-Star rating on Google Play Store!", color = PureWhite) },
                confirmButton = {
                    LuxuryGoldButton(text = "RATE 5 STARS", onClick = { showRateDialog = false })
                },
                dismissButton = {
                    TextButton(onClick = { showRateDialog = false }) { Text("LATER", color = PureWhite) }
                }
            )
        }

        // Check Update Dialog
        if (showUpdateDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateDialog = false },
                containerColor = CardDark,
                title = { Text("App Update Checker", color = GoldPrimary) },
                text = { Text("You are using the latest version v2.4.0 (Production Build) of Mansuri Paints App.", color = PureWhite) },
                confirmButton = {
                    LuxuryGoldButton(text = "OK", onClick = { showUpdateDialog = false })
                }
            )
        }
    }
}

@Composable
private fun ProfileLinkItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = PureWhite)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = PureWhite.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
    }
}
