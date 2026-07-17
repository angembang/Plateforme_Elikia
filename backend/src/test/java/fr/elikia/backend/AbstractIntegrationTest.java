package fr.elikia.backend;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class AbstractIntegrationTest {
    @MockitoBean
    protected JavaMailSender mailSender;
}
