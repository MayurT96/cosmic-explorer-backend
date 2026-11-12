package com.cosmicexplorer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Runs against an in-memory H2 database, so no PostgreSQL is needed:  mvn test */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ApiSmokeTest {

    @Autowired
    MockMvc mvc;

    @Test
    void catalogDataIsSeeded() throws Exception {
        mvc.perform(get("/api/planets")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(10));
        mvc.perform(get("/api/planets/earth")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Earth"));
        mvc.perform(get("/api/exoplanets")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(14));
        mvc.perform(get("/api/missions")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(19));
        mvc.perform(get("/api/quiz")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(12));
    }

    @Test
    void healthIsOk() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void privateRoutesNeedLogin() throws Exception {
        mvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/me/history")).andExpect(status().isUnauthorized());
    }

    @Test
    void registerLoginHistoryAndFavourites() throws Exception {
        String registered = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(registered, "$.token");
        String bearer = "Bearer " + token;

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Again\",\"email\":\"TEST@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/me").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"));

        mvc.perform(post("/api/me/history").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kind\":\"Planet\",\"text\":\"Mars\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/me/history").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Mars"));

        mvc.perform(put("/api/me/favourites/earth").header("Authorization", bearer)).andExpect(status().isNoContent());
        mvc.perform(get("/api/me/favourites").header("Authorization", bearer))
                .andExpect(jsonPath("$[0]").value("earth"));
        mvc.perform(delete("/api/me/favourites/earth").header("Authorization", bearer)).andExpect(status().isNoContent());
        mvc.perform(get("/api/me/favourites").header("Authorization", bearer))
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(put("/api/me/best").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":5}"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/me").header("Authorization", bearer)).andExpect(jsonPath("$.bestScore").value(5));
    }
}
