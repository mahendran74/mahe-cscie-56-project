package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash

import grails.converters.JSON

class AdminController {

	UserService userService
	
    def index() { }
	
	def home() {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		
		//Get all user except the admin
		def userList = User.findAllByUsernameNotEqual(loggedInUsername)
		def roleList = Role.list()
		def projectList = Project.list()
		[currentUser: currentUser, userList: userList, roleList: roleList, projectList: projectList]
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
		render userService.activate(id)
	}
	
	def deactivate(Integer id) {
		render userService.deactivate(id)
	}
	
	def deleteUser(Integer id) {
		render userService.deleteUser(id)
	}
	
	def getUser(Integer id) {
		render User.findById(id) as JSON
	}
	
	def resetPassword() {
		render userService.resetPassword(params)
	}
	
	def addUser() {
		render userService.addUser(params)
	}
	
	def updateUser() {
		render userService.updateUser(params)
	}
}
