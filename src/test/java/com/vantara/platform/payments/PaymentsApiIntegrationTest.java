package com.vantara.platform.payments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:vantara_payments_api;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
class PaymentsApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void exposesTheFrontendPaymentContract() throws Exception {
        var createBody = """
                {
                  "rancherId": "usr-002",
                  "appointmentId": null,
                  "concept": "Suscripción Profesional",
                  "amount": "49.90",
                  "currency": "PEN",
                  "status": "paid",
                  "createdAt": "2026-10-05",
                  "confirmedAt": "2026-10-05",
                  "cancelledAt": null,
                  "paymentMethod": {
                    "brand": "VISA",
                    "last4": "4582",
                    "holderName": "JUAN QUISPE"
                  }
                }
                """;

        var createResult = mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var paymentJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        var paymentId = paymentJson.get("id").asText();
        assertThat(paymentJson.get("status").asText()).isEqualTo("paid");
        assertThat(paymentJson.get("appointmentId").isNull()).isTrue();
        assertThat(paymentJson.get("paymentMethod").get("brand").asText()).isEqualTo("VISA");
        assertThat(paymentJson.get("paymentMethod").get("last4").asText()).isEqualTo("4582");

        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        var transactionsResult = mockMvc.perform(get("/api/v1/transactions")
                        .param("paymentId", paymentId))
                .andExpect(status().isOk())
                .andReturn();
        var transactionsJson = objectMapper.readTree(transactionsResult.getResponse().getContentAsString());
        assertThat(transactionsJson).hasSize(1);
        assertThat(transactionsJson.get(0).get("status").asText()).isEqualTo("approved");

        var receiptsResult = mockMvc.perform(get("/api/v1/receipts")
                        .param("paymentId", paymentId))
                .andExpect(status().isOk())
                .andReturn();
        var receiptsJson = objectMapper.readTree(receiptsResult.getResponse().getContentAsString());
        assertThat(receiptsJson).hasSize(1);
        var receiptId = receiptsJson.get(0).get("id").asText();

        var pdf = mockMvc.perform(get("/api/v1/receipts/{id}/document", receiptId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }
}
