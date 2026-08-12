package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.repository.RouterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    MONITOR("Monitor", "dashboard"),
    PYTHON_CODE("Rules", "code"),
    PLAYGROUND("Playground", "terminal"),
    LOGS("Logs", "history"),
    SETTINGS("Settings", "settings")
}

class MainViewModel(
    val repository: RouterRepository = RouterRepository()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(AppTab.MONITOR)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    val serverState: StateFlow<RouterServerState> = repository.serverState
    val logs: StateFlow<List<RequestLogItem>> = repository.logs

    // Playground state
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _promptInput = MutableStateFlow("Create a 2D top-down CharacterBody2D movement script with smooth acceleration")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _selectedModelId = MutableStateFlow("meta-llama/llama-3-8b-instruct:free")
    val selectedModelId: StateFlow<String> = _selectedModelId.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentSupervisionResult = MutableStateFlow<SupervisionResult?>(null)
    val currentSupervisionResult: StateFlow<SupervisionResult?> = _currentSupervisionResult.asStateFlow()

    private val _userNotification = MutableStateFlow<String?>(null)
    val userNotification: StateFlow<String?> = _userNotification.asStateFlow()

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun setApiKey(key: String) {
        _apiKey.value = key
    }

    fun setPromptInput(prompt: String) {
        _promptInput.value = prompt
    }

    fun setSelectedModel(modelId: String) {
        _selectedModelId.value = modelId
        repository.setActiveModel(modelId)
    }

    fun toggleServerState() {
        repository.toggleServerState()
        val isActive = repository.serverState.value.isServerActive
        showToast(if (isActive) "Router Server Started on port 5000" else "Router Server Stopped")
    }

    fun updateServerConfig(host: String, port: Int) {
        repository.updateServerConfig(host, port)
        showToast("Router Config Saved: $host:$port")
    }

    fun applyPreset(preset: PromptPreset) {
        _promptInput.value = preset.prompt
        _selectedModelId.value = preset.targetModel
        showToast("Loaded preset: ${preset.title}")
    }

    fun runCodeGeneration() {
        if (_promptInput.value.isBlank()) {
            showToast("Please enter a prompt instruction!")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            val result = repository.processCodeGeneration(
                apiKey = _apiKey.value,
                modelId = _selectedModelId.value,
                prompt = _promptInput.value
            )
            _currentSupervisionResult.value = result
            _isGenerating.value = false
            showToast("AI Code Generated & Supervised Successfully!")
        }
    }

    fun clearLogs() {
        repository.clearLogs()
        showToast("Terminal Logs Cleared")
    }

    fun showToast(msg: String) {
        _userNotification.value = msg
    }

    fun dismissNotification() {
        _userNotification.value = null
    }
}
