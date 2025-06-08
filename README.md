# Aplikasi Manajemen Transaksi

Aplikasi ini adalah sistem manajemen transaksi berbasis Java yang menggunakan database SQL untuk menyimpan data. Aplikasi ini mencakup fitur login, manajemen transaksi, dan pelacakan riwayat transaksi.

## Fitur Utama
- Login dan logout pengguna
- Manajemen transaksi (penyimpanan dan pembaruan stok produk)
- Riwayat transaksi berdasarkan kasir atau semua transaksi

## Persyaratan
- **Java**: Versi 11 atau lebih baru
- **Database**: MySQL atau database SQL lainnya
- **IDE**: IntelliJ IDEA (disarankan) atau IDE lain yang mendukung Java
- **Maven**: Untuk manajemen dependensi

## Langkah-Langkah Penggunaan

### 1. Clone Repository
Clone repository ini ke komputer Anda menggunakan perintah berikut:
```bash
git clone https://github.com/januarsyah901/Aplikasi-Kasir.git
```

### 2. Buka Proyek di IntelliJ IDEA
1. Buka IntelliJ IDEA.
2. Pilih **Open** dan arahkan ke folder proyek yang telah di-clone.
3. Tunggu hingga IntelliJ selesai memuat proyek dan mengunduh dependensi Maven.

### 3. Konfigurasi Database
1. Buat database di MySQL (atau database lain yang Anda gunakan).
2. Import file SQL yang berisi struktur tabel dan data awal (jika ada).
3. Jalankan perintah di file schema.sql untuk membuat tabel yang diperlukan.
3. Perbarui konfigurasi koneksi database di file `src/util/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/kasir_db";
   private static final String USER = "root";
   private static final String PASSWORD = "";
   ```

### 4. Jalankan Aplikasi
1. Jalankan aplikasi dengan mengeksekusi file `Main.java` di IDE Anda.
2. Aplikasi akan berjalan di terminal atau konsol.

### 5. Panduan Pengoperasian
- **Login**: Masukkan username dan password yang valid untuk login.
- **Manajemen Transaksi**: Tambahkan transaksi baru melalui fitur yang tersedia.
- **Riwayat Transaksi**: Lihat riwayat transaksi berdasarkan kasir atau semua transaksi.

### 6. Logout
Gunakan fitur logout untuk keluar dari sesi pengguna saat ini.

## Struktur Proyek
- `src/controller`: Berisi logika aplikasi (AuthController, TransactionController, dll.).
- `src/model`: Berisi model data (User, Product, Transaction, dll.).
- `src/util`: Berisi utilitas seperti koneksi database.
- `src/view`: (Opsional) Berisi antarmuka pengguna jika ada.

## Catatan
- Pastikan database sudah berjalan sebelum menjalankan aplikasi.
- Gunakan akun admin untuk mengakses fitur tertentu seperti manajemen pengguna.


## Lisensi
Proyek ini dilisensikan di bawah [MIT License](LICENSE).