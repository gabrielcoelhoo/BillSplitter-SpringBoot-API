package com.example.model;

//I changed price for string which I will parse to make mathematical operations
//only for test, later I will return it to double

public class Expense {
	private String username;
	private String trip;
	private String description;
	private String price;
	
	public Expense(String username, String trip, String description,String string) {
		super();
		this.username = username;
		this.trip = trip;
		this.description = description;
		this.price = string;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTrip() {
		return trip;
	}

	public void setTrip(String trip) {
		this.trip = trip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	

	@Override
	public String toString() {
		return "Expense [username=" + username + ", trip=" + trip + ", description=" + description + ", price=" + price
				+ "]";
	}
	
	
	
	

}
