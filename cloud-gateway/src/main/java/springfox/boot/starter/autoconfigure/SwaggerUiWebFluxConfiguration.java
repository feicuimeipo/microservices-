package springfox.boot.starter.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static springfox.documentation.builders.BuilderDefaults.nullToEmpty;

@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
    value = "springfox.documentation.swagger-ui.enabled",
    havingValue = "true",
    matchIfMissing = true)
@Primary
public class SwaggerUiWebFluxConfiguration {
  @Value("${springfox.documentation.swagger-ui.base-url:}")
  private String swaggerBaseUrl;

  @Bean
  public SwaggerUiWebFluxConfigurer swaggerUiWebfluxConfigurer() {
    return new SwaggerUiWebFluxConfigurer(fixup(swaggerBaseUrl));
  }

  @Bean
  public WebFilter uiForwarder() {
    return new CustomWebFilter();
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }

  private static class CustomWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      String path = exchange.getRequest().getURI().getPath();

      if (path.matches(".*/swagger-ui/")) {
        String myPath = StringUtils.trimTrailingCharacter(path, '/') + "/index.html";
        ServerHttpRequest httpRequest =
               exchange.getRequest()
                .mutate()
                .path(myPath)
                .build();

        return chain.filter( exchange.mutate().request(httpRequest).build());
      }

      return chain.filter(exchange);
    }
  }
}
