package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.FactCheckResult
import com.example.data.repository.FactCheckRepository
import com.example.data.repository.RateLimitExceededException
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HaqiqaViewModel(private val repository: FactCheckRepository) : ViewModel() {

    // UI Input States
    private val _inputContent = MutableStateFlow("")
    val inputContent: StateFlow<String> = _inputContent.asStateFlow()

    private val _selectedEngine = MutableStateFlow("Gemini") // "Gemini", "Galaxy AI", "Fact GPT"
    val selectedEngine: StateFlow<String> = _selectedEngine.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _contentType = MutableStateFlow("Link") // "Link", "Text", "Image", "Audio", "Video"
    val contentType: StateFlow<String> = _contentType.asStateFlow()

    // UI Feedback States
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _lastCheckResult = MutableStateFlow<FactCheckResult?>(null)
    val lastCheckResult: StateFlow<FactCheckResult?> = _lastCheckResult.asStateFlow()

    // Limit States
    private val _remainingRequests = MutableStateFlow(5)
    val remainingRequests: StateFlow<Int> = _remainingRequests.asStateFlow()

    private val _nextResetTime = MutableStateFlow(0L)
    val nextResetTime: StateFlow<Long> = _nextResetTime.asStateFlow()

    // Simulation States
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _isExtensionActive = MutableStateFlow(false)
    val isExtensionActive: StateFlow<Boolean> = _isExtensionActive.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false) // Start in light mode
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // History Flow from Database
    val history: StateFlow<List<FactCheckResult>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var countdownJob: Job? = null

    init {
        refreshLimitsState()
        startResetCountdownTimer()
    }

    fun setInputContent(content: String) {
        _inputContent.value = content
    }

    fun selectEngine(engine: String) {
        _selectedEngine.value = engine
    }

    fun selectContentType(type: String) {
        _contentType.value = type
        _inputContent.value = "" // Reset input when switching content types to avoid confusion
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.ARABIC
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun toggleExtensionMode() {
        _isExtensionActive.value = !_isExtensionActive.value
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setLastResult(result: FactCheckResult?) {
        _lastCheckResult.value = result
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun verifyContent(
        customInput: String? = null,
        customType: String? = null,
        customBase64: String? = null
    ) {
        val finalInput = customInput ?: _inputContent.value
        val finalType = customType ?: _contentType.value

        if (finalInput.trim().isEmpty()) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null
            try {
                val result = repository.verifyContent(
                    inputContent = finalInput,
                    contentType = finalType,
                    selectedEngine = _selectedEngine.value,
                    base64Image = customBase64,
                    languageCode = _currentLanguage.value.code
                )
                _lastCheckResult.value = result
                _inputContent.value = "" // Clear form
                refreshLimitsState()
            } catch (e: RateLimitExceededException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Verification error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun simulateVoiceNoteRecording() {
        if (_isRecordingVoice.value) {
            // Stopping recording -> Process mock voice notes
            _isRecordingVoice.value = false
            val isAr = _currentLanguage.value == AppLanguage.ARABIC
            val mockTranscript = if (isAr) {
                "سمعت أن تناول الثوم المغلي والليمون على الريق يشفي تماماً من الفيروسات التاجية في 24 ساعة"
            } else {
                "I heard on a WhatsApp audio message that drinking boiled garlic and lemon cures all coronavirus variants in 24 hours"
            }
            verifyContent(customInput = mockTranscript, customType = "Audio")
        } else {
            // Starting recording
            _isRecordingVoice.value = true
        }
    }

    fun simulateDemoSample(title: String, type: String) {
        verifyContent(customInput = title, customType = type)
    }

    fun resetLimits() {
        viewModelScope.launch {
            repository.resetUsageLimits()
            refreshLimitsState()
            _errorMessage.value = null
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _lastCheckResult.value = null
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
            if (_lastCheckResult.value?.id == id) {
                _lastCheckResult.value = null
            }
        }
    }

    private fun refreshLimitsState() {
        viewModelScope.launch {
            _remainingRequests.value = repository.getRemainingRequests()
            _nextResetTime.value = repository.getNextResetTimeMillis()
        }
    }

    private fun startResetCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_nextResetTime.value > 0) {
                    val remaining = _nextResetTime.value - System.currentTimeMillis()
                    if (remaining <= 0) {
                        refreshLimitsState()
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

class HaqiqaViewModelFactory(private val repository: FactCheckRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HaqiqaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HaqiqaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
