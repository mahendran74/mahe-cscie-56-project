package com.quickpm

import grails.converters.*
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils

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
		render User.findById(id) as JSON
	}
	
	def changePassword() {
		def authToken = new UsernamePasswordToken(params.username, params.oldPassword as String)
		try {
			SecurityUtils.subject.login(authToken)
		} catch (AuthenticationException e) {
			flash.message = message(code: "login.failed")
			
		}
	}
}
