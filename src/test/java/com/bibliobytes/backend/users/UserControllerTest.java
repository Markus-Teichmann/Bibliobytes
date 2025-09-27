package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.dtos.AccessTokenDto;
import com.bibliobytes.backend.users.requests.ConfirmationCodeRequest;
import com.bibliobytes.backend.users.requests.*;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.auth.services.mail.MailService;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JweService jweService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private MailService mockMailService;
//    @MockitoBean
//    private UserRepository mockUserRepository;

    private static final User validExternal = new User();
    private static final User validApplicant = new User();
    private static final String validApplicantClearPassword = "password";
    private static final User validUser = new User();
    private static final String validUserClearPassword = "password";
    private static final User validAdmin = new User();
    private static final String validAdminClearPassword = "password";

    private static final RegisterCodeRequest validRegisterCode = new RegisterCodeRequest();
    private static final ConfirmationCodeRequest validUpdateCode = new ConfirmationCodeRequest();

    @BeforeAll
    static void setFinalData() {
        validExternal.setId(UUID.randomUUID());
        validApplicant.setId(UUID.randomUUID());
        validUser.setId(UUID.randomUUID());
        validAdmin.setId(UUID.randomUUID());

        validRegisterCode.setCode("123456");
        validUpdateCode.setCodeFromOldEmail("123456");
        validUpdateCode.setCodeFromNewEmail("123456");
    }
    @BeforeEach
    void restoreDefaultData() {
        validExternal.setEmail("external.one@bibliobytes.at");
        validExternal.setFirstName("external");
        validExternal.setLastName("one");
        validExternal.setRole(Role.EXTERNAL);

        validApplicant.setEmail("applicant.one@bibliobytes.at");
        validApplicant.setFirstName("applicant");
        validApplicant.setLastName("one");
        validApplicant.setPassword(passwordEncoder.encode(validApplicantClearPassword));
        validApplicant.setRole(Role.APPLICANT);

        validUser.setEmail("user.one@bibliobytes.at");
        validUser.setFirstName("user");
        validUser.setLastName("one");
        validUser.setPassword(passwordEncoder.encode(validUserClearPassword));
        validUser.setRole(Role.USER);

        validAdmin.setEmail("admin.one@bibliobytes.at");
        validAdmin.setFirstName("admin");
        validAdmin.setLastName("one");
        validAdmin.setPassword(passwordEncoder.encode(validAdminClearPassword));
        validAdmin.setRole(Role.ADMIN);
    }
    @BeforeEach
    void setDefaultBehavior_ValidData() {
        when(mockMailService.sendCodeTo(anyString())).thenReturn("123456");
//
//        when(mockUserRepository.findByEmail(validExternal.getEmail())).thenReturn(Optional.of(validExternal));
//        when(mockUserRepository.findByEmail(validApplicant.getEmail())).thenReturn(Optional.of(validApplicant));
//        when(mockUserRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
//        when(mockUserRepository.findByEmail(validAdmin.getEmail())).thenReturn(Optional.of(validAdmin));
//
//        when(mockUserRepository.findById(validExternal.getId())).thenReturn(Optional.of(validExternal));
//        when(mockUserRepository.findById(validApplicant.getId())).thenReturn(Optional.of(validApplicant));
//        when(mockUserRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));
//        when(mockUserRepository.findById(validAdmin.getId())).thenReturn(Optional.of(validAdmin));
//
//        when(mockUserRepository.findAllByRole(Role.EXTERNAL)).thenReturn(Set.of(validExternal));
//        when(mockUserRepository.findAllByRole(Role.APPLICANT)).thenReturn(Set.of(validApplicant));
//        when(mockUserRepository.findAllByRole(Role.USER)).thenReturn(Set.of(validUser));
//        when(mockUserRepository.findAllByRole(Role.SERVICE)).thenReturn(Set.of());
//        when(mockUserRepository.findAllByRole(Role.ADMIN)).thenReturn(Set.of(validAdmin));
//    }
//    private void clearDatabase() {
//        when(mockUserRepository.findByEmail(validExternal.getEmail())).thenReturn(Optional.empty());
//        when(mockUserRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.empty());
//        when(mockUserRepository.findByEmail(validAdmin.getEmail())).thenReturn(Optional.empty());
    }
    @Sql(scripts = "/TestResources/DeleteExternal.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testRegisterExternal_ValidData() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(validExternal.getEmail());
        request.setFirstName(validExternal.getFirstName());
        request.setLastName(validExternal.getLastName());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        Assertions.assertEquals(validExternal.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validExternal.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validExternal.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validExternal.getRole().name(), responseMap.get("role"));
    }
    @Sql(scripts = "/TestResources/DeleteUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testRegisterUser_ValidData() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(validUser.getEmail());
        request.setFirstName(validUser.getFirstName());
        request.setLastName(validUser.getLastName());
        request.setPassword(validUserClearPassword);

        //First Part - Creating a Encrypted Token with the Userdata and sending a verificationcode to the Email
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        System.out.println("Hello from Test Class");
        Cookie cookie = result.getResponse().getCookie("register_token");
        String token = cookie.getValue();
        Jwe jwe = jweService.parse(token);
        Assertions.assertEquals("123456", jwe.getCode());
        RegisterUserRequest createdData = jwe.toDto();
        Assertions.assertEquals(validUser.getEmail(), createdData.getEmail());
        Assertions.assertEquals(validUser.getFirstName(), createdData.getFirstName());
        Assertions.assertEquals(validUser.getLastName(), createdData.getLastName());
        Assertions.assertEquals(validUserClearPassword, createdData.getPassword());

        //Second Part - Confirming the Email and saving the Tokendata
        result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/register/confirm")
                        .cookie(new Cookie("register_token", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterCode))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(request.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(request.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(request.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(Role.APPLICANT.name(), responseMap.get("role"));
    }
    @Sql(scripts = "/TestResources/DeleteAdmin.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/TestResources/InsertAdmin.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testLoginAdmin_ValidData() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(validAdmin.getEmail());
        request.setPassword(validAdminClearPassword);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isOk());
    }
    private String getAccessToken_ValidData(User user) throws Exception {
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setId(user.getId());
        accessTokenDto.setRole(user.getRole());
        Jwe jwe = jweService.generateAccessToken(accessTokenDto);
        return "Bearer " + jwe.toString();
    }
    @Sql(scripts = "/TestResources/DeleteUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/TestResources/InsertUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/TestResources/SetUserID.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void testAdminUpdateForeignRole_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validAdmin);

        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setRole(Role.USER);
//        request.setId(validApplicant.getId());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/users/" + validApplicant.getId() + "/role")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validApplicant.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals(validApplicant.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validApplicant.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validApplicant.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(Role.USER.name(), responseMap.get("role"));
    }
    @Test
    public void testAdminUpdateForeignEmail_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validAdmin);

        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setOldEmail(validUser.getEmail());
        request.setNewEmail("new.email@bibliobytes.at");
        request.setConfirmNewEmail("new.email@bibliobytes.at");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/users/" + validUser.getId() + "/email")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals("new.email@bibliobytes.at", responseMap.get("email"));
        Assertions.assertEquals(validUser.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), responseMap.get("role"));
        Assertions.assertTrue(passwordEncoder.matches("password", validUser.getPassword()));
    }
    @Test
    public void testUserUpdateOwnCredentials_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validUser);

        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setOldEmail(validUser.getEmail());
        request.setNewEmail("new.email@bibliobytes.at");
        request.setConfirmNewEmail("new.email@bibliobytes.at");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/me/email")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        String updateCredentialsToken = responseMap.get("token");

        ConfirmationCodeRequest confirmationCodeRequest = new ConfirmationCodeRequest();
        confirmationCodeRequest.setCodeFromOldEmail("123456");
        confirmationCodeRequest.setCodeFromNewEmail("123456");
        result = mockMvc.perform(MockMvcRequestBuilders
                .put("/me/email")
                .cookie(new Cookie("update_credentials_token", updateCredentialsToken))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmationCodeRequest))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        response = result.getResponse().getContentAsString();
        responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals("new.email@bibliobytes.at", responseMap.get("email"));
        Assertions.assertEquals(validUser.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), responseMap.get("role"));
        Assertions.assertTrue(passwordEncoder.matches("password", validUser.getPassword()));
    }
    @Test
    public void testUserUpdateOwnProfile_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validUser);
        UpdateFirstNameRequest request = new UpdateFirstNameRequest();
        request.setFirstName("first");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/me/firstname")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals(validUser.getEmail(), responseMap.get("email"));
        Assertions.assertEquals("first", responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), responseMap.get("role"));
    }
    @Test
    public void testUserSelfDelete_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validUser);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/users")
                .header("Authorization", token)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals(validUser.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validUser.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(Role.EXTERNAL.name(), responseMap.get("role"));
    }
    @Test
    public void testUserGetDetails_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validUser);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/users/" + validUser.getId())
                .header("Authorization", token)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getId(), UUID.fromString(responseMap.get("id")));
        Assertions.assertEquals(validUser.getEmail(), responseMap.get("email"));
        Assertions.assertEquals(validUser.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), responseMap.get("role"));
    }
    @Test
    public void testAdminGetAllApplicants_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validAdmin);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/users/new")
                .header("Authorization", token)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        List<Map<String,String>> responseList = objectMapper.readValue(response, ArrayList.class);
        for (Map<String,String> responseMap : responseList) {
            Assertions.assertEquals(validApplicant.getId(), UUID.fromString(responseMap.get("id")));
            Assertions.assertEquals(validApplicant.getEmail(), responseMap.get("email"));
            Assertions.assertEquals(validApplicant.getFirstName(), responseMap.get("firstName"));
            Assertions.assertEquals(validApplicant.getLastName(), responseMap.get("lastName"));
            Assertions.assertEquals(validApplicant.getRole().name(), responseMap.get("role"));
        }
    }
    @Test
    public void testAdminGetAllUsers_ValidData() throws Exception {
        String token = getAccessToken_ValidData(validAdmin);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .header("Authorization", token)
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String,List<Map<String,String>>> responseMap = objectMapper.readValue(response, HashMap.class);
        for (Map<String,String> map : responseMap.get("externals")) {
            Assertions.assertEquals(validExternal.getId(), UUID.fromString(map.get("id")));
            Assertions.assertEquals(validExternal.getEmail(), map.get("email"));
            Assertions.assertEquals(validExternal.getFirstName(), map.get("firstName"));
            Assertions.assertEquals(validExternal.getLastName(), map.get("lastName"));
            Assertions.assertEquals(validExternal.getRole().name(), map.get("role"));
        }
        for (Map<String,String> map : responseMap.get("applicants")) {
            Assertions.assertEquals(validApplicant.getId(), UUID.fromString(map.get("id")));
            Assertions.assertEquals(validApplicant.getEmail(), map.get("email"));
            Assertions.assertEquals(validApplicant.getFirstName(), map.get("firstName"));
            Assertions.assertEquals(validApplicant.getLastName(), map.get("lastName"));
            Assertions.assertEquals(validApplicant.getRole().name(), map.get("role"));
        }
        for (Map<String,String> map : responseMap.get("users")) {
            Assertions.assertEquals(validUser.getId(), UUID.fromString(map.get("id")));
            Assertions.assertEquals(validUser.getEmail(), map.get("email"));
            Assertions.assertEquals(validUser.getFirstName(), map.get("firstName"));
            Assertions.assertEquals(validUser.getLastName(), map.get("lastName"));
            Assertions.assertEquals(validUser.getRole().name(), map.get("role"));
        }
        for (Map<String,String> map : responseMap.get("admins")) {
            Assertions.assertEquals(validAdmin.getId(), UUID.fromString(map.get("id")));
            Assertions.assertEquals(validAdmin.getEmail(), map.get("email"));
            Assertions.assertEquals(validAdmin.getFirstName(), map.get("firstName"));
            Assertions.assertEquals(validAdmin.getLastName(), map.get("lastName"));
            Assertions.assertEquals(validAdmin.getRole().name(), map.get("role"));
        }

    }
}