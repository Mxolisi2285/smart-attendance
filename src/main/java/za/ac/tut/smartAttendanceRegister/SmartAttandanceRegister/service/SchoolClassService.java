package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.ClassRepository;
import java.util.List;
import java.util.Optional;

@Service
public class SchoolClassService {

    @Autowired
    private ClassRepository schoolClassRepository;

    // Create or update a class
    public SchoolClass saveClass(SchoolClass schoolClass) {
        return schoolClassRepository.save(schoolClass);
    }

    // Retrieve all classes
    public List<SchoolClass> getAllClasses() {
        return schoolClassRepository.findAll();
    }

    // Find class by ID
    public Optional<SchoolClass> getClassById(Long id) {
        return schoolClassRepository.findById(id);
    }

    // Find class by name
    public SchoolClass getClassByName(String name) {
        return schoolClassRepository.findByName(name);
    }

    // Delete class
    public void deleteClass(Long id) {
        schoolClassRepository.deleteById(id);
    }
}

