package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    ARABIC("ar", "العربية", true)
}

object AppTranslations {
    private val en = mapOf(
        "app_name" to "Haqiqa",
        "tagline" to "Truth, in real time.",
        "input_label" to "Verify Social Media Content",
        "input_placeholder" to "Paste link (Facebook, X, WhatsApp, TikTok) or write a claim...",
        "verify_btn" to "Verify Content",
        "analyzing" to "Haqiqa Core is analyzing...",
        "engine_label" to "Fact-Checking AI Engine",
        "limit_label" to "Usage Limit",
        "limit_desc" to "AI requests remaining this hour",
        "reset_limit_btn" to "Reset Limits (Demo)",
        "reset_in" to "Resets in",
        "history_title" to "Recent Truth Snapshots",
        "no_history" to "No verifications yet. Check a link or claim above!",
        "history_empty_tip" to "Tip: Try pasting a link or writing a claim about the Moon landing or deepfakes to see different analysis modes.",
        "verdict_title" to "Truth Snapshot",
        "verdict_verified" to "VERIFIED CLAIM",
        "verdict_false" to "FALSE CLAIM",
        "verdict_unverifiable" to "UNVERIFIABLE",
        "confidence" to "Confidence Score",
        "is_deepfake" to "Deepfake Warning",
        "deepfake_yes" to "Fabrication Detected",
        "deepfake_no" to "Authentic Media Scan",
        "claims_detected" to "Extracted Claims & Corrections",
        "sources_cited" to "Credible Sources Cited",
        "share_btn" to "Share Truth Snapshot",
        "delete_tooltip" to "Delete from offline database",
        "clear_history" to "Clear History",
        "language_switch" to "العربية",
        "link_extraction" to "Simulating content extraction from",
        "voice_recording_btn" to "Record Voice Note",
        "voice_recording_stop" to "Stop & Analyze",
        "voice_recording_sim" to "Recording Voice Note... Click Stop to analyze.",
        "image_scan_btn" to "Scan Visuals / Image",
        "select_sample_prompt" to "Select a Visual Demo Sample:",
        "sample_deepfake_title" to "Elon Musk Crypto Scam (Deepfake Video)",
        "sample_moon_title" to "NASA Moon Landing Photo (Authentic Image)",
        "sample_health_title" to "WhatsApp Garlic COVID Remedy (False Text)",
        "extension_btn" to "Show Chrome Extension",
        "extension_title" to "Haqiqa Browser Extension",
        "extension_desc" to "Simulating real-time desktop browser fact-checking.",
        "extension_placeholder" to "Paste text on web page...",
        "close_btn" to "Close",
        "rate_limit_error" to "Usage limit exceeded! Try again after the hourly cooldown or click the Demo Reset button below.",
        "api_fallback_note" to "Note: Active in high-fidelity offline simulation mode.",
        "community_insight" to "Community Insight",
        "about_text" to "Haqiqa verifies online claims instantly. It scans social posts, checks databases, and detects deepfakes to stop the spread of fake news."
    )

    private val ar = mapOf(
        "app_name" to "الحقيقة",
        "tagline" to "الحقيقة، في الوقت الحقيقي.",
        "input_label" to "التحقق من محتوى وسائل التواصل الاجتماعي",
        "input_placeholder" to "أدخل رابط منشور (فيسبوك، إكس، واتساب، تيك توك) أو اكتب ادعاءً...",
        "verify_btn" to "تحقق من المحتوى",
        "analyzing" to "يجري فحص المحتوى عبر عقل الحقيقة...",
        "engine_label" to "محرك التحقق بالذكاء الاصطناعي",
        "limit_label" to "حد الاستخدام",
        "limit_desc" to "عمليات التحقق المتبقية هذا الوقت",
        "reset_limit_btn" to "إعادة تعيين الحدود (تجريبي)",
        "reset_in" to "إعادة التعيين خلال",
        "history_title" to "لقطات الحقيقة الأخيرة",
        "no_history" to "لا توجد عمليات تحقق سابقة. جرب فحص رابط أو ادعاء أعلاه!",
        "history_empty_tip" to "نصيحة: جرب لصق رابط أو كتابة ادعاء حول الهبوط على القمر أو التزييف العميق لرؤية أوضاع تحليل مختلفة.",
        "verdict_title" to "لقطة الحقيقة",
        "verdict_verified" to "ادعاء موثوق",
        "verdict_false" to "ادعاء زائف",
        "verdict_unverifiable" to "غير قابل للتحقق",
        "confidence" to "مستوى الثقة",
        "is_deepfake" to "تحذير التزييف العميق",
        "deepfake_yes" to "تم كشف تزييف في الوسائط",
        "deepfake_no" to "فحص الوسائط: أصلي",
        "claims_detected" to "الادعاءات المستخرجة والتصحيحات",
        "sources_cited" to "المصادر الموثوقة المستند إليها",
        "share_btn" to "مشاركة لقطة الحقيقة",
        "delete_tooltip" to "حذف من قاعدة البيانات المحلية",
        "clear_history" to "مسح السجل",
        "language_switch" to "English",
        "link_extraction" to "محاكاة استخراج المحتوى من",
        "voice_recording_btn" to "تسجيل ملاحظة صوتية",
        "voice_recording_stop" to "إيقاف وتحليل",
        "voice_recording_sim" to "جاري تسجيل ملاحظة صوتية... انقر إيقاف للتحليل.",
        "image_scan_btn" to "مسح الصور / الوسائط",
        "select_sample_prompt" to "اختر عينة وسائط تجريبية:",
        "sample_deepfake_title" to "احتيال إيلون ماسك للعملات الرقمية (فيديو مزيف عميق)",
        "sample_moon_title" to "صورة ناسا للهبوط على القمر (صورة حقيقية)",
        "sample_health_title" to "علاج كورونا بالثوم على واتساب (نص زائف)",
        "extension_btn" to "عرض ملحق المتصفح",
        "extension_title" to "ملحق متصفح الحقيقة",
        "extension_desc" to "محاكاة التحقق من صحة المواقع في الوقت الحقيقي على ديسكتوب.",
        "extension_placeholder" to "انسخ نصاً على صفحة الويب...",
        "close_btn" to "إغلاق",
        "rate_limit_error" to "لقد تجاوزت حد الاستخدام! يرجى الانتظار لحين انتهاء فترة التبريد أو انقر على زر إعادة تعيين التجريبي أدناه.",
        "api_fallback_note" to "ملاحظة: نشط في وضع المحاكاة عالي الدقة دون اتصال.",
        "community_insight" to "رأي المجتمع الموثوق",
        "about_text" to "يقوم تطبيق الحقيقة بالتحقق الفوري من الادعاءات والشائعات المنتشرة عبر وسائل التواصل الاجتماعي من خلال فحص النصوص والصور والمقاطع الصوتية وكشف التزييف العميق."
    )

    fun translate(key: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.ARABIC) {
            ar[key] ?: en[key] ?: key
        } else {
            en[key] ?: key
        }
    }
}
