/** Represents one student record. */
public class Student {
    private String studentId;
    private String firstName;
    private String lastName;
    private String major;
    private String phone;
    private String gpa;
    private String dateOfBirth;

    public Student(String studentId, String firstName, String lastName, String major,
                   String phone, String gpa, String dateOfBirth) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.phone = phone;
        this.gpa = gpa;
        this.dateOfBirth = dateOfBirth;
    }

    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMajor() { return major; }
    public String getPhone() { return phone; }
    public String getGpa() { return gpa; }
    public String getDateOfBirth() { return dateOfBirth; }

    public void setFirstName(String value) { firstName = value; }
    public void setLastName(String value) { lastName = value; }
    public void setMajor(String value) { major = value; }
    public void setPhone(String value) { phone = value; }
    public void setGpa(String value) { gpa = value; }
    public void setDateOfBirth(String value) { dateOfBirth = value; }

    public String[] toRow() {
        return new String[] { studentId, firstName, lastName, major, phone, gpa, dateOfBirth };
    }
}
