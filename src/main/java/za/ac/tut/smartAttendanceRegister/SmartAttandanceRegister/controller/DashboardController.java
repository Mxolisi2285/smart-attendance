package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Attendance;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.AttendanceRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.ClassRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.StudentRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.FaceRecognitionService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.bytedeco.opencv.opencv_core.Mat;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;

@Controller
public class DashboardController {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    // ===================== MAIN DASHBOARD =====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<SchoolClass> classes = classRepository.findAll();
        List<Student> allStudents = studentRepository.findAll();
        LocalDate today = LocalDate.now();

        // Get IDs of students marked PRESENT today
        List<Long> presentStudentIds = attendanceRepository
                .findByDateAndStatus(today, Attendance.Status.PRESENT)
                .stream()
                .map(a -> a.getStudent().getId())
                .collect(Collectors.toList());

        // Absent = all students NOT present today
        List<Student> absentToday = allStudents.stream()
                .filter(student -> !presentStudentIds.contains(student.getId()))
                .collect(Collectors.toList());

        model.addAttribute("classes", classes);
        model.addAttribute("students", allStudents);
        model.addAttribute("attendanceRecords", attendanceRepository.findTop10ByOrderByDateDesc());
        model.addAttribute("absentToday", absentToday);
        model.addAttribute("today", today);
        return "dashboard";
    }

    // ===================== SINGLE IMAGE ATTENDANCE =====================
    @PostMapping("/dashboard/markAttendance")
    public String markAttendance(
            @RequestParam("file") MultipartFile file,
            @RequestParam("classId") Long classId,
            RedirectAttributes redirectAttributes) {

        try {
            if (file == null || file.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "❌ No image uploaded.");
                return "redirect:/dashboard";
            }

            SchoolClass schoolClass = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));

            byte[] imageBytes = file.getBytes();
            Mat capturedFace = imdecode(new Mat(imageBytes), CV_8UC3);

            List<Student> allStudents = studentRepository.findAll();
            faceRecognitionService.trainRecognizer(allStudents);

            Student recognized = faceRecognitionService.recognizeFace(capturedFace);
            if (recognized != null) {
                boolean alreadyMarked = attendanceRepository.existsByStudentIdAndDate(
                        recognized.getId(), LocalDate.now());
                if (alreadyMarked) {
                    redirectAttributes.addFlashAttribute("message", "ℹ️ Attendance already marked for: " + recognized.getName());
                } else {
                    Attendance attendance = new Attendance(
                            recognized, schoolClass, LocalDate.now(), Attendance.Status.PRESENT);
                    attendanceRepository.save(attendance);
                    redirectAttributes.addFlashAttribute("message", "✅ Attendance marked for: " + recognized.getName());
                }
            } else {
                redirectAttributes.addFlashAttribute("message", "❌ Student not recognized.");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "⚠️ Failed to read image: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "⚠️ Error: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ===================== VIDEO ATTENDANCE =====================
    @PostMapping("/attendance/markVideo")
    public String markAttendanceFromVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("classId") Long classId,
            RedirectAttributes redirectAttributes) {

        try {
            if (video == null || video.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "❌ Uploaded video file is empty.");
                return "redirect:/dashboard";
            }

            SchoolClass schoolClass = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));

            List<Student> allStudents = studentRepository.findAll();
            faceRecognitionService.trainRecognizer(allStudents);

            List<Student> recognizedStudents = faceRecognitionService.extractFramesAndRecognize(video);

            int newRecords = 0;
            for (Student student : recognizedStudents) {
                boolean alreadyMarked = attendanceRepository.existsByStudentIdAndDate(
                        student.getId(), LocalDate.now());
                if (!alreadyMarked) {
                    Attendance attendance = new Attendance(
                            student, schoolClass, LocalDate.now(), Attendance.Status.PRESENT);
                    attendanceRepository.save(attendance);
                    newRecords++;
                }
            }

            if (recognizedStudents.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "✅ Video processed, but no students were recognized.");
            } else {
                String msg = "✅ Attendance marked for " + newRecords + " new student(s)! (" +
                        recognizedStudents.size() + " recognized total)";
                redirectAttributes.addFlashAttribute("message", msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "❌ Failed to read video: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "⚠️ Unexpected error: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    // ===================== ADD NEW CLASS =====================
    @PostMapping("/dashboard/addClass")
    public String addClass(@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            if (classRepository.findByName(name) != null) {
                redirectAttributes.addFlashAttribute("message", "❌ Class '" + name + "' already exists!");
            } else {
                classRepository.save(new SchoolClass(name));
                redirectAttributes.addFlashAttribute("message", "✅ Class '" + name + "' created successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "⚠️ Failed to create class: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ===================== ADD NEW STUDENT =====================
    @PostMapping("/dashboard/addStudent")
    public String addStudent(
            @RequestParam("name") String name,
            @RequestParam("parentEmail") String parentEmail,
            @RequestParam("classId") Long classId,
            @RequestParam("faceFile") MultipartFile faceFile,
            RedirectAttributes redirectAttributes) {

        try {
            if (faceFile == null || faceFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "❌ Face image is required.");
                return "redirect:/dashboard";
            }

            SchoolClass schoolClass = classRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));

            Student student = new Student();
            student.setName(name);
            student.setParentEmail(parentEmail);
            student.setSchoolClass(schoolClass);
            student.setFaceEncoding(faceFile.getBytes());

            studentRepository.save(student);
            redirectAttributes.addFlashAttribute("message", "✅ Student '" + name + "' added successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "⚠️ Failed to read face image: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "⚠️ Failed to add student: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ===================== CSV DOWNLOAD =====================
    @GetMapping("/dashboard/downloadAttendanceCSV")
    public ResponseEntity<byte[]> downloadAttendanceCSV() {
        List<Attendance> allRecords = attendanceRepository.findAllByOrderByDateAsc();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);

        // CSV Header
        writer.println("Student Name,Class,Date,Status");

        // CSV Rows
        for (Attendance record : allRecords) {
            String studentName = record.getStudent().getName();
            String className = record.getSchoolClass() != null ? record.getSchoolClass().getName() : "—";
            String date = record.getDate().toString();
            String status = record.getStatus().name();
            writer.println(String.format("%s,%s,%s,%s", studentName, className, date, status));
        }

        writer.flush();
        byte[] csvBytes = out.toByteArray();
        writer.close();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
