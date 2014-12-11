package com.quickpm

class Role {
	
	String name
	String description

    static hasMany = [ users: User, permissions: String ]
    static belongsTo = User
	
    static constraints = {
    }
	
	Role(String name) {
		this.name = name
	}
}
