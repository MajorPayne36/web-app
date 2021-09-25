package org.example.framework.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpFilter;
import org.bouncycastle.util.encoders.Base64;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;


public class BasicAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;

    private String username = "";
    private String password = "";
    private String realm = "Protected";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        username = filterConfig.getInitParameter("username");
//        password = filterConfig.getInitParameter("password");
//        String paramRealm = filterConfig.getInitParameter("realm");
//        if (StringUtils.isNotBlank(paramRealm)) {
//            realm = paramRealm;
//        }
        super.init(filterConfig);
        provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.BASIC_PROVIDER_ATTR));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (!authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }

//        super.doFilter(req, res, chain);

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                try {
                    String credentials = new String(Base64.decode(st.nextToken()), StandardCharsets.UTF_8);
                    try {
                        final var authentication = provider.authenticate(new BasicAuthentication("Basic", credentials));
                        req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
                    } catch (AuthenticationException e) {
                        unauthorized(res, "Invalid authentication");
                    }

                } catch (UnsupportedEncodingException e) {
                    throw new Error("Couldn't retrieve authentication", e);
                }
            }
        } else {
            unauthorized(res);
        }
    }

//    @Override
//    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
//        if (!authenticationIsRequired(req)) {
//            super.doFilter(req, res, chain);
//            return;
//        }
//
//        final var token = req.getHeader("Authorization");
//        if (token == null) {
//            super.doFilter(req, res, chain);
//            return;
//        }
//
//        try {
//            final var authentication = provider.authenticate(new TokenAuthentication(token, null));
//            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
//        } catch (AuthenticationException e) {
//            res.sendError(401);
//            return;
//        }
//
//        super.doFilter(req, res, chain);
//    }

    @Override
    public void destroy() {
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        response.sendError(401, message);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        unauthorized(response, "Unauthorized");
    }

    private boolean authenticationIsRequired(HttpServletRequest req) {
        final var existingAuth = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return AnonymousAuthentication.class.isAssignableFrom(existingAuth.getClass());
    }

}
