import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AplikasiKeuangan extends JFrame {
    private JTextField txtKeterangan, txtJumlah;
    private JComboBox<String> cbJenis;
    private JTable tabelRiwayat;
    private DefaultTableModel tableModel;
    private JLabel lblTotalSaldo;
    private long totalSaldo = 0;
    private JButton btnTambah;

    public AplikasiKeuangan() {
        // Terapkan FlatLaf Modern Look and Feel jika tersedia secara global
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            // Abaikan jika library belum ter-import di classpath
        }

        // Pengaturan Dasar Window
        setTitle("Manajer Keuangan Pribadi");
        setSize(550, 680); // Ditambah sedikit tingginya agar space kotak input baru muat sempurna
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Agar bisa konfirmasi logout
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // --- Menu Bar ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Menu");
        JMenuItem menuDashboard = new JMenuItem("Dashboard");
        menuDashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDashboard();
            }
        });
        menuFile.add(menuDashboard);
        
        JMenuItem menuRiwayat = new JMenuItem("Riwayat Transaksi");
        menuRiwayat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRiwayat();
            }
        });
        menuFile.add(menuRiwayat);
        
        JMenuItem menuLogout = new JMenuItem("Logout");
        menuLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        menuFile.add(menuLogout);
        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // Menangani Close Button Window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                logout();
            }
        });

        // =================================================================
        // 1. COMPONENT HEADER (BAGIAN ATAS)
        // =================================================================
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(41, 128, 185)); // Biru Elegan Dashboard
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel lblHeaderTitle = new JLabel("💸 Input Transaksi Baru");
        lblHeaderTitle.setFont(new Font("Dialog", Font.BOLD, 20));
        lblHeaderTitle.setForeground(Color.WHITE);
        lblHeaderTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panelHeader.add(lblHeaderTitle);
        
        add(panelHeader, BorderLayout.NORTH);

        // =================================================================
        // 2. COMPONENT UTAMA (FORM & TABEL / CENTER)
        // =================================================================
        JPanel panelKontenUtama = new JPanel(new BorderLayout(0, 20)); // Spasi antar komponen dinaikkan ke 20
        panelKontenUtama.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Margin kanan-kiri-atas-bawah diperlonggar
        panelKontenUtama.setBackground(Color.WHITE);

        // --- Panel Form Input ---
        JPanel panelFormInput = new JPanel(new GridLayout(4, 2, 10, 18)); // Jarak vertikal antar baris form dinaikkan ke 18
        panelFormInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Detail Transaksi "));
        panelFormInput.setBackground(Color.WHITE);

        Font fontLabel = new Font("Dialog", Font.PLAIN, 14);
        Font fontInput = new Font("Dialog", Font.PLAIN, 14);

        // Baris 1: Keterangan
        JLabel lblKeterangan = new JLabel("📝 Keterangan:");
        lblKeterangan.setFont(fontLabel);
        panelFormInput.add(lblKeterangan);
        
        txtKeterangan = new JTextField();
        txtKeterangan.setFont(fontInput);
        // POLESAN: Mengatur tinggi kolom teks menjadi 32px agar terlihat tebal dan modern
        txtKeterangan.setPreferredSize(new Dimension(txtKeterangan.getPreferredSize().width, 32));
        panelFormInput.add(txtKeterangan);

        // Baris 2: Jumlah
        JLabel lblJumlah = new JLabel("💰 Jumlah (Rp):");
        lblJumlah.setFont(fontLabel);
        panelFormInput.add(lblJumlah);
        
        txtJumlah = new JTextField();
        txtJumlah.setFont(fontInput);
        // POLESAN: Mengatur tinggi kolom teks menjadi 32px
        txtJumlah.setPreferredSize(new Dimension(txtJumlah.getPreferredSize().width, 32));
        panelFormInput.add(txtJumlah);

        // Baris 3: Jenis
        JLabel lblJenis = new JLabel("🔄 Jenis:");
        lblJenis.setFont(fontLabel);
        panelFormInput.add(lblJenis);
        
        cbJenis = new JComboBox<>(new String[] { "Pemasukan", "Pengeluaran" });
        cbJenis.setFont(fontInput);
        // POLESAN: Mengatur tinggi JComboBox menjadi 32px agar serasi dengan textfield
        cbJenis.setPreferredSize(new Dimension(cbJenis.getPreferredSize().width, 32));
        panelFormInput.add(cbJenis);

        // Baris 4: Tombol Tambah Transaksi
        btnTambah = new JButton("➕ Tambah Transaksi");
        btnTambah.setBackground(new Color(41, 128, 185)); // Biru Kustom
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Dialog", Font.BOLD, 14));
        btnTambah.setFocusPainted(false);
        btnTambah.putClientProperty("JButton.buttonType", "roundRect"); // Desain Sudut Melengkung FlatLaf
        
        // POLESAN UTAMA: Memberikan padding di dalam tombol (Atas: 10px, Bawah: 10px) agar tombol lebih empuk/tebal
        btnTambah.setMargin(new Insets(10, 15, 10, 15));
        
        panelFormInput.add(new JLabel("")); // Spacer kosong kolom kiri
        panelFormInput.add(btnTambah);

        panelKontenUtama.add(panelFormInput, BorderLayout.NORTH);

        // --- Tabel Riwayat Tradisional ---
        String[] kolom = { "Keterangan", "Jenis", "Jumlah" };
        tableModel = new DefaultTableModel(kolom, 0);
        tabelRiwayat = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabelRiwayat);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Entri Terkini"));
        panelKontenUtama.add(scrollPane, BorderLayout.CENTER);

        add(panelKontenUtama, BorderLayout.CENTER);

        // =================================================================
        // 3. COMPONENT RINGKASAN SALDO (BAGIAN BAWAH)
        // =================================================================
        JPanel panelStatus = new JPanel(new BorderLayout());
        panelStatus.setBackground(Color.WHITE);
        panelStatus.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 25));

        lblTotalSaldo = new JLabel("Total Saldo: Rp 0");
        lblTotalSaldo.setFont(new Font("Dialog", Font.BOLD, 18));
        lblTotalSaldo.setHorizontalAlignment(SwingConstants.RIGHT);
        panelStatus.add(lblTotalSaldo, BorderLayout.CENTER);
        
        add(panelStatus, BorderLayout.SOUTH);

        // Load data dari database
        loadData();

        // --- Logika fungsional tombol ---
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahData();
            }
        });
    }

    private void tambahData() {
        try {
            String ket = txtKeterangan.getText();
            long jml = Long.parseLong(txtJumlah.getText());
            String jenis = (String) cbJenis.getSelectedItem();

            if (jenis.equals("Pengeluaran")) {
                totalSaldo -= jml;
                tableModel.addRow(new Object[] { ket, jenis, "- " + formatRupiah(jml) });
            } else {
                totalSaldo += jml;
                tableModel.addRow(new Object[] { ket, jenis, "+ " + formatRupiah(jml) });
            }

            if (!Database.addTransaction(ket, jenis, jml)) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi ke database.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            updateSaldoLabel();
            txtKeterangan.setText("");
            txtJumlah.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka yang valid untuk jumlah!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatRupiah(long nominal) {
        return "Rp " + String.format("%,d", nominal).replace(",", ".");
    }

    private void updateSaldoLabel() {
        lblTotalSaldo.setText("Total Saldo: " + formatRupiah(totalSaldo));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        totalSaldo = 0;
        List<Database.Transaction> transactions = Database.getTransactions();
        for (Database.Transaction tx : transactions) {
            if (tx.type.equals("Pengeluaran")) {
                totalSaldo -= tx.amount;
                tableModel.addRow(new Object[] { tx.description, tx.type, "- " + formatRupiah(tx.amount) });
            } else {
                totalSaldo += tx.amount;
                tableModel.addRow(new Object[] { tx.description, tx.type, "+ " + formatRupiah(tx.amount) });
            }
        }
        updateSaldoLabel();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }

    private void showDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
        });
    }

    private void showRiwayat() {
        SwingUtilities.invokeLater(() -> {
            RiwayatFrame riwayat = new RiwayatFrame();
            riwayat.setLocationRelativeTo(this);
            riwayat.setVisible(true);
        });
    }

    public static void main(String[] args) {
        try {
        // Aktifkan tampilan modern FlatLaf
        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
    } catch (Exception e) {
        e.printStackTrace();
    }
        try {
            Database.initDatabase();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null,
                    "Gagal menghubungkan database: " + ex.getMessage(),
                    "Koneksi Database",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}