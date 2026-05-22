import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class RiwayatFrame extends JFrame {
    private JTable tabelRiwayat;
    private DefaultTableModel tableModel;
    private JLabel lblTotalSaldo;
    private long totalSaldo = 0;

    public RiwayatFrame() {
        // Pengaturan Window
        setTitle("Riwayat Transaksi - Manajer Keuangan");
        setSize(700, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // --- Panel Judul (Atas) ---
        JPanel panelJudul = new JPanel();
        panelJudul.setBackground(new Color(41, 128, 185));
        JLabel lblJudul = new JLabel("RIWAYAT TRANSAKSI");
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        panelJudul.add(lblJudul);
        add(panelJudul, BorderLayout.NORTH);

        // --- Tabel Riwayat (Tengah) ---
        String[] kolom = { "Keterangan", "Jenis", "Jumlah" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tidak bisa diedit
            }
        };
        tabelRiwayat = new JTable(tableModel);
        tabelRiwayat.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelRiwayat.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelRiwayat.setRowHeight(25);
        add(new JScrollPane(tabelRiwayat), BorderLayout.CENTER);

        // --- Panel Status dan Tombol (Bawah) ---
        JPanel panelBawah = new JPanel(new BorderLayout(10, 10));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panelBawah.setBackground(Color.WHITE);

        // Label Total Saldo
        lblTotalSaldo = new JLabel("Total Saldo: Rp 0");
        lblTotalSaldo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalSaldo.setHorizontalAlignment(SwingConstants.LEFT);
        panelBawah.add(lblTotalSaldo, BorderLayout.WEST);

        // Panel Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelTombol.setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        panelTombol.add(btnRefresh);

        JButton btnExportMinggu = new JButton("Export Mingguan");
        btnExportMinggu.setBackground(new Color(39, 174, 96));
        btnExportMinggu.setForeground(Color.WHITE);
        btnExportMinggu.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportMinggu.setFocusPainted(false);
        btnExportMinggu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportByPeriod("minggu");
            }
        });
        panelTombol.add(btnExportMinggu);

        JButton btnExportBulan = new JButton("Export Bulanan");
        btnExportBulan.setBackground(new Color(39, 174, 96));
        btnExportBulan.setForeground(Color.WHITE);
        btnExportBulan.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportBulan.setFocusPainted(false);
        btnExportBulan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportByPeriod("bulan");
            }
        });
        panelTombol.add(btnExportBulan);

        JButton btnExportTahun = new JButton("Export Tahunan");
        btnExportTahun.setBackground(new Color(39, 174, 96));
        btnExportTahun.setForeground(Color.WHITE);
        btnExportTahun.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportTahun.setFocusPainted(false);
        btnExportTahun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportByPeriod("tahun");
            }
        });
        panelTombol.add(btnExportTahun);

        JButton btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(149, 165, 166));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFont(new Font("Arial", Font.BOLD, 12));
        btnKembali.setFocusPainted(false);
        btnKembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelTombol.add(btnKembali);

        panelBawah.add(panelTombol, BorderLayout.EAST);
        add(panelBawah, BorderLayout.SOUTH);

        // Load data setelah semua komponen UI dibuat
        loadData();

        setBackground(Color.WHITE);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        totalSaldo = 0;

        for (Database.Transaction tx : Database.getTransactions()) {
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

    private void refreshData() {
        loadData();
        JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSaldoLabel() {
        lblTotalSaldo.setText("Total Saldo: " + formatRupiah(totalSaldo));
    }

    private void exportByPeriod(String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;
        switch (period) {
            case "minggu":
                startDate = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = startDate.plusDays(7);
                break;
            case "bulan":
                startDate = now.withDayOfMonth(1);
                endDate = startDate.plusMonths(1);
                break;
            case "tahun":
                startDate = now.withDayOfYear(1);
                endDate = startDate.plusYears(1);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Periode tidak dikenali", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        Timestamp startTs = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTs = Timestamp.valueOf(endDate.atStartOfDay());

        List<Database.Transaction> list = Database.getTransactionsBetween(startTs, endTs);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada transaksi pada periode ini.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        String defaultName = "riwayat_" + period + ".csv";
        chooser.setSelectedFile(new File(defaultName));
        int retval = chooser.showSaveDialog(this);
        if (retval != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File target = chooser.getSelectedFile();
        try {
            exportTransactionsToCsv(list, target);
            JOptionPane.showMessageDialog(this, "Export berhasil: " + target.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTransactionsToCsv(List<Database.Transaction> list, File file) throws IOException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Tanggal,Keterangan,Jenis,Jumlah\n");
            for (Database.Transaction tx : list) {
                String tanggal = "";
                if (tx.createdAt != null) {
                    LocalDateTime ldt = tx.createdAt.toLocalDateTime();
                    tanggal = ldt.format(fmt);
                }
                String jumlah = (tx.type.equals("Pengeluaran") ? "- " : "+ ") + formatRupiah(tx.amount);
                // Escape double quotes in description
                String ket = tx.description.replace("\"", "\"\"");
                fw.write(String.format("%s,%s,%s,%s\n", tanggal, ket, tx.type, jumlah));
            }
        }
    }

    private String formatRupiah(long nominal) {
        return "Rp " + String.format("%,d", nominal).replace(",", ".");
    }

    public static void main(String[] args) {
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
            RiwayatFrame riwayat = new RiwayatFrame();
            riwayat.setVisible(true);
        });
    }
}