package com.enesincekara.reparo.business.api;

import com.enesincekara.reparo.PostgresTestConfiguration;
import com.enesincekara.reparo.business.domain.Business;
import com.enesincekara.reparo.business.persistence.BusinessEntity;
import com.enesincekara.reparo.business.persistence.BusinessJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestConfiguration.class)
class BusinessControllerTest {

    private static final String URL = "/api/v1/businesses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BusinessJpaRepository repository;

    @Test
    void shouldCreateAndPersistBusiness() throws Exception {
        ResultActions response = mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "  Deniz Bilgisayar  "
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(header().string(
                        "Location",
                        startsWith(URL + "/")
                ))
                .andExpect(jsonPath("$.name").value(
                        "Deniz Bilgisayar"
                ));

        String location = response.andReturn()
                .getResponse()
                .getHeader("Location");

        assertNotNull(location);

        UUID id = UUID.fromString(
                location.substring(location.lastIndexOf('/') + 1)
        );

        BusinessEntity saved = repository.findById(id)
                .orElseThrow();

        assertEquals("Deniz Bilgisayar", saved.getName());

        response
                .andExpect(jsonPath("$.id").value(
                        saved.getId().toString()
                ))
                .andExpect(jsonPath("$.createdAt").value(
                        saved.getCreatedAt().toString()
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"name\":null}",
            "{\"name\":\"\"}",
            "{\"name\":\"   \"}"
    })
    void shouldRejectMissingOrBlankName(String request)
            throws Exception {

        long countBefore = repository.count();

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value(
                        "INVALID_BUSINESS_NAME"
                ));

        assertEquals(countBefore, repository.count());
    }

    @Test
    void shouldRejectNameLongerThan160CodePoints()
            throws Exception {

        String request = """
                {
                  "name": "%s"
                }
                """.formatted("A".repeat(161));

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(
                        "INVALID_BUSINESS_NAME"
                ));
    }

    @Test
    void shouldAccept160SupplementaryUnicodeCharacters()
            throws Exception {

        String name = "🔧".repeat(160);

        String request = """
                {
                  "name": "%s"
                }
                """.formatted(name);

        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(
                        post(URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetBusinessById() throws Exception {
        UUID id = UUID.fromString(
                "c77acb24-41bb-4204-b4f9-c027408aec29"
        );

        Business business = Business.restore(
                id,
                "Kent Teknik",
                Instant.parse("2026-09-17T10:30:00Z")
        );

        repository.saveAndFlush(
                BusinessEntity.from(business)
        );

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Kent Teknik"))
                .andExpect(jsonPath("$.createdAt").value(
                        "2026-09-17T10:30:00Z"
                ));
    }

    @Test
    void shouldReturnNotFoundWhenBusinessDoesNotExist()
            throws Exception {

        UUID id = UUID.fromString(
                "cf185839-ec3e-4428-8f11-ae15d21106f9"
        );

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value(
                        "Business not found"
                ))
                .andExpect(jsonPath("$.code").value(
                        "BUSINESS_NOT_FOUND"
                ))
                .andExpect(jsonPath("$.businessId").value(
                        id.toString()
                ));
    }
}