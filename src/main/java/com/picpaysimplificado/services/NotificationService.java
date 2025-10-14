package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.EmailRequestDTO;
import com.picpaysimplificado.dtos.EmailRequestDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service

public class NotificationService {
	
	private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    // Novo método que aceita DTO
    public void notifyByEmail(EmailRequestDTO dto) throws Exception {
        // 1️) Envia email usando EmailService
        emailService.enviarEmail(dto);

        // 2️) Envia notificação externa (mock)
        String url = "https://util.devi.tools/api/v1/notify";
        Map<String, String> request = new HashMap<>();
        request.put("email", dto.getDestinatario());
        request.put("message", dto.getMensagem());

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Falha ao enviar notificação externa");
        }

        System.out.println("Notificação enviada para: " + dto.getDestinatario());
    }

    // Mantém o método antigo se ainda quiser enviar notificações por User
    public void sendNotification(User user, String message) throws Exception {
        String url = "https://util.devi.tools/api/v1/notify";
        Map<String, String> request = new HashMap<>();
        request.put("email", user.getEmail());
        request.put("message", message);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Falha ao enviar notificação externa");
        }

        System.out.println("Notificação enviada para: " + user.getEmail());
    }
}
	
	
	
	
	/*
	 * @Autowired private RestTemplate restTemplate;
	 * 
	 * 
	 * private final EmailService emailService;
	 * 
	 * public NotificationService(EmailService emailService) { this.emailService =
	 * emailService; }
	 * 
	 * 
	 * public void notificarPorEmail(EmailRequestDTO dto) throws Exception {
	 * 
	 * emailService.enviarEmail(dto); //delega para o email service
	 * 
	 * 
	 * System.out.println("Notificacao enviada para o usuario"); }
	 */


