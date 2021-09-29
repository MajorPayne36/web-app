package org.example.framework.security;

public interface BasicAuthenticationProvider {
    Authentication baseAuthenticate(Authentication authentication) throws AuthenticationException;

}
