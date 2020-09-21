package com.arfat.JWTTokenUtils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author Arfat Bin Kileb
 * Created at 17-09-2020 10:57 PM
 */
public class JWTParserBuilder {
    private final String token;
    private String signingKey;
    private SignatureAlgorithm signatureAlgorithm;
    private JWTTokenBody body = new JWTTokenBody();

    private JWTParserBuilder(String token) {
        this.token = token;
    }

    public static JWTParserBuilder withToken(String token) {
        Assert.hasText(token, "JWT token can not be null or empty.");
        return new JWTParserBuilder(token);
    }

    public JWTParserBuilder withKey(String key) {
        Assert.hasText(key, "Signing key can not be null or empty.");
        this.signingKey = key;
        return this;
    }

    public JWTParserBuilder withSigningAlgorithm(SignatureAlgorithm algorithm) {
        this.signatureAlgorithm = algorithm;
        return this;
    }

    public JWTTokenBody parse() throws IllegalArgumentException {

        final JwtParser parser = Jwts.parser();
        try {
            Assert.isTrue(parser.isSigned(token), "Invalid signature.");
            final Jwt jwt = parser.setSigningKey(this.signingKey).parse(token);
            validateSignature(jwt);
            getTokenBody(jwt);

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException)
                throw e;
            if(e instanceof ExpiredJwtException)
                throw new IllegalArgumentException("JWT token expired.");
            else throw new IllegalArgumentException("Invalid token.");
        }
        return this.body;
    }

    private void getTokenBody(Jwt jwt) {
        final Map body = (Map) jwt.getBody();

        this.body = new JWTTokenBody(body);
        this.body.setUserName((String) body.get("sub"));
        this.body.setAuthorities((List<String>) body.get("authorities"));
    }

    private void validateSignature(Jwt jwt) {
        final Header header = jwt.getHeader();
        Assert.isTrue(header.get("alg").equals(this.signatureAlgorithm.getValue()),
                "Invalid Signing algorithm. Expected '" + signatureAlgorithm.getValue() + "', provided '" + header.get("alg") + "'");
    }

}
