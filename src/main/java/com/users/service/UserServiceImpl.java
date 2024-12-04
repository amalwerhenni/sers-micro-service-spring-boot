package com.users.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.users.entity.Roles;
import com.users.entity.User;
import com.users.entity.VerificationToken;
import com.users.exception.EmailAlreadyExistsException;
import com.users.exception.ExpiredTokenException;
import com.users.exception.InvalidTokenException;
import com.users.register.RegistationRequest;
import com.users.repos.RoleRepository;
import com.users.repos.UsersRepository;
import com.users.repos.VerificationTokenRepository;
import com.users.util.EmailSender;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepo;

    @Autowired
    EmailSender emailSender;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public User saveUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return this.usersRepository.save(user);
    }

    @Override
    public User findUserByUsername (String username){
        return this.usersRepository.findByUsername(username);
    }
    @Override
    public Roles addRole(Roles role){
        return this.roleRepository.save(role);
    }
    @Override
    public User addRoleToUser(String username, String rolename){
        User user = this.usersRepository.findByUsername(username);
        Roles role = this.roleRepository.findByRole(rolename);
        user.getRoles().add(role);
        this.usersRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return this.usersRepository.findAll();
    }


    @Override
    public User registerUser(RegistationRequest request) {
        Optional<User> optionaluser = this.usersRepository.findByEmail(request.getEmail());
        if(optionaluser.isPresent())
            throw new EmailAlreadyExistsException("email déjà existant!");
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());

        newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);
        this.usersRepository.save(newUser);
        //ajouter à newUser le role par défaut USER
        Roles r = this.roleRepository.findByRole("USER");
        List<Roles> roles = new ArrayList<>();
        roles.add(r);
        newUser.setRoles(roles);
        this.usersRepository.save(newUser);
        //génére le code secret
        String code = this.generateCode();

        VerificationToken token = new VerificationToken(code, newUser);
        this.verificationTokenRepo.save(token);

        sendEmailUser(newUser,token.getToken());


        return newUser;
    }

    public String generateCode() {
        Random random = new Random();
        Integer code = 100000 + random.nextInt(900000);

        return code.toString();
    }

    @Override
    public void sendEmailUser(User u, String code) {
        String emailBody ="Bonjour "+ "<h1>"+u.getUsername() +"</h1>" +
        " Votre code de validation est "+"<h1>"+code+"</h1>";
       emailSender.sendEmail(u.getEmail(), emailBody);
    }


    @Override
    public User validateToken(String code) {
        VerificationToken token = verificationTokenRepo.findByToken(code);
        if(token == null){
            throw new InvalidTokenException("Invalid Token");
        }

        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepo.delete(token);
            throw new ExpiredTokenException("expired Token");
        }
        user.setEnabled(true);
        this.usersRepository.save(user);
        return user;
    }
       

    
}
