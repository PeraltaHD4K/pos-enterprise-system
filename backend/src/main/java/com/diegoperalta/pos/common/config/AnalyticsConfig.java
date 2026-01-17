package com.diegoperalta.pos.common.config;

import java.net.Proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AnalyticsConfig {

    @Bean("analyticsRestClient")
    public RestClient analyticsRestClient(@Value("${app.services.analytics-key}") String apiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000); // 1 segundo
        factory.setReadTimeout(3000); // 3 segundos
        factory.setProxy(Proxy.NO_PROXY);

        // ✅ Usamos el método estático para obtener una instancia nueva del builder
        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader("X-INTERNAL-API-KEY", apiKey)
                .build();
    }

}
