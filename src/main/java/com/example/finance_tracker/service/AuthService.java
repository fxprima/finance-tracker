package com.example.finance_tracker.service;

import com.example.finance_tracker.form.CredentialsForm;
import com.example.finance_tracker.model.User;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;

@Service
public interface AuthService {

    public User auth (CredentialsForm credentials) throws CredentialNotFoundException;

}
