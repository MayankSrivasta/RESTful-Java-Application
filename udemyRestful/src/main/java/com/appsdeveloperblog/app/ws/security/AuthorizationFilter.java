package com.appsdeveloperblog.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    
    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
     }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        
        String header = req.getHeader(SecurityConstants.HEADER_STRING);
        
        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }
        
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }   
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        
        if (token != null) {
            
        	//the below line is written to remove the "Bearer" from the JWT 
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");
            
            //it parses the JWT token, decrypt it and get the UserDetails from the TOKEN 
            //so we just have to provide the "token" which is written and then "SecurityConstants.TOKEN_SECRET"
            //so once the JWT token is generate it one secret it can't be decrypted with another secret
            String user = Jwts.parser()
                    .setSigningKey( SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            
            return null;
        }
        
        return null;
    } 
    
}
 