package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.ClassRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.StudentRepository;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    // ✅ Get all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    // ✅ Add new student with image upload
    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestParam("name") String name,
                                             @RequestParam("parentEmail") String parentEmail,
                                             @RequestParam("classId") Long classId,
                                             @RequestParam("faceFile") MultipartFile faceFile) throws IOException {

        // Check if class exists
        SchoolClass schoolClass = classRepository.findById(classId).orElse(null);
        if (schoolClass == null) {
            return ResponseEntity.badRequest().body("Error: Class not found");
        }

        // Get uploaded face bytes
        byte[] faceBytes = faceFile.getBytes();

        // Create and save student
        Student student = new Student(name, parentEmail, schoolClass, faceBytes);
        studentRepository.save(student);

        return ResponseEntity.ok("✅ Student added successfully");
    }

    // ✅ Get a specific student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete a student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Error: Student not found");
        }
        studentRepository.deleteById(id);
        return ResponseEntity.ok("🗑️ Student deleted successfully");
    }
}
