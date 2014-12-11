package com.quickpm

import grails.transaction.Transactional

import org.apache.shiro.crypto.hash.Sha512Hash

@Transactional
class UserService {

    def checkEmail(String emailAddress) {
		User user = User.findAllByUsername(emailAddress)
		if (user)
			true
		else
			false
    }
	
	def signUp(params) {
		def user = User.findByUsername(params.username)
		if (user) {
			"User already exists with the username '${params.username}'"
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
			"User '${params.firstName} ${params.lastName}' has been added as a PM"
		}
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
}
