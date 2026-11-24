package team.foodlog.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import team.foodlog.backend.properties.GroqProperties;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AiConfig {

    public static final String GROQ_REST_CLIENT_BEAN_NAME = "groqRestClient";

    private final GroqProperties groqProperties;

    @Bean(name = GROQ_REST_CLIENT_BEAN_NAME)
    RestClient groqRestClient(RestClient.Builder builder) {
        log.info("Groq api key length: " + groqProperties.apiKey().length());
        return builder
                .baseUrl(groqProperties.baseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + groqProperties.apiKey()
                ).build();
    }
}
