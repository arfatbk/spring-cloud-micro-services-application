package com.arfat.OAuthServer.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Arfat Bin Kileb
 * Created at 10-09-2020 03:13 PM
 */
@Service
public class JwtTokenConverter implements AccessTokenConverter {

    @Autowired
    HttpServletRequest request;

    private UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();
    private String scopeAttribute = "scope";
    private String clientIdAttribute = "client_id";
    private boolean includeGrantType;

    private JwtTokenGenerator jwtTokenGenerator;

    JwtTokenConverter(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    public void setUserTokenConverter(UserAuthenticationConverter userTokenConverter) {
        this.userTokenConverter = userTokenConverter;
    }

    public void setScopeAttribute(String scopeAttribute) {
        this.scopeAttribute = scopeAttribute;
    }

    public void setClientIdAttribute(String clientIdAttribute) {
        this.clientIdAttribute = clientIdAttribute;
    }

    public void setIncludeGrantType(boolean includeGrantType) {
        this.includeGrantType = includeGrantType;
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {


        Map<String, Object> response = new HashMap();
        Map<String, Object> claims = new HashMap();

        final String resource = request.getParameter("resource");
        if (resource == null || resource.equals("null")) {
            throw new InvalidTokenException("resource can not be null");
        } else {
            claims.put("aud", resource);
        }


        if (token.getScope() != null) {
            claims.put(this.scopeAttribute, token.getScope());
        }

        OAuth2Request clientToken = authentication.getOAuth2Request();

        claims.put(this.clientIdAttribute, clientToken.getClientId());
        if (!authentication.isClientOnly()) {
            claims.putAll(this.userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
        } else if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty()) {
            claims.put("authorities", AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));
        }

        if (this.includeGrantType && authentication.getOAuth2Request().getGrantType() != null) {
            claims.put("grant_type", authentication.getOAuth2Request().getGrantType());
        }

        claims.putAll(token.getAdditionalInformation());
        claims.put(this.clientIdAttribute, clientToken.getClientId());
        String jwtToken = jwtTokenGenerator.generate(claims);
        response.put("jwt", jwtToken);
        return response;
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);
        Map<String, Object> info = new HashMap(map);
        info.remove("exp");
        info.remove("aud");
        info.remove(this.clientIdAttribute);
        info.remove(this.scopeAttribute);
        if (map.containsKey("exp")) {
            token.setExpiration(new Date((Long)map.get("exp") * 1000L));
        }

        if (map.containsKey("jti")) {
            info.put("jti", map.get("jti"));
        }

        token.setScope(this.extractScope(map));
        token.setAdditionalInformation(info);
        return token;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap();
        Set<String> scope = this.extractScope(map);
        Authentication user = this.userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(this.clientIdAttribute);
        parameters.put(this.clientIdAttribute, clientId);
        if (this.includeGrantType && map.containsKey("grant_type")) {
            parameters.put("grant_type", (String) map.get("grant_type"));
        }

        Set<String> resourceIds = new LinkedHashSet((Collection) (map.containsKey("aud") ? this.getAudience(map) : Collections.emptySet()));
        Collection<? extends GrantedAuthority> authorities = null;
        if (user == null && map.containsKey("authorities")) {
            String[] roles = (String[]) ((Collection) map.get("authorities")).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(roles);
        }

        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, (String) null, (Set) null, (Map) null);
        return new OAuth2Authentication(request, user);
    }

    private Collection<String> getAudience(Map<String, ?> map) {
        Object auds = map.get("aud");
        if (auds instanceof Collection) {
            Collection<String> result = (Collection) auds;
            return result;
        } else {
            return Collections.singleton((String) auds);
        }
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        if (map.containsKey(this.scopeAttribute)) {
            Object scopeObj = map.get(this.scopeAttribute);
            if (String.class.isInstance(scopeObj)) {
                scope = new LinkedHashSet(Arrays.asList(((String) String.class.cast(scopeObj)).split(" ")));
            } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
                Collection<String> scopeColl = (Collection) scopeObj;
                scope = new LinkedHashSet(scopeColl);
            }
        }

        return (Set) scope;
    }

}
