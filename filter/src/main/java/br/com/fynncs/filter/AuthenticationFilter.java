package br.com.fynncs.filter;

import br.com.fynncs.filter.exception.MapperGenericException;
import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.service.Security;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;

@Component
public class AuthenticationFilter implements Filter {

    private final Security security;

    public AuthenticationFilter(Security security) {
        this.security = security;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (request.getMethod().equals("OPTIONS")) {
                HttpServletResponse resp = ((HttpServletResponse) servletResponse);
                resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else if (request.getMethod() != null && (request.getMethod().equals("GET")
                    || request.getMethod().equals("POST")
                    || request.getMethod().equals("PUT")
                    || request.getMethod().equals("DELETE"))) {
                String origin = request.getHeader("Origin");
                ((HttpServletResponse) servletResponse).addHeader(
                        "Access-Control-Allow-Origin", "*");
                ((HttpServletResponse) servletResponse).addHeader(
                        "Access-Control-Allow-Credentials", "true");
                ((HttpServletResponse) servletResponse).addHeader(
                        "Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization");
                ((HttpServletResponse) servletResponse).addHeader(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                HttpServletResponse resp = (HttpServletResponse) servletResponse;

                String authorization = ((HttpServletRequest) servletRequest).getHeader(HttpHeaders.AUTHORIZATION);
                if (!request.getRequestURL().toString().contains("login")) {
                    if (authorization == null || !authorization.startsWith("Bearer ")) {
                        throw new NotAuthorizedException("Unauthorized",
                                new Throwable("Uninformed authorization to use the service."), this);
                    }
                    Authentication authentication = getAuthentication(authorization);
                    modifierRequestContext(servletRequest, authentication);
                }
                filterChain.doFilter(request, servletResponse);
            }
        } catch (Exception ex) {
            MapperGenericException.errorResponse((HttpServletResponse) servletResponse, ex);
        }
    }

    private Authentication getAuthentication(String authorization) {
        Authentication authentication = null;
        try {
            authentication = security.validateToken(authorization);
        } catch (Exception e) {
            throw new NotAuthorizedException("Error verifying token", e);
        }

        if (authentication == null) {
            throw new NotAuthorizedException("Invalid authorization to use the service.");
        }
        return authentication;
    }

    private void modifierRequestContext(ServletRequest servletRequest,
                                        Authentication authentication) {
        servletRequest.setAttribute("userPrincipal", (Principal) authentication::serializer);
    }
}
