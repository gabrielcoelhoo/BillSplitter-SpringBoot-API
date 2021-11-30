package com.example.dto;

public class AnswerRequest {
	
	private String message;
	private int status;
	private Double value;
	
	public AnswerRequest(String message, int status, Double value) {
		this.message = message;
		this.status = status;
		this.value = value;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	

}
