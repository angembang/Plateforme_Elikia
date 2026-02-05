package fr.elikia.backend.auth;

import fr.elikia.backend.bll.AuthService;
import fr.elikia.backend.bo.Admin;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dao.idao.IDAOAdmin;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
import fr.elikia.backend.dto.LoginDTO;
import fr.elikia.backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private IDAOMember idaoMember;
    @Mock
    private IDAOAdmin idaoAdmin;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private IDAORole idaoRole;

    @InjectMocks
    private AuthService authService;

    @Test
    void itShouldLoginAdminSuccessfully() {
        // GIVEN
        LoginDTO dto = new LoginDTO("admin@mail.com", "password123");

        Admin admin = new Admin();
        admin.setEmail("admin@mail.com");
        admin.setPassword("hashed");
        admin.setFailedLoginAttempts(0);

        when(idaoAdmin.findByEmail("admin@mail.com")).thenReturn(admin);
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("admin@mail.com", "ADMIN"))
                .thenReturn("jwt-token");

        // WHEN
        LogicResult<String> result = authService.login(dto);

        // THEN
        assertEquals("200", result.getCode());
        assertEquals("ADMIN login successful", result.getMessage());
        assertEquals("jwt-token", result.getData());
    }

    @Test
    void itShouldFailWhenAdminPasswordIsWrong() {
        LoginDTO dto = new LoginDTO("admin@mail.com", "wrong");

        Admin admin = new Admin();
        admin.setEmail("admin@mail.com");
        admin.setPassword("hashed");
        admin.setFailedLoginAttempts(0);

        when(idaoAdmin.findByEmail("admin@mail.com")).thenReturn(admin);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        LogicResult<String> result = authService.login(dto);

        assertEquals("401", result.getCode());
        assertEquals("Incorrect password", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void itShouldReturn403WhenMemberNotValidated() {
        LoginDTO dto = new LoginDTO("member@mail.com", "password123");

        Member member = new Member();
        member.setEmail("member@mail.com");
        member.setPassword("hashed");
        member.setStatus("INSCRIPTION_TRANSMISE");

        when(idaoAdmin.findByEmail("member@mail.com")).thenReturn(null);
        when(idaoMember.findByEmail("member@mail.com")).thenReturn(member);
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        LogicResult<String> result = authService.login(dto);

        assertEquals("403", result.getCode());
        assertEquals("Your membership is currently being processed.", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void itShouldReturn403WhenMemberIsCancelled() {
        LoginDTO dto = new LoginDTO("member@mail.com", "password123");

        Member member = new Member();
        member.setEmail("member@mail.com");
        member.setPassword("hashed");
        member.setStatus("ANNULEE");

        when(idaoAdmin.findByEmail("member@mail.com")).thenReturn(null);
        when(idaoMember.findByEmail("member@mail.com")).thenReturn(member);
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        LogicResult<String> result = authService.login(dto);

        assertEquals("403", result.getCode());
        assertEquals("Your membership has been cancelled. Please contact support.", result.getMessage());
    }

    @Test
    void itShouldReturn401WhenEmailDoesNotExist() {
        LoginDTO dto = new LoginDTO("unknown@mail.com", "password");

        when(idaoAdmin.findByEmail("unknown@mail.com")).thenReturn(null);
        when(idaoMember.findByEmail("unknown@mail.com")).thenReturn(null);

        LogicResult<String> result = authService.login(dto);

        assertEquals("401", result.getCode());
        assertEquals("Invalid email or password", result.getMessage());
        assertNull(result.getData());
    }

}
