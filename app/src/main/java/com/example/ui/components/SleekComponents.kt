package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GodotNodeItem
import com.example.data.RequestLogItem
import com.example.ui.theme.*
import com.example.viewmodel.AppTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleekTopBar(
    isServerActive: Boolean,
    onSettingsClick: () -> Unit
) {
    Surface(
        color = SleekBg,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Hub",
                        tint = SleekPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Godot AI Bridge",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SleekTextMain,
                            fontSize = 18.sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PulsingStatusDot(isActive = isServerActive)
                        Text(
                            text = if (isServerActive) "SERVER ACTIVE" else "ROUTER OFFLINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isServerActive) SleekStatusGreen else SleekTextMuted,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SleekCard)
                    .testTag("top_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = SleekTextMain
                )
            }
        }
    }
}

@Composable
fun PulsingStatusDot(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                if (isActive) SleekStatusGreen.copy(alpha = alpha) else Color.Red.copy(alpha = 0.5f)
            )
    )
}

@Composable
fun SleekBottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(
        color = SleekCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                val icon = when (tab) {
                    AppTab.MONITOR -> if (isSelected) Icons.Filled.Dashboard else Icons.Outlined.Dashboard
                    AppTab.PYTHON_CODE -> if (isSelected) Icons.Filled.Code else Icons.Outlined.Code
                    AppTab.PLAYGROUND -> if (isSelected) Icons.Filled.Terminal else Icons.Outlined.Terminal
                    AppTab.LOGS -> if (isSelected) Icons.Filled.History else Icons.Outlined.History
                    AppTab.SETTINGS -> if (isSelected) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SleekPrimaryContainer else Color.Transparent)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) SleekPrimary else SleekTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SleekPrimary else SleekTextMuted
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SleekCardContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = SleekCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ActiveRouteCard(
    currentModelId: String,
    onSelectModelClick: () -> Unit
) {
    SleekCardContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVE ROUTER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary,
                    letterSpacing = 1.5.sp,
                    fontSize = 11.sp
                )
            )
            Surface(
                color = SleekSurfaceVariant,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "ID: flask-router-v1",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SleekTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.25f))
                .clickable { onSelectModelClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SleekPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Model",
                    tint = SleekPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Model (OpenRouter)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SleekTextMuted,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = currentModelId,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = SleekTextMain,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Change",
                tint = SleekTextMuted
            )
        }
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, SleekBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SleekSurfaceVariant.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SleekPrimary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SleekTextMuted,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = SleekTextMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
fun CodeBlockView(
    code: String,
    filename: String = "script.gd",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SleekCodeBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Code header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Code",
                        tint = SleekStatusAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = filename,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SleekTextMain,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }

                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied code to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = SleekPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Copy",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SleekPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Divider(color = SleekBorder, thickness = 1.dp)

            // Code Content Container
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .padding(12.dp)
                ) {
                    val lines = code.lines()
                    itemsIndexed(lines) { index, line ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}".padStart(3, ' '),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SleekTextMuted.copy(alpha = 0.4f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.width(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = highlightGDScriptLineColor(line),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun highlightGDScriptLineColor(line: String): Color {
    val trimmed = line.trim()
    return when {
        trimmed.startsWith("#") -> Color(0xFF6B7280) // Gray comments
        trimmed.startsWith("extends ") -> SleekPrimary // Purple keyword
        trimmed.startsWith("func ") || trimmed.startsWith("signal ") -> SleekStatusGreen // Green functions
        trimmed.startsWith("@export") || trimmed.startsWith("@onready") -> SleekStatusAmber // Amber annotations
        trimmed.contains("var ") -> Color(0xFF93C5FD) // Light blue variables
        else -> SleekTextMain
    }
}

@Composable
fun GodotNodeTreeView(nodes: List<GodotNodeItem>) {
    SleekCardContainer {
        Text(
            text = "GODOT SCENE HIERARCHY TREE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SleekPrimary,
                letterSpacing = 1.2.sp,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        nodes.forEach { rootNode ->
            NodeItemRow(node = rootNode, depth = 0)
        }
    }
}

@Composable
fun NodeItemRow(node: GodotNodeItem, depth: Int) {
    Column(modifier = Modifier.padding(start = (depth * 16).dp, top = 4.dp, bottom = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SleekSurfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = when (node.nodeType) {
                    "CharacterBody2D", "RigidBody3D" -> Icons.Default.DirectionsRun
                    "Sprite2D", "MeshInstance3D" -> Icons.Default.Image
                    "CollisionShape2D" -> Icons.Default.CropFree
                    "Camera2D", "Camera3D" -> Icons.Default.Videocam
                    "Area2D" -> Icons.Default.Radar
                    else -> Icons.Default.AccountTree
                },
                contentDescription = node.nodeType,
                tint = SleekPrimary,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = node.nodeName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekTextMain,
                    fontSize = 12.sp
                )
            )

            Text(
                text = "(${node.nodeType})",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SleekTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        node.children.forEach { child ->
            NodeItemRow(node = child, depth = depth + 1)
        }
    }
}
