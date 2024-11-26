package com.javatechie.filter;

import com.javatechie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    //    @Autowired
//    private RestTemplate template;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest serverHttpRequest = null;
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    jwtUtil.validateToken(authHeader); // Validate the token
                    String role = jwtUtil.extractClaim(authHeader, claims -> claims.get("role", String.class)); // Extract the role
                    String path = exchange.getRequest().getURI().getPath();
                    System.out.println(path + " path");
                    System.out.println(role + " role");

                    // Check if the path starts with "/admin/" and the user has ADMIN role
                    if (path.startsWith("/123/") && !"ADMIN".equals(role)) {

                        throw new RuntimeException("Access denied: User does not have ADMIN role.");
                    }

                    // Add the username to the request for further processing if needed
                    serverHttpRequest = exchange.getRequest().mutate().header("userName", jwtUtil.extractUsername(authHeader)).build();
                    System.out.println("HTTP request is successful");

                } catch (Exception e) {
                    System.out.println("Invalid access...!");
                    throw new RuntimeException("Unauthorized access to the application" + e);
                }
            }
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        });
    }



    public static class Config {

    }
}
