package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.ClassRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.StudentRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.AttendanceService;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.FaceRecognitionService;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.EmailService;

import java.io.IOException;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;

@RestController
@RequestMapping("/api/attendance") // ← Changed path to avoid conflict
public class AttendanceController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/mark")
    public ResponseEntity<String> markAttendance(
            @RequestParam("file") MultipartFile file,
            @RequestParam("classId") Long classId) throws IOException {

        SchoolClass schoolClass = classRepository.findById(classId).orElse(null);
        if (schoolClass == null) {
            return ResponseEntity.badRequest().body("Class not found");
        }

        byte[] bytes = file.getBytes();
        Mat capturedFace = imdecode(new Mat(bytes), CV_8UC3);

        List<Student> students = studentRepository.findAll();
        faceRecognitionService.trainRecognizer(students);

        Student recognizedStudent = faceRecognitionService.recognizeFace(capturedFace);
        if (recognizedStudent != null) {
            attendanceService.markPresent(recognizedStudent, schoolClass);
            double pct = attendanceService.calculateMonthlyAttendancePercentage(recognizedStudent);
            if (pct < 30.0) {
                emailService.sendEmail(recognizedStudent.getParentEmail(),
                        "Attendance Warning",
                        "Your child " + recognizedStudent.getName() +
                                " has attendance below 30% (" + String.format("%.2f", pct) + "%) for this month.");
            }
            return ResponseEntity.ok("Attendance marked for " + recognizedStudent.getName());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not recognized");
    }

    @PostMapping("/markVideo")
    public ResponseEntity<String> markAttendanceFromVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("classId") Long classId) throws IOException {

        if (video == null || video.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded video file is empty.");
        }

        SchoolClass schoolClass = classRepository.findById(classId).orElse(null);
        if (schoolClass == null) {
            return ResponseEntity.badRequest().body("Class not found");
        }

        List<Student> allStudents = studentRepository.findAll();
        faceRecognitionService.trainRecognizer(allStudents);

        List<Student> recognizedStudents = faceRecognitionService.extractFramesAndRecognize(video);

        for (Student student : recognizedStudents) {
            attendanceService.markPresent(student, schoolClass);
            double pct = attendanceService.calculateMonthlyAttendancePercentage(student);
            if (pct < 30.0) {
                emailService.sendEmail(student.getParentEmail(),
                        "Attendance Warning",
                        "Your child " + student.getName() +
                                " has attendance below 30% (" + String.format("%.2f", pct) + "%) for this month.");
            }
        }

        return ResponseEntity.ok("Attendance marked for " + recognizedStudents.size() + " student(s)");
    }
}