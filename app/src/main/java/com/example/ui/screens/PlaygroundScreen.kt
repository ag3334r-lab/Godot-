package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GodotNodeTreeView
import com.example.ui.components.SleekCardContainer
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(viewModel: MainViewModel) {
    val apiKey by viewModel.apiKey.collectAsState()
    val promptInput by viewModel.promptInput.collectAsState()
    val selectedModelId by viewModel.selectedModelId.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val supervisionResult by viewModel.currentSupervisionResult.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Request Configuration Card
        SleekCardContainer {
            Text(
                text = "ROUTER REQUEST SIMULATOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary,
                    letterSpacing = 1.2.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // OpenRouter API Key Input
            OutlinedTextField(
                value = apiKey,
                onValueChange = { viewModel.setApiKey(it) },
                label = { Text("OpenRouter API Key (Optional)", color = SleekTextMuted) },
                placeholder = { Text("sk-or-v1-...", color = SleekTextMuted.copy(alpha = 0.5f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_key_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorder,
                    focusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.1f),
                    focusedTextColor = SleekTextMain,
                    unfocusedTextColor = SleekTextMain
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (apiKey.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Key Entered",
                            tint = SleekStatusGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Model Selection Chips
            Text(
                text = "Target AI Model:",
                style = MaterialTheme.typography.labelSmall.copy(color = SleekTextMuted)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.repository.availableModels) { model ->
                    val isSelected = model.id == selectedModelId
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedModel(model.id) },
                        label = {
                            Text(
                                text = model.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SleekPrimaryContainer,
                            selectedLabelColor = SleekPrimary,
                            containerColor = SleekSurfaceVariant.copy(alpha = 0.3f),
                            labelColor = SleekTextMuted
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prompt Preset Quick Chips
            Text(
                text = "Godot Prompt Presets:",
                style = MaterialTheme.typography.labelSmall.copy(color = SleekTextMuted)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.repository.promptPresets) { preset ->
                    AssistChip(
                        onClick = { viewModel.applyPreset(preset) },
                        label = { Text(preset.title, fontSize = 11.sp, color = SleekTextMain) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SleekStatusAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = SleekSurfaceVariant.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prompt TextArea
            OutlinedTextField(
                value = promptInput,
                onValueChange = { viewModel.setPromptInput(it) },
                label = { Text("Game Engine Instruction / Prompt", color = SleekTextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("prompt_text_area"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPrimary,
                    unfocusedBorderColor = SleekBorder,
                    focusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.1f),
                    focusedTextColor = SleekTextMain,
                    unfocusedTextColor = SleekTextMain
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Send Button
            Button(
                onClick = { viewModel.runCodeGeneration() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("send_request_button"),
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPrimaryContainer,
                    disabledContainerColor = SleekSurfaceVariant
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = SleekPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Supervising & Routing Prompt...", color = SleekPrimary)
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = SleekPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send to OpenRouter Bridge", color = SleekPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Supervision & Preview Output Window
        supervisionResult?.let { result ->
            SleekCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SUPERVISION & PREVIEW WINDOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        )
                    )

                    Surface(
                        color = if (result.passed) SleekStatusGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (result.passed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (result.passed) SleekStatusGreen else Color.Red,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (result.passed) "REVIEW PASSED" else "SAFETY FLAG",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (result.passed) SleekStatusGreen else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Safety Flags
                if (result.flags.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SleekStatusAmber.copy(alpha = 0.1f))
                            .padding(10.dp)
                    ) {
                        result.flags.forEach { flag ->
                            Text(
                                text = "• $flag",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SleekStatusAmber,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Code Preview
                CodeBlockView(
                    code = result.code,
                    filename = result.filenameSuggestion
                )

                // Node Tree if present
                result.nodeTree?.let { nodes ->
                    Spacer(modifier = Modifier.height(12.dp))
                    GodotNodeTreeView(nodes = nodes)
                }
            }
        }
    }
}
