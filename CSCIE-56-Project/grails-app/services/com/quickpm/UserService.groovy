package com.quickpm

import grails.transaction.Transactional

import org.apache.shiro.crypto.hash.Sha512Hash

@Transactional
class UserService {

    def checkEmail(String emailAddress) {
		System.println ("Email - " + emailAddress)
		def userList = User.findAllByUsername(emailAddress)
		if (userList)
			true
		else
			false
    }
	
	def signUp(params) {
		def user = User.findByUsername(params.username)
		def result = [:]
		if (user) {
			result['code'] = 'Failure'
			result['message'] = "User already exists with the username '${params.username}'"
		} else {
			def newUser = new User(
							firstName: params.firstName, 
							lastName: params.lastName, 
							middleInitial: params.middleInitial,
							username: params.username,
							passwordHash: new Sha512Hash(params.password).toHex(),
							active: true).save(flush: true, failOnError: true)
			def userRole =  Role.findByName('ROLE_PM')
            newUser.addToRoles(userRole)
            newUser.save(flush:true)
			result['code'] = 'Success'
			result['message'] = "User '${params.firstName} ${params.lastName}' has been added as a Project Manager"
		}
		result
	}
	
	def activate(Integer id) {
		User userInstance = User.findById(id)
		userInstance.active = true
		userInstance.save(flush:true)
		"User has been activated."
	}
	
	def deactivate(Integer id) {
		User userInstance = User.findById(id)
		userInstance.active = false
		userInstance.save(flush:true)
		"User has been deactivated."
	}
	
	def resetPassword(params) {
		def user = User.findById(params.id)
		user.passwordHash = new Sha512Hash(params.password).toHex()
		user.save(flush: true)
		"The password has been changed."
	}
	
	def addUser(params) {
		def user = User.findByUsername(params.username)
		if (user) {
			"User already exists with the username '${params.username}'"
		} else {
			def newUser = new User(
							firstName: params.firstName, 
							lastName: params.lastName, 
							middleInitial: params.middleInitial,
							username: params.username,
							passwordHash: new Sha512Hash(params.newUserPassword).toHex(),
							active: true).save(flush: true, failOnError: true)
			def userRole =  Role.findById(params.role)
            newUser.addToRoles(userRole)
            newUser.save(flush:true)
			"User '${params.firstName} ${params.lastName}' has been added as a ${userRole.description}"
		}
	}
	
	def updateUser(params) {
		
		def user = User.findById(params.user_id)
		if (user) {
			user.firstName = params.firstName
			user.lastName = params.lastName
			user.middleInitial = params.middleInitial
			user.username = params.username
			user.save(flush: true, failOnError: true)
			
			def role =  Role.findById(params.role)
			user.roles.clear()
			user.addToRoles(role)
			user.save(flush:true)
			"User '${params.firstName} ${params.lastName}' has been updated."
		} else {
			"User '${params.firstName} ${params.lastName}' does not exist."
		}
	}
	
	def deleteUser(id) {
		def user = User.get(id)
		user.delete(flush: true)
		"User has been deleted."
	}
}
