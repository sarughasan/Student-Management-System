import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AppGUI extends JFrame {
    private final StudentFileHandler fileHandler = new StudentFileHandler();
    private List<Student> students = new ArrayList<Student>();
    private List<Student> visibleStudents = new ArrayList<Student>();
    private final JTextField idField = new JTextField(12);
    private final JTextField firstNameField = new JTextField(12);
    private final JTextField lastNameField = new JTextField(12);
    private final JTextField majorField = new JTextField(12);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField gpaField = new JTextField(12);
    private final JTextField dobField = new JTextField(12);
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final JLabel statusLabel = new JLabel("Ready");

    private static final String[] COLUMNS = {
            "Student ID", "First Name", "Last Name", "Major", "Phone", "GPA", "Date of Birth"
    };

    public AppGUI() {
        super("Student Management System - Core Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(18, 18, 12, 18));
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JLabel heading = new JLabel("STUDENT MANAGEMENT SYSTEM");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(new Color(35, 55, 80));
        root.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;
        addFormRow(form, gbc, 0, "Student ID *", idField);
        addFormRow(form, gbc, 1, "First Name *", firstNameField);
        addFormRow(form, gbc, 2, "Last Name *", lastNameField);
        addFormRow(form, gbc, 3, "Major *", majorField);
        addFormRow(form, gbc, 4, "Phone", phoneField);
        addFormRow(form, gbc, 5, "GPA *", gpaField);
        addFormRow(form, gbc, 6, "Date of Birth", dobField);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 8, 8));
        buttons.setOpaque(false);
        JButton addButton = makeButton("Add Student");
        JButton displayButton = makeButton("Display All");
        JButton searchButton = makeButton("Search");
        JButton sortButton = makeButton("Sort");
        JButton modifyButton = makeButton("Modify Selected");
        JButton deleteButton = makeButton("Delete Selected");
        JButton clearButton = makeButton("Clear Fields");
        buttons.add(addButton); buttons.add(displayButton); buttons.add(searchButton);
        buttons.add(sortButton); buttons.add(modifyButton); buttons.add(deleteButton); buttons.add(clearButton);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(buttons, gbc);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(studentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Student Records"));
        center.add(form, BorderLayout.WEST);
        center.add(tableScroll, BorderLayout.CENTER);

        statusLabel.setBorder(new EmptyBorder(5, 2, 0, 2));
        statusLabel.setForeground(new Color(75, 85, 99));
        root.add(statusLabel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addStudent());
        displayButton.addActionListener(e -> displayAll());
        searchButton.addActionListener(e -> searchStudents());
        sortButton.addActionListener(e -> sortStudents());
        modifyButton.addActionListener(e -> modifySelected());
        deleteButton.addActionListener(e -> deleteSelected());
        clearButton.addActionListener(e -> clearFields());
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedIntoFields();
        });

        loadFromFile();
        displayAll();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridwidth = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    private void loadFromFile() {
        try {
            students = fileHandler.loadStudents();
            setStatus("Loaded " + students.size() + " student(s). File: " + fileHandler.getFilePath());
        } catch (IOException ex) {
            showError("Could not read student file. " + ex.getMessage());
        }
    }

    private void persist() {
        try {
            fileHandler.saveStudents(students);
            setStatus("Saved successfully. Total students: " + students.size());
        } catch (IOException ex) {
            showError("Could not save student records. " + ex.getMessage());
        }
    }

    private void addStudent() {
        Student student = readForm();
        if (student == null) return;
        if (findById(student.getStudentId()) != null) {
            showError("A student with this ID already exists.");
            return;
        }
        students.add(student);
        persist();
        displayAll();
        clearFields();
        JOptionPane.showMessageDialog(this, "Student added successfully.");
    }

    private Student readForm() {
        String id = idField.getText().trim();
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String major = majorField.getText().trim();
        String phone = phoneField.getText().trim();
        String gpa = gpaField.getText().trim();
        String dob = dobField.getText().trim();
        if (id.isEmpty() || first.isEmpty() || last.isEmpty() || major.isEmpty() || gpa.isEmpty()) {
            showError("Please fill all required fields marked with *.");
            return null;
        }
        try {
            double value = Double.parseDouble(gpa);
            if (value < 0 || value > 10) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("GPA must be a number between 0 and 10.");
            return null;
        }
        if (!phone.isEmpty() && !phone.matches("[0-9+()\\- ]{7,10}")) {
            showError("Enter a valid phone number (7 to 10 characters).");
            return null;
        }
        return new Student(id, first, last, major, phone, gpa, dob);
    }

    private void displayAll() {
        visibleStudents = new ArrayList<Student>(students);
        refreshTable();
        setStatus("Displaying all " + students.size() + " student(s).");
    }

    private void searchStudents() {
        String[] choices = {"Student ID", "Last Name", "Major"};
        int choice = JOptionPane.showOptionDialog(this, "Search by:", "Search Students",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (choice < 0) return;
        String term = JOptionPane.showInputDialog(this, "Enter search text:");
        if (term == null) return;
        term = term.trim().toLowerCase();
        final int selected = choice;
        final String query = term;
        visibleStudents = new ArrayList<Student>();
        for (Student s : students) {
            String field = selected == 0 ? s.getStudentId() : selected == 1 ? s.getLastName() : s.getMajor();
            if (field.toLowerCase().contains(query)) visibleStudents.add(s);
        }
        refreshTable();
        setStatus("Search returned " + visibleStudents.size() + " result(s).");
        if (visibleStudents.isEmpty()) JOptionPane.showMessageDialog(this, "No matching students found.");
    }

    private void sortStudents() {
        String[] choices = {"First Name", "Last Name", "Major"};
        int choice = JOptionPane.showOptionDialog(this, "Sort by:", "Sort Students",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (choice < 0) return;
        visibleStudents = new ArrayList<Student>(students);
        final int selected = choice;
        Collections.sort(visibleStudents, new Comparator<Student>() {
            @Override public int compare(Student a, Student b) {
                String x = selected == 0 ? a.getFirstName() : selected == 1 ? a.getLastName() : a.getMajor();
                String y = selected == 0 ? b.getFirstName() : selected == 1 ? b.getLastName() : b.getMajor();
                return x.compareToIgnoreCase(y);
            }
        });
        refreshTable();
        setStatus("Sorted records. Original storage order is unchanged.");
    }

    private void modifySelected() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { showError("Select a student row to modify."); return; }
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        Student existing = findById(id);
        if (existing == null) return;
        Student updated = readForm();
        if (updated == null) return;
        if (!existing.getStudentId().equals(updated.getStudentId())) {
            showError("Student ID cannot be changed. Clear the ID field back to " + existing.getStudentId() + ".");
            return;
        }
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setMajor(updated.getMajor());
        existing.setPhone(updated.getPhone());
        existing.setGpa(updated.getGpa());
        existing.setDateOfBirth(updated.getDateOfBirth());
        persist();
        displayAll();
        clearFields();
        JOptionPane.showMessageDialog(this, "Student details updated successfully.");
    }

    private void deleteSelected() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { showError("Select a student row to delete."); return; }
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this, "Delete student " + id + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Student target = findById(id);
        if (target != null) students.remove(target);
        persist();
        displayAll();
        clearFields();
    }

    private void loadSelectedIntoFields() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return;
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        firstNameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        lastNameField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        majorField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        gpaField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        dobField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private Student findById(String id) {
        for (Student student : students) if (student.getStudentId().equalsIgnoreCase(id)) return student;
        return null;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Student student : visibleStudents) tableModel.addRow(student.toRow());
    }

    private void clearFields() {
        idField.setText(""); firstNameField.setText(""); lastNameField.setText("");
        majorField.setText(""); phoneField.setText(""); gpaField.setText(""); dobField.setText("");
        studentTable.clearSelection();
    }

    private void setStatus(String message) { statusLabel.setText(message); }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Student Management System", JOptionPane.ERROR_MESSAGE); }
}
