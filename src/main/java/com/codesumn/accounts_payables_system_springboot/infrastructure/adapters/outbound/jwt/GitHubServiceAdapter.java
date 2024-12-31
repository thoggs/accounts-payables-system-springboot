package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.outbound.jwt;

import com.codesumn.accounts_payables_system_springboot.application.config.EnvironConfig;
import com.codesumn.accounts_payables_system_springboot.application.dtos.github.GitHubUserDto;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.GitHubServicePort;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.CustomUnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GitHubServiceAdapter implements GitHubServicePort {

    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final String githubUserPath;
    private final String githubScheme;

    @Autowired
    public GitHubServiceAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = EnvironConfig.GITHUB_API_URL;
        this.githubUserPath = EnvironConfig.GITHUB_USER_PATH;
        this.githubScheme = EnvironConfig.GITHUB_SCHEME;
    }

    @Override
    public GitHubUserDto getGitHubUser(String token) {
        String url = UriComponentsBuilder.newInstance()
                .scheme(githubScheme)
                .host(githubApiUrl)
                .path(githubUserPath)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GitHubUserDto.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomUnauthorizedException("Invalid GitHub token");
            } else {
                throw e;
            }
        }
    }
}