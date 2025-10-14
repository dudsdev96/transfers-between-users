package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;


@Service
public class AuthorizationService {

	 private final RestTemplate restTemplate = new RestTemplate();

	    public boolean authorizeTransaction() {
	        String url = "https://util.devi.tools/api/v2/authorize";
	        try {
	            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
	            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
	                Object authorized = response.getBody().get("authorized");
	                return authorized != null && authorized.toString().equalsIgnoreCase("true");
	            }
	        } catch (Exception e) {
	            System.out.println("Erro ao chamar serviço autorizador: " + e.getMessage());
	        }
	        return false;
	    }
	
		
	
	/*
	 * @Autowired private RestTemplate restTemplate;
	 * 
	 * public boolean authorizeTransaction(User sender, BigDecimal value) {
	 * ResponseEntity<Map> authorizationResponse =
	 * restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize",
	 * Map.class);
	 * 
	 * if (authorizationResponse.getStatusCode() == HttpStatus.OK) { Map<String,
	 * Object> body = authorizationResponse.getBody();
	 * 
	 * if (body != null && body.containsKey("data")) { Map<String, Object> data =
	 * (Map<String, Object>) body.get("data"); Object authValue =
	 * data.get("authorization"); return authValue instanceof Boolean && (Boolean)
	 * authValue; } } return false; }
	 */
}
		
	

