package com.quickpm

class User {
	
	String firstName
	String lastName
	String middleIntitial
	String email
	String password
	String token
	boolean active

    static constraints = {
		email blank: false, unique: true
		password blank: false
    }
}
