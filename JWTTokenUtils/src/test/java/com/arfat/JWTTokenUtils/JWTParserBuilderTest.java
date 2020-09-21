package com.arfat.JWTTokenUtils;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Arfat Bin Kileb
 * Created at 17-09-2020 11:01 PM
 */
class JWTParserBuilderTest {

    final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4eXoiLCJhdWQiOiJjdXN0b21lci1zZXJ2aWNlIiwiZXhwIjoxNjAwNzU3NzU4LCJhdXRob3JpdGllcyI6WyJBRE1JTiJdfQ._kkrWO9TEmVIy46QGinJjd5bHJMLHqiXplZXZ5nP8FA";
    private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4eXoiLCJhdWQiOiJjdXN0b21lci1zZXJ2aWNlIiwiZXhwIjoxNjAwNzA0OTg1LCJhdXRob3JpdGllcyI6WyJBRE1JTiJdfQ.TTsUMn-SCboFpGwYCrTDywmIKUIIO5aFnNtsrEbgTZg";
    final String PRIVATE_KEY = "ABCD1234567890";
    private String subject = "xyz";
    private List authorities = Arrays.asList("ADMIN");
    Map claims = new HashMap();

    @BeforeEach
    void init() {
        claims.put("aud", "customer-service");
        claims.put("sub", "xyz");
    }

    @Test
    @DisplayName("Should create JWT Parser Builder with token")
    void shouldCreateJwtParserBuilderWithToken() {
        assertThrows(IllegalArgumentException.class, () -> JWTParserBuilder.withToken(null));
        JWTParserBuilder parser = JWTParserBuilder.withToken("token");
        assertNotNull(parser);
    }

    @Test
    @DisplayName("Should create Parser with Token and key")
    void shouldCreateParserWithTokenAndKey() {
        assertThrows(IllegalArgumentException.class,
                () -> JWTParserBuilder.withToken(VALID_TOKEN).withKey(null));

        JWTParserBuilder parser = JWTParserBuilder
                .withToken(VALID_TOKEN)
                .withKey(PRIVATE_KEY);
        assertNotNull(parser);
    }

    @Test
    @DisplayName("Should create Parser with Token and key and algorithm")
    void shouldCreateParserWithTokenAndKeyAndAlgorithm() {
        JWTParserBuilder parser = JWTParserBuilder.withToken(VALID_TOKEN)
                .withKey(PRIVATE_KEY)
                .withSigningAlgorithm(SignatureAlgorithm.HS256);
        assertNotNull(parser);
    }

    @Test
    @DisplayName("Should throw Invalid Token exception for invalid signing algo")
    void shouldThrowInvalidTokenExceptionForInvalidAlgorithm() {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> JWTParserBuilder.withToken(VALID_TOKEN)
                        .withKey(PRIVATE_KEY)
                        .withSigningAlgorithm(SignatureAlgorithm.RS512)
                        .parse());
        assertThat(exception.getMessage(), is("Invalid Signing algorithm. Expected 'RS512', provided 'HS256'"));
    }

    @Test
    @DisplayName("Should throw Token Expired exception")
    void shouldThrowTokeExpiredException() {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> JWTParserBuilder.withToken(EXPIRED_TOKEN)
                        .withKey(PRIVATE_KEY)
                        .withSigningAlgorithm(SignatureAlgorithm.HS256)
                        .parse());
        assertThat(exception.getMessage(), is("JWT token expired."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wwqdwdq.qwqwqwe.qwqwqwqw","qwqweqwe.qweqweqw.sdffwer"})
    @DisplayName("Should throw invalid token exception for malformed token")
    void shouldThrowInvalidTokenExceptionForMalformedToken(String invalidToken) {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> JWTParserBuilder.withToken(invalidToken)
                        .withKey(PRIVATE_KEY)
                        .withSigningAlgorithm(SignatureAlgorithm.HS256)
                        .parse());
        assertThat(exception.getMessage(), is("Invalid token."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sfdscsdcsdcsd","123456789"})
    @DisplayName("Should throw invalid signature exception for malformed signature")
    void shouldThrowInvalidSignatureExceptionForMalformedSignature(String invalidToken) {
        final Exception exception = assertThrows(IllegalArgumentException.class,
                () -> JWTParserBuilder.withToken(invalidToken)
                        .withKey(PRIVATE_KEY)
                        .withSigningAlgorithm(SignatureAlgorithm.HS256)
                        .parse());
        assertThat(exception.getMessage(), is("Invalid signature."));
    }

    @Test
    @DisplayName("Should return token body")
    void shouldReturnTokenBody() {

        JWTTokenBody body = JWTParserBuilder.withToken(VALID_TOKEN)
                .withKey(PRIVATE_KEY)
                .withSigningAlgorithm(SignatureAlgorithm.HS256)
                .parse();
        assertNotNull(body);
        assertThat(body.getUserName(), is(subject));
        assertThat(body.getAuthorities(), is(authorities));

        claims.forEach((o, o2) ->
                assertThat(body.get((String) o), is(o2)));
    }
}