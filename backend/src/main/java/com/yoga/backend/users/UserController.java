package com.yoga.backend.users;

import com.yoga.backend.common.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class UserController {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users users) {
        Users savedUsers = null;
        ResponseEntity response = null;
        try {
            String hashPwd = passwordEncoder.encode(users.getPwd());
            users.setPwd(hashPwd);

            savedUsers = usersRepository.save(users);
            if (savedUsers.getId() > 0) {
                response = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Given user details are successfully registered");
            }
        } catch (Exception ex) {
            response = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An exception occured due to " + ex.getMessage());
        }
        return response;
    }

}