package team.foodlog.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import team.foodlog.backend.properties.UsdaProperties;

@Configuration
@RequiredArgsConstructor
public class ProviderConfig {

    public static final String USDA_REST_CLIENT_BEAN_NAME = "usdaRestClient";

    private final UsdaProperties usdaProperties;

    @Bean(name = USDA_REST_CLIENT_BEAN_NAME)
    public RestClient usdaRestClient(
            RestClient.Builder builder
    ) {
        return builder.baseUrl(usdaProperties.baseUrl()).build();
    }

}
