package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class UserService {

	private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findUserById(Long id) throws Exception {
        return repository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void validateTransaction(User sender, BigDecimal value) throws Exception {
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Usuário do tipo MERCHANT não pode enviar dinheiro.");
        }
        if (sender.getBalance().compareTo(value) < 0) {
            throw new Exception("Saldo insuficiente.");
        }
    }

    public void saveUser(User user) {
        repository.save(user);
    }

    // Novo método para o Controller
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setfirstName(userDTO.firstName());
        user.setEmail(userDTO.email());
        user.setBalance(userDTO.balance());
        user.setUserType(userDTO.userType());
        return repository.save(user);
    }

    // Novo método para listar todos os usuários
    public List<User> getAllUsers() {
        return repository.findAll();
    }
	
}
	/*
	 * @Autowired private UserRepository repository;
	 * 
	 * public void validateTransaction(User sender, BigDecimal amount) throws
	 * Exception { if (sender.getUserType() == UserType.MERCHANT) { throw new
	 * Exception("Usuário do tipo lojista não está autorizado a realizar transação."
	 * ); }
	 * 
	 * if (sender.getBalance().compareTo(amount) < 0) { throw new
	 * Exception("Saldo insuficiente"); } }
	 * 
	 * public User findUserById(Long id) throws Exception { return
	 * this.repository.findUserById(id).orElseThrow(() -> new
	 * Exception("Usuário não encontrado")); }
	 * 
	 * public User createUser(UserDTO data) { User newUser = new User(data);
	 * this.saveUser(newUser); return newUser; }
	 * 
	 * public List<User> getAllUsers(){ return this.repository.findAll(); }
	 * 
	 * public void saveUser(User user) { this.repository.save(user); }
	 */

