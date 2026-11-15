import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes student records in a local CSV file (no database/JDBC). */
public class StudentFileHandler {
    private final File file;
    private static final String HEADER = "Student_ID,First_Name,Last_Name,Major,Phone,GPA,DOB";

    public StudentFileHandler() {
        this(new File("data/students.csv"));
    }

    public StudentFileHandler(File file) {
        this.file = file;
    }

    public List<Student> loadStudents() throws IOException {
        List<Student> students = new ArrayList<Student>();
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            saveStudents(students);
            return students;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8));
        try {
            String line = reader.readLine(); // Skip CSV header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> values = parseCsvLine(line);
                if (values.size() == 7) {
                    students.add(new Student(values.get(0), values.get(1), values.get(2),
                            values.get(3), values.get(4), values.get(5), values.get(6)));
                }
            }
        } finally {
            reader.close();
        }
        return students;
    }

    public void saveStudents(List<Student> students) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create data folder: " + parent.getAbsolutePath());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));
        try {
            writer.write(HEADER);
            writer.newLine();
            for (Student student : students) {
                String[] row = student.toRow();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) writer.write(',');
                    writer.write(escapeCsv(row[i]));
                }
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public String getFilePath() { return file.getAbsolutePath(); }

    private String escapeCsv(String value) {
        if (value == null) value = "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<String>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                result.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }
        result.add(value.toString());
        return result;
    }
}
