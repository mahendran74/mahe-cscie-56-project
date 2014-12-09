package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash

import grails.converters.JSON

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
	
	def changePassword() {
		def authToken = new UsernamePasswordToken(params.username, params.oldPassword as String)
		try {
			SecurityUtils.subject.login(authToken)
			def user = User.findByUsername(params.username)
			user.passwordHash = new Sha512Hash(params.password).toHex()
			user.save(flush: true)
			render "The password has been changed."
		} catch (AuthenticationException e) {
			flash.message = "Invalid old password"
			render flash.message
		}
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
	
	def resetPassword() {
		def user = User.findById(params.id)
		user.passwordHash = new Sha512Hash(params.password).toHex()
		user.save(flush: true)
		render "The password has been changed."
	}
}
