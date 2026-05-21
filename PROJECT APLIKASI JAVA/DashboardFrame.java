import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class DashboardFrame extends JFrame {
    private JLabel lblWelcome, lblTotalSaldo, lblTotalPemasukan, lblTotalPengeluaran;
    private JButton btnTambahTransaksi, btnLihatRiwayat, btnLogout;

    public DashboardFrame() {
        // Pengaturan Window
        setTitle("Dashboard - Manajer Keuangan");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // --- Panel Judul (Atas) ---
        JPanel panelJudul = new JPanel();
        panelJudul.setBackground(new Color(41, 128, 185));
        lblWelcome = new JLabel("Selamat Datang di Dashboard Keuangan");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        panelJudul.add(lblWelcome);
        add(panelJudul, BorderLayout.NORTH);

        // --- Panel Ringkasan (Tengah) ---
        JPanel panelRingkasan = new JPanel(new GridLayout(3, 1, 10, 20));
        panelRingkasan.setBorder(BorderFactory.createTitledBorder("Ringkasan Keuangan"));
        panelRingkasan.setBackground(Color.WHITE);

        lblTotalSaldo = new JLabel("Total Saldo: Rp 0");
        lblTotalSaldo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        panelRingkasan.add(lblTotalSaldo);

        lblTotalPemasukan = new JLabel("Total Pemasukan: Rp 0");
        lblTotalPemasukan.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTotalPemasukan.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalPemasukan.setForeground(new Color(46, 204, 113));
        panelRingkasan.add(lblTotalPemasukan);

        lblTotalPengeluaran = new JLabel("Total Pengeluaran: Rp 0");
        lblTotalPengeluaran.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTotalPengeluaran.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalPengeluaran.setForeground(new Color(192, 57, 43));
        panelRingkasan.add(lblTotalPengeluaran);

        add(panelRingkasan, BorderLayout.CENTER);

        // --- Panel Tombol (Bawah) ---
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelTombol.setBackground(Color.WHITE);

        btnTambahTransaksi = new JButton("Tambah Transaksi");
        btnTambahTransaksi.setBackground(new Color(41, 128, 185));
        btnTambahTransaksi.setForeground(Color.WHITE);
        btnTambahTransaksi.setFont(new Font("Arial", Font.BOLD, 14));
        btnTambahTransaksi.setFocusPainted(false);
        btnTambahTransaksi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Buka halaman tambah transaksi
                SwingUtilities.invokeLater(() -> {
                    AplikasiKeuangan app = new AplikasiKeuangan();
                    app.setVisible(true);
                    setVisible(false); // Sembunyikan dashboard
                });
            }
        });
        panelTombol.add(btnTambahTransaksi);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(155, 89, 182));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Refresh data real-time
                updateRingkasan();
            }
        });
        panelTombol.add(btnRefresh);

        JButton btnReset = new JButton("Reset Saldo");
        btnReset.setBackground(new Color(231, 76, 60));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Arial", Font.BOLD, 14));
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSaldo();
            }
        });
        panelTombol.add(btnReset);

        btnLihatRiwayat = new JButton("Lihat Riwayat");
        btnLihatRiwayat.setBackground(new Color(52, 152, 219));
        btnLihatRiwayat.setForeground(Color.WHITE);
        btnLihatRiwayat.setFont(new Font("Arial", Font.BOLD, 14));
        btnLihatRiwayat.setFocusPainted(false);
        btnLihatRiwayat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRiwayat();
            }
        });
        panelTombol.add(btnLihatRiwayat);

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logout dan kembali ke login
                int confirm = JOptionPane.showConfirmDialog(DashboardFrame.this, "Apakah Anda yakin ingin logout?",
                        "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        LoginFrame login = new LoginFrame();
                        login.setVisible(true);
                    });
                }
            }
        });
        panelTombol.add(btnLogout);

        add(panelTombol, BorderLayout.SOUTH);

        // Update ringkasan (simulasi)
        updateRingkasan();
    }

    private void showRiwayat() {
        SwingUtilities.invokeLater(() -> {
            RiwayatFrame riwayat = new RiwayatFrame();
            riwayat.setLocationRelativeTo(this);
            riwayat.setVisible(true);
        });
    }

    private void updateRingkasan() {
        Database.Totals totals = Database.getTotals();
        lblTotalSaldo.setText("Total Saldo: " + formatRupiah(totals.totalSaldo));
        lblTotalPemasukan.setText("Total Pemasukan: " + formatRupiah(totals.totalPemasukan));
        lblTotalPengeluaran.setText("Total Pengeluaran: " + formatRupiah(totals.totalPengeluaran));
    }

    private void resetSaldo() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin mereset semua data transaksi? Tindakan ini tidak dapat dibatalkan.",
                "Konfirmasi Reset", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.resetTransactions()) {
                updateRingkasan();
                JOptionPane.showMessageDialog(this, "Saldo berhasil direset!", "Reset Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error saat mereset data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String formatRupiah(long nominal) {
        return "Rp " + String.format("%,d", nominal).replace(",", ".");
    }
}