package com.quickpm

import org.apache.shiro.subject.Subject
import  org.apache.shiro.SecurityUtils
import static org.springframework.http.HttpStatus.*

class AdminController {

    def index() { }
	
	def home() {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		
		//Get all user except the admin
		def userList = User.findAllByUsernameNotEqual(loggedInUsername)
		
		[currentUser: currentUser, userList: userList]
	}
}
