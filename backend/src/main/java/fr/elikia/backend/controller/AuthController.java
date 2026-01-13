package fr.elikia.backend.controller;

import fr.elikia.backend.bll.AuthService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.LoginDTO;
import fr.elikia.backend.dto.RegisterDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Post mapping for register
     * @return the register method of the Auth service
     */
    @PostMapping("/register")
    public LogicResult<Void> register(@RequestBody RegisterDTO registerDTO){
        return authService.register(registerDTO);
    }


    /**
     * Post mapping for login
     * @return the login method of the Auth service
     */
    @PostMapping("/login")
    public LogicResult<String> login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

}
