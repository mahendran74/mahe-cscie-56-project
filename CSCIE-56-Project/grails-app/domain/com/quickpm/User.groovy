package com.quickpm

class User {
	
	String firstName
	String lastName
	String middleInitial
	String username
	String passwordHash
	boolean active
	
	static hasMany = [roles: Role, permissions: String]

    static constraints = {
		username nullable: false, blank: false, unique: true
		passwordHash blank: false
    }
	
	def isAdmin() {
		
	}
}
