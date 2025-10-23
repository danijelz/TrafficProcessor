package com.example.traficprocessor.adapter.presentation.rest;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.OPEN_API_DESCRIPTION;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.OPEN_API_TITLE;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.Type.OAUTH2;

import com.example.traficprocessor.adapter.presentation.rest.observability.RequestLoggingFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:presentation_rest.properties")
public class RestPresentationConfig {
  @Bean
  OpenAPI openApi(
      @Value("${application.version:SNAPSHOT}") String applicationVersion,
      @Value("${springdoc.swagger-ui.oauth.authorization-url:#{null}}")
          Optional<String> authorizationUrl,
      @Value("${springdoc.swagger-ui.oauth.scheme-name:Keycloak}") String schemeName) {
    var info =
        new Info()
            .title(OPEN_API_TITLE)
            .description(OPEN_API_DESCRIPTION)
            .version(applicationVersion);
    var openApi = new OpenAPI().info(info);
    authorizationUrl.ifPresent(au -> initSecurity(openApi, au, schemeName));

    return openApi;
  }

  private void initSecurity(OpenAPI openApi, String authorizationUrl, String schemeName) {
    var oauthFlow = new OAuthFlow().authorizationUrl(authorizationUrl);
    var oauthFlows = new OAuthFlows().implicit(oauthFlow);
    var scheme = new SecurityScheme().name(schemeName).type(OAUTH2).in(HEADER).flows(oauthFlows);
    var components = new Components().addSecuritySchemes(schemeName, scheme);
    var requirements = List.of(new SecurityRequirement().addList(schemeName));
    openApi.components(components).security(requirements);
  }

  @Bean
  RequestLoggingFilter requestLoggingFilter() {
    return new RequestLoggingFilter();
  }

  @Bean
  LocaleResolver localeResolver() {
    var resolver = new AcceptHeaderLocaleResolver();
    resolver.setDefaultLocale(Locale.US);
    return resolver;
  }
}
