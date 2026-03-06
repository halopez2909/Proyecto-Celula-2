package com.reto.order.client;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.reto.order.dto.StockResponse;
@Component
public class CatalogClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String CATALOG_URL = "http://localhost:8082/products/check-stock";
    public StockResponse checkStock(Long productId, Integer quantity, String correlationId) {
        System.out.println("[DEV 5 - LOG] Llamando a Catalog - Producto: " + productId + " | CorrelationId: " + correlationId);
        String url = UriComponentsBuilder.fromHttpUrl(CATALOG_URL)
                .queryParam("productId", productId)
                .queryParam("quantity", quantity)
                .toUriString();
        try {
            return restTemplate.getForObject(url, StockResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error de comunicacion con el servicio de Catalogo");
        }
    }
}