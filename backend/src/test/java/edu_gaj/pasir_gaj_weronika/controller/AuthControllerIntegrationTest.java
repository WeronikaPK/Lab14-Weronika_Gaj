package edu_gaj.pasir_gaj_weronika.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu_gaj.pasir_gaj_weronika.dto.LoginDTO;
import edu_gaj.pasir_gaj_weronika.dto.UserDTO;
import edu_gaj.pasir_gaj_weronika.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_USERNAME = "test_user_integration";
    private static final String TEST_PASSWORD = "SecurePassword123";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    private String generateUniqueEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@pk.pl";
    }

    @Test
    @Order(1)
    @DisplayName("Powinien zarejestrowac nowego uzytkownika z poprawnymi danymi")
    void shouldRegisterNewUser() throws Exception {
        String email = generateUniqueEmail();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(email);
        userDTO.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.password").value(not(TEST_PASSWORD)));
    }

    @Test
    @Order(2)
    @DisplayName("Powinien zalogowac uzytkownika i zwrocic token JWT")
    void shouldLoginAndReturnJwtToken() throws Exception {
        String email = generateUniqueEmail();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(email);
        userDTO.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").value(matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")));
    }

    @Test
    @Order(3)
    @DisplayName("Powinien zwrocic 401 przy logowaniu z nieprawidlowym haslem")
    void shouldReturn401WhenLoginWithWrongPassword() throws Exception {
        String email = generateUniqueEmail();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(TEST_USERNAME);
        userDTO.setEmail(email);
        userDTO.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("Powinien zwrocic 401 przy logowaniu nieistniejacego uzytkownika")
    void shouldReturn401WhenLoginNonExistentUser() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");
        loginDTO.setPassword("AnyPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    @DisplayName("Powinien zwrocic 400 przy rejestracji z nieprawidlowym emailem")
    void shouldReturn400WhenRegisterWithInvalidEmail() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("invalid-email");
        userDTO.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("Powinien zwrocic 400 przy rejestracji z pustymi polami")
    void shouldReturn400WhenRegisterWithEmptyFields() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    @DisplayName("Powinien zwrocic 400 przy logowaniu z pustym emailem")
    void shouldReturn400WhenLoginWithEmptyEmail() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("");
        loginDTO.setPassword("Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("Haslo powinno byc zahashowane w bazie danych BCrypt")
    void passwordShouldBeHashedInDatabase() throws Exception {
        String email = generateUniqueEmail();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(TEST_USERNAME + "_hash");
        userDTO.setEmail(email);
        userDTO.setPassword(TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Assertions.assertFalse(jsonResponse.contains(TEST_PASSWORD));
        Assertions.assertTrue(jsonResponse.contains("$2a$") || jsonResponse.contains("$2b$"));
    }

    @Test
    @Order(9)
    @DisplayName("Token JWT powinien zawierac prawidlowa strukture")
    void jwtTokenShouldHaveValidStructure() throws Exception {
        String email = generateUniqueEmail();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(TEST_USERNAME + "_jwt");
        userDTO.setEmail(email);
        userDTO.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(jsonResponse).get("token").asText();

        String[] parts = token.split("\\.");

        Assertions.assertEquals(3, parts.length);
    }

    @Test
    @Order(10)
    @DisplayName("Powinien zwrocic 409 przy probie rejestracji z istniejacym emailem")
    void shouldReturn409WhenRegisterWithDuplicateEmail() throws Exception {
        String duplicateEmail = generateUniqueEmail();

        UserDTO firstUser = new UserDTO();
        firstUser.setUsername("first_user");
        firstUser.setEmail(duplicateEmail);
        firstUser.setPassword("FirstPassword123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(duplicateEmail));

        UserDTO secondUser = new UserDTO();
        secondUser.setUsername("second_user");
        secondUser.setEmail(duplicateEmail);
        secondUser.setPassword("SecondPassword456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUser)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(containsString("istnieje")));
    }
}