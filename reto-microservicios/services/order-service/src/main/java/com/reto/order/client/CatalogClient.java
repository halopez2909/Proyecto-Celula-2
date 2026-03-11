package com.reto.order.client;
 
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.reto.order.dto.StockResponse;
 
@Component
public class CatalogClient {
    // Usamos RestTemplate como pide el Sprint
    private final RestTemplate restTemplate = new RestTemplate();
   
    // URL del microservicio de Jhon (Catalog)
    private final String CATALOG_URL = "http://localhost:8083/catalog/check-stock";
 
    public StockResponse checkStock(Long productId, Integer quantity, String correlationId) {
        // Log mostrando la llamada REST con el Correlation ID
        System.out.println("[DEV 5 - LOG] Llamando a Catalog - Producto: " + productId + " | CorrelationId: " + correlationId);
 
        // Construimos la URL con los parámetros productId y quantity
        String url = UriComponentsBuilder.fromHttpUrl(CATALOG_URL)
                .queryParam("productId", productId)
                .queryParam("quantity", quantity)
                .toUriString();
 
        try {
            // en esto llamaos al GET
            return restTemplate.getForObject(url, StockResponse.class);
        } catch (RestClientException e) {
            // es para que lanze error para que devuelva 500
            throw new RuntimeException("Error de comunicación con el servicio de Catálogo");
        }
    }
}