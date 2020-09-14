package com.arfat.customerservice.security;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arfat Bin Kileb
 * Created at 12-09-2020 04:25 PM
 */
public class JwtUtils {

    private final String PRIVATE_KEY = "ABCD1234567890";
    private TokenBody body = new TokenBody();
    @Value("${spring.application.name:customer1-service}")
    String APPLICATION_NAME = "customer-service";

    public JwtUtils() {

    }

    public TokenBody getBody() {
        return body;
    }

    public JwtUtils parse(String token) throws IllegalArgumentException {
        final JwtParser parser = Jwts.parser();
        try {
            if (parser.isSigned(token)) {
                final Header header = parser.setSigningKey(PRIVATE_KEY).parse(token).getHeader();
                final Object alg = header.get("alg");
                Assert.isTrue(alg.equals("HS256"), "Unsupported Algorithm");

                final Map body = (Map) parser.setSigningKey(PRIVATE_KEY)
                        .parse(token)
                        .getBody();
                if (!body.get("aud").equals(APPLICATION_NAME))
                    throw new IllegalArgumentException("Invalid Audience");

                this.body.setUserName((String) body.get("sub"));
                final List<String> authorities = (List<String>) body.get("authorities");
                this.body.setAuthorities(authorities);

            } else throw new IllegalArgumentException("Invalid Token");
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException)
                throw new IllegalArgumentException(e.getMessage());
            throw new IllegalArgumentException("Invalid Token");
        }
        return this;
    }

    class TokenBody {
        private String userName;
        private List authorities;

        TokenBody() {

        }

        public void setUserName(String sub) {
            this.userName = sub;
        }

        public String getUsername() {
            return userName;
        }

        public void setAuthorities(List<String> authorities) {
            this.authorities = authorities.stream().map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList());
        }

        public List getAuthorities() {
            return authorities;
        }
    }
}
