package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaintCostEstimate
import com.example.data.models.PaintService
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    services: List<PaintService>,
    currentEstimate: PaintCostEstimate,
    onUpdateEstimate: (areaSqFt: Double, roomsCount: Int, serviceTitle: String, customRate: Double?) -> Unit,
    onShowPdfModal: (PaintCostEstimate) -> Unit,
    onBookNow: () -> Unit,
    onBack: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit
) {
    var areaInput by remember { mutableFloatStateOf(currentEstimate.areaSqFt.toFloat()) }
    var selectedServiceTitle by remember { mutableStateOf(currentEstimate.serviceTitle) }
    var roomsCount by remember { mutableStateOf(currentEstimate.roomsCount) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val activeService = services.find { it.title == selectedServiceTitle } ?: services.first()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OnyxBlack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MansuriTopAppBar(
                title = "Paint Cost Calculator",
                subtitle = "Instant Area & Material Cost Estimator",
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
                // Input Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "1. Select Paint Quality / Service",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = "${activeService.title} (₹${activeService.pricePerSqFt.toInt()}/sq ft)",
                                onValueChange = {},
                                readOnly = true,
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
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier.background(CardDark)
                            ) {
                                services.forEach { service ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = service.title, color = PureWhite, fontWeight = FontWeight.Bold)
                                                Text(text = "₹${service.pricePerSqFt.toInt()}/sq ft", color = GoldPrimary)
                                            }
                                        },
                                        onClick = {
                                            selectedServiceTitle = service.title
                                            isDropdownExpanded = false
                                            onUpdateEstimate(
                                                areaInput.toDouble(),
                                                roomsCount,
                                                service.title,
                                                service.pricePerSqFt
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Wall Area (Sq Ft):",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldMetallic
                            )
                            Box(
                                modifier = Modifier
                                    .background(GoldContainer, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${areaInput.toInt()} sq ft",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = areaInput,
                            onValueChange = { newValue ->
                                areaInput = newValue
                                onUpdateEstimate(
                                    newValue.toDouble(),
                                    roomsCount,
                                    selectedServiceTitle,
                                    activeService.pricePerSqFt
                                )
                            },
                            valueRange = 100f..4000f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = GoldPrimary,
                                activeTrackColor = GoldPrimary,
                                inactiveTrackColor = CardBorderDark
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "100 sq ft (1 Room)", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.5f))
                            Text(text = "4000 sq ft (Full House)", style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.5f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "3. Rooms Count",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldMetallic
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 4, 5).forEach { r ->
                                val isSelected = roomsCount == r
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .background(
                                            if (isSelected) GoldPrimary else CardBorderDark,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) GoldPrimary else CardBorderDark,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(4.dp)
                                        .border(0.dp, CardBorderDark, RoundedCornerShape(8.dp))
                                        .clickable {
                                            roomsCount = r
                                            onUpdateEstimate(
                                                areaInput.toDouble(),
                                                r,
                                                selectedServiceTitle,
                                                activeService.pricePerSqFt
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$r Room${if (r > 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) OnyxBlack else PureWhite
                                    )
                                }
                            }
                        }
                    }
                }

                // Calculation Result Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESTIMATED TOTAL:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                            Text(
                                text = "₹${currentEstimate.totalCost.toInt()}",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = GoldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = GoldPrimary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        EstimateRow("Paint Material Cost (65%):", "₹${currentEstimate.paintMaterialCost.toInt()}")
                        EstimateRow("Labor & Surface Prep (35%):", "₹${currentEstimate.laborCost.toInt()}")
                        EstimateRow("Required Paint Quantity:", "~${String.format("%.1f", currentEstimate.estimatedLiters)} Liters")
                        EstimateRow("Estimated Project Days:", "${currentEstimate.estimatedDays} Days")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LuxuryOutlinedButton(
                        text = "PDF Quote",
                        onClick = { onShowPdfModal(currentEstimate) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.PictureAsPdf
                    )
                    LuxuryGoldButton(
                        text = "Book Painter",
                        onClick = onBookNow,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun EstimateRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = PureWhite.copy(alpha = 0.8f))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PureWhite)
    }
}
