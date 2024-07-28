package com.backend.komeet.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 설정 클래스
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    /**
     * Swagger Docket을 설정하기 위한 빈 생성
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.backend.komeet"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * API 정보를 설정하기 위한 ApiInfo 객체 생성.
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("KO-MEET REST API")
                .description("Spring boot 이민자 커뮤니티 앱 REST API")
                .version("0.0.1")
                .contact(new Contact(
                        "조현수",
                        "https://github.com/HyunsooZo",
                        "bzhs1992@icloud.com")
                ).build();
    }

    /**
     * 리소스 핸들러를 추가하기 위해 메서드 재정의
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI HTML을 제공하기 위한 리소스 핸들러 추가
        registry
                .addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // webjar 리소스를 위한 리소스 핸들러 추가 (Swagger UI 종속성)
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
