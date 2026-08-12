package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OpenRouterModelItem
import com.example.ui.components.ActiveRouteCard
import com.example.ui.components.MetricCard
import com.example.ui.components.SleekCardContainer
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(
    viewModel: MainViewModel,
    onNavigateToPlayground: () -> Unit,
    onNavigateToLogs: () -> Unit
) {
    val serverState by viewModel.serverState.collectAsState()
    val logs by viewModel.logs.collectAsState()
    var showModelSelectorSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Route Card
        item {
            ActiveRouteCard(
                currentModelId = serverState.activeModelId,
                onSelectModelClick = { showModelSelectorSheet = true }
            )
        }

        // Live Traffic Stream Terminal Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SleekBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SleekBg),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SleekCard)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Terminal",
                                tint = SleekStatusAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "LIVE REQUEST STREAM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = SleekTextMain,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        TextButton(
                            onClick = onNavigateToLogs,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "View All (${logs.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SleekPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Divider(color = SleekBorder)

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (logs.isEmpty()) {
                            Text(
                                text = "No request logs yet. Use the Playground to send an AI code request.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SleekTextMuted,
                                    fontSize = 12.sp
                                )
                            )
                        } else {
                            val latest = logs.first()
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "[${latest.timestamp}]",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF60A5FA),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "${latest.method} ${latest.endpoint}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SleekStatusGreen,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                                Surface(
                                    color = SleekStatusGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${latest.statusCode} OK",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SleekStatusGreen,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "// Prompt: \"${latest.promptSnippet}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SleekTextMuted.copy(alpha = 0.6f),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = latest.fullCodeResponse.take(160) + "...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFF472B6),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Passed",
                                    tint = SleekStatusGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Response Supervised & Formatted for Godot Editor Preview",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SleekTextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // System Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        icon = Icons.Default.Speed,
                        title = "Latency",
                        value = "${serverState.latencyMs}ms",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.Dns,
                        title = "Uptime",
                        value = serverState.uptimeString,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        icon = Icons.Default.MultipleStop,
                        title = "Requests",
                        value = "${serverState.totalRequests}",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Success Rate",
                        value = "${serverState.successRate}%",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Test Request Action Card
        item {
            SleekCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Test Router Connection",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SleekTextMain
                            )
                        )
                        Text(
                            text = "Send a test prompt to verify dynamic OpenRouter routing",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SleekTextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = onNavigateToPlayground,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Launch",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = SleekPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Model Selector
    if (showModelSelectorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showModelSelectorSheet = false },
            containerColor = SleekCard,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select OpenRouter AI Model",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMain
                    )
                )

                viewModel.repository.availableModels.forEach { model ->
                    ModelSelectItem(
                        model = model,
                        isSelected = model.id == serverState.activeModelId,
                        onSelect = {
                            viewModel.setSelectedModel(model.id)
                            showModelSelectorSheet = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ModelSelectItem(
    model: OpenRouterModelItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        color = if (isSelected) SleekPrimaryContainer.copy(alpha = 0.6f) else SleekSurfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SleekPrimary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) SleekPrimary else SleekSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = if (isSelected) SleekPrimaryContainer else SleekTextMuted
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SleekTextMain,
                            fontSize = 13.sp
                        )
                    )
                    Surface(
                        color = when (model.badge) {
                            "FREE" -> SleekStatusGreen.copy(alpha = 0.2f)
                            "PRO" -> SleekPrimary.copy(alpha = 0.2f)
                            else -> SleekStatusAmber.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = model.badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (model.badge) {
                                    "FREE" -> SleekStatusGreen
                                    "PRO" -> SleekPrimary
                                    else -> SleekStatusAmber
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    text = model.id,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SleekTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = SleekPrimary
                )
            }
        }
    }
}
