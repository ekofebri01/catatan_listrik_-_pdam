package com.example

import com.example.ui.components.SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.database.DatabaseManagementScreen
import com.example.ui.electricity.ElectricityScreen
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.BentoDarkBg
import com.example.ui.theme.BentoTileInner
import com.example.ui.theme.BentoVioletDark
import com.example.ui.theme.BentoVioletPrimary
import com.example.ui.theme.ElectricGoldPrimary
import com.example.ui.theme.ElectricGoldSecondary
import com.example.ui.theme.ElectricGoldSubtle
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WaterCyanDark
import com.example.ui.theme.WaterCyanPrimary
import com.example.ui.theme.WaterCyanSecondary
import com.example.ui.viewmodel.UtilityViewModel
import com.example.ui.water.WaterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: UtilityViewModel = viewModel()
) {
    var showSplash by remember { androidx.compose.runtime.mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashComplete = { showSplash = false })
    } else {
        val uiState by viewModel.uiState.collectAsState()
        var selectedTab by remember { mutableIntStateOf(0) }
        Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            shape = CircleShape,
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                Brush.linearGradient(listOf(ElectricGoldPrimary, WaterCyanSecondary))
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ElectricBolt,
                                        contentDescription = null,
                                        tint = ElectricGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = WaterCyanSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Listrik & PDAM",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Catatan Pengeluaran & Meteran",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoDarkBg
                )
            )
        },
        bottomBar = {
            Surface(
                color = BentoCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_navigation_bar"),
                    containerColor = BentoCardBg,
                    tonalElevation = 0.dp
                ) {
                    // Tab 1: PLN Electricity
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.ElectricBolt else Icons.Outlined.ElectricBolt,
                                contentDescription = "Listrik PLN"
                            )
                        },
                        label = { Text("Listrik PLN", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1B00),
                            selectedTextColor = ElectricGoldPrimary,
                            indicatorColor = ElectricGoldPrimary,
                            unselectedIconColor = TextMutedDark,
                            unselectedTextColor = TextMutedDark
                        ),
                        modifier = Modifier.testTag("nav_item_electricity")
                    )

                    // Tab 2: PDAM Water
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            if (uiState.unpaidWaterBillsCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = RoseAlert,
                                            contentColor = Color.White
                                        ) {
                                            Text(uiState.unpaidWaterBillsCount.toString())
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                                        contentDescription = "Air PDAM"
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selectedTab == 1) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                                    contentDescription = "Air PDAM"
                                )
                            }
                        },
                        label = { Text("Air PDAM", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = WaterCyanSecondary,
                            indicatorColor = WaterCyanPrimary,
                            unselectedIconColor = TextMutedDark,
                            unselectedTextColor = TextMutedDark
                        ),
                        modifier = Modifier.testTag("nav_item_water")
                    )

                    // Tab 3: Database & Tarif
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Storage else Icons.Outlined.Storage,
                                contentDescription = "Database & Tarif"
                            )
                        },
                        label = { Text("Database & Tarif", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BentoVioletPrimary,
                            indicatorColor = BentoVioletDark,
                            unselectedIconColor = TextMutedDark,
                            unselectedTextColor = TextMutedDark
                        ),
                        modifier = Modifier.testTag("nav_item_database")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "MainTabContent"
            ) { tab ->
                when (tab) {
                    0 -> ElectricityScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    1 -> WaterScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    2 -> DatabaseManagementScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
}
