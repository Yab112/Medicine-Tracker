import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class MedicineDetailTracker {
    private Map<String, LocalDate> medicineExpirationDates;

    public MedicineDetailTracker() {
        medicineExpirationDates = new HashMap<>();
    }

    public boolean addMedicine(String medicineName, LocalDate expirationDate) {
        String key = medicineName + "-" + expirationDate.toString();
        if (medicineExpirationDates.containsKey(key)) {
            return false;
        }
        medicineExpirationDates.put(key, expirationDate);
        return true;
    }

    public boolean isMedicineExpired(String medicineName, LocalDate expirationDate) {
        String key = medicineName + "-" + expirationDate.toString();
        if (!medicineExpirationDates.containsKey(key)) {
            return false;
        }
        return expirationDate.isBefore(LocalDate.now());
    }

    public Map<String, LocalDate> getExpiringMedicines(int daysBeforeExpiration) {
        Map<String, LocalDate> expiringMedicines = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        for (Map.Entry<String, LocalDate> entry : medicineExpirationDates.entrySet()) {
            if (entry.getValue().isBefore(currentDate.plusDays(daysBeforeExpiration))) {
                expiringMedicines.put(entry.getKey(), entry.getValue());
            }
        }
        return expiringMedicines;
    }

    public Map<String, LocalDate> getAllMedicines() {
        return new HashMap<>(medicineExpirationDates);
    }

    public boolean deleteMedicine(String medicineName, LocalDate expirationDate) {
        String key = medicineName + "-" + expirationDate.toString();
        if (medicineExpirationDates.containsKey(key)) {
            medicineExpirationDates.remove(key);
            return true;
        }
        return false;
    }
}

class MedicineDetailTrackerUI extends JFrame {
    private MedicineDetailTracker tracker;
    private JTextField addMedicineNameField;
    private JTextField addExpirationDateField;
    private JTextField deleteMedicineNameField;
    private JTextField deleteExpirationDateField;
    private JTextField checkDaysBeforeExpirationField;
    private JTextArea outputArea;
    private DefaultTableModel tableModel;
    private DefaultTableModel expiringTableModel;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MedicineDetailTrackerUI() {
        tracker = new MedicineDetailTracker();
        setTitle("Medicine Detail Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel(new GridLayout(1, 4));
        JButton addButton = new JButton("Add Medicine");
        JButton checkButton = new JButton("Check Expiring Medicines");
        JButton viewButton = new JButton("View All Medicines");
        JButton deleteButton = new JButton("Delete Medicine");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMedicinePanel();
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCheckExpiringMedicinesPanel();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showViewAllMedicinesPanel();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteMedicinePanel();
            }
        });

        menuPanel.add(addButton);
        menuPanel.add(checkButton);
        menuPanel.add(viewButton);
        menuPanel.add(deleteButton);
        add(menuPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createAddMedicinePanel(), "Add Medicine");
        cardPanel.add(createCheckExpiringMedicinesPanel(), "Check Expiring Medicines");
        cardPanel.add(createViewAllMedicinesPanel(), "View All Medicines");
        cardPanel.add(createDeleteMedicinePanel(), "Delete Medicine");

        add(cardPanel, BorderLayout.CENTER);

        showMainMenu();
    }

    private void showMainMenu() {
        cardLayout.show(cardPanel, "Main Menu");
    }

    private void showAddMedicinePanel() {
        cardLayout.show(cardPanel, "Add Medicine");
    }

    private void showCheckExpiringMedicinesPanel() {
        cardLayout.show(cardPanel, "Check Expiring Medicines");
    }

    private void showViewAllMedicinesPanel() {
        cardLayout.show(cardPanel, "View All Medicines");
        updateMedicineTable();
    }

    private void showDeleteMedicinePanel() {
        cardLayout.show(cardPanel, "Delete Medicine");
    }

    private JPanel createAddMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Medicine Name:"));
        addMedicineNameField = new JTextField();
        inputPanel.add(addMedicineNameField);

        inputPanel.add(new JLabel("Expiration Date (YYYY-MM-DD):"));
        addExpirationDateField = new JTextField();
        inputPanel.add(addExpirationDateField);

        JButton addMedicineButton = new JButton("Add Medicine");
        addMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicine();
            }
        });
        inputPanel.add(addMedicineButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        inputPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCheckExpiringMedicinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Days Before Expiration:"));
        checkDaysBeforeExpirationField = new JTextField();
        inputPanel.add(checkDaysBeforeExpirationField);

        JButton checkButton = new JButton("Check Medicines");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkExpiringMedicines();
            }
        });
        inputPanel.add(checkButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        inputPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        expiringTableModel = new DefaultTableModel(new Object[] { "Medicine Name", "Expiration Date" }, 0);
        JTable expiringTable = new JTable(expiringTableModel);
        JScrollPane expiringScrollPane = new JScrollPane(expiringTable);
        panel.add(expiringScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createViewAllMedicinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[] { "Medicine Name", "Expiration Date" }, 0);
        JTable medicineTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(medicineTable);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDeleteMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Medicine Name:"));
        deleteMedicineNameField = new JTextField();
        inputPanel.add(deleteMedicineNameField);

        inputPanel.add(new JLabel("Expiration Date (YYYY-MM-DD):"));
        deleteExpirationDateField = new JTextField();
        inputPanel.add(deleteExpirationDateField);

        JButton deleteMedicineButton = new JButton("Delete Medicine");
        deleteMedicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMedicine();
            }
        });
        inputPanel.add(deleteMedicineButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        inputPanel.add(backButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        return panel;
    }

    private void addMedicine() {
        String medicineName = addMedicineNameField.getText();
        String expirationDateText = addExpirationDateField.getText().trim();

        if (medicineName.isEmpty() || expirationDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both medicine name and expiration date.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate expirationDate = LocalDate.parse(expirationDateText, formatter);

            if (tracker.addMedicine(medicineName, expirationDate)) {
                outputArea.append("Added: " + medicineName + " with expiration date " + expirationDateText + "\n");
                JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                updateMedicineTable();
            } else {
                JOptionPane.showMessageDialog(this, "Medicine already included", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkExpiringMedicines() {
        try {
            int daysBeforeExpiration = Integer.parseInt(checkDaysBeforeExpirationField.getText());
            Map<String, LocalDate> expiringMedicines = tracker.getExpiringMedicines(daysBeforeExpiration);
            expiringTableModel.setRowCount(0); // Clear existing rows
            if (expiringMedicines.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No medicines expiring within the next " + daysBeforeExpiration + " days.", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Map.Entry<String, LocalDate> entry : expiringMedicines.entrySet()) {
                    expiringTableModel.addRow(new Object[] { entry.getKey(), entry.getValue().toString() });
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for days before expiration.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine() {
        String medicineName = deleteMedicineNameField.getText();
        String expirationDateText = deleteExpirationDateField.getText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate expirationDate = LocalDate.parse(expirationDateText, formatter);
            if (tracker.deleteMedicine(medicineName, expirationDate)) {
                outputArea.append("Deleted: " + medicineName + " with expiration date " + expirationDateText + "\n");
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                updateMedicineTable();
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicineTable() {
        tableModel.setRowCount(0); // Clear existing rows
        Map<String, LocalDate> allMedicines = tracker.getAllMedicines();
        for (Map.Entry<String, LocalDate> entry : allMedicines.entrySet()) {
            tableModel.addRow(new Object[] { entry.getKey(), entry.getValue().toString() });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MedicineDetailTrackerUI().setVisible(true);
            }
        });
    }
}