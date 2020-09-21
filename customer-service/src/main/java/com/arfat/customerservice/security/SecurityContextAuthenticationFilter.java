package com.arfat.customerservice.security;

import com.arfat.JWTTokenUtils.JWTParserBuilder;
import com.arfat.JWTTokenUtils.JWTTokenBody;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Arfat Bin Kileb
 * Created at 12-09-2020 12:34 PM
 */
@Service
public class SecurityContextAuthenticationFilter extends OncePerRequestFilter {

    private boolean shouldContinue = true;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String authorization = request.getHeader("Authorization");
        String username = null;
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        if (authorization != null && authorization.startsWith("Bearer")) {
            final String token = authorization.substring(7);

            try {

                String PRIVATE_KEY = "ABCD1234567890";
                JWTTokenBody body = JWTParserBuilder.withToken(token)
                        .withKey(PRIVATE_KEY)
                        .withSigningAlgorithm(SignatureAlgorithm.HS256)
                        .parse();

                username = body.getUserName();
                authorities = body.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                shouldContinue = true;
            } catch (Exception e) {
                shouldContinue = false;
                response.setStatus(401);
                response.setContentType("application/json");
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.println("{");
                outputStream.println("\"timestamp\": \"" + LocalDateTime.now() + "\",");
                outputStream.println("\"status\": 401,");
                outputStream.println("\"error\": \"Forbidden\",");
                if (e instanceof IllegalArgumentException)
                    outputStream.println("\"message\": \""+e.getMessage()+"\",");
                else outputStream.println("\"message\": \"Access Denied\",");
                outputStream.println("\"path\": \"/\"");
                outputStream.println("}");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = new User(username, "null", authorities);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        if (shouldContinue)
            chain.doFilter(request, response);
    }
}
