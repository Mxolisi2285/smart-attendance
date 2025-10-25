package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;

public interface ClassRepository extends JpaRepository<SchoolClass, Long> {

    SchoolClass findByName(String name);

}

