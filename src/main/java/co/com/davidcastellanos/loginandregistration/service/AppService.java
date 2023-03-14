package co.com.davidcastellanos.loginandregistration.service;

import co.com.davidcastellanos.loginandregistration.model.LoginUser;
import co.com.davidcastellanos.loginandregistration.model.User;
import co.com.davidcastellanos.loginandregistration.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class AppService {
    @Autowired
    private UserRepository userRepository;

    //función que registra un usuario
    public User registerUser(User nuevoUser, BindingResult result) {
        //verificar si el password y su confirmación existen
        if (!nuevoUser.getPassword().equals(nuevoUser.getConfirmPassword())) {
            result.rejectValue("password", "Matches", "Las contraseñas no coinciden");

        }

        //revisar si el mail ya existe
        String newEmail = nuevoUser.getEmail();
        if (userRepository.findByEmail(newEmail).isPresent()) {
            result.rejectValue("email", "Unique", "El email fue ingresado previamente");
        }

        if (result.hasErrors()) {
            return null;
        } else {
            // Encriptar la contraseña
            String passWordEncrypt = BCrypt.hashpw(nuevoUser.getPassword(), BCrypt.gensalt());
            nuevoUser.setPassword(passWordEncrypt);
            return userRepository.save(nuevoUser);
        }
    }

    public User loginUser(LoginUser newLogin, BindingResult result) {

        Optional<User> posibleUsuario = userRepository.findByEmail(newLogin.getEmail());

        if (!posibleUsuario.isPresent()) {
            result.rejectValue("email", "Unique", "Correo NO registrado");
            return null;
        }

        User userLogin = posibleUsuario.get();
        if (!BCrypt.checkpw(newLogin.getPassword(), userLogin.getPassword())){
            result.rejectValue("password", "Matches", "La contraseña ingresada no coincide");
        }

        if (result.hasErrors()) {
            return null;
        } else {
            return userLogin;
        }

    }
}
