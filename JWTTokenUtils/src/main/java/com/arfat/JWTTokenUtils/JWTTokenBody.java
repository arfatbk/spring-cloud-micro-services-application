package com.arfat.JWTTokenUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Arfat Bin Kileb
 * Created at 18-09-2020 03:18 PM
 */
public class JWTTokenBody {

    private Map claims;
    private String userName;
    private List<String> authorities;

    public JWTTokenBody() {

    }

    JWTTokenBody(Map claims) {
        this.claims = claims;
    }

    public void setUserName(String sub) {
        this.userName = sub;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public Object get(String claim) {
        return claims.get(claim);
    }
}
