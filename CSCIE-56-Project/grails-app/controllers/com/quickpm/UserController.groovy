package com.quickpm

import grails.converters.*
class UserController {

    def signUp() {
		
	}
	
	def checkEmail(String email) {
		User user = User.findAllByUsername(email)
		if (user == null)
			render "Email not used"
		else
			render "Email is available"
		
	}
	
	def activate(Integer id) {
		User userInstance = User.findById(id)
		userInstance.active = true
		userInstance.save(flush:true)
		render "User has been activated."
	}
	
	def deactivate(Integer id) {
		User userInstance = User.findById(id)
		userInstance.active = false
		userInstance.save(flush:true)
		render "User has been deactivated."
	}
	
	def getUser(Integer id) {
		render User.get(id) as JSON
	}
}
