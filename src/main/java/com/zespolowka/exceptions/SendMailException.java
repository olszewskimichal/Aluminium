package com.zespolowka.exceptions;

public class SendMailException extends RuntimeException {
	public SendMailException(String error) {
		super(String.format("Bład podczas wysłania maila %s", error));
	}

}
