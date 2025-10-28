package com.xpto.financeiro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Erro de regra de negócio
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
