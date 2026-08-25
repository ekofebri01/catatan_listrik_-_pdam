package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CustomerProfile
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.ElectricGoldBorder
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSecondary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WaterCyanBorder
import com.example.ui.theme.WaterCyanLight
import com.example.ui.theme.WaterCyanPrimary
import com.example.ui.theme.WaterCyanSecondary
import com.example.ui.theme.WaterCyanSubtle
import com.example.ui.viewmodel.UtilityViewModel

@Composable
fun ExecutiveHeroMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    primaryValue: String,
    secondaryValue: String,
    primarySublabel: String,
    secondarySublabel: String,
    icon: ImageVector,
    isElectric: Boolean,
    timeFilterLabel: String,
    onTimeFilterClick: () -> Unit,
    actionButtonText: String = if (isElectric) "Beli Token" else "Catat Meter",
    onActionClick: (() -> Unit)? = null
) {
    val accentColor = if (isElectric) ElectricGoldPrimary else WaterCyanSecondary
    val lightAccentColor = if (isElectric) ElectricGoldSecondary else WaterCyanLight
    val pillBg = if (isElectric) ElectricGoldSubtle else WaterCyanSubtle
    val pillBorder = if (isElectric) ElectricGoldBorder else WaterCyanBorder
    val categoryLabel = if (isElectric) "LISTRIK • PRABAYAR" else "PDAM • AIR BERSIH"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Large Faded Watermark Icon in Top-Right
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(92.dp)
                    .align(Alignment.TopEnd)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Tag with Dot Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.2.sp
                        ),
                        color = TextSecondaryDark
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = primarySublabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextMutedDark
                )

                // Main Metric (4xl light font + unit)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = primaryValue,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp, // slightly smaller so Rp fits better
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Row: Sub-metric + Bento Pill Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = secondarySublabel.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextMutedDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = secondaryValue,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = lightAccentColor
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = pillBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, pillBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onTimeFilterClick() }
                    ) {
                        Text(
                            text = timeFilterLabel,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TokenDisplayCard(
    tokenNumber: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    val formattedToken = UtilityViewModel.formatTokenFormatted(tokenNumber)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("PLN Token", tokenNumber.replace(Regex("[^0-9]"), ""))
                clipboard.setPrimaryClip(clip)
                copied = true
                Toast.makeText(context, "Nomor Token PLN disalin ke clipboard!", Toast.LENGTH_SHORT).show()
            },
        color = BentoTileInner,
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NO. STROOM TOKEN PLN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ElectricGoldSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (formattedToken.isNotEmpty()) formattedToken else "-",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        fontSize = 15.sp
                    ),
                    color = ElectricGoldPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElectricGoldSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Salin Token",
                    tint = ElectricGoldPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
fun LocationSelectorBar(
    profiles: List<CustomerProfile>,
    selectedLocation: String?,
    onSelectLocation: (String?) -> Unit,
    modifier: Modifier = Modifier,
    isElectricTheme: Boolean = true,
    onAddNewLocation: (() -> Unit)? = null
) {
    val activeBorderColor = if (isElectricTheme) ElectricGoldBorder else WaterCyanBorder
    val activeSubtleColor = if (isElectricTheme) ElectricGoldSubtle else WaterCyanSubtle
    val activeTextColor = if (isElectricTheme) ElectricGoldPrimary else WaterCyanSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Option: Semua Lokasi (All Locations)
        val isAllSelected = selectedLocation == null
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isAllSelected) activeSubtleColor else BentoTileInner,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isAllSelected) activeBorderColor else BentoBorder
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onSelectLocation(null) }
                .testTag("location_filter_all")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = if (isAllSelected) activeTextColor else TextMutedDark,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Semua Lokasi",
                    fontSize = 12.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAllSelected) activeTextColor else TextSecondaryDark
                )
            }
        }

        // Each individual profile / location
        profiles.forEachIndexed { index, profile ->
            val isSelected = selectedLocation.equals(profile.name, ignoreCase = true)
            val icon = when {
                profile.name.contains("kontrakan", ignoreCase = true) || profile.name.contains("kost", ignoreCase = true) || profile.name.contains("2", ignoreCase = true) -> Icons.Default.Apartment
                else -> Icons.Default.Home
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) activeSubtleColor else BentoTileInner,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) activeBorderColor else BentoBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectLocation(profile.name) }
                    .testTag("location_filter_${profile.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) activeTextColor else TextMutedDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profile.name,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeTextColor else TextSecondaryDark
                    )
                }
            }
        }

        // Optional Quick Add Location Button
        if (onAddNewLocation != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BentoTileInner,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onAddNewLocation() }
                    .testTag("add_new_location_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Lokasi",
                        tint = TextMutedDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tambah",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMutedDark
                    )
                }
            }
        }
    }
}

@Composable
fun LocationBadge(
    locationName: String,
    modifier: Modifier = Modifier,
    isElectric: Boolean = true
) {
    val bg = if (isElectric) ElectricGoldSubtle else WaterCyanSubtle
    val border = if (isElectric) ElectricGoldBorder else WaterCyanBorder
    val text = if (isElectric) ElectricGoldPrimary else WaterCyanSecondary
    val icon = when {
        locationName.contains("kontrakan", ignoreCase = true) || locationName.contains("kost", ignoreCase = true) || locationName.contains("2", ignoreCase = true) -> Icons.Default.Apartment
        else -> Icons.Default.Home
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = text,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = locationName.ifEmpty { "Lokasi 1" },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = text
            )
        }
    }
}

