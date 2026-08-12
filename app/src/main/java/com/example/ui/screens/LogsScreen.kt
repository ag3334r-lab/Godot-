package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RequestLogItem
import com.example.ui.components.CodeBlockView
import com.example.ui.components.SleekCardContainer
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun LogsScreen(viewModel: MainViewModel) {
    val logs by viewModel.logs.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredLogs = remember(logs, selectedFilter) {
        when (selectedFilter) {
            "POST" -> logs.filter { it.method == "POST" }
            "GET" -> logs.filter { it.method == "GET" }
            else -> logs
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Log Bar Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Terminal",
                    tint = SleekStatusAmber,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Request Terminal Stream",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SleekTextMain
                    )
                )
            }

            TextButton(
                onClick = { viewModel.clearLogs() },
                enabled = logs.isNotEmpty(),
                modifier = Modifier.testTag("clear_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear",
                    tint = SleekPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear Stream", color = SleekPrimary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("ALL", "POST", "GET").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SleekPrimaryContainer,
                        selectedLabelColor = SleekPrimary,
                        containerColor = SleekCard,
                        labelColor = SleekTextMuted
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Logs List
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No request logs recorded.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SleekTextMuted)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLogs, key = { it.id }) { logItem ->
                    LogCardItem(logItem = logItem)
                }
            }
        }
    }
}

@Composable
fun LogCardItem(logItem: RequestLogItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = SleekCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "[${logItem.timestamp}]",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF60A5FA),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Surface(
                        color = when (logItem.method) {
                            "POST" -> SleekPrimaryContainer
                            else -> SleekSurfaceVariant
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = logItem.method,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (logItem.method) {
                                    "POST" -> SleekPrimary
                                    else -> SleekTextMain
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Text(
                        text = logItem.endpoint,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SleekTextMain,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                Surface(
                    color = SleekStatusGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${logItem.statusCode} OK",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SleekStatusGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Model: ${logItem.model}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SleekTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${logItem.latencyMs}ms",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SleekStatusAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                ) {
                    Divider(color = SleekBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Prompt Instruction:",
                        style = MaterialTheme.typography.labelSmall.copy(color = SleekTextMuted)
                    )
                    Text(
                        text = logItem.promptSnippet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SleekTextMain,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CodeBlockView(
                        code = logItem.fullCodeResponse,
                        filename = "response_payload.gd"
                    )
                }
            }
        }
    }
}
