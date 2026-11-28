# Aturan Transliterasi Arab (Arab → Latin)

> **Tentang dokumen ini**
>
> - Ini adalah **rujukan utama** untuk mentransliterasikan teks Arab di repositori ini. Jika ada pekerjaan di repositori ini yang bertentangan dengannya, ikuti dokumen ini atau ajukan perubahan terhadapnya.
> - Ini **bukan standar yang diakui** dan **bukan aturan universal**. Aturan ini tidak berlaku di luar repositori ini, dan tidak mengklaim bahwa sistem transliterasi lain itu salah.
> - Dokumen ini menyatakan **posisi kami sebagai pengembang**: pilihan yang kami buat agar transliterasi di sini tetap konsisten dan dapat diperiksa. Dokumen ini tidak memiliki otoritas keagamaan.
> - Dokumen ini bisa saja salah dan dapat berubah. Jika Anda menemukan transliterasi yang tidak mengikuti aturan ini, atau aturan yang perlu diperbaiki, mohon bantu kami. Lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

Aturan-aturan ini mendefinisikan **dua lapisan** transliterasi:

1. **Transliterasi default**: satu sistem Latin untuk pembaca dari berbagai latar belakang bahasa (misalnya, di Indonesia, Malaysia, dunia Arab, India, Eropa, Afrika, dan Amerika Serikat). Isinya sama untuk semua bahasa dan menjadi sumber kebenaran (bagian 1 sampai 6).
2. **Transliterasi locale**: bentuk baca yang disesuaikan dengan ejaan satu bahasa pembaca. Isinya diturunkan dari transliterasi default, dan jika tidak tersedia, pembaca mendapat transliterasi default (bagian 7; profil per bahasa ada di [`transliteration_locales.md`](./transliteration_locales.md)).

Penerjemahan didefinisikan secara terpisah dalam [`arabic_translation.md`](./arabic_translation.md).

## 1. Prinsip

1. **Sumber tunggal: teks Arab berharakat** (dengan harakat), diperiksa berdasarkan rujukan yang dapat dipercaya ke koleksi aslinya (misalnya, sunnah.com). Catat nama koleksi dan nomor hadisnya.
2. **Transliterasi default ditulis langsung dari bahasa Arab**, dengan menerapkan aturan-aturan ini. Jangan menyalin atau mengadaptasi transliterasi lain, dan jangan menulis berdasarkan kebiasaan atau cara pengejaan kata yang biasa digunakan. **Transliterasi locale tidak ditulis dari kebiasaan ejaan, tetapi diturunkan dari transliterasi default** dengan cara yang didefinisikan di bagian 7.
3. **Satu konsonan Arab = satu representasi Latin yang tetap**, baik berupa simbol tunggal maupun kombinasi dua huruf (seperti `th` atau `sh`). Jangan menukar representasi. Prinsip ini berlaku untuk transliterasi default. Setiap profil locale memiliki pemetaannya sendiri (lihat [`transliteration_locales.md`](./transliteration_locales.md)).
4. **Transliterasi default menulis huruf aslinya, bukan bunyi yang dihasilkan.** Aturan tajwid seperti idgham, ikhfa', iqlab, dan lainnya tidak dicerminkan dalam teks Latin default. Transliterasi locale boleh menyelesaikan sebagian bunyi tersebut, hanya sebatas yang didefinisikan di bagian 7.
5. **Tanda kurung `(x)` menandai akhiran** yang diucapkan ketika disambung dengan kata berikutnya (washal) dan dihilangkan ketika berhenti (waqaf) (lihat bagian 5). Tanda kurung hanya ada di transliterasi default. Transliterasi locale sudah menyelesaikannya.
6. **Latin hanyalah jembatan.** Latin tidak menggantikan teks Arab berharakat atau pembelajaran pengucapan dengan mendengarkan.
7. **Gunakan Unicode NFC** untuk semua teks Latin.
8. **Locale diutamakan, default adalah cadangan.** Jika transliterasi locale untuk bahasa pembaca tersedia, itu yang ditampilkan. Jika tidak, transliterasi default yang ditampilkan (lihat bagian 7).

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

Aturan di tabel ini berlaku untuk transliterasi default. Transliterasi locale menurunkannya sesuai bagian 7.

## 5. Aturan tanda kurung `(x)`

- **Disambung dengan kata berikutnya (washal):** baca apa yang ada di dalam tanda kurung.  
  `Muḥammad(in) wa` → _Muḥammadin wa_
- **Berhenti (waqaf):** hilangkan apa yang ada di dalam tanda kurung.  
  `Muḥammad(in)` → _Muḥammad_
- **Pengecualian yang diketahui:** ketika berhenti pada _tanwin fathah_ (`an`) pada kata yang tidak diakhiri dengan ta marbuta, akhiran tersebut umumnya dibaca sebagai _ā_ panjang, bukan dihilangkan. Cara menandainya belum diputuskan (lihat hal yang masih perlu diputuskan).
- Tanda kurung hanya untuk bunyi yang hilang atau berubah ketika berhenti. Bunyi yang selalu dibaca tidak dimasukkan ke dalam tanda kurung.
- Pembaca transliterasi default menerapkan aturan idgham dan tajwid lainnya sendiri. Misalnya, sambungan `ḥamīd(un) majīd(un)` dibaca _ḥamīdum majīd_ (idgham dengan ghunnah). Teks Latin default tetap ditulis `ḥamīd(un) majīd(un)`.

**Tanda kurung dalam terjemahan** memiliki fungsi yang berbeda: tanda ini menandai kata bantu yang tidak ada dalam bahasa Arab (lihat [`arabic_translation.md`](./arabic_translation.md)). Di mana pun keduanya ditampilkan kepada pembaca, jelaskan perbedaannya. Transliterasi locale tidak memuat tanda kurung `(x)` sama sekali, sehingga perbedaan itu hanya perlu dijelaskan ketika transliterasi default yang ditampilkan.

## 6. Nama

Aturan ini berlaku untuk nama yang ditulis dalam transliterasi, seperti nama perawi dalam catatan konteks.

- Gunakan huruf kapital untuk nama. Artikel definit tetap huruf kecil dan tidak dilebur: _Abū Masʿūd al-Anṣārī_.
- Nama ditulis tanpa akhiran kasus dan tanpa tanda kurung.
- **Di dalam teks terjemahan dan terjemahan glosarium**, nama mengikuti [`arabic_translation.md`](./arabic_translation.md) (misalnya, "Muhammad" dan "Ibrahim"). Dalam transliterasi teks Arab, nama mengikuti dokumen ini (_Muḥammad_, _Ibrāhīm_).
- Dalam transliterasi locale, nama mengikuti pemetaan karakter profil locale dan tetap berhuruf kapital.

## 7. Lapisan default dan locale

### 7.1 Definisi

| Lapisan                   | Isi                                                                                   | Siapa yang menulis                                                                 |
| ------------------------- | ------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| **Transliterasi default** | Satu teks per zikir atau hadis, mengikuti bagian 1 sampai 6. Sama untuk semua bahasa. | Ditulis langsung dari teks Arab, lalu ditinjau.                                    |
| **Transliterasi locale**  | Satu teks per bahasa pembaca, boleh kosong. Tidak memuat tanda kurung `(x)`.          | Diturunkan dari transliterasi default (7.3). Pengecualian: aksara non-Latin (7.4). |

Transliterasi default adalah **sumber kebenaran**. Transliterasi locale hanyalah tampilan yang bisa dibuat ulang dari default kapan saja.

### 7.2 Urutan pemakaian: locale diutamakan, default sebagai cadangan

Ketika aplikasi atau API menampilkan transliterasi untuk seorang pembaca:

1. **Jika transliterasi locale untuk bahasa pembaca tersedia, tampilkan itu.** Locale berada di atas default.
2. **Jika tidak tersedia, tampilkan transliterasi default.**

Ketentuan tambahan:

- **"Tidak tersedia"** berarti nilainya `null`, key-nya tidak ada, atau stringnya kosong atau hanya berisi spasi.
- **Satu lapisan utuh, tidak dicampur.** Seluruh teks diambil dari locale atau seluruhnya dari default. Jangan menggabungkan per kata atau per kalimat.
- **Fallback berlaku per bidang.** Terjemahan bisa tersedia sementara transliterasi locale kosong. Dalam kasus itu terjemahan tetap ditampilkan dan transliterasinya jatuh ke default.
- **Tidak ada fallback antar-locale.** Jika locale `ms` kosong, langsung ke default, bukan ke `id`. Ejaan setiap locale bersifat khusus dan tidak boleh dipinjam begitu saja.
- **Pencocokan memakai kode bahasa dasar** (`id`, `en`, `ms`). Varian wilayah (misalnya `en-GB`) mengikuti kode dasarnya.
- Aplikasi boleh menyediakan tombol untuk melihat transliterasi default secara eksplisit, misalnya untuk pembelajar yang ingin melihat huruf aslinya. Itu pilihan pembaca, bukan pengganti aturan di atas.

### 7.3 Cara menurunkan transliterasi locale (aksara Latin)

Transliterasi locale beraksara Latin dihasilkan dari transliterasi default oleh kode, **bukan ditulis tangan**. Jika transliterasi default berubah, transliterasi locale dibuat ulang. Jangan menyunting hasilnya secara langsung. Perbaikan dilakukan di transliterasi default, di pemetaan profil, atau di daftar pengecualian.

Langkah-langkah, **berurutan**:

1. **Selesaikan tanda kurung `(x)`.** Titik berhenti adalah koma, titik, atau akhir teks di transliterasi default.
   - Isi kurung yang tepat berada sebelum titik berhenti **dibuang** (waqaf).
   - Isi kurung di tempat lain **dibuka**, yaitu ditulis tanpa kurung (washal).
   - `Muḥammad(in) wa ʿalā āli Muḥammad(in),` → _Muḥammadin wa ʿalā āli Muḥammad,_
2. **Selesaikan artikel definit** ketika huruf berikutnya adalah huruf syamsiyah, yaitu `t`, `th`, `d`, `dh`, `r`, `z`, `s`, `sh`, `ṣ`, `ḍ`, `ṭ`, `ẓ`, `l`, `n`.
   - Setelah kata yang diakhiri vokal: `l-` dilebur. Tulis huruf syamsiyah dua kali dengan tanda hubung di antaranya, dan gabungkan dengan akhir kata sebelumnya.  
     `yaghfir(u) l-dhunūb(a)` → _yaghfirudh-dhunūba_
   - Di awal ujaran: `al-` menjadi `a` + huruf + `-` + huruf.  
     `al-shams` → _ash-shams_
   - Huruf qamariyah tidak berubah: `fī l-ʿālamīn(a)` tetap memakai `l-`.
3. **Terapkan daftar pengecualian kata** (profil locale boleh memilikinya). Contoh: alif pada _anā_ hanya dibaca ketika berhenti, sehingga sebuah profil boleh menuliskannya tanpa alif panjang ketika disambung.
4. **Petakan karakter** sesuai tabel profil locale (lihat [`transliteration_locales.md`](./transliteration_locales.md)).
5. **Normalisasi ke Unicode NFC.**

Hal-hal yang **tidak** diterapkan oleh langkah-langkah di atas (lihat hal yang masih perlu diputuskan):

- Hukum tajwid lain, seperti idgham tanwin (`ḥamīd(un) majīd(un)` yang dibaca _ḥamīdum majīd_).
- Pengecualian berhenti pada tanwin fathah (dibaca _ā_).
- Pemisahan atau penggabungan awalan seperti `fa`, `bi`, dan `wa`: awalan yang ditulis terpisah di transliterasi default tetap terpisah di transliterasi locale.

### 7.4 Locale beraksara non-Latin

Transliterasi ke aksara lain (misalnya Hindi, Thai, atau Urdu) **tidak diturunkan dari teks Latin** oleh kode. Transliterasi itu ditulis langsung dari teks Arab berharakat oleh penutur asli, lalu ditinjau oleh pemeriksa bahasa Arab. Selama belum ada, aturan 7.2 berlaku: pembaca mendapat transliterasi default.

### 7.5 Penayangan

- Aplikasi sebaiknya menandai bentuk mana yang sedang tampil (misalnya "ejaan Indonesia" atau "transliterasi baku"), supaya pembaca tahu apakah teks yang dilihatnya menuliskan huruf atau bunyi.
- **Peringatan:** transliterasi locale menghilangkan perbedaan antarhuruf. Misalnya, pada profil yang tidak membedakan huruf emfatis, ص dan س sama-sama menjadi `s`, ح dan ه sama-sama menjadi `h`, dan ع dan ء sama-sama menjadi `'`. Karena itu transliterasi locale tidak boleh dipakai untuk memeriksa huruf aslinya. Gunakan teks Arab atau transliterasi default untuk itu.

## 8. Profil locale

Profil per bahasa (tabel pemetaan karakter, daftar pengecualian kata, dan contoh) ada di berkas terpisah: **[`transliteration_locales.md`](./transliteration_locales.md)**. Baca berkas itu sebelum membuat atau memeriksa transliterasi locale.

Dokumen ini hanya mendefinisikan aturan umum (bagian 7). Bahasa yang belum memiliki profil di berkas tersebut tidak memiliki transliterasi locale, sehingga pembaca mendapat transliterasi default (bagian 7.2).

## 9. Tinjauan (Review)

1. Periksa teks Arab berharakat dengan rujukan untuk koleksi aslinya.
2. Transliterasikan menggunakan tabel dan aturan di atas (transliterasi default).
3. Mintalah seseorang yang memahami bahasa Arab untuk meninjaunya.
4. Perlakukan transliterasi sebagai **draf** hingga tinjauan tersebut selesai, dan jangan menyajikannya sebagai hasil yang sudah ditinjau.
5. Transliterasi locale beraksara Latin **mewarisi status** transliterasi default yang menjadi sumbernya. Jika default masih draf, locale juga draf. Jika default berubah, buat ulang locale.

Untuk mengusulkan perubahan pada aturan atau transliterasi, lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 10. Contoh: Salawat Ibrahimiyyah (Sahih Muslim 405)

Contoh ini adalah **kutipan**: hanya pada bagian lafaz salawat. Dalam [sumbernya](https://sunnah.com/muslim:405), hadis tersebut dilanjutkan dengan pernyataan singkat tentang salam, yang tidak disertakan di sini. Koma dalam teks Latin ditambahkan untuk kemudahan membaca, dan koma itu juga menjadi titik berhenti pada langkah 1 bagian 7.3. Contoh transliterasi locale ada di [`transliteration_locales.md`](./transliteration_locales.md).

**Bahasa Arab**

اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى آلِ إِبْرَاهِيمَ وَبَارِكْ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَى آلِ إِبْرَاهِيمَ فِي الْعَالَمِينَ إِنَّكَ حَمِيدٌ مَجِيدٌ

**Transliterasi default**

> Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un)

## 11. Hal yang masih perlu diputuskan

- **Berhenti pada tanwin fathah.** `(an)` menyiratkan bahwa seluruh akhiran dihilangkan, tetapi umumnya dibaca sebagai _ā_ ketika berhenti. Notasi terpisah mungkin diperlukan.
- **Ta marbuta dengan akhiran kasus.** `a(t)` tidak menunjukkan vokal atau tanwin yang mengikuti _t_ ketika disambung. Apakah harus ditulis, misalnya sebagai `a(tu)` atau `a(tin)`, belum diputuskan.
- **Vokal pendek akhir lainnya.** Hanya tanwin dan akhiran i'rab yang dimasukkan ke dalam tanda kurung. Vokal akhir yang bukan i'rab, seperti pada _innaka_ dan _bārakta_, juga biasanya dihilangkan ketika berhenti. Apakah harus menandainya belum diputuskan.
- **Kombinasi dua huruf.** `th`, `kh`, `dh`, `sh`, dan `gh` dapat disalahartikan sebagai dua konsonan terpisah yang berurutan (misalnya, ت diikuti oleh ه). Apakah akan menambahkan pemisah belum diputuskan.
- **Kasus yang belum tercakup:** artikel definit dan kata _Allah_ setelah kata lain atau preposisi yang melekat, hamzah washal di awal ucapan, dan alif khanjariyah (dagger alif).
- **Titik berhenti pada transliterasi locale.** Saat ini koma, titik, dan akhir teks di transliterasi default dianggap titik berhenti. Padahal koma di teks default ditambahkan untuk kemudahan membaca. Apakah koma harus dikelola ketat sebagai bagian dari data belum diputuskan.
- **Hukum tajwid lain pada transliterasi locale**, misalnya idgham tanwin (_ḥamīdum majīd_), belum diterapkan.
- **Penulisan artikel yang dilebur.** Saat ini huruf syamsiyah digabung dengan kata sebelumnya (_yaghfirudh-dhunūba_), sedangkan huruf qamariyah tetap terpisah (`l-`). Apakah keduanya harus seragam belum diputuskan.
- **Awalan seperti `fa`, `bi`, dan `wa`.** Di transliterasi locale saat ini tetap terpisah. Apakah pembaca lebih terbantu dengan bentuk yang digabung (misalnya _faghfir_) belum diputuskan.
- **Penyimpanan daftar pengecualian kata** (misalnya _anā_ menjadi _ana_): di dalam kode, di berkas profil, atau di tempat lain.
- **Fallback untuk locale beraksara non-Latin.** Pembaca yang tidak bisa membaca aksara Latin mungkin tidak terbantu oleh transliterasi default. Apakah aplikasi boleh menampilkan teks Arab saja dalam kasus itu belum diputuskan.
- **Pemeriksa bahasa Arab.** Belum ditunjuk.
- **Lingkup untuk teks Al-Qur'an.** Apakah aturan ini berlaku untuk transliterasi Al-Qur'an belum diputuskan.
