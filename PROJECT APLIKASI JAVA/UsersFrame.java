import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsersFrame extends JFrame {
    private JTable tabelUsers;
    private DefaultTableModel tableModel;

    public UsersFrame() {
        // Pengaturan Window
        setTitle("Daftar Pengguna - Manajer Keuangan");
        setSize(600, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // --- Panel Judul (Atas) ---
        JPanel panelJudul = new JPanel();
        panelJudul.setBackground(new Color(41, 128, 185));
        JLabel lblJudul = new JLabel("DAFTAR PENGGUNA TERDAFTAR");
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        panelJudul.add(lblJudul);
        add(panelJudul, BorderLayout.NORTH);

        // --- Tabel Pengguna (Tengah) ---
        String[] kolom = { "ID", "Username" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelUsers = new JTable(tableModel);
        tabelUsers.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelUsers.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelUsers.setRowHeight(25);
        add(new JScrollPane(tabelUsers), BorderLayout.CENTER);

        // --- Panel Tombol (Bawah) ---
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBawah.setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        panelBawah.add(btnRefresh);

        JButton btnClose = new JButton("Tutup");
        btnClose.setBackground(new Color(149, 165, 166));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Arial", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panelBawah.add(btnClose);

        add(panelBawah, BorderLayout.SOUTH);

        loadData();
        setBackground(Color.WHITE);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Database.User user : Database.getAllUsers()) {
            tableModel.addRow(new Object[] { user.id, user.username });
        }
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
            UsersFrame usersFrame = new UsersFrame();
            usersFrame.setVisible(true);
        });
    }
}
