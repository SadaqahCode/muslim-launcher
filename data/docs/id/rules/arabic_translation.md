# Aturan Penerjemahan Arab (Arab → Bahasa Apa Pun)

> **Tentang dokumen ini**
>
> - Ini adalah **rujukan utama** untuk menerjemahkan teks Arab di repositori ini. Jika ada pekerjaan di repositori ini yang bertentangan dengannya, ikuti dokumen ini atau ajukan perubahan terhadapnya.
> - Ini **bukan standar yang diakui** dan **bukan aturan universal**. Aturan ini tidak berlaku di luar repositori ini, dan tidak mengklaim bahwa pendekatan penerjemahan lain itu salah.
> - Dokumen ini menyatakan **posisi kami sebagai pengembang**: pilihan yang kami buat agar terjemahan di sini tetap konsisten dan dapat diperiksa. Dokumen ini tidak memiliki otoritas keagamaan.
> - Dokumen ini bisa saja salah dan dapat berubah. Jika Anda menemukan terjemahan yang tidak mengikuti aturan ini, atau aturan yang perlu diperbaiki, mohon bantu kami. Lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

Aturan-aturan ini mencakup penerjemahan hadis, doa, dan salawat. Aturan ini bersifat netral terhadap bahasa. Pilihan khusus untuk bahasa tertentu terdapat dalam glosarium (bagian 5) dan profil bahasa (bagian 6). Transliterasi Latin dari teks Arab didefinisikan secara terpisah dalam [`arabic_transliteration.md`](./arabic_transliteration.md).

## 1. Prinsip

1. **Sumber tunggal: teks Arab berharakat** (dengan harakat), diperiksa berdasarkan rujukan yang dapat dipercaya ke koleksi aslinya (misalnya, sunnah.com). Catat nama koleksi dan nomor hadisnya.
2. **Terjemahkan langsung dari bahasa Arab**, jangan pernah dari terjemahan bahasa Inggris atau terjemahan bahasa lain.
3. **Literal, dengan padanan yang alami.** Pertahankan urutan kata bahasa Arab selama bahasa target masih terdengar alami.
4. **Satu kata Arab = satu padanan** di seluruh repositori, mengikuti glosarium (bagian 5).
5. **Kata bantu ditulis dalam tanda kurung `(xxx)`.** Kata-kata yang ada dalam bahasa Arab ditulis secara normal. Kata-kata yang ditambahkan agar kalimat terdengar alami dalam bahasa target ditulis dalam tanda kurung (lihat bagian 2).
6. **Tidak ada interpretasi di dalam teks.** Penjelasan makna ditempatkan dalam catatan kaki atau catatan konteks, jangan pernah dalam tanda kurung.
7. **Jangan menyalin terjemahan lain.** Terjemahan dari penerbit, pemerintah, atau situs web hanya boleh digunakan untuk memeriksa makna yang terlewat, jangan pernah sebagai sumber kalimat. Setiap terjemahan di repositori ini harus merupakan karya asli atau memiliki lisensi yang eksplisit dan kompatibel.

## 2. Aturan tanda kurung `(xxx)`

**Ditulis dalam tanda kurung**

| Jenis                                                            | Contoh (Bahasa Indonesia)                   |
| ---------------------------------------------------------------- | ------------------------------------------- |
| Kata kerja bantu yang diperlukan untuk menyertai kata benda      | `(berilah) shalawat` untuk _ṣalli_          |
| Penanda kala (tense) yang melekat pada kata kerja bantu tersebut | `(telah memberi) shalawat` untuk _ṣallayta_ |
| Rujukan yang dihilangkan dan dibiarkan implisit oleh bahasa Arab | `(Allah)`, `(mereka)`                       |
| Kata hubung yang disyaratkan oleh tata bahasa bahasa target      | `(agar)`, `(yaitu)`                         |

**Tidak ditulis dalam tanda kurung**

- Kata-kata yang ada dalam bahasa Arab, bahkan ketika diterjemahkan dengan lebih dari satu kata dalam bahasa target (misalnya, bahasa Indonesia _ḥamīd_ → "Maha Terpuji").
- Kata-kata yang disyaratkan oleh tata bahasa bahasa target dan tidak menambah makna tersendiri: kopula ("Engkau **Maha** Terpuji"), artikel, dan penanda kesesuaian (agreement markers).
- Gelar kehormatan (lihat bagian 4).

**Tidak pernah ditambahkan, dengan atau tanpa tanda kurung, di dalam terjemahan:** penjelasan makna, sejarah, atau hukum. Hal-hal ini ditempatkan dalam catatan kaki.

**Tanda kurung dalam transliterasi** memiliki fungsi yang berbeda: tanda ini menandai akhiran yang hanya diucapkan ketika disambung dengan kata berikutnya (lihat [`arabic_transliteration.md`](./arabic_transliteration.md)). Di mana pun keduanya ditampilkan kepada pembaca, jelaskan perbedaannya.

## 3. Struktur kalimat

- **Ikuti urutan kata bahasa Arab** selama bahasa target masih terdengar alami.
- **Konstruksi genitif (idafa)** mengikuti urutan bahasa target: _āl Muḥammad_ → "keluarga Muhammad".
- **Kata hubung `wa` (و)** diterjemahkan sebagai "dan". Jangan menghilangkannya di awal klausa.
- **Preposisi dan negasi yang diulang dalam bahasa Arab tetap dipertahankan**, misalnya "atas Muhammad dan atas keluarga Muhammad" (mengulang _ʿalā_).
- **Klausa keadaan (hāl)** diterjemahkan dengan "sedangkan" atau padanannya, tidak digabungkan menjadi dua kata kerja yang sejajar.
- **Rangkaian asindeton** (dua kata yang berurutan tanpa kata hubung, seperti _ḥamīdun majīdun_) ditulis dengan koma dan tanpa penambahan kata hubung.
- **Tanda baca:** koma di antara klausa, titik di akhir kalimat. Tanda baca mengikuti bahasa target, bukan bahasa Arab.

## 4. Kata ganti, nama, dan gelar kehormatan

| Bahasa Arab                        | Bahasa Indonesia (`id`)           | Bahasa Inggris (`en`)             | Catatan                                        |
| ---------------------------------- | --------------------------------- | --------------------------------- | ---------------------------------------------- |
| _Allāhumma_                        | Ya Allah                          | O Allah                           | Panggilan (Vokatif)                            |
| _-ka_ (mengalamatkan kepada Allah) | Engkau, -Mu                       | You, Your                         | Huruf kapital                                  |
| _-hi_ (orang ketiga maskulin)      | -nya                              | his, him                          | Bukan bentuk gelar kehormatan seperti _beliau_ |
| _-kum_                             | kamu atau kalian                  | you (plural)                      | Pilih satu per bahasa dan tetap konsisten      |
| _Muḥammad_                         | Muhammad                          | Muhammad                          | Tanpa tambahan "Nabi" atau "the Prophet"       |
| _Ibrāhīm_                          | Ibrahim                           | Ibrahim                           | Tanpa tambahan "Nabi" atau "the Prophet"       |
| Nama perawi                        | Ikuti `arabic_transliteration.md` | Ikuti `arabic_transliteration.md` | Contoh: Abū Masʿūd al-Anṣārī                   |

**Gelar kehormatan**

- **Dalam teks terjemahan (matan):** tidak ditulis, kecuali jika ada dalam bahasa Arab.
- **Dalam catatan konteks (riwayat):** simbol gelar kehormatan standar (ﷺ) boleh digunakan.
- **Dalam presentasi:** gelar kehormatan boleh ditampilkan di samping teks, tetapi bukan merupakan bagian dari terjemahan itu sendiri.

Ini adalah aturan untuk menjaga agar terjemahan tetap setia pada susunan kata dari sumbernya. Ini bukan merupakan sikap mengenai apakah harus mengucapkan atau menuliskan salawat ketika Nabi ﷺ disebutkan.

## 5. Glosarium padanan

Tambahkan istilah ke dalam tabel ini **sebelum** menggunakannya dalam terjemahan apa pun. Saat menambahkan bahasa, tambahkan sebuah kolom.

| Bahasa Arab       | Transliterasi | Bahasa Indonesia (`id`)  | Bahasa Inggris (`en`) |
| ----------------- | ------------- | ------------------------ | --------------------- |
| اللَّهُمَّ        | Allāhumma     | Ya Allah                 | O Allah               |
| صَلِّ             | ṣalli         | (berilah) shalawat       | (send) salawat        |
| صَلَّيْتَ         | ṣallayta      | (telah memberi) shalawat | (have sent) salawat   |
| بَارِكْ           | bārik         | (berilah) berkah         | (send) blessings      |
| بَارَكْتَ         | bārakta       | (telah memberi) berkah   | (have sent) blessings |
| عَلَى             | ʿalā          | atas                     | upon                  |
| آل                | āl            | keluarga                 | family                |
| كَمَا             | kamā          | sebagaimana              | as                    |
| وَ                | wa            | dan                      | and                   |
| فِي الْعَالَمِينَ | fī l-ʿālamīn  | di seluruh alam          | in the worlds         |
| إِنَّكَ           | innaka        | sesungguhnya Engkau      | Indeed, You           |
| حَمِيد            | ḥamīd         | Maha Terpuji             | Praiseworthy          |
| مَجِيد            | majīd         | Maha Mulia               | Glorious              |
| أَزْوَاج          | azwāj         | istri-istri              | wives                 |
| ذُرِّيَّة         | dhurriyya     | keturunan                | descendants           |
| عَبْد             | ʿabd          | hamba                    | servant               |
| رَسُول            | rasūl         | Rasul                    | Messenger             |

## 6. Profil bahasa

Setiap bahasa memiliki profil konvensi singkatnya sendiri. Profil bahasa tidak boleh bertentangan dengan bagian 1 hingga 4.

### Bahasa Indonesia (`id`)

- _Shalawat_ dipertahankan sebagai kata serapan, tidak diterjemahkan.
- _ʿalā_ → "atas" (bawaan; lihat hal yang masih perlu diputuskan).
- Tidak ada kata "lagi" di antara dua sifat: "Maha Terpuji, Maha Mulia".
- _-hi_ → "-nya", bukan "beliau".
- Kata ganti untuk Tuhan menggunakan huruf kapital: "Engkau", "-Mu".
- Tidak ada tambahan "Nabi" sebelum Muhammad atau Ibrahim.

### Bahasa Inggris (`en`)

- _Salawat_ dipertahankan sebagai kata serapan, ditulis tanpa "h".
- _ʿalā_ → "upon".
- _innaka_ → "Indeed, You". Ketika tata bahasa Inggris memerlukan kopula ("Indeed, You are"), kata tersebut ditulis tanpa tanda kurung.
- Kata ganti untuk Tuhan menggunakan huruf kapital: "You", "Your".
- Tidak menggunakan bentuk kuno (thee, thou).
- Tidak ada tambahan "the Prophet" sebelum Muhammad atau Ibrahim.

### Menambahkan bahasa baru

1. Tambahkan kolom ke dalam glosarium dan isi setiap istilah yang sudah ada.
2. Tentukan cara penulisan _shalawat_ dan kata serapan lainnya.
3. Tentukan konvensi kata ganti (bentuk formal atau jamak untuk "you", penggunaan huruf kapital untuk kata ganti Tuhan).
4. Tulis profil bahasa yang singkat di bagian ini.
5. Mintalah terjemahan tersebut diperiksa oleh penutur asli dan pemeriksa bahasa Arab (lihat bagian 8).

## 7. Catatan konteks

Catatan konteks menjelaskan latar belakang atau keadaan suatu riwayat. Catatan ini **bukan** merupakan terjemahan dari teks bahasa Arab.

- Tulis sebagai parafrase singkat dari riwayat tersebut (siapa yang bertanya, apa yang ditanyakan, siapa yang meriwayatkannya).
- Jangan menyalin riwayat dari terjemahan yang sudah diterbitkan.
- Nama perawi mengikuti `arabic_transliteration.md`.
- Simbol gelar kehormatan standar (ﷺ) diperbolehkan di sini.
- Pertahankan fakta yang sama di semua bahasa.

## 8. Tinjauan (Review)

1. Periksa teks Arab berharakat dengan rujukan untuk koleksi aslinya.
2. Buat draf terjemahan literal menggunakan glosarium.
3. Bandingkan maknanya dengan terjemahan lain, tanpa menyalin.
4. Mintalah seseorang yang memahami bahasa Arab untuk meninjaunya.
5. Perlakukan terjemahan sebagai **draf** hingga tinjauan tersebut selesai, dan jangan menyajikannya sebagai hasil yang sudah ditinjau.

Untuk mengusulkan perubahan pada aturan atau terjemahan, lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 9. Contoh: Salawat Ibrahimiyyah (Sahih Muslim 405)

Contoh ini adalah **kutipan**: hanya pada bagian lafaz salawat. Dalam [sumbernya](https://sunnah.com/muslim:405), hadis tersebut dilanjutkan dengan pernyataan singkat tentang salam, yang tidak disertakan di sini.

**Bahasa Arab**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Bahasa Indonesia (`id`)**

> Ya Allah, (berilah) shalawat atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) shalawat atas keluarga Ibrahim. Dan (berilah) berkah atas Muhammad dan atas keluarga Muhammad, sebagaimana Engkau (telah memberi) berkah atas keluarga Ibrahim di seluruh alam. Sesungguhnya Engkau Maha Terpuji, Maha Mulia.

**Bahasa Inggris (`en`)**

> O Allah, (send) salawat upon Muhammad and upon the family of Muhammad, as You (have sent) salawat upon the family of Ibrahim. And (send) blessings upon Muhammad and upon the family of Muhammad, as You (have sent) blessings upon the family of Ibrahim in the worlds. Indeed, You are Praiseworthy, Glorious.

## 10. Hal yang masih perlu diputuskan

- **`ʿalā`: "atas" atau "kepada" (Bahasa Indonesia).** Bawaan saat ini: "atas" (literal). "Kepada" mungkin terdengar lebih alami bagi pembaca bahasa Indonesia. Putuskan dan perbarui glosarium jika diperlukan.
- **"kamu" atau "kalian" untuk _-kum_ (Bahasa Indonesia).** Belum diputuskan.
- **Kata dengan lebih dari satu makna.** Prinsip "Satu kata Arab = satu padanan" mungkin tidak berlaku ketika sebuah kata memiliki makna yang berbeda dalam konteks yang berbeda (misalnya, _ṣalāh_ sebagai salat wajib atau sebagai salawat). Apakah glosarium boleh mencantumkan satu padanan per makna belum diputuskan.
- **Posisi "di seluruh alam" (_fī l-ʿālamīn_).** Dipertahankan sesuai urutan bahasa Arab (setelah klausa pemberkahan, sebelum _innaka_).
- **Pemeriksa bahasa Arab.** Belum ditunjuk.
- **Lingkup untuk terjemahan Al-Qur'an.** Aturan-aturan ini ditulis untuk hadis, doa, dan salawat. Apakah terjemahan Al-Qur'an mengikuti aturan yang sama atau menggunakan terjemahan berlisensi terpisah belum diputuskan.
