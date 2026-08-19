package com.victor.auth.services;
import com.victor.auth.dto.LoginRequest;
import com.victor.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}

