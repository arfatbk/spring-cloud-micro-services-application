package com.arfat.OAuthServer.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author Arfat Bin Kileb
 * Created at 10-09-2020 03:46 PM
 */
@Service
public class JwtTokenGenerator {
    //TODO: Change Algo to RSA, set public private key pairs
    private final String PRIVATE_KEY = "ABCD1234567890";
    private final long TOKEN_EXPIRATION_IN_SECONDS = 2 * 60;

    public String generate(Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject((String)claims.remove("user_name"))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256,this.PRIVATE_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + this.TOKEN_EXPIRATION_IN_SECONDS * 1000))
                .compact();
    }
}
