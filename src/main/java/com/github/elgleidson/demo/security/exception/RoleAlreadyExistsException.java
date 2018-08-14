package com.github.elgleidson.demo.security.exception;

public class RoleAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public RoleAlreadyExistsException(String nome) {
		super(nome);
	}
	
}
