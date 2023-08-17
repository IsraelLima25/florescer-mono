package br.com.loja.florescer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

	
	@Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                       .components(new Components()
                           .addSecuritySchemes("bearer-key",
                           new SecurityScheme().type(SecurityScheme.Type.HTTP)
                           .scheme("bearer").bearerFormat("JWT")))
                       .info(new Info()
                    		   .title("API - Florescer")
                    		   .description("API de gerenciamento loja de lembranças datas comemorativas")
                    		   .contact(new Contact()
                    				   .name("Admin Florescer")
                    				   .email("florescer.admin@outlook.com"))
                    		   .license(new License()
                    				   .name("License")
                    				   .url("****")));
   }
	
}
