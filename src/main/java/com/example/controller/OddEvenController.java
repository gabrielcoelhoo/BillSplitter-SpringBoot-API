package com.example.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AnswerRequest;
import com.example.model.Expense;
import com.example.model.Token;
import com.example.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("expenses")
public class OddEvenController {

	private String SECRET_KEY = "secret";

	private List<User> users;

	private List<Expense> expenses;

	private static boolean isTripOpened = true;

	public OddEvenController() {

		// read users in the text file and add it to the list of users
		users = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] user = line.split(" ");
				users.add(new User(user[0], user[1]));
			}
			br.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// read expenses in the text file and add it to the list of expenses
		expenses = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("expenses.txt"))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] expense = line.split(" ");
				expenses.add(new Expense(expense[0], expense[1], expense[2], expense[3]));
			}
			br.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	// creation of token

	private Token createJWT(String id, String subject, String issuer) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = SECRET_KEY.getBytes();
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);
		// https://github.com/oktadev/okta-java-jwt-example/blob/master/src/main/java/com/okta/createverifytokens/JWTDemo.java
		// Here shows how to add expiration.
		return new Token(builder.compact());
	}

	private Claims verifyToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
		return claims;
	}

	// check user name and password and return a JWT

	@GetMapping("signup")
	public Token login(String username, String password) {

		for (User user : users) {
			if (user.getUsername().contentEquals(username) && user.getPassword().contentEquals(password)) {
				return createJWT("cbwa", user.getUsername(), "david");
			}
		}
		throw new RuntimeException("User not found");
	}

	// add new expenses inside a text file
	// https://kodejava.org/how-do-i-create-and-write-data-into-text-file/

	@PostMapping
	public ResponseEntity<?> expense(Expense ex) throws IOException {
		// https://stackoverflow.com//4614227/how-to-add-a-new-line-of-text-to-an-existing-file-in-java

		if (!isTripOpened) {
			return ResponseEntity.status(400).build();
		}

		String s = System.lineSeparator();

		Writer writer = new BufferedWriter(new FileWriter("expenses.txt", true));
		writer.write(s);
		writer.append(ex.getUsername() + " " + ex.getTrip() + " " + ex.getDescription() + " " + ex.getPrice());
		expenses.add(ex);
		writer.close();
		return ResponseEntity.status(HttpStatus.CREATED).body(ex);
	}

	@GetMapping("/closeTrip")
	public ResponseEntity<?> closeTrip() {
		isTripOpened = false;
		return ResponseEntity.ok(new AnswerRequest("Closing operation", HttpStatus.OK.value(), 0.0));
	}

	@GetMapping("/openTrip")
	public ResponseEntity<?> openTrip() {
		isTripOpened = true;
		return ResponseEntity.ok(new AnswerRequest("Opening operation", HttpStatus.OK.value(), 0.0));
	}

	// method to display mathematical concepts into the front-end

	@GetMapping("/statusOfTrip")
	public ResponseEntity<?> statusOfTrip(String trip) {
		double total = 0.0;
		double numberOfPurchases = 0.0;
		double averageResult = 0.0;
		double highest = 0.0;
		double lowest = 0.0;

		for (int i = 0; i < expenses.size(); i++) {

			if (expenses.get(i).getTrip().equalsIgnoreCase(trip)) {

				if (Double.parseDouble(expenses.get(i).getPrice()) > highest) {
					highest = Double.parseDouble(expenses.get(i).getPrice());
				}

				total += Double.parseDouble(expenses.get(i).getPrice());

				numberOfPurchases++;

				averageResult = total / numberOfPurchases;

			}

		}

		for (int i = 0; i < expenses.size(); i++) {

			if (expenses.get(i).getTrip().equalsIgnoreCase(trip)) {
				if (lowest == 0) {
					lowest = Double.parseDouble(expenses.get(i).getPrice());
				} else if (Double.parseDouble(expenses.get(i).getPrice()) < lowest) {
					lowest = Double.parseDouble(expenses.get(i).getPrice());
				}
			}

		}

		String highestReturn = ("highest purchase of the trip �" + highest);
		String totalReturn = ("total of the trip �" + total);
		String lowestReturn = ("lowest purchase of the trip �" + lowest);
		String averageResultReturn = ("average of the trip �" + averageResult);
		String numberOfPurchasesReturn = ("number of purchases " + numberOfPurchases);

		return ResponseEntity
				.ok(List.of(highestReturn, totalReturn, averageResultReturn, numberOfPurchasesReturn, lowestReturn));
	}

	@GetMapping("/reportOfPurchases")
	public ResponseEntity<?> ReportOfPurchases() {

		Double davidExpenses = 0.0;
		Double amilcarExpenses = 0.0;
		Double grahamExpenses = 0.0;

		String amilcar_david_calculation = "";
		String amilcar_graham_calculation = "";
		String david_graham_calculation = "";

		// get all expenses and divide by the number of people in the group

		for (int i = 0; i < expenses.size(); i++) {

			if (expenses.get(i).getUsername().equalsIgnoreCase("david")) {
				davidExpenses += Double.parseDouble(expenses.get(i).getPrice());

			} else if (expenses.get(i).getUsername().equalsIgnoreCase("amilcar")) {
				amilcarExpenses += Double.parseDouble(expenses.get(i).getPrice());

			} else if (expenses.get(i).getUsername().equalsIgnoreCase("graham")) {
				grahamExpenses += Double.parseDouble(expenses.get(i).getPrice());

			}

			Double davidExpensesDivided = davidExpenses / users.size();
			Double amilcarExpensesDivided = amilcarExpenses / users.size();
			Double grahamExpensesDivided = grahamExpenses / users.size();

			// check how much need to be paid or pay;

			if (davidExpensesDivided > amilcarExpensesDivided) {
				amilcar_david_calculation = ("amilcar need to pay �" + (davidExpensesDivided - amilcarExpensesDivided)
						+ " for david");
			} else if (davidExpensesDivided < amilcarExpensesDivided) {
				amilcar_david_calculation = ("david need to pay �" + (amilcarExpensesDivided - davidExpensesDivided)
						+ " for amilcar");
			} else {
				amilcar_david_calculation = ("david and amilcar spent the same with each other");
			}

			if (grahamExpensesDivided > amilcarExpensesDivided) {
				amilcar_graham_calculation = ("amilcar need to pay �" + (grahamExpensesDivided - amilcarExpensesDivided)
						+ " for graham");
			} else if (grahamExpensesDivided < amilcarExpensesDivided) {
				amilcar_graham_calculation = ("graham need to pay �" + (amilcarExpensesDivided - grahamExpensesDivided)
						+ " for amilcar");
			} else {
				amilcar_graham_calculation = ("graham and amilcar spent the same with each other");
			}

			if (grahamExpensesDivided > davidExpensesDivided) {
				david_graham_calculation = ("david need to pay �" + (grahamExpensesDivided - davidExpensesDivided)
						+ " for graham");
			} else if (grahamExpensesDivided < davidExpensesDivided) {
				david_graham_calculation = ("graham need to pay �" + (davidExpensesDivided - grahamExpensesDivided)
						+ " for david");
			} else {
				david_graham_calculation = ("graham and david spent the same with each other");
			}

		}
		return ResponseEntity
				.ok(List.of(amilcar_david_calculation, david_graham_calculation, amilcar_graham_calculation));
	}
}
