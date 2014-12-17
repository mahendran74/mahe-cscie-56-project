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
		[currentUser: currentUser, projectList: projectList, allowedRole: allowedRole.id]
	}
	
	def gantt(Integer id) {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		def project = Project.findByIdAndProjectManager(id, currentUser)
		if (!project)
		{
			redirect(action: "home") 
		}
		def userList = User.list();
		def taskList = Task.findAllByProject(project)
		def projectXMLString = projectService.getProjectXMLString(project)
		[currentUser: currentUser, project: project, projectXMLString: projectXMLString.toString(), tmList: userList, taskList: taskList]
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
	
	def getGroup(Integer id) {
		render TaskGroup.findById(id) as JSON
	}
	
	def addGroup() {
		render projectService.addGroup(params) as JSON
	}
	
	def updateGroup() {
		render projectService.updateGroup(params) as JSON
	}
	
	def deleteGroup(Integer id) {
		render projectService.deleteGroup(id) as JSON
	}
	
	def getTask(Integer id) {
		render Task.findById(id) as JSON
	}
	
	def addTask() {
		render projectService.addTask(params) as JSON
	}
	
	def updateTask() {
		render projectService.updateTask(params) as JSON
	}
	
	def deleteTask(Integer id) {
		render projectService.deleteTask(id) as JSON
	}
	//	def defaultExceptionHandler(Exception e){
	//		def result = [:]
	//		result['code'] = 'Failure'
	//		result['message'] = e.message
	//		render result as JSON
	//	}
	
}