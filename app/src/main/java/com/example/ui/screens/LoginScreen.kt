package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.LuxuryCard
import com.example.ui.components.LuxuryGoldButton
import com.example.ui.components.LuxuryOutlinedButton
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldMetallic
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnyxBlack
import com.example.ui.theme.PureWhite

@Composable
fun LoginScreen(
    onLoginPhone: (phone: String, name: String) -> Unit,
    onLoginGoogle: (email: String, name: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToAdminLogin: () -> Unit,
    onSkip: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("9876543210") }
    var nameInput by remember { mutableStateOf("Mansuri Client") }
    var otpInput by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotPhoneInput by remember { mutableStateOf("") }
    var forgotSentMessage by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, GoldPrimary, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mansuri_logo_1785404842036),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Welcome to Mansuri Paints",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = GoldPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sign in to book expert painters & track orders",
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            LuxuryCard {
                Column {
                    Text(
                        text = "Phone OTP Authentication",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GoldMetallic
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name", color = PureWhite.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorderDark,
                            focusedLabelColor = GoldPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number (+91)", color = PureWhite.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorderDark,
                            focusedLabelColor = GoldPrimary
                        ),
                        singleLine = true
                    )

                    if (isOtpSent) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("Enter 4-Digit OTP (e.g. 1234)", color = PureWhite.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorderDark,
                                focusedLabelColor = GoldPrimary
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isOtpSent) {
                        LuxuryGoldButton(
                            text = "SEND OTP CODE",
                            onClick = {
                                if (phoneInput.isNotEmpty()) {
                                    isOtpSent = true
                                }
                            }
                        )
                    } else {
                        LuxuryGoldButton(
                            text = "VERIFY & LOGIN",
                            onClick = {
                                onLoginPhone("+91 $phoneInput", nameInput)
                            }
                        )
                    }

                    TextButton(
                        onClick = { showForgotDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Forgot Login Details?", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorderDark)
                Text(
                    text = "  OR  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = PureWhite.copy(alpha = 0.5f)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorderDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LuxuryOutlinedButton(
                text = "Sign in with Google",
                onClick = {
                    onLoginGoogle("mansuriusman498@gmail.com", nameInput.ifEmpty { "Mansuri Client" })
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LuxuryOutlinedButton(
                    text = "New User? Register",
                    onClick = onNavigateToSignUp,
                    modifier = Modifier.weight(1f)
                )
                LuxuryOutlinedButton(
                    text = "Admin Login",
                    onClick = onNavigateToAdminLogin,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LuxuryOutlinedButton(
                text = "Continue as Guest",
                onClick = onSkip
            )
        }

        // Forgot Login Dialog
        if (showForgotDialog) {
            AlertDialog(
                onDismissRequest = { showForgotDialog = false },
                containerColor = CardDark,
                title = { Text("Reset Login / Recover Account", color = GoldPrimary) },
                text = {
                    Column {
                        Text("Enter your registered mobile number or email to receive a login recovery OTP link:", color = PureWhite, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = forgotPhoneInput,
                            onValueChange = { forgotPhoneInput = it },
                            label = { Text("Mobile Number or Email", color = PureWhite.copy(alpha = 0.7f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = CardBorderDark)
                        )
                        if (forgotSentMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(forgotSentMessage, color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    LuxuryGoldButton(
                        text = "SEND RECOVERY CODE",
                        onClick = {
                            forgotSentMessage = "Recovery code sent via SMS & Email to $forgotPhoneInput"
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showForgotDialog = false }) {
                        Text("CLOSE", color = PureWhite)
                    }
                }
            )
        }
    }
}
