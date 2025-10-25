package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "schoolClass")
    private List<Student> students;

    // Constructors
    public SchoolClass() {}

    public SchoolClass(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}

