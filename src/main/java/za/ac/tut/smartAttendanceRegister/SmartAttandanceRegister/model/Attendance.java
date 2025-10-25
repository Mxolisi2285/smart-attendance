package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PRESENT, ABSENT
    }

    // Constructors
    public Attendance() {}

    public Attendance(Student student, SchoolClass schoolClass, LocalDate date, Status status) {
        this.student = student;
        this.schoolClass = schoolClass;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", student=" + student +
                ", schoolClass=" + schoolClass +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}

