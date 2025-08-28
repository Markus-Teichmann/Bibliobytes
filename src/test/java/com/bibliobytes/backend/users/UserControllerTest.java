package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.auth.services.mail.MailService;
import com.bibliobytes.backend.users.dtos.*;
import com.bibliobytes.backend.users.entities.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    //auth/login
    //update/credentials
    //update/credentials/confirm
    //update/profile
    //update/role
    //delete
    //get -- {id}
    //applicants
    //get -- allUsers

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JweService jweService;

    @MockitoBean
    private MailService mockMailService;

    /*
        Bin mir nicht ganz sicher, ob wir die Repository die Daten tatsächlich speichern lassen sollen, einerseits kann
        dann Anhand der Daten genau nachverfolgt werden, ob tatsächlich alles funktioniert hat. Anderseits brauchen wir
        dafür später einen Testing Branch um die original-Datenbank nicht unpassenden Daten zu füllen.
     */
    private static final RegisterUserRequest validExternal = new RegisterUserRequest();
    private static final RegisterUserRequest validUser = new RegisterUserRequest();
    private static final RegisterUserRequest validAdmin = new RegisterUserRequest();

    private static final RegisterCodeRequest validRegisterCode = new RegisterCodeRequest();
    private static final UpdateCodeRequest validUpdateCode = new UpdateCodeRequest();

    @BeforeAll
    static void setData() {
        validExternal.setEmail("external.one@bibliobytes.at");
        validExternal.setFirstName("external");
        validExternal.setLastName("one");

        validUser.setEmail("user.one@bibliobytes.at");
        validUser.setFirstName("user");
        validUser.setLastName("one");
        validUser.setPassword("password");

        validAdmin.setEmail("admin.one@bibliobytes.at");
        validAdmin.setFirstName("admin");
        validAdmin.setLastName("one");
        validAdmin.setPassword("password");

        validRegisterCode.setCode("123456");

        validUpdateCode.setCodeFromOldEmail("123456");
        validUpdateCode.setCodeFromNewEmail("123456");
    }

    @BeforeEach
    void setMockings() {
        when(mockMailService.sendCodeTo(validExternal.getEmail())).thenReturn("123456");
        when(mockMailService.sendCodeTo(validUser.getEmail())).thenReturn("123456");
        when(mockMailService.sendCodeTo(validAdmin.getEmail())).thenReturn("123456");
        when(mockMailService.sendCodeTo("test.one@bibliobytes.at")).thenReturn("123456");
    }

    @Test
    public void testRegisterExternal_ValidData() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validExternal))
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        Assertions.assertEquals(validExternal.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validExternal.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validExternal.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(Role.EXTERNAL.name(), responseMap.get("role"));
    }

    private void testRegister_ValidData(RegisterUserRequest validData) throws Exception {
        //First Part - Creating a Encrypted Token with the Userdata and sending a verificationcode to the Email
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validData))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String,String> responseMap = objectMapper.readValue(response, HashMap.class);
        String token = responseMap.get("token");
        Jwe jwe = jweService.parse(token);
        Assertions.assertEquals("123456", jwe.getCode());
        RegisterUserRequest createdData = jwe.toDto();
        Assertions.assertEquals(validData.getEmail(), createdData.getEmail());
        Assertions.assertEquals(validData.getFirstName(), createdData.getFirstName());
        Assertions.assertEquals(validData.getLastName(), createdData.getLastName());
        Assertions.assertEquals(validData.getPassword(), createdData.getPassword());

        //Second Part - Confirming the Email and saving the Tokendata
        result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register/confirm")
                .cookie(new Cookie("register_token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterCode))
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();
        response = result.getResponse().getContentAsString();
        responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validData.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validData.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validData.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(Role.APPLICANT.name(), responseMap.get("role"));
    }

    @Test
    public void testRegisterUser_ValidData() throws Exception {
        testRegister_ValidData(validUser);
    }

    @Test
    @Sql(scripts = "/TestResources/SetAdminRole.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegisterAdmin_ValidData() throws Exception {
        testRegister_ValidData(validAdmin);
    }
}
