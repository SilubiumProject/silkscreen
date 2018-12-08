package com.deaking.wallet.silkscreen.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author shenzucai
 * @time 2018.08.07 14:43
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket newsApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.enable(true);
        docket.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.deaking.wallet.silkscreen.controller")).paths(PathSelectors.any()).build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Deaking Silkscreen").build();
    }
}
