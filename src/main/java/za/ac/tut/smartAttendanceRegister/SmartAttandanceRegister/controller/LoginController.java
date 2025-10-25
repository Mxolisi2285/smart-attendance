package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // This should resolve to login.html (or .jsp, etc.)
    }
}
