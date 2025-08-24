package com.bibliobytes.backend.auth.services;

import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.users.dtos.RegisterUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest(classes = {JweConfig.class, MailServerConfig.class})
//@ContextConfiguration(classes = {JweService.class, MailService.class})
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
public class JweServiceTest {
    @Autowired
    private JweService jweService;
    private static RegisterUserRequest validData;
    private static String encryptedToken;

    @BeforeEach
    void setUp() throws Exception {
        validData = new RegisterUserRequest();
        validData.setEmail("test.user@bibliobytes.at");
        validData.setFirstName("Test");
        validData.setLastName("User");
        validData.setPassword("password");
        Jwe token = jweService.generateRegisterUserToken(validData);
        encryptedToken = token.toString();
    }

    @Test
    public void testGenerateRegisterTokenContainingSerializedDataFromValidData() throws Exception {
        Jwe token = jweService.generateRegisterUserToken(validData);
        RegisterUserRequest returnData = token.toDto();
        System.out.println("======= Results Start =======");
        System.out.println(validData.getEmail() + " = " + returnData.getEmail());
        Assertions.assertTrue(returnData.getEmail().equals(validData.getEmail()));
        System.out.println(validData.getFirstName() + " = " + returnData.getFirstName());
        Assertions.assertTrue(returnData.getFirstName().equals(validData.getFirstName()));
        System.out.println(validData.getLastName() + " = " + returnData.getLastName());
        Assertions.assertTrue(returnData.getLastName().equals(validData.getLastName()));
        System.out.println(validData.getPassword() + " = " + returnData.getPassword());
        Assertions.assertTrue(returnData.getPassword().equals(validData.getPassword()));
        System.out.println("======= Results End =======");
    }

    @Test
    public void testDecryptionFromTokenContainigValidData() {
        System.out.println("Encrypted Token: " + encryptedToken);
        Jwe token = jweService.parse(encryptedToken);
        RegisterUserRequest returnData = token.toDto();
        System.out.println("======= Results Start =======");
        System.out.println(validData.getEmail() + " = " + returnData.getEmail());
        Assertions.assertTrue(returnData.getEmail().equals(validData.getEmail()));
        System.out.println(validData.getFirstName() + " = " + returnData.getFirstName());
        Assertions.assertTrue(returnData.getFirstName().equals(validData.getFirstName()));
        System.out.println(validData.getLastName() + " = " + returnData.getLastName());
        Assertions.assertTrue(returnData.getLastName().equals(validData.getLastName()));
        System.out.println(validData.getPassword() + " = " + returnData.getPassword());
        Assertions.assertTrue(returnData.getPassword().equals(validData.getPassword()));
        System.out.println("======= Results End =======");
    }
}
