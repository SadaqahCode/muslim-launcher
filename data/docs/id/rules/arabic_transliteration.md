# Aturan Transliterasi Arab (Arab → Latin)

> **Tentang dokumen ini**
>
> - Ini adalah **rujukan utama** untuk mentransliterasikan teks Arab di repositori ini. Jika ada pekerjaan di repositori ini yang bertentangan dengannya, ikuti dokumen ini atau ajukan perubahan terhadapnya.
> - Ini **bukan standar yang diakui** dan **bukan aturan universal**. Aturan ini tidak berlaku di luar repositori ini, dan tidak mengklaim bahwa sistem transliterasi lain itu salah.
> - Dokumen ini menyatakan **posisi kami sebagai pengembang**: pilihan yang kami buat agar transliterasi di sini tetap konsisten dan dapat diperiksa. Dokumen ini tidak memiliki otoritas keagamaan.
> - Dokumen ini bisa saja salah dan dapat berubah. Jika Anda menemukan transliterasi yang tidak mengikuti aturan ini, atau aturan yang perlu diperbaiki, mohon bantu kami. Lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

Aturan-aturan ini mendefinisikan satu sistem Latin untuk pembaca dari berbagai latar belakang bahasa (misalnya, di Indonesia, Malaysia, dunia Arab, India, Eropa, Afrika, dan Amerika Serikat). Penerjemahan didefinisikan secara terpisah dalam [`arabic_translation.md`](./arabic_translation.md).

## 1. Prinsip

1. **Sumber tunggal: teks Arab berharakat** (dengan harakat), diperiksa berdasarkan rujukan yang dapat dipercaya ke koleksi aslinya (misalnya, sunnah.com). Catat nama koleksi dan nomor hadisnya.
2. **Transliterasikan langsung dari bahasa Arab**, dengan menerapkan aturan-aturan ini. Jangan menyalin atau mengadaptasi transliterasi lain, dan jangan menulis berdasarkan kebiasaan atau cara pengejaan kata yang biasa digunakan.
3. **Satu konsonan Arab = satu representasi Latin yang tetap**, baik berupa simbol tunggal maupun kombinasi dua huruf (seperti `th` atau `sh`). Jangan menukar representasi.
4. **Tulis huruf aslinya, bukan bunyi yang dihasilkan.** Aturan tajwid seperti idgham, ikhfa', iqlab, dan lainnya tidak dicerminkan dalam teks Latin.
5. **Tanda kurung `(x)` menandai akhiran** yang diucapkan ketika disambung dengan kata berikutnya (washal) dan dihilangkan ketika berhenti (waqaf) (lihat bagian 5).
6. **Latin hanyalah jembatan.** Latin tidak menggantikan teks Arab berharakat atau pembelajaran pengucapan dengan mendengarkan.
7. **Gunakan Unicode NFC** untuk semua teks Latin.

## 2. Konsonan

| Bahasa Arab  | Latin | Catatan                                                                                   |
| ------------ | ----- | ----------------------------------------------------------------------------------------- |
| ب            | b     |                                                                                           |
| ت            | t     |                                                                                           |
| ث            | th    |                                                                                           |
| ج            | j     |                                                                                           |
| ح            | ḥ     | h faringal                                                                                |
| خ            | kh    |                                                                                           |
| د            | d     |                                                                                           |
| ذ            | dh    |                                                                                           |
| ر            | r     |                                                                                           |
| ز            | z     |                                                                                           |
| س            | s     |                                                                                           |
| ش            | sh    | Lihat hal yang masih perlu diputuskan untuk kombinasi dua huruf                           |
| ص            | ṣ     | Emfatis                                                                                   |
| ض            | ḍ     | Emfatis                                                                                   |
| ط            | ṭ     | Emfatis                                                                                   |
| ظ            | ẓ     | Emfatis                                                                                   |
| ع            | ʿ     | ʿAyn (U+02BF)                                                                             |
| غ            | gh    |                                                                                           |
| ف            | f     |                                                                                           |
| ق            | q     |                                                                                           |
| ك            | k     |                                                                                           |
| ل            | l     |                                                                                           |
| م            | m     |                                                                                           |
| ن            | n     |                                                                                           |
| ه            | h     |                                                                                           |
| و (konsonan) | w     |                                                                                           |
| ي (konsonan) | y     |                                                                                           |
| ء            | ʾ     | Hamzah (U+02BE). Ditulis di tengah kata dan di akhir kata; **tidak ditulis** di awal kata |

## 3. Vokal

| Bahasa Arab                            | Latin | Catatan |
| -------------------------------------- | ----- | ------- |
| ـَ (fathah)                            | a     | Pendek  |
| ـِ (kasrah)                            | i     | Pendek  |
| ـُ (dammah)                            | u     | Pendek  |
| ـَا, ـَى (alif panjang / alif maqsura) | ā     | Panjang |
| ـِي (ya panjang)                       | ī     | Panjang |
| ـُو (waw panjang)                      | ū     | Panjang |
| ـَيْ                                   | ay    | Diftong |
| ـَوْ                                   | aw    | Diftong |

## 4. Kasus khusus

| Kasus                         | Aturan                                                                                                          | Contoh                                   |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------- | ---------------------------------------- |
| **Syadda (tasydid)**          | Tulis konsonan dua kali                                                                                         | Allāhumma, ṣallayta                      |
| **Tanwin**                    | Di dalam tanda kurung                                                                                           | Muḥammad(un), Muḥammad(in), Muḥammad(an) |
| **Vokal akhir kasus (i'rab)** | Di dalam tanda kurung                                                                                           | Ibrāhīm(a), ʿālamīn(a)                   |
| **Ta marbuta (ة)**            | Dibaca `a` ketika berhenti dan `at` ketika disambung; ditulis `a(t)`                                            | raḥma(t)                                 |
| **Artikel definit (ال)**      | `al-` di awal kalimat; `l-` setelah kata yang diakhiri vokal. Huruf syamsiyah **tidak** dilebur dalam penulisan | al-shams, fī l-ʿālamīn(a)                |
| **Hamzah washal**             | Hamzah itu sendiri tidak ditulis                                                                                |                                          |
| **Kata Allah (الله)**         | Ditulis `Allāh`                                                                                                 |                                          |
| **Spasi**                     | Satu spasi antar kata; kata-kata ditulis secara terpisah                                                        | wa ʿalā                                  |

## 5. Aturan tanda kurung `(x)`

- **Disambung dengan kata berikutnya (washal):** baca apa yang ada di dalam tanda kurung.  
  `Muḥammad(in) wa` → _Muḥammadin wa_
- **Berhenti (waqaf):** hilangkan apa yang ada di dalam tanda kurung.  
  `Muḥammad(in)` → _Muḥammad_
- **Pengecualian yang diketahui:** ketika berhenti pada _tanwin fathah_ (`an`) pada kata yang tidak diakhiri dengan ta marbuta, akhiran tersebut umumnya dibaca sebagai _ā_ panjang, bukan dihilangkan. Cara menandainya belum diputuskan (lihat hal yang masih perlu diputuskan).
- Tanda kurung hanya untuk bunyi yang hilang atau berubah ketika berhenti. Bunyi yang selalu dibaca tidak dimasukkan ke dalam tanda kurung.
- Pembaca menerapkan aturan idgham dan tajwid lainnya sendiri. Misalnya, sambungan `ḥamīd(un) majīd(un)` dibaca _ḥamīdum majīd_ (idgham dengan ghunnah). Teks Latin tetap ditulis `ḥamīd(un) majīd(un)`.

**Tanda kurung dalam terjemahan** memiliki fungsi yang berbeda: tanda ini menandai kata bantu yang tidak ada dalam bahasa Arab (lihat [`arabic_translation.md`](./arabic_translation.md)). Di mana pun keduanya ditampilkan kepada pembaca, jelaskan perbedaannya.

## 6. Nama

Aturan ini berlaku untuk nama yang ditulis dalam transliterasi, seperti nama perawi dalam catatan konteks.

- Gunakan huruf kapital untuk nama. Artikel definit tetap huruf kecil dan tidak dilebur: _Abū Masʿūd al-Anṣārī_.
- Nama ditulis tanpa akhiran kasus dan tanpa tanda kurung.
- **Di dalam teks terjemahan dan terjemahan glosarium**, nama mengikuti [`arabic_translation.md`](./arabic_translation.md) (misalnya, "Muhammad" dan "Ibrahim"). Dalam transliterasi teks Arab, nama mengikuti dokumen ini (_Muḥammad_, _Ibrāhīm_).

## 7. Bentuk sederhana (opsional)

Bentuk lengkap adalah **bawaan (default)** dan rujukan. Bentuk sederhana dapat ditawarkan sebagai cara alternatif untuk menampilkannya. Bentuk ini diturunkan dari bentuk lengkap dan tidak dikelola sebagai transliterasi yang terpisah.

| Bentuk lengkap | Bentuk sederhana                                   |
| -------------- | -------------------------------------------------- |
| ṣ, ḍ, ṭ, ẓ     | s, d, t, z                                         |
| ḥ              | h                                                  |
| ʿ, ʾ           | '                                                  |
| ā, ī, ū        | aa, ii, uu (lihat hal yang masih perlu diputuskan) |

**Peringatan:** bentuk sederhana menghilangkan perbedaan antarhuruf. Misalnya, ص dan س keduanya menjadi `s`, ح dan ه keduanya menjadi `h`, dan ع dan ء keduanya menjadi `'`.

## 8. Tinjauan (Review)

1. Periksa teks Arab berharakat dengan rujukan untuk koleksi aslinya.
2. Transliterasikan menggunakan tabel dan aturan di atas.
3. Mintalah seseorang yang memahami bahasa Arab untuk meninjaunya.
4. Perlakukan transliterasi sebagai **draf** hingga tinjauan tersebut selesai, dan jangan menyajikannya sebagai hasil yang sudah ditinjau.

Untuk mengusulkan perubahan pada aturan atau transliterasi, lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 9. Contoh: Salawat Ibrahimiyyah (Sahih Muslim 405)

Contoh ini adalah **kutipan**: hanya pada bagian lafaz salawat. Dalam [sumbernya](https://sunnah.com/muslim:405), hadis tersebut dilanjutkan dengan pernyataan singkat tentang salam, yang tidak disertakan di sini. Koma dalam teks Latin ditambahkan untuk kemudahan membaca.

**Bahasa Arab**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Latin (bentuk lengkap)**

> Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un)

**Latin (bentuk sederhana)**

> Allaahumma salli 'alaa Muhammad(in) wa 'alaa aali Muhammad(in), kamaa sallayta 'alaa aali Ibraahiim(a), wa baarik 'alaa Muhammad(in) wa 'alaa aali Muhammad(in), kamaa baarakta 'alaa aali Ibraahiim(a) fii l-'aalamiin(a), innaka hamiid(un) majiid(un)

## 10. Hal yang masih perlu diputuskan

- **Berhenti pada tanwin fathah.** `(an)` menyiratkan bahwa seluruh akhiran dihilangkan, tetapi umumnya dibaca sebagai _ā_ ketika berhenti. Notasi terpisah mungkin diperlukan.
- **Ta marbuta dengan akhiran kasus.** `a(t)` tidak menunjukkan vokal atau tanwin yang mengikuti _t_ ketika disambung. Apakah harus ditulis, misalnya sebagai `a(tu)` atau `a(tin)`, belum diputuskan.
- **Vokal pendek akhir lainnya.** Hanya tanwin dan akhiran i'rab yang dimasukkan ke dalam tanda kurung. Vokal akhir yang bukan i'rab, seperti pada _innaka_ dan _bārakta_, juga biasanya dihilangkan ketika berhenti. Apakah harus menandainya belum diputuskan.
- **Kombinasi dua huruf.** `th`, `kh`, `dh`, `sh`, dan `gh` dapat disalahartikan sebagai dua konsonan terpisah yang berurutan (misalnya, ت diikuti oleh ه). Apakah akan menambahkan pemisah belum diputuskan.
- **Kasus yang belum tercakup:** artikel definit dan kata _Allah_ setelah kata lain atau preposisi yang melekat, hamzah washal di awal ucapan, dan alif khanjariyah (dagger alif).
- **Vokal panjang dalam bentuk sederhana.** `aa, ii, uu` mempertahankan panjang vokal; `a, i, u` menghilangkannya. Contoh menggunakan `aa, ii, uu`.
- **Pemeriksa bahasa Arab.** Belum ditunjuk.
- **Lingkup untuk teks Al-Qur'an.** Apakah aturan ini berlaku untuk transliterasi Al-Qur'an belum diputuskan.
