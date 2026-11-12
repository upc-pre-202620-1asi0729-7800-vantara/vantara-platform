package com.vantara.platform.profile.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the profile REST API end to end with real security, services, persistence
 * (H2) and an account created through the IAM context facade.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfilesEndpointsTest {

    private static final String EMAIL = "profile-endpoints@example.com";
    private static final String EMPTY_EMAIL = "profile-empty@example.com";
    private static final String PASSWORD = "Password1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IamContextFacade iamContextFacade;

    @BeforeEach
    void ensureTheAccountsExist() {
        iamContextFacade.createUser(EMAIL, PASSWORD, null);
        iamContextFacade.createUser(EMPTY_EMAIL, PASSWORD, null);
    }

    @Test
    @WithMockUser(username = EMPTY_EMAIL)
    void answersNotFoundBeforeTheFirstSave() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void createsReadsAndUpdatesTheOwnProfile() throws Exception {
        var createBody = """
                {"fullName":"Ana Pérez","organizationName":"Agrosur","phone":"+51 999 999 999",
                 "photoUrl":"","theme":"dark","locale":"es"}
                """;
        mockMvc.perform(put("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.fullName").value("Ana Pérez"))
                .andExpect(jsonPath("$.theme").value("dark"))
                .andExpect(jsonPath("$.locale").value("es"));

        mockMvc.perform(get("/api/v1/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.fullName").value("Ana Pérez"))
                .andExpect(jsonPath("$.organizationName").value("Agrosur"))
                .andExpect(jsonPath("$.phone").value("+51 999 999 999"));

        var updateBody = """
                {"fullName":"Ana Pérez","organizationName":"Agrosur","phone":"",
                 "photoUrl":"https://cdn.example/ana.png","theme":"light","locale":"en"}
                """;
        mockMvc.perform(put("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value(nullValue()))
                .andExpect(jsonPath("$.photoUrl").value("https://cdn.example/ana.png"))
                .andExpect(jsonPath("$.theme").value("light"))
                .andExpect(jsonPath("$.locale").value("en"));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void rejectsAnUnknownTheme() throws Exception {
        var body = """
                {"fullName":"Ana Pérez","organizationName":"Agrosur","phone":"",
                 "photoUrl":"","theme":"blue","locale":"es"}
                """;
        mockMvc.perform(put("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/me"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
