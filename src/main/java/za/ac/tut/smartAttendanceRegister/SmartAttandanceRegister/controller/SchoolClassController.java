package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.SchoolClassService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class SchoolClassController {

    @Autowired
    private SchoolClassService schoolClassService;

    // Get all classes
    @GetMapping
    public List<SchoolClass> getAllClasses() {
        return schoolClassService.getAllClasses();
    }

    // Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClass> getClassById(@PathVariable Long id) {
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(id);
        return schoolClass.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Add a new class
    @PostMapping("/add")
    public ResponseEntity<SchoolClass> addClass(@RequestParam String name) {
        if (schoolClassService.getClassByName(name) != null) {
            return ResponseEntity.badRequest().build(); // Or throw a custom exception
        }

        SchoolClass schoolClass = new SchoolClass(name);
        SchoolClass savedClass = schoolClassService.saveClass(schoolClass);
        return ResponseEntity.ok(savedClass);
    }

    // Delete a class
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        Optional<SchoolClass> schoolClass = schoolClassService.getClassById(id);
        if (schoolClass.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        schoolClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}