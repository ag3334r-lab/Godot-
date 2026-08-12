package com.example.data

data class OpenRouterModelItem(
    val id: String,
    val name: String,
    val provider: String,
    val badge: String, // FREE, PRO, POPULAR
    val speed: String,
    val description: String
)

data class SupervisionResult(
    val passed: Boolean,
    val flags: List<String>,
    val filenameSuggestion: String,
    val linesCount: Int,
    val code: String,
    val nodeTree: List<GodotNodeItem>? = null,
    val rawResponse: String = ""
)

data class GodotNodeItem(
    val nodeName: String,
    val nodeType: String, // e.g. CharacterBody2D, CollisionShape2D, Sprite2D
    val properties: Map<String, String> = emptyMap(),
    val children: List<GodotNodeItem> = emptyList()
)

data class RequestLogItem(
    val id: String,
    val timestamp: String,
    val method: String,
    val endpoint: String,
    val statusCode: Int,
    val latencyMs: Long,
    val model: String,
    val promptSnippet: String,
    val supervisionPassed: Boolean,
    val fullCodeResponse: String
)

data class PromptPreset(
    val title: String,
    val category: String, // GDScript, Nodes, Shader, StateMachine
    val prompt: String,
    val targetModel: String = "meta-llama/llama-3-8b-instruct:free"
)

data class RouterServerState(
    val isServerActive: Boolean = true,
    val host: String = "0.0.0.0",
    val port: Int = 5000,
    val activeModelId: String = "meta-llama/llama-3-8b-instruct:free",
    val latencyMs: Long = 245,
    val uptimeString: String = "12d 4h",
    val totalRequests: Int = 142,
    val successRate: Float = 98.5f
)
