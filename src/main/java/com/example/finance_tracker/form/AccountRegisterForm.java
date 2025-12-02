package com.example.finance_tracker.form;

import lombok.Data;

@Data
public class AccountRegisterForm {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
}
