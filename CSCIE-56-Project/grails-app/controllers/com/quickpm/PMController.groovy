package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils
import static org.springframework.http.HttpStatus.*

class PMController {

    def index() { }
	
	def home() {
		Subject currentUser = SecurityUtils.getSubject()
		def loggedInUsername = currentUser.principal
		User userInstance = User.findAllByUsername(loggedInUsername).get(0)
		respond userInstance
	}
}