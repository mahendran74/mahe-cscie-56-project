package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*

import grails.converters.JSON

class AdminController {

	UserService userService
	ProjectService projectService
	
    def index() { }
	
	def home() {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		
		//Get all user except the admin
		def userList = User.findAllByUsernameNotEqual(loggedInUsername)
		def roleList = Role.list()
		def projectList = Project.list()
		def userCriteria = User.createCriteria()
		def pmList = userCriteria.list { // Get list of all user who are PM or Admin
			roles {
				or {
					eq ('name', 'ROLE_PM')
					eq ('name', 'ROLE_ADMIN')
				}
			}
			eq('active', true)
		}
		def userTabActive, projectTabActive
		if (params.show == 'project'){
			userTabActive = ''
			projectTabActive = 'active'
		} else {
			userTabActive = 'active'
			projectTabActive = ''
		}
			
		[currentUser: currentUser, userList: userList, roleList: roleList, projectList: projectList, pmList: pmList, userTabActive: userTabActive, projectTabActive: projectTabActive]
	}
	
	def changePassword() {
		render userService.changePassword(params) as JSON
	}
	
	def nullPointerExceptionHandler(NullPointerException npe){
			def result = [:]
			result['code'] = 'Failure'
			result['message'] = npe.message
			render result as JSON
	}
	
	def defaultExceptionHandler(Exception e){
		def result = [:]
		result['code'] = 'Failure'
		result['message'] = e.message
		render result as JSON
	}
	
	def activate(Integer id) {
		render userService.activate(id) as JSON
	}
	
	def deactivate(Integer id) {
		render userService.deactivate(id) as JSON
	}
	
	def deleteUser(Integer id) {
		render userService.deleteUser(id) as JSON
	}
	
	def getUser(Integer id) {
		render User.findById(id) as JSON
	}
	
	def resetPassword() {
		render userService.resetPassword(params)
	}
	
	def addUser() {
		render userService.addUser(params) as JSON
	}
	
	def updateUser() {
		render userService.updateUser(params) as JSON
	}
	
	def getProject(Integer id) {
		render Project.findById(id) as JSON
	}
	
	def updateProject() {
		render projectService.updateProject(params) as JSON
	}
	
	def deleteProject(Integer id) {
		render projectService.deleteProject(id) as JSON
	}
}
