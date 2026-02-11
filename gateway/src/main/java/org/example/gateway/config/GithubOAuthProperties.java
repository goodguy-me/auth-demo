//package org.example.gateway.config;
//
///**
// * @author ZSZ
// * @date 2026/2/11 12:28
// * @description
// */
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "github.oauth")
//public class GithubOAuthProperties {
//
//    private String clientId;
//    private String clientSecret;
//    private String redirectUri;
//    private String authorizeUrl = "https://github.com/login/oauth/authorize";
//    private String tokenUrl = "https://github.com/login/oauth/access_token";
//    private String userInfoUrl = "https://api.github.com/user";
//    private String userEmailsUrl = "https://api.github.com/user/emails";
//
//    // getters and setters
//    public String getClientId() { return clientId; }
//    public void setClientId(String clientId) { this.clientId = clientId; }
//    public String getClientSecret() { return clientSecret; }
//    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
//    public String getRedirectUri() { return redirectUri; }
//    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
//    public String getAuthorizeUrl() { return authorizeUrl; }
//    public void setAuthorizeUrl(String authorizeUrl) { this.authorizeUrl = authorizeUrl; }
//    public String getTokenUrl() { return tokenUrl; }
//    public void setTokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; }
//    public String getUserInfoUrl() { return userInfoUrl; }
//    public void setUserInfoUrl(String userInfoUrl) { this.userInfoUrl = userInfoUrl; }
//    public String getUserEmailsUrl() { return userEmailsUrl; }
//    public void setUserEmailsUrl(String userEmailsUrl) { this.userEmailsUrl = userEmailsUrl; }
//}
