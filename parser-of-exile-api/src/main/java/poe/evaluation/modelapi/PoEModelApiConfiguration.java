package poe.evaluation.modelapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PoEModelApiConfiguration {

    @Bean
    RestClient modelApiRestClient(@Value("${item-evaluator.model-api-url}") String modelApiBaseUrl) {
        return RestClient.create(modelApiBaseUrl);
    }
}
