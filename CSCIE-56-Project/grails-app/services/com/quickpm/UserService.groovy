package com.quickpm

import grails.transaction.Transactional

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.SecurityUtils

@Transactional
class UserService {

    def checkEmail(String emailAddress) {
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
			return result
		}
		def userRole =  Role.findByName('ROLE_PM')
		if (!userRole) {
			result['code'] = 'Failure'
			result['message'] = "Unable to find a role for Project Manager. Please establish a ROLE_PM role."
			return result
		}
		def newUser = new User(
						firstName: params.firstName, 
						lastName: params.lastName, 
						middleInitial: params.middleInitial,
						username: params.username,
						passwordHash: new Sha512Hash(params.password).toHex(),
						active: true).save(flush: true, failOnError: true)

        newUser.addToRoles(userRole)
        newUser.save(flush:true)
		result['code'] = 'Success'
		result['message'] = "User '${params.firstName} ${params.lastName}' has been added as a Project Manager"
		return result
	}
	
	def activate(Long id) {
		def result = [:]
		User user = User.findById(id)
		if (user) {
			user.active = true
			user.save(flush:true)
			result['code'] = 'Success'
			result['message'] = "User has been activated."
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${id}' does not exist."
		}
		result
	}
	
	def deactivate(Long id) {
		def result = [:]
		User user = User.findById(id)
		if (user) {
			user.active = false
			user.save(flush:true)
			result['code'] = 'Success'
			result['message'] = "User has been deactivated."
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${id}' does not exist."
		}
		result
	}
	
	def changePassword(params) {
		def result = [:]
		def user = User.findByUsername(params.username)
		if (!user) {
			result['code'] = 'Failure'
			result['message'] = "User with username - '${params.username}' does not exist."
			return result
		}
		def authToken = new UsernamePasswordToken(params.username, params.oldPassword as String)
		try {
			SecurityUtils.subject.login(authToken)
			user.passwordHash = new Sha512Hash(params.password).toHex()
			user.save(flush: true)
			result['code'] = 'Success'
			result['message'] = 'The password has been successfully changed.'
		} catch (AuthenticationException e) {
			result['code'] = 'Failure'
			result['message'] = 'Invalid old password.'
		} 
		result
	}
	
	def resetPassword(params) {
		def result = [:]
		def user = User.findById(params.id)
		if (user) {
			user.passwordHash = new Sha512Hash(params.password).toHex()
			user.save(flush: true)
			result['code'] = 'Success'
			result['message'] = "The password has been changed."
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' does not exist."
		}
		result
	}
	
	def addUser(params) {
		def result = [:]
		def user = User.findByUsername(params.username)
		if (user) {
			result['code'] = 'Failure'
			result['message'] = "User already exists with the username '${params.username}'"
		} else {
			def newUser = new User(
							firstName: params.firstName, 
							lastName: params.lastName, 
							middleInitial: params.middleInitial,
							username: params.username,
							passwordHash: new Sha512Hash(params.newUserPassword).toHex(),
							active: true).save(flush: true, failOnError: true)
			def roleId = params.role
			if (!roleId)
				roleId = 3 // Default it to Team Member
			def userRole =  Role.findById(roleId)
			if (userRole){
	            newUser.addToRoles(userRole)
	            newUser.save(flush:true)
				result['code'] = 'Success'
				result['message'] = "User '${params.firstName} ${params.lastName}' has been added as a ${userRole.description}"
			} else {
				result['code'] = 'Failure'
				result['message'] = "Role with role id - '${roleId}' does not exist."
			}
		}
		result
	}
	
	def updateUser(params) {
		
		def result = [:]
		def user = User.findById(params.user_id)
		if (user) {
			user.firstName = params.firstName
			user.lastName = params.lastName
			user.middleInitial = params.middleInitial
			user.username = params.username
			user.save(flush: true, failOnError: true)
			
			def role =  Role.findById(params.role)
			if (role) {
				user.roles.clear()
				user.addToRoles(role)
				user.save(flush:true)
				result['code'] = 'Success'
				result['message'] = "User '${params.firstName} ${params.lastName}' has been updated."
			} else {
				result['code'] = 'Failure'
				result['message'] = "The selected role does not exist."
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "User '${params.firstName} ${params.lastName}' does not exist."
		}
		result
	}
	
	def deleteUser(Long id) {
		def result = [:]
		def user = User.get(id)
		if (user) {
			user.delete(flush: true)
			result['code'] = 'Success'
			result['message'] = "User has been deleted."
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${id}' does not exist."
		}
		result
	}
}
