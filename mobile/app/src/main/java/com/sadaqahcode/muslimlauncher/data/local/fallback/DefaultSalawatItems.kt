package com.sadaqahcode.muslimlauncher.data.local.tools.salawat

import com.sadaqahcode.muslimlauncher.domain.model.tools.salawat.Salawat

/**
 * Hardcoded fallback shown immediately when the salawat page opens — before
 * DataStore or network resolve. Replaced transparently once the real data emits.
 */
object DefaultSalawatItems {

    val ALL: List<Salawat> = listOf(
        Salawat(
            id = 1,
            titles = mapOf(
                "en" to "Salawat Ibrahimiyyah (Narration of Kaʿb bin ʿUjrah)",
                "id" to "Shalawat Ibrahimiyah (Riwayat Kaʿb bin ʿUjrah)",
                "ar" to "الصلاة الإبراهيمية",
                "ms" to "Selawat Ibrahimiyah",
                "ur" to "درود ابراہیمی",
                "hi" to "सलावात इब्राहिमिय्या",
            ),
            arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ اللَّهُمَّ بَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            texts = mapOf(
                "en" to "O Allah, (send) salawat upon Muhammad and upon the family of Muhammad, as You (have sent) salawat upon the family of Ibrahim. Indeed, You are Praiseworthy, Glorious. O Allah, (send) blessings upon Muhammad and upon the family of Muhammad, as You (have sent) blessings upon the family of Ibrahim. Indeed, You are Praiseworthy, Glorious.",
                "id" to "Ya Allah, (berilah) shalawat atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) shalawat atas keluarga Ibrahim. Sesungguhnya Engkau Maha Terpuji, Maha Mulia. Ya Allah, (berilah) berkah atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) berkah atas keluarga Ibrahim. Sesungguhnya Engkau Maha Terpuji, Maha Mulia.",
            ),
            contexts = mapOf(
                "en" to "The companions said, 'O Messenger of Allah, we already know how to greet you with salam, so how should we send salawat upon you?' Narrated by Kaʿb bin ʿUjrah.",
                "id" to "Para sahabat berkata, 'Wahai Rasulullah, kami sudah tahu cara mengucapkan salam kepadamu, lalu bagaimana cara kami bershalawat kepadamu?' Diriwayatkan oleh Kaʿb bin ʿUjrah.",
            ),
            latin = "Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in) kamā ṣallayta ʿalā āli Ibrāhīm(a) innaka ḥamīd(un) majīd(un). Allāhumma bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in) kamā bārakta ʿalā āli Ibrāhīm(a) innaka ḥamīd(un) majīd(un).",
            reference = "Muslim 406",
        ),
    )
}