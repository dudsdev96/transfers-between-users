package com.picpaysimplificado.services;


import com.picpaysimplificado.dtos.EmailRequestDTO;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

	    private final String sendGridApiKey = System.getenv("SENDGRID_API_KEY");
	    
	    @Value("${sendgrid.from}")
	    private String fromEmail; // Spring injeta aqui o valor que está no application.properties


	    public void enviarEmail(EmailRequestDTO dto) {
	        Email from = new Email(fromEmail);
	        String subject = dto.getAssunto();
	        Email to = new Email(dto.getDestinatario());
	        Content content = new Content("text/plain", dto.getMensagem());

	        Mail mail = new Mail(from, subject, to, content);

	        SendGrid sg = new SendGrid(sendGridApiKey);
	        Request request = new Request();

	        try {
	            request.setMethod(Method.POST);
	            request.setEndpoint("mail/send");
	            request.setBody(mail.build());
	            Response response = sg.api(request);

	            System.out.println("Status Code: " + response.getStatusCode());
	            System.out.println("Body: " + response.getBody());
	            System.out.println("Headers: " + response.getHeaders());

	        } catch (IOException ex) {
	            throw new RuntimeException("Erro ao enviar e-mail", ex);
	        }
	    }
}

