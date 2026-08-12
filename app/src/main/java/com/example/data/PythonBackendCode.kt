package com.example.data

object PythonBackendCode {

    val SCRIPT_CODE = """
# ==============================================================================
# GODOT AI BRIDGE - Dynamic OpenRouter AI Router & Supervision Server
# ==============================================================================
# A production-ready Python Flask backend acting as an AI bridge and dynamic
# model router for Godot Engine editor plugins.
#
# Features:
# 1. Dynamic Model Selection (OpenRouter API e.g. meta-llama/llama-3-8b-instruct:free)
# 2. Supervision & Safety Review Logic (Parses GDScript code blocks & Node hierarchies)
# 3. Godot 4.x / 3.x System Prompts (Optimized for GDScript, Shaders & Nodes)
# 4. Endpoints:
#    - GET  /              : Health Check & Server Metrics
#    - POST /generate-code : Main AI Code & Scene Logic Generator
#    - GET  /models        : OpenRouter Models Directory
#    - POST /validate-key  : OpenRouter API Key Inspector
#    - POST /preview-diff  : GDScript Line Diff Generator
#
# Mobile Setup (Pydroid 3 / Termux):
#   pip install flask flask-cors requests
#   python godot_ai_bridge.py
# ==============================================================================

import os
import re
import time
import json
import logging
from typing import Dict, Any, List, Tuple
from flask import Flask, request, jsonify, Response
from flask_cors import CORS
import requests

# ------------------------------------------------------------------------------
# Logging & Server Configuration
# ------------------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="[%(asctime)s] %(levelname)s - %(message)s",
    datefmt="%H:%M:%S"
)
logger = logging.getLogger("GodotAIBridge")

app = Flask(__name__)
CORS(app)  # Enable Cross-Origin Resource Sharing for Godot HTTPClient

SERVER_START_TIME = time.time()
OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1"

# Recommended OpenRouter models for Godot Engine logic
RECOMMENDED_MODELS = [
    {
        "id": "meta-llama/llama-3-8b-instruct:free",
        "name": "Llama 3 8B Instruct (Free)",
        "provider": "Meta",
        "badge": "FREE",
        "speed": "Fast",
        "description": "Great for standard GDScript functions, player controllers, and node setups."
    },
    {
        "id": "google/gemini-2.5-flash:free",
        "name": "Gemini 2.5 Flash (Free)",
        "provider": "Google",
        "badge": "FREE",
        "speed": "Ultra Fast",
        "description": "Excellent for rapid GDScript generation and complex scene tree structures."
    },
    {
        "id": "anthropic/claude-3.5-sonnet",
        "name": "Claude 3.5 Sonnet",
        "provider": "Anthropic",
        "badge": "PRO",
        "speed": "Balanced",
        "description": "Top-tier model for complex Godot architecture, custom shaders, and algorithms."
    },
    {
        "id": "openai/gpt-4o",
        "name": "GPT-4o",
        "provider": "OpenAI",
        "badge": "PRO",
        "speed": "Fast",
        "description": "High intelligence model for advanced GDScript refactoring and state machines."
    },
    {
        "id": "deepseek/deepseek-coder",
        "name": "DeepSeek Coder 33B",
        "provider": "DeepSeek",
        "badge": "POPULAR",
        "speed": "Fast",
        "description": "Specialized code generation model with strong GDScript synthesis capabilities."
    }
]


# ------------------------------------------------------------------------------
# Default Godot System Prompts
# ------------------------------------------------------------------------------
DEFAULT_GDSCRIPT_SYSTEM_PROMPT = ""${'"'}
You are an expert Godot Engine 4.x game developer and GDScript architect.
Your job is to generate production-ready, clean, safe, and bug-free GDScript code or node structures based on user prompts.

RULES:
1. Always write idiomatic Godot 4 GDScript (using @export, @onready, CharacterBody2D/3D, move_and_slide(), signal definitions).
2. Cleanly isolate GDScript code inside ```gdscript code blocks.
3. If node tree setups are requested, provide a structured JSON block inside ```json with node types, names, and parent-child hierarchy.
4. Include helpful comments explaining physics processes, signals, and exported variables.
5. Do NOT perform dangerous operating system executions or file system wipes unless specifically asked.
""${'"'}


# ------------------------------------------------------------------------------
# Supervision & Safety Review Engine
# ------------------------------------------------------------------------------
def review_and_parse_output(ai_response_text: str) -> Dict[str, Any]:
    ""${'"'}
    Supervision logic: Parses raw AI markdown response, extracts GDScript blocks,
    inspects code for safety/syntax issues, and extracts Godot Node structures.
    ""${'"'}
    # 1. Extract GDScript code blocks
    gdscript_blocks = re.findall(r"```(?:gdscript|python|code)?\n(.*?)```", ai_response_text, re.DOTALL)
    
    clean_code = ""
    if gdscript_blocks:
        clean_code = gdscript_blocks[0].strip()
    else:
        # Fallback if no block ticks were used
        clean_code = ai_response_text.strip()

    # 2. Extract JSON Node structures if present
    node_structure = None
    json_blocks = re.findall(r"```json\n(.*?)```", ai_response_text, re.DOTALL)
    if json_blocks:
        try:
            node_structure = json.loads(json_blocks[0].strip())
        except Exception:
            node_structure = None

    # 3. Perform Safety & Supervision Inspection
    safety_flags = []
    has_extends = "extends" in clean_code
    has_os_execute = "OS.execute" in clean_code
    has_file_remove = "DirAccess.remove" in clean_code or "FileAccess" in clean_code
    has_infinite_loop = "while true:" in clean_code or "while True:" in clean_code

    if not has_extends and "func " in clean_code:
        safety_flags.append("Warning: Missing 'extends <NodeType>' clause at top of script.")
    if has_os_execute:
        safety_flags.append("Caution: Code contains 'OS.execute()' system process invocation.")
    if has_file_remove:
        safety_flags.append("Caution: Code accesses disk or file deletion operations.")
    if has_infinite_loop:
        safety_flags.append("Error: Potential unhandled infinite loop 'while true' detected.")

    supervision_passed = not (has_os_execute or has_infinite_loop)

    # 4. Extract filename suggestion
    filename_match = re.search(r"#\s*@filename:\s*([\w_]+\.gd)", clean_code)
    filename_suggestion = filename_match.group(1) if filename_match else "ai_generated_script.gd"

    return {
        "supervision_passed": supervision_passed,
        "safety_flags": safety_flags,
        "filename_suggestion": filename_suggestion,
        "code": clean_code,
        "node_structure": node_structure,
        "has_extends": has_extends,
        "lines_count": len(clean_code.splitlines()) if clean_code else 0
    }


# ------------------------------------------------------------------------------
# Flask API Routes
# ------------------------------------------------------------------------------

@app.route('/', methods=['GET'])
def health_check():
    ""${'"'}Health check & router status endpoint.""${'"'}
    uptime_seconds = int(time.time() - SERVER_START_TIME)
    return jsonify({
        "status": "online",
        "service": "Godot AI Bridge Router",
        "version": "1.0.0",
        "uptime_seconds": uptime_seconds,
        "openrouter_endpoint": f"{OPENROUTER_BASE_URL}/chat/completions",
        "recommended_models_count": len(RECOMMENDED_MODELS),
        "supervision_enabled": True,
        "endpoints": {
            "health": "GET /",
            "generate_code": "POST /generate-code",
            "models": "GET /models",
            "validate_key": "POST /validate-key",
            "preview_diff": "POST /preview-diff"
        }
    }), 200


@app.route('/models', methods=['GET'])
def list_models():
    ""${'"'}Returns catalog of recommended OpenRouter AI models.""${'"'}
    return jsonify({
        "status": "success",
        "count": len(RECOMMENDED_MODELS),
        "models": RECOMMENDED_MODELS
    }), 200


@app.route('/validate-key', methods=['POST'])
def validate_key():
    ""${'"'}Validates an OpenRouter API key.""${'"'}
    data = request.get_json() or {}
    api_key = data.get("api_key", "").strip()

    if not api_key:
        return jsonify({"valid": False, "error": "API key is required"}), 400

    headers = {
        "Authorization": f"Bearer {api_key}",
        "HTTP-Referer": "https://godotengine.org",
        "X-Title": "Godot AI Bridge Router"
    }

    try:
        res = requests.get(f"{OPENROUTER_BASE_URL}/auth/key", headers=headers, timeout=10)
        if res.status_code == 200:
            key_data = res.json().get("data", {})
            return jsonify({
                "valid": True,
                "label": key_data.get("label", "OpenRouter Key"),
                "usage": key_data.get("usage", 0.0),
                "limit": key_data.get("limit", None)
            }), 200
        else:
            return jsonify({"valid": False, "error": f"Invalid key (Status {res.status_code})"}), 401
    except Exception as e:
        return jsonify({"valid": False, "error": str(e)}), 500


@app.route('/generate-code', methods=['POST'])
def generate_code():
    ""${'"'}
    Main processing endpoint:
    Accepts prompt, OpenRouter model ID, and API key.
    Forwards request to OpenRouter API and returns supervised GDScript output.
    ""${'"'}
    start_req_time = time.time()
    payload = request.get_json() or {}

    api_key = payload.get("api_key", "").strip() or os.getenv("OPENROUTER_API_KEY", "")
    model = payload.get("model", "meta-llama/llama-3-8b-instruct:free").strip()
    prompt = payload.get("prompt", "").strip()
    system_prompt = payload.get("system_prompt", DEFAULT_GDSCRIPT_SYSTEM_PROMPT)
    temperature = float(payload.get("temperature", 0.7))
    max_tokens = int(payload.get("max_tokens", 2048))
    existing_code = payload.get("existing_code", "")

    if not api_key:
        return jsonify({
            "status": "error",
            "error_code": "MISSING_API_KEY",
            "message": "Please provide an 'api_key' in the request JSON payload or set OPENROUTER_API_KEY environment variable."
        }), 400

    if not prompt:
        return jsonify({
            "status": "error",
            "error_code": "MISSING_PROMPT",
            "message": "Please provide a 'prompt' describing the GDScript logic or Godot scene required."
        }), 400

    # Build messages array for OpenRouter
    messages = [{"role": "system", "content": system_prompt}]
    
    if existing_code:
        messages.append({
            "role": "user",
            "content": f"Existing GDScript context:\n```gdscript\n{existing_code}\n```"
        })

    messages.append({"role": "user", "content": prompt})

    openrouter_payload = {
        "model": model,
        "messages": messages,
        "temperature": temperature,
        "max_tokens": max_tokens
    }

    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
        "HTTP-Referer": "https://godotengine.org",
        "X-Title": "Godot AI Bridge Editor Plugin"
    }

    logger.info(f"Forwarding prompt to OpenRouter model [{model}]...")

    try:
        response = requests.post(
            f"{OPENROUTER_BASE_URL}/chat/completions",
            headers=headers,
            json=openrouter_payload,
            timeout=60
        )

        latency_ms = int((time.time() - start_req_time) * 1000)

        if response.status_code != 200:
            logger.error(f"OpenRouter API Error ({response.status_code}): {response.text}")
            return jsonify({
                "status": "error",
                "openrouter_status": response.status_code,
                "message": f"OpenRouter API returned error code {response.status_code}",
                "details": response.text
            }), response.status_code

        res_json = response.json()
        choices = res_json.get("choices", [])
        if not choices:
            return jsonify({"status": "error", "message": "No response choices returned from model."}), 500

        raw_ai_text = choices[0].get("message", {}).get("content", "")

        # Run Supervision & Parsing logic
        supervision = review_and_parse_output(raw_ai_text)

        return jsonify({
            "status": "success",
            "model_used": model,
            "latency_ms": latency_ms,
            "supervision": {
                "passed": supervision["supervision_passed"],
                "flags": supervision["safety_flags"],
                "filename_suggestion": supervision["filename_suggestion"],
                "lines_count": supervision["lines_count"],
            },
            "code": supervision["code"],
            "node_structure": supervision["node_structure"],
            "raw_response": raw_ai_text,
            "usage": res_json.get("usage", {})
        }), 200

    except requests.exceptions.Timeout:
        return jsonify({"status": "error", "message": "Request to OpenRouter timed out after 60s"}), 504
    except Exception as e:
        logger.exception("Unexpected error in /generate-code")
        return jsonify({"status": "error", "message": str(e)}), 500


@app.route('/preview-diff', methods=['POST'])
def preview_diff():
    ""${'"'}Generates a visual comparison between original GDScript and newly generated GDScript.""${'"'}
    data = request.get_json() or {}
    old_code = data.get("original_code", "").splitlines()
    new_code = data.get("new_code", "").splitlines()

    diff_lines = []
    max_len = max(len(old_code), len(new_code))

    for i in range(max_len):
        old_l = old_code[i] if i < len(old_code) else None
        new_l = new_code[i] if i < len(new_code) else None

        if old_l == new_l:
            diff_lines.append({"line": i + 1, "type": "same", "content": old_l})
        elif old_l is not None and new_l is None:
            diff_lines.append({"line": i + 1, "type": "removed", "content": old_l})
        elif old_l is None and new_l is not None:
            diff_lines.append({"line": i + 1, "type": "added", "content": new_l})
        else:
            diff_lines.append({"line": i + 1, "type": "modified", "old": old_l, "content": new_l})

    return jsonify({"status": "success", "diff": diff_lines}), 200


# ------------------------------------------------------------------------------
# Entry Point
# ------------------------------------------------------------------------------
if __name__ == '__main__':
    port = int(os.getenv("PORT", 5000))
    host = os.getenv("HOST", "0.0.0.0")

    print("=" * 70)
    print("  GODOT AI BRIDGE - Python Router Server Running")
    print(f"  Local Host: http://{host}:{port}")
    print("  Endpoints:")
    print(f"    - Health Check  : GET  http://localhost:{port}/")
    print(f"    - Generate Code : POST http://localhost:{port}/generate-code")
    print(f"    - Model List    : GET  http://localhost:{port}/models")
    print("=" * 70)

    app.run(host=host, port=port, debug=False)
""".trimIndent()

    val REQUIREMENTS_TXT = """
flask>=3.0.0
flask-cors>=4.0.0
requests>=2.31.0
urllib3>=2.0.0
""".trimIndent()

    val PYDROID_INSTRUCTIONS = """
=== RUNNING ON PYDROID 3 (ANDROID) ===
1. Install Pydroid 3 from Google Play Store or APK repository.
2. Open Pydroid 3 -> Menu -> Terminal.
3. Install required packages by running:
   pip install flask flask-cors requests

4. Create a file named 'godot_ai_bridge.py' and paste the Python code.
5. Tap the yellow 'Play' button in Pydroid 3 to start the server.
6. The server will run at http://127.0.0.1:5000 (or your Android local IP).
7. In your Godot Editor, configure your AI plugin HTTP request to point to:
   http://127.0.0.1:5000/generate-code

=== RUNNING ON LOCAL PC / MAC / LINUX ===
1. Ensure Python 3.9+ is installed: python --version
2. Install dependencies:
   pip install flask flask-cors requests
3. Run the bridge:
   python godot_ai_bridge.py
4. Optional environment variables:
   export OPENROUTER_API_KEY="your-key-here"
   export PORT=5000
""".trimIndent()

    val GODOT_PLUGIN_SCRIPT = """
# ==============================================================================
# GODOT EDITOR PLUGIN - AI BRIDGE CLIENT (ai_bridge_dock.gd)
# ==============================================================================
@tool
extends Control

@onready var prompt_input: TextEdit = ${'$'}VBox/PromptInput
@onready var model_select: OptionButton = ${'$'}VBox/ModelSelect
@onready var generate_btn: Button = ${'$'}VBox/GenerateButton
@onready var code_preview: CodeEdit = ${'$'}VBox/CodePreview
@onready var status_label: Label = ${'$'}VBox/StatusLabel

var http_request: HTTPRequest
var server_url = "http://127.0.0.1:5000/generate-code"
var api_key = "YOUR_OPENROUTER_API_KEY"

func _ready():
    http_request = HTTPRequest.new()
    add_child(http_request)
    http_request.request_completed.connect(_on_request_completed)
    generate_btn.pressed.connect(_on_generate_pressed)

func _on_generate_pressed():
    if prompt_input.text.strip_edges() == "":
        status_label.text = "Please enter a prompt."
        return

    status_label.text = "Sending request to Python AI Bridge..."
    generate_btn.disabled = true

    var selected_model = "meta-llama/llama-3-8b-instruct:free"
    var body = JSON.stringify({
        "api_key": api_key,
        "model": selected_model,
        "prompt": prompt_input.text
    })

    var headers = ["Content-Type: application/json"]
    http_request.request(server_url, headers, HTTPClient.METHOD_POST, body)

func _on_request_completed(result, response_code, headers, body):
    generate_btn.disabled = false
    if response_code == 200:
        var json = JSON.parse_string(body.get_string_from_utf8())
        if json and json.has("code"):
            code_preview.text = json["code"]
            status_label.text = "Success! Supervision Passed: " + str(json["supervision"]["passed"])
        else:
            status_label.text = "Failed to parse code response."
    else:
        status_label.text = "Server Error Code: " + str(response_code)
""".trimIndent()

}
