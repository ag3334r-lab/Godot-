package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SleekBottomNavBar
import com.example.ui.components.SleekTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SleekBg
import com.example.viewmodel.AppTab
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GodotAiBridgeApp()
            }
        }
    }
}

@Composable
fun GodotAiBridgeApp(viewModel: MainViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val serverState by viewModel.serverState.collectAsState()
    val userNotification by viewModel.userNotification.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userNotification) {
        userNotification?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.dismissNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SleekBg,
        topBar = {
            SleekTopBar(
                isServerActive = serverState.isServerActive,
                onSettingsClick = { viewModel.selectTab(AppTab.SETTINGS) }
            )
        },
        bottomBar = {
            SleekBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    AppTab.MONITOR -> MonitorScreen(
                        viewModel = viewModel,
                        onNavigateToPlayground = { viewModel.selectTab(AppTab.PLAYGROUND) },
                        onNavigateToLogs = { viewModel.selectTab(AppTab.LOGS) }
                    )
                    AppTab.PYTHON_CODE -> PythonCodeScreen()
                    AppTab.PLAYGROUND -> PlaygroundScreen(viewModel = viewModel)
                    AppTab.LOGS -> LogsScreen(viewModel = viewModel)
                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
