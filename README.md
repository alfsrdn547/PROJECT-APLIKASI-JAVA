# Aplikasi Keuangan

Aplikasi desktop untuk pencatatan keuangan pribadi berbasis Java Swing dengan database MySQL.

## Fitur

- Login dan registrasi pengguna
- Pencatatan pemasukan dan pengeluaran
- Riwayat transaksi
- Dashboard dan export transaksi
- Inisialisasi database otomatis lewat `Database.java`

## Prasyarat

- Java JDK 17 atau lebih baru
- MySQL Server
- Connector MySQL JDBC (`lib/mysql-connector-j-9.6.0.jar`)

## Struktur Proyek

- `PROJECT APLIKASI JAVA/` - source code utama
- `lib/` - library eksternal
- `transactions.txt` - data transaksi lokal/backup
- `produk_db.sql` - skrip inisialisasi database

## Cara Menjalankan

### 1. Pastikan MySQL berjalan

Buka terminal dan pastikan koneksi ke MySQL berhasil:

```bash
mysql -u root
```

Jika MySQL meminta password, atur environment variable:

```powershell
$env:MYSQL_PASSWORD = "YOUR_PASSWORD"
```

### 2. Jalankan aplikasi

Gunakan batch script:

```bash
run.bat
```

Atau jalankan secara manual:

```bash
javac -cp ".;lib/mysql-connector-j-9.6.0.jar" *.java
java -cp ".;lib/mysql-connector-j-9.6.0.jar" LoginFrame
```

### 3. Bangun executable JAR

Gunakan skrip berikut untuk membuat file JAR yang bisa dijalankan dengan `java -jar`:

```powershell
powershell -ExecutionPolicy Bypass -File .\PROJECT APLIKASI JAVA\build-jar.ps1
```

Atau:

```cmd
cd "PROJECT APLIKASI JAVA"
build-jar.bat
```

JAR yang dihasilkan akan bernama `AplikasiKeuangan.jar` dan menggunakan `lib/mysql-connector-j-9.6.0.jar` dari folder `lib/`.

### 4. Bangun launcher `.exe`

Jalankan skrip build berikut untuk membuat paket aplikasi Windows yang bisa dijalankan dengan klik:

```powershell
powershell -ExecutionPolicy Bypass -File .\build-exe.ps1
```

Launcher akan dihasilkan di `build-exe/output/AplikasiKeuangan/AplikasiKeuangan.exe`.

## Kredensial Default

- Username: `admin`
- Password: `123`

> Password default untuk MySQL disetel melalui `MYSQL_PASSWORD` atau fallback ke nilai di `Database.java` jika variabel lingkungan belum diset.

## Database

Aplikasi akan mencoba membuat database `produk_db` dan tabel yang diperlukan saat dijalankan. File SQL awal:

- `produk_db.sql`

Jika ada kendala koneksi, cek `SETUP_DATABASE.txt` dan `Database.java`.

## Catatan

- Jika aplikasi gagal terhubung ke MySQL, pastikan `mysql-connector-j-9.6.0.jar` berada di folder `lib/`.
- Untuk testing koneksi manual, gunakan `TestKoneksi.java`.

# Aplikasi Keuangan

Aplikasi desktop Java Swing untuk pencatatan keuangan pribadi (pemasukan & pengeluaran) dengan penyimpanan data di MySQL. Dilengkapi login multi-user, dashboard ringkasan, riwayat transaksi, dan export data.

## Tentang Aplikasi

**Stack:** Java Swing (UI) + MySQL 8.0 (database) + MySQL Connector/J 9.6 (JDBC).

**Fitur utama:**

- Login & registrasi pengguna (password disimpan plain di database — untuk demo)
- Input transaksi: keterangan, jenis (Pemasukan/Pengeluaran), jumlah
- Riwayat transaksi dengan filter rentang tanggal
- Dashboard ringkasan: total saldo, total pemasukan, total pengeluaran, pie chart
- Export transaksi ke file teks
- Inisialisasi database otomatis saat aplikasi dijalankan pertama kali (membuat database `produk_db`, tabel `users`/`transactions`/`products`, dan user admin default)

**Akun default (di-seed otomatis):**

- Username: `admin`
- Password: `123`

**Batas versi:** tested di JDK 25, MySQL 8.0, Windows 11.

## Prasyarat

1. **Java JDK 17 atau lebih baru** (disertai JRE). Cek:
   ```bash
   java -version
   ```
2. **MySQL Server 8.0** yang sedang berjalan di `localhost:3306`. Cek:
   ```bash
   mysql -u root -p
   ```
   Masuk? MySQL siap dipakai.
3. **Library JDBC:** `lib/mysql-connector-j-9.6.0.jar` (sudah disertakan di project).
4. **User MySQL `root`** harus punya password yang cocok dengan yang tersimpan di `run.bat` (saat ini: `Alfi_syahrin54789`). Jika berbeda, edit dulu sebelum menjalankan — lihat bagian [Konfigurasi Password](#konfigurasi-password-mysql) di bawah.

## Langkah Menjalankan Aplikasi

### Cara 1 — Klik dua kali (paling gampang)

1. Buka folder `PROJECT APLIKASI JAVA/` di File Explorer.
2. Klik dua kali `run.bat`.
3. Tunggu sebentar — script akan compile ulang lalu menampilkan jendela login.
4. Login dengan `admin` / `123`.
5. Setelah login, Anda masuk ke dashboard. Gunakan menu **Menu** untuk navigasi.

### Cara 2 — Manual dari terminal

```cmd
cd "C:\Users\alfis\Downloads\PROJECT-APLIKASI-JAVA-V1\PROJECT-APLIKASI-JAVA-main\PROJECT APLIKASI JAVA"
run.bat
```

### Cara 3 — Kalau tidak ingin pakai script

```cmd
cd "C:\Users\alfis\Downloads\PROJECT-APLIKASI-JAVA-V1\PROJECT-APLIKASI-JAVA-main\PROJECT APLIKASI JAVA"
javac -cp "lib\mysql-connector-j-9.6.0.jar" *.java
java -cp ".;lib\mysql-connector-j-9.6.0.jar" LoginFrame
```

## Konfigurasi Password MySQL

Repo ini TIDAK menyertakan `run.bat` / `run-jar.bat` (di-gitignore). Gunakan template yang tersedia:

1. **Copy template ke file aktif:**
   ```cmd
   cd "PROJECT APLIKASI JAVA"
   copy run.bat.template run.bat
   copy run-jar.bat.template run-jar.bat
   ```
2. **Edit file hasil copy**, ganti `GANTI_DENGAN_PASSWORD_ANDA` dengan password MySQL root Anda.
3. **Jangan commit** `run.bat` / `run-jar.bat` — file asli ada di `.gitignore` untuk mencegah kebocoran password ke repository publik.

**Atau pakai environment variable** (lebih aman, tidak ada password di file):

```cmd
set MYSQL_PASSWORD=password_anda
run.bat
```

`Database.java` membaca `MYSQL_PASSWORD` lebih dulu sebelum fallback ke string kosong.

## Alur Penggunaan Setelah Login

1. **Input transaksi** — Pada aplikasi utama, isi Keterangan, Jumlah, pilih Jenis (Pemasukan/Pengeluaran), klik **Tambah Transaksi**.
2. **Lihat ringkasan** — Menu → **Dashboard** untuk melihat total saldo, pie chart, dan daftar transaksi.
3. **Lihat/filter riwayat** — Menu → **Riwayat Transaksi**. Bisa filter per rentang tanggal dan export ke file teks.
4. **Kelola user** — Menu → **Users** (hanya tersedia dari Dashboard) untuk melihat daftar user terdaftar.
5. **Logout / keluar** — Menu → **Logout**, atau tombol **Keluar** di halaman login.

## Struktur Proyek

```
PROJECT-APLIKASI-JAVA-V1/
├── PROJECT-APLIKASI-JAVA-main/
│   ├── README.md                          ← file ini
│   ├── transactions.txt                   ← data transaksi (backup lokal)
│   ├── .claude/                           ← folder tersembunyi (abaikan)
│   ├── build-exe.ps1                      ← skrip build launcher .exe
│   ├── build-exe/                         ← output build .exe
│   └── PROJECT APLIKASI JAVA/
│       ├── *.java                         ← source code (LoginFrame, Database, dll)
│       ├── lib/
│       │   └── mysql-connector-j-9.6.0.jar
│       ├── run.bat                        ← launcher utama (klik dua kali)
│       ├── run-jar.bat                    ← launcher untuk .jar
│       ├── build-jar.bat / build-jar.ps1  ← skrip build JAR
│       └── produk_db.sql                  ← skema tabel products
```

## Inisialisasi Database

Saat pertama kali aplikasi dijalankan, `Database.initDatabase()` akan:

1. Membuat database `produk_db` jika belum ada.
2. Membuat tabel `users`, `transactions`, dan mengeksekusi `produk_db.sql` (membuat tabel `products`).
3. Membuat user `admin` / `123` jika belum ada di tabel `users`.

Proses ini **idempoten** — aman dijalankan berulang kali.

**Reset total** (hapus database & buat ulang): login ke MySQL lalu jalankan:

```sql
DROP DATABASE produk_db;
```

Setelah itu, jalankan ulang `run.bat`.

## Build JAR (untuk distribusi)

```cmd
cd "PROJECT APLIKASI JAVA"
build-jar.bat
```

Output: `AplikasiKeuangan.jar`. Jalankan dengan klik dua kali `run-jar.bat` atau:

```cmd
java -jar AplikasiKeuangan.jar
```

## Build Launcher `.exe` (Windows)

```powershell
cd PROJECT-APLIKASI-JAVA-V1
powershell -ExecutionPolicy Bypass -File .\build-exe.ps1
```

Output: `build-exe/output/AplikasiKeuangan/AplikasiKeuangan.exe` — bisa dijalankan dengan klik dua kali tanpa perlu install Java (JRE dibundle di dalamnya).

## Troubleshooting Singkat

| Gejala | Penyebab & Solusi |
|---|---|
| "Access denied for user 'root'@'localhost'" | Password di `run.bat` tidak cocok. Edit `MYSQL_PASSWORD` di `run.bat`. |
| "Communications link failure" / "Connection refused" | MySQL belum jalan. Start: `net start MySQL80`. |
| Compile error / class bentrok | Hapus semua `*.class` di folder source, jalankan `run.bat` ulang. |
| "MySQL JDBC Driver tidak ditemukan" | File `lib/mysql-connector-j-9.6.0.jar` hilang. Restore dari backup. |
| Lupa password `admin` | Login ke MySQL, hapus user `admin`, lalu jalankan ulang `run.bat` (admin akan di-seed ulang). |
| Folder `.claude` di root mengganggu | Itu folder memori Claude Code, sudah disembunyikan (`Hidden + System` attribute). Aman diabaikan. |

## Testing Koneksi Manual

Untuk memastikan MySQL bisa dihubungi sebelum menjalankan aplikasi:

```cmd
cd "PROJECT APLIKASI JAVA"
javac -cp "lib\mysql-connector-j-9.6.0.jar" TestKoneksi.java
java -cp ".;lib\mysql-connector-j-9.6.0.jar" TestKoneksi
```

Atau dari MySQL client:

```cmd
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```
