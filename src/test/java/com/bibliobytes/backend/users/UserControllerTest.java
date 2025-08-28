package com.bibliobytes.backend.users;

import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.auth.services.mail.MailService;
import com.bibliobytes.backend.users.dtos.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {
    //register
    //register/confirm
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

    @MockitoBean
    private UserRepository mockUserRepository;

    private static final User validUser = new User();
    private static final RegisterUserRequest validRegisterRequest = new RegisterUserRequest();;
    private static final RegisterCodeRequest codeRegisterRequest = new RegisterCodeRequest();
    private static final UpdateCredentialsDto validCredentialsUpdate = new UpdateCredentialsDto();
    private static final UpdateCodeRequest codeUpdateRequest = new UpdateCodeRequest();
    private static final UpdateProfileDto validProfileUpdate = new UpdateProfileDto();
    private static final UpdateRole validRoleUpdate = new UpdateRole();
    private static final DelteUserRequest validDelteUserRequest = new DelteUserRequest();
    private static final UUID validUserId = UUID.randomUUID();

    @BeforeAll
    static void setData() {
        SecurityContextHolder.clearContext();
        validUser.setId(validUserId);
        validUser.setEmail("test.user@bibliobytes.at");
        validUser.setFirstName("Test");
        validUser.setLastName("User");
        validUser.setPassword("password");
        validRegisterRequest.setEmail(validUser.getEmail());
        validRegisterRequest.setFirstName(validUser.getFirstName());
        validRegisterRequest.setLastName(validUser.getLastName());
        validRegisterRequest.setPassword(validUser.getPassword());
        codeRegisterRequest.setCode("123456");
        validCredentialsUpdate.setId(validUser.getId());
        validCredentialsUpdate.setOldEmail(validUser.getEmail());
        validCredentialsUpdate.setNewEmail("user.test@bibliobytes.at");
        validCredentialsUpdate.setConfirmNewEmail("user.test@bibliobytes.at");
        validCredentialsUpdate.setOldPassword(validUser.getPassword());
        validCredentialsUpdate.setNewPassword("drowssap");
        validCredentialsUpdate.setConfirmNewPassword("drowssap");
        codeUpdateRequest.setCodeFromOldEmail("123456");
        codeUpdateRequest.setCodeFromNewEmail("123456");
        validProfileUpdate.setId(validUser.getId());
        validProfileUpdate.setFirstName("User");
        validProfileUpdate.setLastName("Test");
        validRoleUpdate.setId(validUser.getId());
        validRoleUpdate.setRole(Role.USER);
        validDelteUserRequest.setId(validUser.getId());
    }

    @BeforeEach
    void setMockings() {
        when(mockMailService.sendCodeTo(validCredentialsUpdate.getOldEmail())).thenReturn("123456");
        when(mockMailService.sendCodeTo(validCredentialsUpdate.getNewEmail())).thenReturn("123456");
        when(mockUserRepository.save(validUser)).thenReturn(validUser);
        when(mockUserRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
        when(mockUserRepository.findById(validUser.getId())).thenReturn(Optional.of(validUser));
    }

    @Test
    public void testRegisterUser_ValidUser() throws Exception {
        //SetUp nur für diesen Test:
        when(mockUserRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.empty());
        when(mockUserRepository.findById(validUser.getId())).thenReturn(Optional.empty());

        //First Part - Creating a Encrypted Token with the Userdata and sending a verificationcode to the Email
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String,String> responseMap = objectMapper.readValue(response, HashMap.class);
        String token = responseMap.get("token");
        Jwe jwe = jweService.parse(token);
        Assertions.assertEquals("123456", jwe.getCode());
        RegisterUserRequest createdData = jwe.toDto();
        Assertions.assertEquals(validRegisterRequest.getEmail(), createdData.getEmail());
        Assertions.assertEquals(validRegisterRequest.getFirstName(), createdData.getFirstName());
        Assertions.assertEquals(validRegisterRequest.getLastName(), createdData.getLastName());
        Assertions.assertEquals(validRegisterRequest.getPassword(), createdData.getPassword());

        //Second Part - Confirming the Email and saving the Tokendata
        result = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register/confirm")
                .cookie(new Cookie("register_token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(codeRegisterRequest))
        )
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();
        response = result.getResponse().getContentAsString();
        responseMap = objectMapper.readValue(response, HashMap.class);
        Assertions.assertEquals(validUser.getFirstName(), responseMap.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), responseMap.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), responseMap.get("role"));
    }


}
