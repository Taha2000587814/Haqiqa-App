package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.FactCheckResult
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppTranslations
import com.example.ui.theme.*
import com.example.ui.viewmodel.HaqiqaViewModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaqiqaMainScreen(
    viewModel: HaqiqaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val lastResult by viewModel.lastCheckResult.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val remainingRequests by viewModel.remainingRequests.collectAsStateWithLifecycle()
    val nextResetTime by viewModel.nextResetTime.collectAsStateWithLifecycle()
    val selectedEngine by viewModel.selectedEngine.collectAsStateWithLifecycle()
    val contentType by viewModel.contentType.collectAsStateWithLifecycle()
    val inputContent by viewModel.inputContent.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val isExtensionActive by viewModel.isExtensionActive.collectAsStateWithLifecycle()

    fun t(key: String): String = AppTranslations.translate(key, language)

    // Handle Right-to-Left (RTL) for Arabic dynamically
    val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Brush.linearGradient(listOf(Emerald500, TechBlue)),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = t("app_name"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = t("tagline"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        // Extension simulator Toggle
                        IconButton(
                            onClick = { viewModel.toggleExtensionMode() },
                            modifier = Modifier.testTag("extension_toggle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = t("extension_btn"),
                                tint = if (isExtensionActive) TechBlue else MaterialTheme.colorScheme.outline
                            )
                        }

                        // Language Switcher Pill
                        Button(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(32.dp)
                                .testTag("language_switch_btn")
                        ) {
                            Text(
                                text = t("language_switch"),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // 1. Hero / Intro Welcome Banner
                    item {
                        IntroCard(language = language, onHelpClick = {
                            Toast.makeText(context, AppTranslations.translate("about_text", language), Toast.LENGTH_LONG).show()
                        })
                    }

                    // 2. Engine & Limits Combined Panel
                    item {
                        EngineAndLimitsPanel(
                            selectedEngine = selectedEngine,
                            remainingRequests = remainingRequests,
                            nextResetTime = nextResetTime,
                            onEngineSelected = { viewModel.selectEngine(it) },
                            onResetLimits = { viewModel.resetLimits() },
                            language = language
                        )
                    }

                    // 3. Verification Form / Dynamic Tabs
                    item {
                        VerificationForm(
                            contentType = contentType,
                            inputContent = inputContent,
                            isAnalyzing = isAnalyzing,
                            isRecordingVoice = isRecordingVoice,
                            onTypeSelected = { viewModel.selectContentType(it) },
                            onInputChange = { viewModel.setInputContent(it) },
                            onVerify = { viewModel.verifyContent() },
                            onVoiceRecordClick = { viewModel.simulateVoiceNoteRecording() },
                            onSampleClick = { title, type -> viewModel.simulateDemoSample(title, type) },
                            language = language
                        )
                    }

                    // 4. Analysis Loading indicator / Shimmer
                    if (isAnalyzing) {
                        item {
                            ShimmerLoadingCard(language = language)
                        }
                    }

                    // 5. Error Banner
                    if (errorMessage != null) {
                        item {
                            ErrorCard(
                                message = errorMessage!!,
                                onDismiss = { viewModel.dismissError() },
                                onResetLimits = { viewModel.resetLimits() },
                                language = language
                            )
                        }
                    }

                    // 6. Active Snapshot / Verdict Card
                    if (lastResult != null && !isAnalyzing) {
                        item {
                            TruthSnapshotCard(
                                result = lastResult!!,
                                onDismiss = { viewModel.setLastResult(null) },
                                onDelete = { viewModel.deleteHistoryItem(lastResult!!.id) },
                                language = language
                            )
                        }
                    }

                    // 7. Recent History Snapshots (Offline Feed)
                    item {
                        Text(
                            text = t("history_title"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (history.isEmpty()) {
                        item {
                            EmptyHistoryCard(language = language)
                        }
                    } else {
                        items(history, key = { it.id }) { item ->
                            HistoryItemCard(
                                item = item,
                                isSelected = lastResult?.id == item.id,
                                onClick = { viewModel.setLastResult(item) },
                                onDelete = { viewModel.deleteHistoryItem(item.id) },
                                language = language
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(
                                    onClick = { viewModel.clearAllHistory() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Red500),
                                    modifier = Modifier.testTag("clear_history_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = t("clear_history"))
                                }
                            }
                        }
                    }

                    // 7.5 Developer Info Card
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "تم التطوير بواسطة طه عبد الكريم" else "Developed by Taha A. Kreem",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.ARABIC) "صنع في السودان" else "Made in Sudan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "🇸🇩",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                // 8. Dynamic Overlay: Interactive Chrome Extension Simulator
                AnimatedVisibility(
                    visible = isExtensionActive,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    ChromeExtensionSimulator(
                        onClose = { viewModel.toggleExtensionMode() },
                        onQuickCheck = { claim ->
                            viewModel.setInputContent(claim)
                            viewModel.selectContentType("Text")
                            viewModel.verifyContent(customInput = claim, customType = "Text")
                            viewModel.toggleExtensionMode()
                        },
                        language = language
                    )
                }
            }
        }
    }
}

@Composable
fun IntroCard(
    language: AppLanguage,
    onHelpClick: () -> Unit
) {
    fun t(key: String): String = AppTranslations.translate(key, language)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Elegant background design sweep
                    drawCircle(
                        color = Emerald500.copy(alpha = 0.08f),
                        radius = size.minDimension * 0.6f,
                        center = androidx.compose.ui.geometry.Offset(size.width, 0f)
                    )
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Emerald500.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "BETA v1.0.2",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald500,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(onClick = onHelpClick) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = Slate300
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = t("app_name") + " • " + t("tagline"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TechBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (language == AppLanguage.ARABIC) {
                    "امسح منشورات فيسبوك، إكس، واتساب، وتيك توك لكشف الزيف والتزييف العميق فورياً بالذكاء الاصطناعي."
                } else {
                    "Scan Facebook, X (Twitter), WhatsApp, & TikTok links or media to detect deepfakes and debunk rumors instantly with AI."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Slate300,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun EngineAndLimitsPanel(
    selectedEngine: String,
    remainingRequests: Int,
    nextResetTime: Long,
    onEngineSelected: (String) -> Unit,
    onResetLimits: () -> Unit,
    language: AppLanguage
) {
    fun t(key: String): String = AppTranslations.translate(key, language)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Card 1: AI Engine Selector Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Engine Selector Heading
                Text(
                    text = t("engine_label"),
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate300,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // AI Engine Selectors - Dropdown Menu
                var expanded by remember { mutableStateOf(false) }
                val engines = listOf("Gemini", "Galaxy AI", "Fact GPT")

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate700)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .clickable { expanded = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("engine_selector_dropdown_anchor"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = when (selectedEngine) {
                                    "Galaxy AI" -> Icons.Default.Info
                                    "Fact GPT" -> Icons.Default.Search
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = "Engine Icon",
                                tint = TechBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = selectedEngine,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = when (selectedEngine) {
                                        "Galaxy AI" -> if (language == AppLanguage.ARABIC) "مستكشف التزييف العميق والصور" else "Deepfake & image scanner"
                                        "Fact GPT" -> if (language == AppLanguage.ARABIC) "أرشيف الأخبار ومحرك الحقائق" else "News archives & fact engine"
                                        else -> if (language == AppLanguage.ARABIC) "منطق متكامل وبحث عميق" else "Reasoning & deep search"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate300
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Toggle Dropdown",
                            tint = Slate300,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(if (expanded) 180f else 0f)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Slate800)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .testTag("engine_selector_dropdown_menu")
                    ) {
                        engines.forEach { engine ->
                            val isSelected = engine == selectedEngine
                            DropdownMenuItem(
                                text = {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            text = engine,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) TechBlue else Color.White
                                        )
                                        Text(
                                            text = when (engine) {
                                                "Galaxy AI" -> if (language == AppLanguage.ARABIC) "مسح عالي الدقة للصور والتزييف العميق" else "High-fidelity deepfake scans"
                                                "Fact GPT" -> if (language == AppLanguage.ARABIC) "مسح عميق لأرشيفات الأخبار" else "Deep crawler of news archives"
                                                else -> if (language == AppLanguage.ARABIC) "منطق معزز وبحث متقاطع" else "Rigorous reasoning & search crosscheck"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Slate300
                                        )
                                    }
                                },
                                onClick = {
                                    onEngineSelected(engine)
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (engine) {
                                            "Galaxy AI" -> Icons.Default.Info
                                            "Fact GPT" -> Icons.Default.Search
                                            else -> Icons.Default.Settings
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) TechBlue else Slate300,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = TechBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("engine_${engine.lowercase().replace(" ", "_")}_item")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Card 2: Hourly Usage Limit Card (Styled in Polish theme: Light Blue bg with Navy blue details)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Limits Display Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = t("limit_label").uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$remainingRequests ",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "/ 5",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Reset Timer Label
                    if (remainingRequests < 5 && nextResetTime > 0) {
                        val remainingSeconds = ((nextResetTime - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
                        val min = remainingSeconds / 60
                        val sec = remainingSeconds % 60
                        Text(
                            text = "${t("reset_in")} ${String.format("%02d:%02d", min, sec)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Limits progress bar
                LinearProgressIndicator(
                    progress = { remainingRequests / 5f },
                    color = TechBlue,
                    trackColor = Slate900,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                // Reset Countdown & Demo Actions
                if (remainingRequests < 5) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t("reset_limit_btn"),
                            style = MaterialTheme.typography.labelSmall,
                            color = TechBlue,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .clickable { onResetLimits() }
                                .testTag("reset_limits_demo_btn")
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationForm(
    contentType: String,
    inputContent: String,
    isAnalyzing: Boolean,
    isRecordingVoice: Boolean,
    onTypeSelected: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onVerify: () -> Unit,
    onVoiceRecordClick: () -> Unit,
    onSampleClick: (String, String) -> Unit,
    language: AppLanguage
) {
    fun t(key: String): String = AppTranslations.translate(key, language)

    val types = listOf("Link", "Text", "Image", "Audio", "Video")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = t("input_label"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Type Select Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                val isSelected = type == contentType

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) TechBlue else Slate800)
                        .border(
                            1.dp,
                            if (isSelected) TechBlue else BorderColor,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onTypeSelected(type) }
                        .padding(vertical = 8.dp)
                        .testTag("type_${type.lowercase()}_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (type) {
                                "Link" -> "🔗"
                                "Text" -> "📝"
                                "Image" -> "🖼️"
                                "Audio" -> "🎙️"
                                else -> "🎥"
                            },
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                when (type) {
                                    "Link" -> "رابط"
                                    "Text" -> "نص"
                                    "Image" -> "صورة"
                                    "Audio" -> "صوت"
                                    else -> "فيديو"
                                }
                            } else type,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Slate300
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Layout Container
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (contentType) {
                    "Link", "Text", "Video" -> {
                        val label = if (contentType == "Link") {
                            "Facebook / X / WhatsApp / TikTok Link"
                        } else if (contentType == "Video") {
                            "Video URL or Metadata Search"
                        } else {
                            "Claim Text"
                        }

                        OutlinedTextField(
                            value = inputContent,
                            onValueChange = onInputChange,
                            placeholder = {
                                Text(
                                    text = if (contentType == "Link") {
                                        "https://twitter.com/user/status/..."
                                    } else if (contentType == "Video") {
                                        "https://tiktok.com/viral-video-deepfake..."
                                    } else {
                                        t("input_placeholder")
                                    },
                                    color = Slate600
                                )
                            },
                            label = { Text(text = label, color = Slate300) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Slate100),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("content_input_field"),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = Slate700,
                                unfocusedContainerColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onVerify() })
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Social Shortcuts for Ease of Demoing
                        if (contentType == "Link") {
                            Text(
                                text = if (language == AppLanguage.ARABIC) "روابط سريعة للتجربة:" else "Quick demo links to test:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TechBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val linkSamples = listOf(
                                "twitter.com/nasa/status/apollo11" to "Apollo 11",
                                "facebook.com/health/corona-cure" to "COVID Cure",
                                "tiktok.com/deepfake/elon-musk" to "Elon Scam"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                linkSamples.forEach { (url, label) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Slate700)
                                            .clickable { onInputChange(url) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate100,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onVerify,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("verify_submit_btn"),
                            enabled = inputContent.trim().isNotEmpty() && !isAnalyzing,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t("verify_btn"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    "Image" -> {
                        // Image Deepfake Scanner Interface
                        Text(
                            text = t("select_sample_prompt"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Visual Sample buttons
                        val samples = listOf(
                            t("sample_deepfake_title") to "Image",
                            t("sample_moon_title") to "Image"
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            samples.forEach { (title, type) ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Slate700),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSampleClick(title, type) }
                                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (title.contains("Scam") || title.contains("مزيف")) "🚨" else "🛡️",
                                            fontSize = 20.sp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate100
                                            )
                                            Text(
                                                text = if (title.contains("Scam") || title.contains("مزيف")) "Tapped to scan for deepfakes" else "Tapped to run visual scan",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Slate300
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Audio" -> {
                        // Voice Note Verification Interface
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isRecordingVoice) t("voice_recording_sim") else "Simulate Recording a WhatsApp voice note and check the audio claims for fabrication.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate300,
                                textAlign = TextAlign.Center
                            )

                            // Animated Waveform
                            if (isRecordingVoice) {
                                VoiceRecordingWaveform()
                            }

                            // Pulsing Record Button
                            Button(
                                onClick = onVoiceRecordClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecordingVoice) Red500 else TechBlue
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(80.dp)
                                    .shadow(4.dp, CircleShape)
                                    .testTag("voice_record_trigger_btn")
                            ) {
                                Icon(
                                    imageVector = if (isRecordingVoice) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = "Voice",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = if (isRecordingVoice) t("voice_recording_stop") else t("voice_recording_btn"),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isRecordingVoice) Red500 else TechBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceRecordingWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val heights = List(10) { index ->
        infiniteTransition.animateFloat(
            initialValue = 10f,
            targetValue = 30f + (index % 3) * 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400 + index * 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "waveform_item_$index"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(50.dp)
            .padding(vertical = 8.dp)
    ) {
        heights.forEach { heightState ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(heightState.value.dp)
                    .background(Red500, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun ShimmerLoadingCard(language: AppLanguage) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    color = TechBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = AppTranslations.translate("analyzing", language),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            // Fake loading bars
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Slate700.copy(alpha = alpha))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Slate700.copy(alpha = alpha))
            )
        }
    }
}

@Composable
fun ErrorCard(
    message: String,
    onDismiss: () -> Unit,
    onResetLimits: () -> Unit,
    language: AppLanguage
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Red50),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Red500.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Red500
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Red700,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Red500
                    )
                }
            }

            if (message.contains("limit") || message.contains("Limit")) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onResetLimits,
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = AppTranslations.translate("reset_limit_btn", language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TruthSnapshotCard(
    result: FactCheckResult,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    language: AppLanguage
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    fun t(key: String): String = AppTranslations.translate(key, language)

    val verdictColor = when (result.verdict) {
        "VERIFIED" -> Emerald500
        "FALSE" -> Red500
        else -> Amber500
    }

    val verdictLabel = when (result.verdict) {
        "VERIFIED" -> t("verdict_verified")
        "FALSE" -> t("verdict_false")
        else -> t("verdict_unverifiable")
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, verdictColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("active_verdict_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("verdict_title"),
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate300,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(onClick = {
                        val txt = "${result.inputContent}\n\n[Haqiqa Snapshot: ${result.verdict}]\n${result.summary}"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, txt)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TechBlue
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verdict Badge
            Surface(
                color = verdictColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = verdictLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = verdictColor,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary / Verdict Text
            Text(
                text = result.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = Slate100,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multimodal Deepfake / Fabrication Scanner Section
            if (result.contentType == "Image" || result.contentType == "Audio" || result.isDeepfake) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isDeepfake) Red500.copy(alpha = 0.1f) else Emerald500.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (result.isDeepfake) Red500.copy(alpha = 0.3f) else Emerald500.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (result.isDeepfake) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (result.isDeepfake) Red500 else Emerald500
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t("is_deepfake") + ": " + (if (result.isDeepfake) t("deepfake_yes") else t("deepfake_no")),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (result.isDeepfake) Red500 else Emerald500
                            )
                        }
                        if (result.deepfakeAssessment != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.deepfakeAssessment,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate300
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Expandable details (Claims list & sources)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = t("claims_detected"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechBlue
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowBack else Icons.Default.Refresh,
                    contentDescription = "Expand",
                    tint = TechBlue
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Parse claims JSON
                val claimsList = remember(result.claimsJson) {
                    try {
                        val arr = JSONArray(result.claimsJson)
                        val list = mutableListOf<HaqiqaClaimUI>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val sourcesArr = obj.getJSONArray("sources")
                            val sList = mutableListOf<String>()
                            for (j in 0 until sourcesArr.length()) {
                                sList.add(sourcesArr.getString(j))
                            }
                            list.add(
                                HaqiqaClaimUI(
                                    claimText = obj.getString("claimText"),
                                    status = obj.getString("status"),
                                    correction = obj.getString("correction"),
                                    sources = sList
                                )
                            )
                        }
                        list
                    } catch (e: Exception) {
                        emptyList<HaqiqaClaimUI>()
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    claimsList.forEach { claim ->
                        val claimColor = when (claim.status) {
                            "VERIFIED" -> Emerald500
                            "FALSE" -> Red500
                            else -> Amber500
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate900, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = claim.claimText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = claimColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = claim.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = claimColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = claim.correction,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate300
                            )

                            if (claim.sources.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = t("sources_cited") + ":",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                claim.sources.forEach { src ->
                                    Text(
                                        text = "• $src",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate300
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Engine: ${result.selectedEngine}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate300
                )

                Text(
                    text = SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault()).format(Date(result.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
            }
        }
    }
}

data class HaqiqaClaimUI(
    val claimText: String,
    val status: String,
    val correction: String,
    val sources: List<String>
)

@Composable
fun EmptyHistoryCard(language: AppLanguage) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📁",
                fontSize = 32.sp
            )
            Text(
                text = AppTranslations.translate("no_history", language),
                style = MaterialTheme.typography.bodyMedium,
                color = Slate300,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = AppTranslations.translate("history_empty_tip", language),
                style = MaterialTheme.typography.bodySmall,
                color = Slate600,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: FactCheckResult,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    language: AppLanguage
) {
    val statusColor = when (item.verdict) {
        "VERIFIED" -> Emerald500
        "FALSE" -> Red500
        else -> Amber500
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Slate700 else Slate800
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isSelected) statusColor else BorderColor,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("history_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Platform emoji/icon bubble
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Slate900, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (item.contentType) {
                            "Link" -> "🔗"
                            "Text" -> "📝"
                            "Image" -> "🖼️"
                            "Audio" -> "🎙️"
                            else -> "🎥"
                        },
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.inputContent,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.verdict,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "Engine: ${item.selectedEngine}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Slate600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChromeExtensionSimulator(
    onClose: () -> Unit,
    onQuickCheck: (String) -> Unit,
    language: AppLanguage
) {
    var textInput by remember { mutableStateOf("") }
    fun t(key: String): String = AppTranslations.translate(key, language)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate900)
            .drawBehind {
                drawLine(
                    color = TechBlue,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🌐", fontSize = 24.sp)
                    Column {
                        Text(
                            text = t("extension_title"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate100
                        )
                        Text(
                            text = t("extension_desc"),
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate300
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Slate300)
                }
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text(text = t("extension_placeholder"), color = Slate600) },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Slate100),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("extension_sandbox_input"),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100
                )
            )

            Button(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        onQuickCheck(textInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("extension_sandbox_btn"),
                enabled = textInput.isNotEmpty()
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = t("verify_btn"), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
