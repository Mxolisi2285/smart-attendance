package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Teacher;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.TeacherRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.TeacherService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherService teacherService;

    @Override
    public void run(String... args) throws Exception {
        String defaultEmail = "admin@school.test";
        if (teacherRepository.findByEmail(defaultEmail) == null) {
            String defaultName = "Administrator";
            String defaultPassword = "Admin@123"; // Only for dev!

            // This should internally use BCryptPasswordEncoder
            Teacher admin = teacherService.createTeacher(defaultName, defaultEmail, defaultPassword);

            // ✅ Safe: only log email and name, NOT password
            System.out.println("✅ Created default admin user:");
            System.out.println("   Name: " + admin.getName());
            System.out.println("   Email: " + admin.getEmail());
            // Do NOT print password — even in dev
        } else {
            System.out.println("ℹ️ Default admin already exists.");
        }
    }
}