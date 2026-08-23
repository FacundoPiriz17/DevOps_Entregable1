package com.devops.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void gameStoreOpenApi_definesApiMetadataAndBearerAuthentication() {
        OpenAPI openAPI = new OpenApiConfig().gameStoreOpenApi();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Game Store API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("v1");
        assertThat(openAPI.getComponents().getSecuritySchemes())
                .containsKey(OpenApiConfig.BEARER_AUTH);
        assertThat(openAPI.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_AUTH).getScheme())
                .isEqualTo("bearer");
    }
}
