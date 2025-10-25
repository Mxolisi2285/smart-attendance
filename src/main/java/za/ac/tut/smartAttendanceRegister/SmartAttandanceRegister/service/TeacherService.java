package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Teacher;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Authenticate by email + raw password
    public Teacher authenticate(String email, String rawPassword) {
        Teacher teacher = teacherRepository.findByEmail(email);
        if (teacher != null && passwordEncoder.matches(rawPassword, teacher.getPassword())) {
            return teacher;
        }
        return null;
    }

    // Create teacher with encoded password
    public Teacher createTeacher(String name, String email, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        Teacher t = new Teacher(name, email, encoded);
        return teacherRepository.save(t);
    }

    // Change password
    public boolean changePassword(Long teacherId, String currentRaw, String newRaw) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) return false;
        if (!passwordEncoder.matches(currentRaw, teacher.getPassword())) return false;
        teacher.setPassword(passwordEncoder.encode(newRaw));
        teacherRepository.save(teacher);
        return true;
    }

    public Teacher findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

}
