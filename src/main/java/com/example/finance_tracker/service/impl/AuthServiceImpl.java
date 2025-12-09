package com.example.finance_tracker.service.impl;

import com.example.finance_tracker.form.CredentialsForm;
import com.example.finance_tracker.mapper.UserMapper;
import com.example.finance_tracker.model.User;
import com.example.finance_tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * @param credentials
     * @return
     */
    @Override
    public User auth(CredentialsForm credentials) throws CredentialNotFoundException {

        User user = userMapper.findByEmail(credentials.getEmail());
        if (user == null)
            throw new CredentialNotFoundException("Username or password is not correct");

        boolean isValid = passwordEncoder.matches(
                credentials.getPassword(),
                user.getPassword()
        );
        
        if (!isValid)
            throw new CredentialNotFoundException("Username or password is not correct");

        return user;
    }
}
