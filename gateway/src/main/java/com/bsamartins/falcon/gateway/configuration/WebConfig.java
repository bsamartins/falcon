package com.bsamartins.falcon.gateway.configuration;

import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@Import(GatewayAutoConfiguration.class)
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("test", spec ->
                    spec.path("/api/documents")
                        .uri("http://localhost:8080"))
                .build();
//        spec.predicate(host().and(path("/image/png")));
//
//                });
//                    spec.addResponseHeader("X-TestHeader", "foobar"));
//                            .and()
//                            .route("test2")
//                            .uri("http://httpbin.org:80")
//                            .predicate(path("/image/webp"))
//                            .add(addResponseHeader("X-AnotherHeader", "baz"))
//                            .and()
//                            .build();
//                }
    }

}
