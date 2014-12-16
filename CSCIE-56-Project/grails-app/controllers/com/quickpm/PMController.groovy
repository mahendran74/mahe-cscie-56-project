package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash

import grails.converters.JSON
import grails.converters.XML

class PMController {
	
	UserService userService
	ProjectService projectService

    def index() { }
	
	def home() {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		def projectList = Project.findAllByProjectManager(currentUser)
		def allowedRole = Role.findByName('ROLE_TM')
		def xmlRole = allowedRole as XML
		// return tjson: xmlRole.toString()
		[currentUser: currentUser, projectList: projectList, allowedRole: allowedRole.id, tjson: xmlRole.toString()]
	}
	
	def gantt(Integer id) {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		def project = Project.findById(id)
		def projectXMLString = projectService.getProjectXMLString(project)
		[currentUser: currentUser, project: project, projectXMLString: projectXMLString.toString()]
	}
	
	def changePassword() {
		def result = [:]
		def authToken = new UsernamePasswordToken(params.username, params.oldPassword as String)
		try {
			SecurityUtils.subject.login(authToken)
			def user = User.findByUsername(params.username)
			user.passwordHash = new Sha512Hash(params.password).toHex()
			user.save(flush: true)
			result['code'] = 'Success'
			result['message'] = 'The password has been successfully changed.'
		} catch (AuthenticationException e) {
			result['code'] = 'Failure'
			result['message'] = 'Invalid old password.'
		}
		render result as JSON
	}
	
	def addUser() {
		render userService.addUser(params) as JSON
	}
	
	def addProject() {
		render projectService.addProject(params) as JSON
	}
	
	def getProject(Integer id) {
		render Project.findById(id) as JSON
	}
	
	def updateProject() {
		render projectService.updateProject(params) as JSON
	}
	//	def defaultExceptionHandler(Exception e){
	//		def result = [:]
	//		result['code'] = 'Failure'
	//		result['message'] = e.message
	//		render result as JSON
	//	}
	
}