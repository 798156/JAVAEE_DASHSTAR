package dashstar.security;

import dashstar.model.User;
import dashstar.repository.UserRepository;
import dashstar.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private UserRepository userRepository;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<String> methodRoles = extractRoles(resourceMethod);

        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("invalid_authorization_header");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        Integer id = null;

        try {
            id = Integer.valueOf(JwtUtil.validateToken(token));
        } catch (Exception e) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        final String finalID = id.toString();
        containerRequestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> finalID;
            }

            @Override
            public boolean isUserInRole(String s) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return "";
            }
        });

        User user = userRepository.findByID(id);
        for (String role : methodRoles) {
            if (user.getRole().equals(role)) {
                return;
            }
        }
        containerRequestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
    }

    private List<String> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<>();
            } else {
                String[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }
}
