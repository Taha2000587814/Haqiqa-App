package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fact_checks")
data class FactCheckResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val inputContent: String,
    val contentType: String, // "Link", "Text", "Image", "Audio", "Video"
    val selectedEngine: String, // "Gemini", "Galaxy AI", "Fact GPT"
    val verdict: String, // "VERIFIED", "FALSE", "UNVERIFIABLE"
    val confidence: Int, // 0 - 100
    val summary: String,
    val deepfakeAssessment: String? = null,
    val isDeepfake: Boolean = false,
    val claimsJson: String, // JSON representation of claims list
    val platform: String? = null // "Facebook", "X", "TikTok", "WhatsApp", "None"
)

@Entity(tableName = "request_logs")
data class RequestLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
