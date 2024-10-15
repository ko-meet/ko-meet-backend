package com.backend.immilog.global.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SwaggerConfig 클래스 테스트")
class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void customOpenAPIIsNotNull() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        assertNotNull(openAPI);
    }

    @Test
    @DisplayName("SecurityScheme 객체가 정상적으로 생성되는지 테스트")
    void componentsAreConfiguredCorrectly() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Components components = openAPI.getComponents();
        assertNotNull(components);
        assertTrue(components.getSecuritySchemes().containsKey("JWT"));

        SecurityScheme securityScheme = components.getSecuritySchemes().get("JWT");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertEquals(SecurityScheme.In.HEADER, securityScheme.getIn());
        assertEquals("Authorization", securityScheme.getName());
    }

    @Test
    @DisplayName("SecurityRequirement 객체가 정상적으로 생성되는지 테스트")
    void securityRequirementsAreConfiguredCorrectly() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        assertFalse(openAPI.getSecurity().isEmpty());

        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("JWT"));
    }

    @Test
    @DisplayName("Info 객체가 정상적으로 생성되는지 테스트")
    void infoIsConfiguredCorrectly() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("IMMILOG REST API", info.getTitle());
        assertEquals("0.0.1", info.getVersion());
        assertEquals("Spring boot 이민자 커뮤니티 앱 REST API", info.getDescription());

        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals("조현수", contact.getName());
        assertEquals("https://github.com/HyunsooZo", contact.getUrl());
        assertEquals("bzhs1992@icloud.com", contact.getEmail());
    }
}