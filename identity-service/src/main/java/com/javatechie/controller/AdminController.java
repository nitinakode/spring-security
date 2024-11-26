package com.javatechie.controller;

import com.javatechie.dto.AuthRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/123")
public class AdminController {
    @GetMapping("/admin1")
    public String getAdmin() {

        System.out.println("answer is admin");
        return "Called by admin";

    }
}
