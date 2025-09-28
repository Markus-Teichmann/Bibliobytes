package com.bibliobytes.backend.items;

import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.items.requests.DonateNewItemRequest;
import com.bibliobytes.backend.users.requests.LoginRequest;
import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.items.entities.*;
import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final DonateNewItemRequest donateValidBook = new DonateNewItemRequest();
    private static final DonateNewItemRequest donateValidDigital = new DonateNewItemRequest();
    private static final Set<String> validTags = new HashSet<>();
    private static final Set<String> validActorNames = new HashSet<>();
    private static final Set<String> validSubtitleLanguages = new HashSet<>();
    private static final Set<String> validLanguageNames = new HashSet<>();

    private static final User validUser = new User();

    @BeforeAll
    static void setFinalData() {
        validUser.setEmail("user.one@bibliobytes.at");
        validUser.setFirstName("user");
        validUser.setLastName("one");
        validUser.setPassword("password");
        validUser.setRole(Role.USER);

        for (long l=0L; l<10; l++) {
            validTags.add("tag" + l);
            validActorNames.add("actor" + l);
            validSubtitleLanguages.add("language" + l);
            validLanguageNames.add("language" + l);
        }
        // Notwendig für den Code
        donateValidBook.setType(Type.BOOK);
        donateValidBook.setCondition(Condition.AS_NEW);
        // Allgemein für Items
        donateValidBook.setTitel("Booktitel");
        donateValidBook.setPlace(null);
        donateValidBook.setTopic("Booktopic");
        donateValidBook.setNote(null);
        donateValidBook.setTags(validTags);
        // Buchspezifisch
        donateValidBook.setAuthor("book author");
        donateValidBook.setPublisher("book publisher");
        donateValidBook.setIsbn("1234567890123");

        // Notwendig für den Code
        donateValidDigital.setType(Type.DIGITAL);
        donateValidDigital.setCondition(Condition.AS_NEW);
        // Allgemein für Items
        donateValidDigital.setTitel("Digitaltitel");
        donateValidDigital.setTopic("Digitaltopic");
        donateValidDigital.setTags(validTags);
        // Digitalspezifisch
        donateValidDigital.setRuntime("90 Minutes");
        donateValidDigital.setActors(validActorNames);
        donateValidDigital.setSubtitles(validSubtitleLanguages);
        donateValidDigital.setLanguages(validLanguageNames);
    }

    private String getAccessToken_ViaLogin_ValidData(User user) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(response, Map.class);
        return "Bearer " + responseMap.get("token");
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
            "/TestResources/ClearUserTable.sql",
            "/TestResources/InsertUser.sql"
    })
    @Test
    public void testValidUserDonatesValidBook() throws Exception {
        String token = getAccessToken_ViaLogin_ValidData(validUser);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/items/donate")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donateValidBook))
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        String response = result.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Assertions.assertEquals(donateValidBook.getCondition().name(), responseMap.get("condition"));
        Assertions.assertEquals(DonationState.APPLIED.name(), responseMap.get("state"));
        Assertions.assertEquals(LocalDate.now().toString(), responseMap.get("date"));
        String ownerString = responseMap.get("owner").toString()
                .replace("{", "{\"")
                .replace("=", "\":\"")
                .replace(", ", "\", \"")
                .replace("}", "\"}");
        Map<String, String> owner = objectMapper.readValue(ownerString, Map.class);
        Assertions.assertEquals(validUser.getEmail(), owner.get("email"));
        Assertions.assertEquals(validUser.getFirstName(), owner.get("firstName"));
        Assertions.assertEquals(validUser.getLastName(), owner.get("lastName"));
        Assertions.assertEquals(validUser.getRole().name(), owner.get("role"));
        String itemString = responseMap.get("item").toString()
                .replace("{", "{\"")
                .replace("=", "\":\"")
                .replace(", ", "\", \"")
                .replace("\"[", "[")
                .replace("\"{", "{")
                .replace("}\"", "}")
                .replace("]\"", "]")
                .replace("}", "\"}");
        System.out.println(itemString);
        Map<String, Object> item = objectMapper.readValue(itemString, Map.class);
        Assertions.assertEquals(donateValidBook.getTitel(), item.get("title"));
        Assertions.assertEquals(donateValidBook.getTopic(), item.get("topic"));
        Assertions.assertEquals(donateValidBook.getAuthor(), item.get("author"));
        Assertions.assertEquals(donateValidBook.getPublisher(), item.get("publisher"));
        Assertions.assertEquals(donateValidBook.getIsbn(), item.get("isbn"));
        System.out.println(item.get("tags"));
        String tagsString = item.get("tags").toString()
                .replace("{", "{\"")
                .replace("=", "\":\"")
                .replace("}", "\"}");
        List<Map<String,String>> tags = objectMapper.readValue(tagsString, List.class);
        for(Map<String, String> tag: tags) {
            Assertions.assertTrue(validTags.contains(tag.get("name")));
        }
    }

    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
            "/TestResources/ClearUserTable.sql",
            "/TestResources/InsertUser.sql"
    })
    @Test
    public void testValidUserDonatesValidDigital() throws Exception {
        String token = getAccessToken_ViaLogin_ValidData(validUser);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/items/donate")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donateValidDigital))
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
    }


}
