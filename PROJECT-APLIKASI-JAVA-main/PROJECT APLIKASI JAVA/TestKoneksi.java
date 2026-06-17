import java.sql.Connection;
import java.sql.DriverManager;

public class TestKoneksi {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        // Konsisten dengan Database.java: baca dari env var, fallback kosong.
        String pass = System.getenv().getOrDefault("MYSQL_PASSWORD", "");

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Mantap! Koneksi MySQL Berhasil.");
            conn.close();
        } catch (Exception e) {
            System.out.println("Waduh, Gagal: " + e.getMessage());
            System.out.println("\nKonfigurasi MySQL:");
            System.out.println("- User: " + user);
            System.out.println("- Password: " + (pass.isEmpty() ? "(kosong)" : "(diset via MYSQL_PASSWORD)"));
            System.out.println("\nJika MySQL root Anda punya password, jalankan:");
            System.out.println("  set MYSQL_PASSWORD=password_anda");
            System.out.println("sebelum menjalankan TestKoneksi atau aplikasi.");
            System.out.println("\nLihat SETUP_DATABASE.txt untuk detail.");
        }
    }
}