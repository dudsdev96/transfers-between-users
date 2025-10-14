package com.picpaysimplificado.services;


import com.picpaysimplificado.repositories.TransactionRepository;
import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.TransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
public class TransactionService {

	 private final UserService userService;
	    private final TransactionRepository repository;
	    private final AuthorizationService authorizationService;
	    private final NotificationService notificationService;

	    public TransactionService(UserService userService, TransactionRepository repository, AuthorizationService authorizationService,
 NotificationService notificationService) {
	    	
	        this.userService = userService;
	        this.repository = repository;
	        this.authorizationService = authorizationService;
	        this.notificationService = notificationService;
	    }

	    @Transactional // garante rollback em caso de erro
	    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {

	        // 1️) Busca usuários
	        User sender = userService.findUserById(transactionDTO.senderId());
	        User receiver = userService.findUserById(transactionDTO.receiverId());

	        // 2️) Valida tipo de usuário e saldo
	        userService.validateTransaction(sender, transactionDTO.value());

	        // 3️) Consulta serviço externo autorizador
	        boolean isAuthorized = authorizationService.authorizeTransaction();
	        if (!isAuthorized) {
	            throw new Exception("Transação não autorizada pelo serviço externo");
	        }

	        // 4️) Realiza movimentação de valores
	        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
	        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

	        // 5️) Cria e salva a transação
	        Transaction transaction = new Transaction();
	        transaction.setAmount(transactionDTO.value());
	        transaction.setSender(sender);
	        transaction.setReceiver(receiver);
	        transaction.setTimestamp(LocalDateTime.now());
	        repository.save(transaction);

	        // 6️) Atualiza saldos
	        userService.saveUser(sender);
	        userService.saveUser(receiver);

	        // 7️) Notifica usuários (mock POST)
	        try {
	            notificationService.sendNotification(receiver, "Você recebeu um pagamento!");
	            notificationService.sendNotification(sender, "Pagamento realizado com sucesso!");
	        } catch (Exception e) {
	            System.out.println("Falha ao enviar notificação: " + e.getMessage());
	            // Não cancela a transação, apenas Loga(log)
	        }

	        return transaction;
	    }
	
	
	/*
	 * @Autowired private UserService userService;
	 * 
	 * @Autowired private TransactionRepository repository;
	 * 
	 * @Autowired private AuthorizationService authService;
	 * 
	 * @Autowired private NotificationService notificationService;
	 * 
	 * public Transaction createTransaction(TransactionDTO transaction) throws
	 * Exception {
	 * 
	 * User sender = this.userService.findUserById(transaction.senderId()); User
	 * receiver = this.userService.findUserById(transaction.receiverId());
	 * 
	 * userService.validateTransaction(sender, transaction.value());
	 * 
	 * boolean isAuthorized = this.authService.authorizeTransaction(sender,
	 * transaction.value()); if(!isAuthorized) { throw new
	 * Exception("Transação não autorizada"); }
	 * 
	 * Transaction newTransaction = new Transaction();
	 * newTransaction.setAmount(transaction.value());
	 * newTransaction.setSender(sender); newTransaction.setReceiver(receiver);
	 * newTransaction.setTimestamp(LocalDateTime.now());
	 * 
	 * sender.setBalance(sender.getBalance().subtract(transaction.value()));
	 * receiver.setBalance(receiver.getBalance().add(transaction.value()));
	 * 
	 * this.repository.save(newTransaction); this.userService.saveUser(sender);
	 * this.userService.saveUser(receiver);
	 * 
	 * this.notificationService.sendNotification(sender,
	 * "Transação realizada com sucesso");
	 * this.notificationService.sendNotification(receiver,
	 * "Transação recebida com sucesso"); return newTransaction; }
	 */
	
}
