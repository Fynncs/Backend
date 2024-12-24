package br.com.fynncs.filtro;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.service.Security;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.security.Principal;

public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getMethod().equals("OPTIONS")) {
            HttpServletResponse resp = ((HttpServletResponse) servletResponse);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
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
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new NotAuthorizedException("Unauthorized",
                        new Throwable("Uninformed authorization to use the service."), this);
            }

            Authentication authentication = null;
            try {
                Security security = new Security();
                authentication = security.validateToken(authorization);
            } catch (Exception e) {
                throw new NotAuthorizedException("Error verifying token", e);
            }

            if (authentication == null) {
                throw new NotAuthorizedException("Invalid authorization to use the service.");
            }
            modifierRequestContext(servletRequest, authentication);
            filterChain.doFilter(request, servletResponse);
        }
    }

    private void modifierRequestContext(ServletRequest servletRequest,
                                         Authentication authentication) {
        servletRequest.setAttribute("userPrincipal", (Principal) authentication::serializer);
    }
}
