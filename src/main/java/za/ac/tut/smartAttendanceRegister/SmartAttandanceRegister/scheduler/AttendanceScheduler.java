package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.StudentRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.AttendanceService;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.EmailService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AttendanceScheduler {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmailService emailService;

    // Schedule to run every Monday at 8 AM
    @Scheduled(cron = "0 0 8 * * MON")
    public void notifyParents() {
        List<Student> students = studentRepository.findAll();
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        LocalDate end = LocalDate.now();

        for (Student student : students) {
            double percentage = attendanceService.calculateAttendancePercentage(student, start, end);
            if (percentage < 70.0) {
                emailService.sendEmail(
                        student.getParentEmail(),
                        "Attendance Warning",
                        "Your child " + student.getName() +
                                " has attendance below 70% (" + String.format("%.2f", percentage) + "%)."
                );
            }
        }
    }
}
