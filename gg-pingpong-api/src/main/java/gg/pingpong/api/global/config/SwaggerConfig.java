package gg.pingpong.api.global.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi partyGroup() {
		return GroupedOpenApi.builder()
			.group("party")
			.pathsToMatch("/party/**")
			.packagesToScan("gg.party.api.user")
			.build();
	}

	@Bean
	public GroupedOpenApi partyAdminGroup() {
		return GroupedOpenApi.builder()
			.group("party admin")
			.pathsToMatch("/party/admin/**")
			.packagesToScan("gg.party.api.admin")
			.build();
	}

	@Bean
	public GroupedOpenApi group1() {
		return GroupedOpenApi.builder()
			.group("pingpong")
			.pathsToMatch("/pingpong/**")
			.packagesToScan("gg.pingpong.api.user")
			.build();
	}

	@Bean
	public GroupedOpenApi recruitGroup() {
		return GroupedOpenApi.builder()
			.group("recruit")
			.pathsToMatch("/recruitments/**")
			.packagesToScan("gg.recruit.api.user")
			.build();
	}

	@Bean
	public GroupedOpenApi recruitAdminGroup() {
		return GroupedOpenApi.builder()
			.group("recruit admin")
			.pathsToMatch("/admin/recruitments/**")
			.packagesToScan("gg.recruit.api.admin")
			.build();
	}

	@Bean
	public GroupedOpenApi admin_group() {
		return GroupedOpenApi.builder()
			.group("pingpong admin")
			.pathsToMatch("/pingpong/admin/**")
			.build();
	}

	@Bean
	public OpenAPI springShopOpenAPI() {
		String jwtSchemeName = "jwtAuth";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.scheme("Bearer")
				.bearerFormat("JWT"));
		Server server = new Server();
		server.setUrl("/");
		server.setDescription("test Server url");
		return new OpenAPI()
			.info(new Info().title("42GG V2 API")
				.description("42GG 백엔드 프로젝트 API 명세서입니다.")
				.version("v2.0.0"))
			.addServersItem(server)
			.addSecurityItem(securityRequirement)
			.components(components);
	}
}
