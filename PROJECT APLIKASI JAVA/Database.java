import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String SQL_FILE = "produk_db.sql";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/produk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static String USER = "root";
    private static String PASSWORD = getPasswordFromConfig();
    private static String currentUsername = null;

    private static String getPasswordFromConfig() {
        String pass = System.getenv("MYSQL_PASSWORD");
        if (pass == null) {
            pass = "Alfi_syahrin54789";
        }
        return pass;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC MySQL tidak ditemukan. Pastikan lib/mysql-connector-j-9.6.0.jar ada di classpath.", e);
        }
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS produk_db");
            st.executeUpdate("USE produk_db");
            executeSqlFile(st, SQL_FILE);

            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(100) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL"
                    + ")");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(100) NOT NULL, "
                    + "description VARCHAR(255) NOT NULL, "
                    + "type VARCHAR(20) NOT NULL, "
                    + "amount BIGINT NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");
            ensureCreatedAtColumn(conn);

            if (!userExists(conn, "admin")) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
                    ps.setString(1, "admin");
                    ps.setString(2, "123");
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal inisialisasi database: " + e.getMessage(), e);
        }
    }

    private static void executeSqlFile(Statement st, String sqlFile) {
        Path path = Path.of(sqlFile);
        if (Files.notExists(path)) {
            return;
        }

        try {
            String sql = Files.readString(path);
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (trimmed.isEmpty() || trimmed.toUpperCase().startsWith("SELECT")) {
                    continue;
                }
                st.execute(trimmed);
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file SQL: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengeksekusi file SQL: " + e.getMessage(), e);
        }
    }

    private static boolean userExists(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void ensureCreatedAtColumn(Connection conn) {
        try {
            ResultSet rs = conn.getMetaData().getColumns(null, null, "transactions", "created_at");
            if (!rs.next()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "ALTER TABLE transactions ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP")) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memastikan kolom created_at: " + e.getMessage());
        }
    }

    public static java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, username FROM users ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                users.add(new User(id, username));
            }
        } catch (SQLException e) {
            System.err.println("Error membaca daftar pengguna: " + e.getMessage());
        }
        return users;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static boolean validateUser(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String actualPassword = rs.getString("password");
                    return actualPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validasi user: " + e.getMessage());
        }
        return false;
    }

    public static boolean registerUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        try (Connection conn = getConnection()) {
            if (userExists(conn, username)) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)") ) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error registrasi user: " + e.getMessage());
            return false;
        }
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static boolean addTransaction(String description, String type, long amount) {
        if (currentUsername == null) {
            System.err.println("Error: User tidak login");
            return false;
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions (username, description, type, amount) VALUES (?, ?, ?, ?)") ) {
            ps.setString(1, currentUsername);
            ps.setString(2, description);
            ps.setString(3, type);
            ps.setLong(4, amount);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error menambahkan transaksi: " + e.getMessage());
            return false;
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        if (currentUsername == null) {
            return transactions;
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT description, type, amount, created_at FROM transactions WHERE username = ? ORDER BY created_at ASC")) {
            ps.setString(1, currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String description = rs.getString("description");
                    String type = rs.getString("type");
                    long amount = rs.getLong("amount");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    transactions.add(new Transaction(description, type, amount, createdAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error membaca transaksi: " + e.getMessage());
        }
        return transactions;
    }

    public static List<Transaction> getTransactionsBetween(Timestamp start, Timestamp end) {
        List<Transaction> transactions = new ArrayList<>();
        if (currentUsername == null) {
            return transactions;
        }
        String sql = "SELECT description, type, amount, created_at FROM transactions WHERE username = ? AND created_at >= ? AND created_at < ? ORDER BY created_at ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUsername);
            ps.setTimestamp(2, start);
            ps.setTimestamp(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String description = rs.getString("description");
                    String type = rs.getString("type");
                    long amount = rs.getLong("amount");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    transactions.add(new Transaction(description, type, amount, createdAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error membaca transaksi antara tanggal: " + e.getMessage());
        }
        return transactions;
    }

    public static Totals getTotals() {
        Totals totals = new Totals(0, 0, 0);
        if (currentUsername == null) {
            return totals;
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT "
                             + "COALESCE(SUM(CASE WHEN type = 'Pemasukan' THEN amount ELSE 0 END), 0) AS totalPemasukan, "
                             + "COALESCE(SUM(CASE WHEN type = 'Pengeluaran' THEN amount ELSE 0 END), 0) AS totalPengeluaran "
                             + "FROM transactions WHERE username = ?")) {
            ps.setString(1, currentUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long pemasukan = rs.getLong("totalPemasukan");
                    long pengeluaran = rs.getLong("totalPengeluaran");
                    long totalSaldo = pemasukan - pengeluaran;
                    totals = new Totals(totalSaldo, pemasukan, pengeluaran);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung total: " + e.getMessage());
        }
        return totals;
    }

    public static boolean resetTransactions() {
        if (currentUsername == null) {
            return false;
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE username = ?")) {
            ps.setString(1, currentUsername);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error mereset transaksi: " + e.getMessage());
            return false;
        }
    }

    public static class Transaction {
        public final String description;
        public final String type;
        public final long amount;
        public final Timestamp createdAt;

        public Transaction(String description, String type, long amount, Timestamp createdAt) {
            this.description = description;
            this.type = type;
            this.amount = amount;
            this.createdAt = createdAt;
        }
    }

    public static class User {
        public final int id;
        public final String username;

        public User(int id, String username) {
            this.id = id;
            this.username = username;
        }
    }

    public static class Totals {
        public final long totalSaldo;
        public final long totalPemasukan;
        public final long totalPengeluaran;

        public Totals(long totalSaldo, long totalPemasukan, long totalPengeluaran) {
            this.totalSaldo = totalSaldo;
            this.totalPemasukan = totalPemasukan;
            this.totalPengeluaran = totalPengeluaran;
        }
    }
}
