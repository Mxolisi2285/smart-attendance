package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String parentEmail;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @Lob
    private byte[] faceEncoding;

    // 👇 ADD THIS RELATIONSHIP
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendanceRecords = new ArrayList<>(); // initialized to avoid null

    // Constructors
    public Student() {}

    public Student(String name, String parentEmail, SchoolClass schoolClass, byte[] faceEncoding) {
        this.name = name;
        this.parentEmail = parentEmail;
        this.schoolClass = schoolClass;
        this.faceEncoding = faceEncoding;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }
    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }
    public byte[] getFaceEncoding() { return faceEncoding; }
    public void setFaceEncoding(byte[] faceEncoding) { this.faceEncoding = faceEncoding; }

    // 👇 ADD GETTER FOR attendanceRecords
    public List<Attendance> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(List<Attendance> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentEmail='" + parentEmail + '\'' +
                ", schoolClass=" + schoolClass +
                ", faceEncoding=" + Arrays.toString(faceEncoding) +
                '}';
    }
}