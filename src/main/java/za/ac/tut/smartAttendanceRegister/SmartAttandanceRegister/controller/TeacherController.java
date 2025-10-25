package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Teacher;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service.TeacherService;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * Show change password form
     */
    @GetMapping("/changePassword")
    public String changePasswordForm() {
        return "changePassword"; // no need for session check; Spring Security handles authentication
    }

    /**
     * Handle change password form submission
     */
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Model model) {

        // Get currently logged-in teacher's email from Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // email is the username in our setup

        // Fetch teacher from database
        Teacher teacher = teacherService.findByEmail(email);
        if (teacher == null) {
            // just in case
            return "redirect:/login";
        }

        boolean ok = teacherService.changePassword(teacher.getId(), currentPassword, newPassword);
        if (ok) {
            model.addAttribute("message", "Password changed successfully");
        } else {
            model.addAttribute("error", "Current password is incorrect");
        }

        return "changePassword";
    }
}
