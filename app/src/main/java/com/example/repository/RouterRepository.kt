package com.example.repository

import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RouterRepository {

    private val _serverState = MutableStateFlow(RouterServerState())
    val serverState: StateFlow<RouterServerState> = _serverState.asStateFlow()

    private val _logs = MutableStateFlow<List<RequestLogItem>>(emptyList())
    val logs: StateFlow<List<RequestLogItem>> = _logs.asStateFlow()

    init {
        // Seed initial live log entries matching the sleek interface design
        _logs.value = listOf(
            RequestLogItem(
                id = "log-101",
                timestamp = "14:20:01",
                method = "POST",
                endpoint = "/generate-code",
                statusCode = 200,
                latencyMs = 245,
                model = "meta-llama/llama-3-8b-instruct:free",
                promptSnippet = "Create a 2D top-down CharacterBody2D movement script",
                supervisionPassed = true,
                fullCodeResponse = """
                    extends CharacterBody2D

                    @export var speed: float = 300.0
                    @export var accel: float = 20.0

                    func _physics_process(delta: float) -> void:
                        var input_dir: Vector2 = Input.get_vector("ui_left", "ui_right", "ui_up", "ui_down")
                        var target_velocity: Vector2 = input_dir.normalized() * speed
                        velocity = velocity.move_toward(target_velocity, accel * speed * delta)
                        move_and_slide()
                """.trimIndent()
            ),
            RequestLogItem(
                id = "log-100",
                timestamp = "14:19:58",
                method = "GET",
                endpoint = "/health",
                statusCode = 200,
                latencyMs = 12,
                model = "N/A",
                promptSnippet = "Health check probe",
                supervisionPassed = true,
                fullCodeResponse = "{\"status\": \"online\", \"version\": \"1.0.0\"}"
            ),
            RequestLogItem(
                id = "log-99",
                timestamp = "14:15:30",
                method = "POST",
                endpoint = "/generate-code",
                statusCode = 200,
                latencyMs = 412,
                model = "google/gemini-2.5-flash:free",
                promptSnippet = "Generate 3D Camera3D spring arm controller",
                supervisionPassed = true,
                fullCodeResponse = """
                    extends SpringArm3D

                    @export var mouse_sensitivity: float = 0.005

                    func _unhandled_input(event: InputEvent) -> void:
                        if event is InputEventMouseMotion and Input.get_mouse_mode() == Input.MOUSE_MODE_CAPTURED:
                            rotation.x -= event.relative.y * mouse_sensitivity
                            rotation.x = clamp(rotation.x, deg_to_rad(-80), deg_to_rad(60))
                            rotation.y -= event.relative.x * mouse_sensitivity
                """.trimIndent()
            )
        )
    }

    val availableModels = listOf(
        OpenRouterModelItem(
            id = "meta-llama/llama-3-8b-instruct:free",
            name = "Llama 3 8B Instruct (Free)",
            provider = "Meta",
            badge = "FREE",
            speed = "Fast (~250ms)",
            description = "Great for standard GDScript functions, player controllers, and node setups."
        ),
        OpenRouterModelItem(
            id = "google/gemini-2.5-flash:free",
            name = "Gemini 2.5 Flash (Free)",
            provider = "Google",
            badge = "FREE",
            speed = "Ultra Fast (~180ms)",
            description = "Excellent for rapid GDScript generation and complex scene tree structures."
        ),
        OpenRouterModelItem(
            id = "anthropic/claude-3.5-sonnet",
            name = "Claude 3.5 Sonnet",
            provider = "Anthropic",
            badge = "PRO",
            speed = "Balanced (~400ms)",
            description = "Top-tier model for complex Godot architecture, custom shaders, and state machines."
        ),
        OpenRouterModelItem(
            id = "openai/gpt-4o",
            name = "GPT-4o",
            provider = "OpenAI",
            badge = "PRO",
            speed = "Fast (~320ms)",
            description = "High intelligence model for advanced GDScript refactoring and procedural generation."
        ),
        OpenRouterModelItem(
            id = "deepseek/deepseek-coder",
            name = "DeepSeek Coder 33B",
            provider = "DeepSeek",
            badge = "POPULAR",
            speed = "Fast (~290ms)",
            description = "Specialized coding model with deep GDScript syntax understanding."
        )
    )

    val promptPresets = listOf(
        PromptPreset(
            title = "Top-Down 2D Movement",
            category = "GDScript",
            prompt = "Create a CharacterBody2D script with smooth acceleration, deceleration, rotation towards mouse, and dash signal."
        ),
        PromptPreset(
            title = "3D Camera & SpringArm",
            category = "GDScript",
            prompt = "Write a SpringArm3D third-person camera controller with mouse look, collision detection, and zoom smoothly."
        ),
        PromptPreset(
            title = "Enemy State Machine",
            category = "StateMachine",
            prompt = "Generate a Godot 4 Enemy State Machine with Idle, Patrol, Chase, Attack states using enum and match statement."
        ),
        PromptPreset(
            title = "Inventory Node Hierarchy",
            category = "Nodes",
            prompt = "Generate a Godot 4 Control UI node structure for a grid inventory with ItemSlot buttons and ItemTooltip overlay."
        ),
        PromptPreset(
            title = "Custom Dissolve Shader",
            category = "Shader",
            prompt = "Write a 2D CanvasItem shader for a dissolve effect using noise texture, edge burn glow, and dissolve progress parameter."
        )
    )

    fun setActiveModel(modelId: String) {
        _serverState.value = _serverState.value.copy(activeModelId = modelId)
    }

    fun toggleServerState() {
        _serverState.value = _serverState.value.copy(isServerActive = !_serverState.value.isServerActive)
    }

    fun updateServerConfig(host: String, port: Int) {
        _serverState.value = _serverState.value.copy(host = host, port = port)
    }

    suspend fun processCodeGeneration(
        apiKey: String,
        modelId: String,
        prompt: String,
        existingCode: String = ""
    ): SupervisionResult {
        val startTime = System.currentTimeMillis()
        delay(600) // Simulate router network turnaround

        val (generatedCode, nodeTree) = generateGodotLogicForPrompt(prompt)
        val supervision = performSupervisionCheck(generatedCode, nodeTree)

        val latencyMs = System.currentTimeMillis() - startTime
        val timeFormatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        // Add to live log
        val newLog = RequestLogItem(
            id = UUID.randomUUID().toString(),
            timestamp = timeFormatted,
            method = "POST",
            endpoint = "/generate-code",
            statusCode = 200,
            latencyMs = latencyMs,
            model = modelId,
            promptSnippet = prompt.take(50) + if (prompt.length > 50) "..." else "",
            supervisionPassed = supervision.passed,
            fullCodeResponse = generatedCode
        )

        _logs.value = listOf(newLog) + _logs.value
        _serverState.value = _serverState.value.copy(
            totalRequests = _serverState.value.totalRequests + 1,
            latencyMs = latencyMs
        )

        return supervision
    }

    private fun performSupervisionCheck(code: String, nodeTree: List<GodotNodeItem>?): SupervisionResult {
        val flags = mutableListOf<String>()
        val hasExtends = code.contains("extends ")
        val hasOsExecute = code.contains("OS.execute")
        val hasFileRemove = code.contains("DirAccess.remove") || code.contains("FileAccess")
        val hasInfiniteLoop = code.contains("while true:") || code.contains("while True:")

        if (!hasExtends && code.contains("func ")) {
            flags.add("Warning: Missing 'extends <NodeType>' clause at script top.")
        }
        if (hasOsExecute) {
            flags.add("Caution: Code contains 'OS.execute()' system call.")
        }
        if (hasFileRemove) {
            flags.add("Caution: Code performs disk file access.")
        }
        if (hasInfiniteLoop) {
            flags.add("Error: Potential infinite loop detected.")
        }

        val passed = !hasOsExecute && !hasInfiniteLoop

        val filenameMatch = Regex("#\\s*@filename:\\s*([\\w_]+\\.gd)").find(code)
        val filenameSuggestion = filenameMatch?.groupValues?.get(1) ?: "ai_generated_script.gd"

        return SupervisionResult(
            passed = passed,
            flags = flags,
            filenameSuggestion = filenameSuggestion,
            linesCount = code.lines().size,
            code = code,
            nodeTree = nodeTree,
            rawResponse = code
        )
    }

    private fun generateGodotLogicForPrompt(prompt: String): Pair<String, List<GodotNodeItem>?> {
        val lower = prompt.lowercase()
        return when {
            lower.contains("shader") || lower.contains("dissolve") -> {
                val code = """
                    # @filename: dissolve_effect.gdshader
                    shader_type canvas_item;

                    uniform sampler2D noise_texture;
                    uniform float dissolve_value : hint_range(0.0, 1.0) = 0.5;
                    uniform vec4 burn_color : source_color = vec4(1.0, 0.4, 0.1, 1.0);
                    uniform float burn_size : hint_range(0.0, 0.2) = 0.05;

                    void fragment() {
                        vec4 main_texture = texture(TEXTURE, UV);
                        float noise = texture(noise_texture, UV).r;
                        
                        if (noise < dissolve_value) {
                            discard;
                        }
                        
                        if (noise < dissolve_value + burn_size) {
                            COLOR = burn_color;
                        } else {
                            COLOR = main_texture;
                        }
                    }
                """.trimIndent()
                Pair(code, null)
            }

            lower.contains("state machine") || lower.contains("enemy") -> {
                val code = """
                    # @filename: enemy_ai.gd
                    extends CharacterBody2D
                    class_name EnemyAI

                    enum State { IDLE, PATROL, CHASE, ATTACK }

                    @export var speed: float = 120.0
                    @export var attack_range: float = 40.0
                    @export var detection_range: float = 200.0

                    @onready var player_detector: Area2D = ${'$'}PlayerDetector
                    @onready var sprite: Sprite2D = ${'$'}Sprite2D

                    var current_state: State = State.IDLE
                    var target_player: Node2D = null

                    signal state_changed(new_state: State)

                    func _physics_process(delta: float) -> void:
                        match current_state:
                            State.IDLE:
                                velocity = Vector2.ZERO
                            State.PATROL:
                                _patrol_behavior(delta)
                            State.CHASE:
                                _chase_behavior(delta)
                            State.ATTACK:
                                velocity = Vector2.ZERO
                                _attack_behavior()

                        move_and_slide()

                    func change_state(new_state: State) -> void:
                        if current_state == new_state:
                            return
                        current_state = new_state
                        emit_signal("state_changed", new_state)

                    func _chase_behavior(delta: float) -> void:
                        if target_player:
                            var dir = (target_player.global_position - global_position).normalized()
                            velocity = dir * speed
                            if global_position.distance_to(target_player.global_position) <= attack_range:
                                change_state(State.ATTACK)
                """.trimIndent()

                val nodes = listOf(
                    GodotNodeItem(
                        nodeName = "EnemyAI",
                        nodeType = "CharacterBody2D",
                        children = listOf(
                            GodotNodeItem("Sprite2D", "Sprite2D", mapOf("texture" to "res://assets/enemy.png")),
                            GodotNodeItem("CollisionShape2D", "CollisionShape2D", mapOf("shape" to "CircleShape2D")),
                            GodotNodeItem(
                                nodeName = "PlayerDetector",
                                nodeType = "Area2D",
                                children = listOf(
                                    GodotNodeItem("CollisionShape2D", "CollisionShape2D", mapOf("shape" to "CircleShape2D(200)"))
                                )
                            )
                        )
                    )
                )
                Pair(code, nodes)
            }

            lower.contains("3d") || lower.contains("camera") || lower.contains("springarm") -> {
                val code = """
                    # @filename: third_person_camera.gd
                    extends SpringArm3D

                    @export var mouse_sensitivity: float = 0.005
                    @export var min_pitch: float = -80.0
                    @export var max_pitch: float = 50.0

                    @onready var camera: Camera3D = ${'$'}Camera3D

                    func _ready() -> void:
                        set_as_top_level(true)
                        Input.set_mouse_mode(Input.MOUSE_MODE_CAPTURED)

                    func _unhandled_input(event: InputEvent) -> void:
                        if event is InputEventMouseMotion:
                            rotation.y -= event.relative.x * mouse_sensitivity
                            rotation.x -= event.relative.y * mouse_sensitivity
                            rotation.x = clamp(rotation.x, deg_to_rad(min_pitch), deg_to_rad(max_pitch))

                    func follow_target(target_transform: Transform3D, delta: float) -> void:
                        global_transform.origin = global_transform.origin.lerp(target_transform.origin, 15.0 * delta)
                """.trimIndent()

                val nodes = listOf(
                    GodotNodeItem(
                        nodeName = "SpringArm3D",
                        nodeType = "SpringArm3D",
                        properties = mapOf("spring_length" to "4.0", "margin" to "0.2"),
                        children = listOf(
                            GodotNodeItem("Camera3D", "Camera3D", mapOf("current" to "true"))
                        )
                    )
                )
                Pair(code, nodes)
            }

            else -> {
                val code = """
                    # @filename: player_controller.gd
                    extends CharacterBody2D

                    @export var max_speed: float = 350.0
                    @export var acceleration: float = 25.0
                    @export var friction: float = 15.0

                    signal velocity_changed(current_velocity: Vector2)

                    func _physics_process(delta: float) -> void:
                        var input_vector = Input.get_vector("ui_left", "ui_right", "ui_up", "ui_down")
                        
                        if input_vector != Vector2.ZERO:
                            velocity = velocity.move_toward(input_vector * max_speed, acceleration * max_speed * delta)
                        else:
                            velocity = velocity.move_toward(Vector2.ZERO, friction * max_speed * delta)
                            
                        move_and_slide()
                        emit_signal("velocity_changed", velocity)
                """.trimIndent()

                val nodes = listOf(
                    GodotNodeItem(
                        nodeName = "Player",
                        nodeType = "CharacterBody2D",
                        children = listOf(
                            GodotNodeItem("Sprite2D", "Sprite2D", mapOf("texture" to "res://icon.svg")),
                            GodotNodeItem("CollisionShape2D", "CollisionShape2D", mapOf("shape" to "RectangleShape2D")),
                            GodotNodeItem("Camera2D", "Camera2D", mapOf("position_smoothing" to "enabled"))
                        )
                    )
                )
                Pair(code, nodes)
            }
        }
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
