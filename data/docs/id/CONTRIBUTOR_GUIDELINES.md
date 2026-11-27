# Panduan Kontributor (CONTRIBUTOR_GUIDELINES.md)

Dokumen ini menjelaskan tata cara dan persyaratan untuk berkontribusi pada Muslim Launcher.

Panduan ini melengkapi [Developer Contract](./DEVELOPER_CONTRACT.md), yang menjadi dasar filosofi, nilai, prinsip desain, dan batasan proyek.

Setiap kontributor diharapkan memahami kedua dokumen tersebut sebelum mengajukan perubahan.

---

## 1. Memahami Developer Contract

Developer Contract merupakan dasar pengambilan keputusan dalam pengembangan Muslim Launcher. Dokumen tersebut bukan sekadar referensi, melainkan batasan yang harus dihormati dalam setiap kontribusi.

Sebelum mulai berkontribusi:

- Baca Developer Contract secara menyeluruh.
- Pahami identitas, tujuan, dan nilai inti proyek.
- Pahami prinsip desain dan batasan yang tidak dapat dilanggar.
- Pertimbangkan dampak perubahan terhadap filosofi dan arah proyek.

Jika sebuah usulan bertentangan dengan Developer Contract, usulan tersebut tidak dapat diterima hanya karena secara teknis memungkinkan.

Jika terdapat ketidakjelasan atau pertentangan dalam Developer Contract, diskusikan terlebih dahulu sebelum mengimplementasikan perubahan.

## 2. Issue dan Feature Request

Sebelum membuat issue baru, periksa issue yang sudah ada untuk menghindari laporan atau usulan duplikat.

### Bug Report

Laporan bug sebaiknya mencakup:

- Deskripsi masalah yang ditemukan.
- Langkah untuk mereproduksi masalah.
- Perilaku yang diharapkan.
- Perilaku aktual yang terjadi.
- Informasi lingkungan yang relevan, seperti versi aplikasi dan versi Android.
- Screenshot atau log jika membantu menjelaskan masalah.

Sampaikan masalah secara spesifik. Hindari laporan yang hanya berisi kesimpulan tanpa informasi yang dapat membantu proses investigasi.

### Feature Request

Usulan fitur harus menjelaskan:

- Masalah atau kebutuhan yang ingin diselesaikan.
- Alasan fitur tersebut relevan dengan tujuan Muslim Launcher.
- Gambaran perilaku atau solusi yang diusulkan.
- Dampak terhadap pengalaman pengguna, privasi, performa, dan kompleksitas aplikasi.

Tidak semua permintaan fitur harus diterapkan. Sebuah fitur dapat ditolak apabila tidak sesuai dengan tujuan, prinsip, atau batasan proyek.

Diskusi dan persetujuan terhadap usulan fitur sebaiknya dilakukan sebelum implementasi dimulai.

## 3. Persyaratan Pull Request

Pull Request (PR) digunakan untuk mengajukan perubahan kode atau dokumentasi agar dapat ditinjau sebelum digabungkan ke dalam proyek.

Setiap PR harus:

- Memiliki tujuan yang jelas dan ruang lingkup yang terukur.
- Menjelaskan masalah yang diselesaikan atau alasan perubahan dilakukan.
- Menjelaskan pendekatan implementasi.
- Menyertakan informasi pengujian yang telah dilakukan.
- Menyertakan screenshot atau rekaman jika perubahan memengaruhi tampilan atau interaksi pengguna.
- Tidak mencampurkan perubahan yang tidak berkaitan.
- Tidak menyertakan file hasil build, kredensial, secret, atau data pribadi.

### Prinsip Pengajuan PR

- Satu PR sebaiknya menangani satu tujuan utama.
- Hindari perubahan besar yang tidak diperlukan untuk menyelesaikan masalah.
- Jangan menyertakan refactoring yang tidak berkaitan tanpa alasan yang jelas.
- Jangan mengubah perilaku yang sudah ada tanpa menjelaskan alasannya.
- Jangan menganggap implementasi yang selesai secara lokal otomatis siap digabungkan.

Jika sebuah perubahan masih dalam tahap diskusi atau belum siap ditinjau, gunakan Draft Pull Request.

## 4. Konvensi Penulisan Kode

Kode harus mudah dibaca, dipahami, diuji, dan dipelihara.

Kontributor diharapkan:

- Mengikuti konvensi bahasa dan framework yang digunakan proyek.
- Mempertahankan konsistensi dengan struktur dan pola kode yang sudah ada.
- Menggunakan penamaan yang jelas dan bermakna.
- Menghindari duplikasi kode yang tidak diperlukan.
- Menghindari abstraksi dan generalisasi yang belum memiliki kebutuhan nyata.
- Menangani error dan kondisi tidak terduga secara tepat.
- Menghapus kode yang tidak digunakan, komentar usang, dan debug statement sebelum mengajukan PR.

Jangan memperkenalkan pola arsitektur baru hanya karena pola tersebut populer atau dianggap lebih canggih.

Setiap perubahan arsitektur harus memiliki alasan teknis yang jelas dan mempertimbangkan kompleksitas jangka panjang.

## 5. Konvensi Commit

Pesan commit harus menjelaskan perubahan yang dilakukan secara singkat dan spesifik.

Gunakan bentuk berikut:

`<type>: <description>`

Jenis commit yang disarankan:

| Type       | Penggunaan                                             |
| ---------- | ------------------------------------------------------ |
| `feat`     | Menambahkan kemampuan atau fitur                       |
| `fix`      | Memperbaiki bug                                        |
| `refactor` | Mengubah struktur kode tanpa mengubah perilaku         |
| `docs`     | Mengubah dokumentasi                                   |
| `style`    | Perubahan format atau gaya kode tanpa perubahan logika |
| `test`     | Menambahkan atau memperbaiki pengujian                 |
| `build`    | Perubahan konfigurasi build atau dependency            |
| `chore`    | Pemeliharaan proyek lainnya                            |

Contoh:

- `feat: add app search`
- `fix: resolve gesture detection issue`
- `refactor: simplify drawer state handling`
- `docs: update developer contract`
- `test: add launcher navigation tests`

Gunakan bahasa Inggris untuk pesan commit agar konsisten dengan riwayat pengembangan proyek.

Hindari pesan yang tidak menjelaskan perubahan, seperti `update`, `changes`, `fix bug`, atau `misc`.

## 6. Justifikasi Dependency

Setiap dependency baru menambah bagian yang harus dipelihara, diperbarui, dan dipercaya.

Karena itu, dependency tidak boleh ditambahkan tanpa kebutuhan dan justifikasi yang jelas.

Sebelum mengusulkan dependency baru, pertimbangkan:

- Apakah kebutuhan tersebut dapat dipenuhi menggunakan kemampuan bawaan Android atau dependency yang sudah tersedia?
- Apakah dependency benar-benar diperlukan?
- Bagaimana ukuran, performa, dan konsumsi sumber daya yang ditambahkannya?
- Apakah dependency memiliki izin, akses jaringan, atau pengumpulan data yang tidak diperlukan?
- Bagaimana status pemeliharaan, lisensi, dan keamanan dependency tersebut?
- Apakah dependency menambah kompleksitas yang sebanding dengan manfaatnya?

Setiap PR yang menambahkan dependency harus menjelaskan alasan pemilihan dan kebutuhan penggunaannya.

Dependency yang tidak memiliki justifikasi memadai dapat ditolak.

## 7. Persyaratan Pengujian

Setiap kontributor bertanggung jawab memastikan perubahan yang diajukan telah diuji secara memadai.

Pengujian harus disesuaikan dengan jenis dan dampak perubahan.

Sebelum mengajukan PR:

- Pastikan proyek dapat dikompilasi.
- Jalankan pengujian yang relevan.
- Periksa kembali perilaku yang terdampak perubahan.
- Pastikan tidak ada regresi pada fungsi yang sudah berjalan.
- Periksa log untuk error atau peringatan yang disebabkan oleh perubahan.
- Pastikan perubahan tidak menimbulkan masalah baru pada performa atau stabilitas.

Untuk perubahan yang memengaruhi interaksi launcher, lakukan pengujian pada perangkat atau emulator Android yang relevan.

Jika pengujian tertentu tidak dapat dilakukan, jelaskan keterbatasannya secara terbuka dalam PR.

Jangan menyatakan sebuah perubahan telah diuji apabila pengujian tersebut belum dilakukan.

## 8. Code Review dan Approval

Setiap PR harus melalui proses review sebelum digabungkan.

Review bertujuan untuk memastikan bahwa perubahan:

- Menyelesaikan masalah yang dimaksud.
- Sesuai dengan Developer Contract.
- Memenuhi standar kualitas dan konsistensi kode.
- Tidak menambahkan kompleksitas yang tidak diperlukan.
- Tidak menimbulkan masalah privasi, keamanan, performa, atau stabilitas.
- Telah diuji secara memadai.

Reviewer dapat meminta penjelasan, perubahan implementasi, atau pengujian tambahan.

Kontributor diharapkan menanggapi masukan review secara konstruktif dan memperbarui PR apabila diperlukan.

Persetujuan PR merupakan kewenangan maintainer proyek. Pengajuan PR maupun diskusi yang telah berlangsung tidak menjamin bahwa perubahan akan diterima.

Maintainer berhak menolak, meminta revisi, atau menutup PR yang tidak sesuai dengan kebutuhan dan arah proyek.

## 9. Mengusulkan Perubahan Developer Contract

Developer Contract merupakan dokumen prinsip dasar proyek. Perubahannya harus diperlakukan lebih serius daripada perubahan dokumentasi atau implementasi biasa.

Jika ingin mengusulkan perubahan terhadap Developer Contract:

1. Buat issue khusus yang menjelaskan bagian yang ingin diubah.
2. Kutip atau tunjukkan bagian yang terdampak.
3. Jelaskan alasan perubahan diperlukan.
4. Jelaskan dampaknya terhadap filosofi, nilai, prinsip desain, dan batasan proyek.
5. Sertakan usulan redaksi atau prinsip pengganti jika memungkinkan.
6. Tunggu diskusi dan keputusan maintainer sebelum mengajukan perubahan dokumen.

Perubahan Developer Contract tidak boleh disisipkan sebagai bagian kecil dari PR fitur atau refactoring yang tidak berkaitan.

Perubahan yang melemahkan prinsip inti proyek harus dipertimbangkan secara khusus dan tidak diterima hanya demi kemudahan implementasi.

Setiap perubahan yang disetujui harus tercermin secara konsisten dalam dokumentasi dan arah pengembangan berikutnya.

---

## Penutup

Berkontribusi pada Muslim Launcher bukan hanya tentang menambahkan kode atau menyelesaikan issue. Setiap perubahan turut menentukan arah dan karakter proyek.

Kami menghargai kontribusi yang menyelesaikan masalah nyata, menjaga kesederhanaan, menghormati pengguna, dan tetap berada dalam batasan yang telah ditetapkan.

**Kualitas kontribusi tidak diukur dari banyaknya kode yang ditambahkan, tetapi dari nilai dan tanggung jawab di balik perubahan tersebut.**
