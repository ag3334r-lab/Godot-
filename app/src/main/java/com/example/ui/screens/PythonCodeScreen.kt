package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.CodeBlockView
import com.example.ui.components.SleekCardContainer
import com.example.ui.theme.*

@Composable
fun PythonCodeScreen() {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf(0) }

    val subTabTitles = listOf("godot_ai_bridge.py", "requirements.txt", "Pydroid 3 Setup", "Godot Addon GDScript")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Sub-tab selectors
        ScrollableTabRow(
            selectedTabIndex = activeSubTab,
            containerColor = SleekCard,
            contentColor = SleekPrimary,
            edgePadding = 8.dp,
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
        ) {
            subTabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeSubTab == index,
                    onClick = { activeSubTab = index },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                        .testTag("code_tab_$index")
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeSubTab == index) SleekPrimary else SleekTextMuted,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Code Container
        Box(modifier = Modifier.weight(1f)) {
            when (activeSubTab) {
                0 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Flask OpenRouter AI Router & Supervision Backend",
                                style = MaterialTheme.typography.bodySmall.copy(color = SleekTextMuted)
                            )
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Python Script", PythonBackendCode.SCRIPT_CODE)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied Python server code!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimaryContainer),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Script", color = SleekPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        CodeBlockView(
                            code = PythonBackendCode.SCRIPT_CODE,
                            filename = "godot_ai_bridge.py",
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }

                1 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Python Dependencies for Pydroid 3 / Server",
                                style = MaterialTheme.typography.bodySmall.copy(color = SleekTextMuted)
                            )
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("requirements", PythonBackendCode.REQUIREMENTS_TXT)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied requirements.txt!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimaryContainer),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Copy File", color = SleekPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        CodeBlockView(
                            code = PythonBackendCode.REQUIREMENTS_TXT,
                            filename = "requirements.txt",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                2 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            SleekCardContainer {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "Mobile",
                                        tint = SleekStatusGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Mobile & Pydroid 3 Deployment Guide",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SleekTextMain
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = PythonBackendCode.PYDROID_INSTRUCTIONS,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SleekTextMain,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }

                3 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Godot Editor Plugin Client Script",
                                style = MaterialTheme.typography.bodySmall.copy(color = SleekTextMuted)
                            )
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Godot GDScript", PythonBackendCode.GODOT_PLUGIN_SCRIPT)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied Godot client GDScript!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimaryContainer),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Copy GDScript", color = SleekPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        CodeBlockView(
                            code = PythonBackendCode.GODOT_PLUGIN_SCRIPT,
                            filename = "ai_bridge_dock.gd",
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}
