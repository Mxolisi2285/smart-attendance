package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Teacher;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.TeacherRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Teacher teacher = teacherRepository.findByEmail(email);
        if (teacher == null) throw new UsernameNotFoundException("Teacher not found");

        return new User(
                teacher.getEmail(),
                teacher.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_TEACHER"))
        );
    }
}

