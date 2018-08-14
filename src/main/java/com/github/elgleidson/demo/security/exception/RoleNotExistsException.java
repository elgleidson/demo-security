package com.github.elgleidson.demo.security.exception;

public class RoleNotExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public RoleNotExistsException(String nome) {
		super(nome);
	}
	
}
