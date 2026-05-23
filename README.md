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

### 3. Bangun launcher `.exe`

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
