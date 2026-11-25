package com.sadaqahcode.muslimlauncher.data.local.tools.tasbih

import com.sadaqahcode.muslimlauncher.domain.model.tools.Dhikr
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrEntry
import com.sadaqahcode.muslimlauncher.domain.model.tools.DhikrItem

/**
 * Hardcoded fallback shown immediately when the tasbih page opens — before
 * DataStore or network resolve. Replaced transparently once the real data emits.
 *
 * One routine "Daily Tasbih" (Istighfar ×3 → Tasbih ×33 → Tahmid ×33 → Takbir ×34)
 * keeps guided mode meaningful from the first frame.
 */
object DefaultDhikrItems {

    private val istighfar = Dhikr(
        id = 1,
        titles = mapOf(
            "en" to "Istighfar",
            "id" to "Istighfar",
            "ar" to "الاستغفار",
            "ms" to "Istighfar",
            "ur" to "استغفار",
            "hi" to "इस्तिग़फ़ार",
        ),
        arabic = "أَسْتَغْفِرُ ٱللَّٰهَ",
        texts = mapOf(
            "en" to "I seek forgiveness from Allah",
            "id" to "Aku memohon ampunan kepada Allah",
            "ar" to "أستغفر الله",
            "ms" to "Aku memohon keampunan daripada Allah",
            "ur" to "میں اللہ سے بخشش مانگتا ہوں",
            "hi" to "मैं अल्लाह से माफ़ी माँगता हूँ",
        ),
        latin = "Astaghfirullah",
        reference = "Muslim 2720",
    )

    private val tasbih = Dhikr(
        id = 2,
        titles = mapOf(
            "en" to "Tasbih",
            "id" to "Tasbih",
            "ar" to "التسبيح",
            "ms" to "Tasbih",
            "ur" to "تسبیح",
            "hi" to "तस्बीह",
        ),
        arabic = "سُبْحَانَ ٱللَّٰهِ",
        texts = mapOf(
            "en" to "Glory be to Allah",
            "id" to "Maha Suci Allah",
            "ar" to "سبحان الله",
            "ms" to "Maha Suci Allah",
            "ur" to "اللہ پاک ہے",
            "hi" to "अल्लाह पवित्र है",
        ),
        latin = "Subhanallah",
        reference = "Bukhari 6405",
    )

    private val tahmid = Dhikr(
        id = 3,
        titles = mapOf(
            "en" to "Tahmid",
            "id" to "Tahmid",
            "ar" to "التحميد",
            "ms" to "Tahmid",
            "ur" to "تحمید",
            "hi" to "तहमीद",
        ),
        arabic = "ٱلْحَمْدُ لِلَّٰهِ",
        texts = mapOf(
            "en" to "All praise is due to Allah",
            "id" to "Segala puji bagi Allah",
            "ar" to "الحمد لله",
            "ms" to "Segala puji bagi Allah",
            "ur" to "تمام تعریفیں اللہ کے لیے ہیں",
            "hi" to "सारी प्रशंसा अल्लाह के लिए है",
        ),
        latin = "Alhamdulillah",
        reference = "Bukhari 6405",
    )

    private val takbir = Dhikr(
        id = 4,
        titles = mapOf(
            "en" to "Takbir",
            "id" to "Takbir",
            "ar" to "التكبير",
            "ms" to "Takbir",
            "ur" to "تکبیر",
            "hi" to "तकबीर",
        ),
        arabic = "ٱللَّٰهُ أَكْبَرُ",
        texts = mapOf(
            "en" to "Allah is the Greatest",
            "id" to "Allah Maha Besar",
            "ar" to "الله أكبر",
            "ms" to "Allah Maha Besar",
            "ur" to "اللہ سب سے بڑا ہے",
            "hi" to "अल्लाह सबसे बड़ा है",
        ),
        latin = "Allahu Akbar",
        reference = "Bukhari 6405",
    )

    val ALL: List<DhikrItem> = listOf(
        DhikrItem(
            id = 1L,
            names = mapOf(
                "en" to "Daily Tasbih",
                "id" to "Tasbih Harian",
                "ar" to "تسبيح يومي",
                "ms" to "Tasbih Harian",
                "ur" to "روزانہ تسبیح",
                "hi" to "दैनिक तस्बीह",
            ),
            texts = mapOf(
                "en" to "A short routine of four dhikr after prayer.",
                "id" to "Rangkaian singkat empat dzikir setelah shalat.",
                "ar" to "سلسلة قصيرة من أربعة أذكار بعد الصلاة.",
                "ms" to "Rangkaian ringkas empat zikir selepas solat.",
                "ur" to "نماز کے بعد چار اذکار کی مختصر ترتیب۔",
                "hi" to "नमाज़ के बाद चार ज़िक्र की छोटी श्रृंखला।",
            ),
            position = 0,
            entries = listOf(
                DhikrEntry(dhikr = istighfar, repetitions = 3),
                DhikrEntry(dhikr = tasbih, repetitions = 33),
                DhikrEntry(dhikr = tahmid, repetitions = 33),
                DhikrEntry(dhikr = takbir, repetitions = 34),
            ),
        ),
    )
}