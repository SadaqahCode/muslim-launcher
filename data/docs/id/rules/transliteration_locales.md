# Profil Locale Transliterasi Arab

> **Tentang dokumen ini**
>
> - Berkas ini adalah **pelengkap** dari [`arabic_transliteration.md`](./arabic_transliteration.md). Aturan umum (transliterasi default, urutan pemakaian locale dan default, serta langkah penurunan locale) ada di sana, terutama bagian 7. Jika berkas ini bertentangan dengan dokumen itu, ikuti dokumen itu atau ajukan perubahan terhadap salah satunya.
> - Ini **bukan standar yang diakui** dan **bukan aturan universal**. Profil di sini hanya berlaku di repositori ini dan tidak mengklaim bahwa ejaan lain itu salah.
> - Dokumen ini menyatakan **posisi kami sebagai pengembang**, bukan otoritas keagamaan, dan bisa saja salah atau berubah. Lihat [`CONTRIBUTING.md`](../../CONTRIBUTING.md).

## 1. Cara kerja

- Transliterasi default berlaku untuk semua pembaca dan menjadi sumber kebenaran. Profil locale hanya menambahkan **tampilan yang disesuaikan dengan ejaan satu bahasa**.
- Bahasa yang **tidak memiliki profil** di berkas ini tidak memiliki transliterasi locale. Pembaca bahasa itu mendapat transliterasi default (lihat bagian 7.2 di `arabic_transliteration.md`). Ini bukan kekurangan: transliterasi default memang dirancang untuk pembaca dari berbagai latar belakang.
- Profil hanya berlaku untuk locale **beraksara Latin**. Transliterasi ke aksara lain ditulis langsung oleh penutur asli (bagian 7.4 di `arabic_transliteration.md`).
- Setiap profil terdiri dari **tabel pemetaan karakter** dan (opsional) **daftar pengecualian kata**. Karakter yang tidak tercantum dibiarkan seperti di transliterasi default.
- Profil tidak boleh bertentangan dengan aturan umum. Tanda kurung `(x)`, artikel definit, dan penyelesaian lainnya sudah ditangani oleh langkah 1 sampai 3 di bagian 7.3 dokumen utama. Profil hanya mengatur langkah 3 (pengecualian kata) dan langkah 4 (pemetaan karakter).

## 2. Bahasa Indonesia (`id`)

**Pemetaan karakter**

| Default    | `id`       | Catatan                          |
| ---------- | ---------- | -------------------------------- |
| dh         | dz         |                                  |
| th         | ts         |                                  |
| sh         | sy         |                                  |
| kh         | kh         | Tetap                            |
| gh         | gh         | Tetap                            |
| ṣ, ḍ, ṭ, ẓ | s, d, t, z | Huruf emfatis tidak dibedakan    |
| ḥ          | h          |                                  |
| ʿ, ʾ       | '          | Keduanya menjadi tanda yang sama |
| ā, ī, ū    | aa, ii, uu | Panjang vokal dipertahankan      |

**Daftar pengecualian kata**

| Default | `id` | Alasan                            |
| ------- | ---- | --------------------------------- |
| anā     | ana  | Alif hanya dibaca ketika berhenti |

**Peringatan:** pemetaan ini menghilangkan perbedaan antarhuruf (ص dan س sama-sama `s`, ح dan ه sama-sama `h`, ع dan ء sama-sama `'`). Selain itu `ts`, `dz`, dan `sy` bisa disalahartikan sebagai dua huruf terpisah (misalnya ت diikuti س). Gunakan teks Arab atau transliterasi default untuk memeriksa huruf aslinya.

**Contoh 1: Salawat Ibrahimiyyah (Sahih Muslim 405)**

| Lapisan               | Teks                                                                                                                                                                                                                             |
| --------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Transliterasi default | Allāhumma ṣalli ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā ṣallayta ʿalā āli Ibrāhīm(a), wa bārik ʿalā Muḥammad(in) wa ʿalā āli Muḥammad(in), kamā bārakta ʿalā āli Ibrāhīm(a) fī l-ʿālamīn(a), innaka ḥamīd(un) majīd(un) |
| Locale `id`           | Allaahumma salli 'alaa Muhammadin wa 'alaa aali Muhammad, kamaa sallayta 'alaa aali Ibraahiim, wa baarik 'alaa Muhammadin wa 'alaa aali Muhammad, kamaa baarakta 'alaa aali Ibraahiima fii l-'aalamiin, innaka hamiidun majiid   |

Perhatikan bahwa tanda kurung sudah diselesaikan: yang berada sebelum koma atau akhir teks dibuang (`Muḥammad(in),` menjadi _Muhammad,_), dan yang lain dibuka (`Muḥammad(in) wa` menjadi _Muhammadin wa_). Idgham tanwin pada _ḥamīdun majīd_ belum diterapkan (lihat hal yang masih perlu diputuskan di dokumen utama).

**Contoh 2: artikel definit dan huruf syamsiyah**

| Lapisan               | Teks                                  |
| --------------------- | ------------------------------------- |
| Transliterasi default | `lā yaghfir(u) l-dhunūb(a) illā anta` |
| Locale `id`           | `laa yaghfirudz-dzunuuba illaa anta`  |

## 3. Menambahkan locale baru

1. Tentukan apakah aksaranya Latin (diturunkan oleh kode, bagian 7.3 dokumen utama) atau bukan (ditulis penutur asli, bagian 7.4 dokumen utama). Profil hanya dibuat untuk aksara Latin.
2. Tambahkan satu bagian bahasa di berkas ini, dengan tabel pemetaan karakter dan daftar pengecualian kata.
3. Tentukan ejaan untuk kombinasi yang berpotensi ambigu dan tuliskan peringatannya.
4. Uji konversi dengan setidaknya satu contoh dari bagian 10 dokumen utama dan satu contoh huruf syamsiyah.
5. Mintalah hasilnya diperiksa oleh penutur asli dan pemeriksa bahasa Arab (bagian 9 dokumen utama). Perlakukan hasilnya sebagai **draf** hingga tinjauan selesai.

## 4. Hal yang masih perlu diputuskan

- **Pemisah untuk kombinasi dua huruf.** `dz`, `ts`, dan `sy` pada profil `id` (serta `dh`, `th`, `sh`, `kh`, `gh` pada transliterasi default) bisa terbaca sebagai dua konsonan terpisah. Apakah akan menambahkan pemisah belum diputuskan.
- **Penyimpanan daftar pengecualian kata.** Apakah daftar ini disimpan di kode, di berkas profil, atau di tempat lain belum diputuskan. Saat ini tabelnya hanya tertulis di berkas ini.
- **Pemeriksa penutur asli.** Belum ditunjuk untuk bahasa Indonesia maupun bahasa lain.
