package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PythonBackendCode
import com.example.ui.components.SleekCardContainer
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val serverState by viewModel.serverState.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val context = LocalContext.current

    var hostInput by remember { mutableStateOf(serverState.host) }
    var portInput by remember { mutableStateOf(serverState.port.toString()) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Router Server Controls Card
        SleekCardContainer {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Router Server Active State",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMain
                        )
                    )
                    Text(
                        text = if (serverState.isServerActive) "Server bound to http://${serverState.host}:${serverState.port}" else "Router is paused",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SleekTextMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                Switch(
                    checked = serverState.isServerActive,
                    onCheckedChange = { viewModel.toggleServerState() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SleekPrimary,
                        checkedTrackColor = SleekPrimaryContainer,
                        uncheckedThumbColor = SleekTextMuted,
                        uncheckedTrackColor = SleekSurfaceVariant
                    ),
                    modifier = Modifier.testTag("server_active_switch")
                )
            }
        }

        // Host & Port Config Card
        SleekCardContainer {
            Text(
                text = "NETWORK & PORT CONFIGURATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = hostInput,
                onValueChange = { hostInput = it },
                label = { Text("Server Host Address", color = SleekTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorder,
                    focusedTextColor = SleekTextMain,
                    unfocusedTextColor = SleekTextMain
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = portInput,
                onValueChange = { portInput = it },
                label = { Text("Flask Server Port", color = SleekTextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorder,
                    focusedTextColor = SleekTextMain,
                    unfocusedTextColor = SleekTextMain
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val portInt = portInput.toIntOrNull() ?: 5000
                    viewModel.updateServerConfig(hostInput, portInt)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Network Settings", color = SleekPrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Plugin Exporter Card
        SleekCardContainer {
            Text(
                text = "GODOT ADDON BUNDLE EXPORT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Copy complete bundle files for your Godot Engine project addon directory (addons/godot_ai_bridge/).",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SleekTextMuted,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val bundleText = """
                        === FILE: godot_ai_bridge.py ===
                        ${PythonBackendCode.SCRIPT_CODE}

                        === FILE: ai_bridge_dock.gd ===
                        ${PythonBackendCode.GODOT_PLUGIN_SCRIPT}

                        === FILE: requirements.txt ===
                        ${PythonBackendCode.REQUIREMENTS_TXT}
                    """.trimIndent()

                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Godot Plugin Bundle", bundleText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied full plugin bundle to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SleekSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = "Export",
                    tint = SleekTextMain,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy Full Plugin Bundle", color = SleekTextMain, fontWeight = FontWeight.Bold)
            }
        }
    }
}
