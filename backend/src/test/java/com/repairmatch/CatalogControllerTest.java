package com.repairmatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllCategoriesReturns16Items() throws Exception {
        mockMvc.perform(get("/api/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(16)))
                .andExpect(jsonPath("$[0].name").value("Smartphones"));
    }

    @Test
    void testGetSmartphonesDetailBySlug() throws Exception {
        mockMvc.perform(get("/api/catalog/categories/smartphones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category.name").value("Smartphones"))
                .andExpect(jsonPath("$.category.requiresBrandAndModel").value(true))
                .andExpect(jsonPath("$.brands", not(empty())))
                .andExpect(jsonPath("$.problemTypes", not(empty())));
    }

    @Test
    void testGetModelsForAppleSmartphones() throws Exception {
        mockMvc.perform(get("/api/catalog/categories/cat-01/brands/brd-01/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].brandName").value("Apple"));
    }

    @Test
    void testGetPlumbingProblems() throws Exception {
        mockMvc.perform(get("/api/catalog/categories/cat-14/problems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].title", hasItem(containsString("Leakage"))));
    }
}
