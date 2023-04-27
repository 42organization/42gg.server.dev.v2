package com.gg.server.global.security.info;

import java.util.Map;

public abstract class OAuthUserInfo {
    protected Map<String, Object> attributes;

    public OAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getIntraId();

    public abstract String getEmail();

    public abstract String getImageUrl();
}