package org.example.authservice.config;

/**
 * @author ZSZ
 * @date 2026/2/11 12:29
 * @description
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GithubClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public AccessTokenResponse getAccessToken(String code, String clientId,
                                              String clientSecret, String redirectUri) {
        String url = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                url, request, AccessTokenResponse.class);

        return response.getBody();
    }

    public GithubUser getUserInfo(String accessToken) {
        String url = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<GithubUser> response = restTemplate.exchange(
                url, HttpMethod.GET, request, GithubUser.class);

        return response.getBody();
    }

    public List<GithubEmail> getUserEmails(String accessToken) {
        String url = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<List<GithubEmail>> response = restTemplate.exchange(
                url, HttpMethod.GET, request,
                new org.springframework.core.ParameterizedTypeReference<List<GithubEmail>>() {});

        return response.getBody();
    }

    @Data
    public static class AccessTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        private String scope;
    }

    @Data
    public static class GithubUser {
        private Long id;
        private String login;
        private String name;
        private String email;
        @JsonProperty("avatar_url")
        private String avatarUrl;
        private String bio;
        private String location;
        @JsonProperty("html_url")
        private String htmlUrl;
    }

    @Data
    public static class GithubEmail {
        private String email;
        private Boolean verified;
        private Boolean primary;
        private String visibility;
    }
}
