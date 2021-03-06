package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*

import grails.converters.JSON

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
		if (!allowedRole) {
			flash.message = "PM:" + message(code: "roles.not.setup", args: ['ROLE_PM'])
			[currentUser: currentUser, projectList: projectList, allowedRole: '']
		} else {
			[currentUser: currentUser, projectList: projectList, allowedRole: allowedRole.id]
		}
	}
	
	def gantt(Long id) {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		def project = Project.findByIdAndProjectManager(id, currentUser)
		if (!project)
		{
			flash.message = "NO_PM:" + message(code: "no.pm")
			redirect(action:"home")
		} else {
			def userList = User.list()
			def taskList = Task.findAllByProject(project)
			def projectXMLString = projectService.getProjectXMLString(project)
			[currentUser: currentUser, project: project, projectXMLString: projectXMLString.toString(), tmList: userList, taskList: taskList]
		}
	}
	
	def changePassword() {
		render userService.changePassword(params) as JSON
	}
	
	def addUser() {
		render userService.addUser(params) as JSON
	}
	
	def addProject() {
		render projectService.addProject(params) as JSON
	}
	
	def getProject(Long id) {
		render Project.findById(id) as JSON
	}
	
	def updateProject() {
		render projectService.updateProject(params) as JSON
	}
	
	def getGroup(Long id) {
		render TaskGroup.findById(id) as JSON
	}
	
	def addGroup() {
		render projectService.addGroup(params) as JSON
	}
	
	def updateGroup() {
		render projectService.updateGroup(params) as JSON
	}
	
	def deleteGroup(Long id) {
		render projectService.deleteGroup(id) as JSON
	}
	
	def getTask(Long id) {
		render Task.findById(id) as JSON
	}
	
	def addTask() {
		render projectService.addTask(params) as JSON
	}
	
	def updateTask() {
		render projectService.updateTask(params) as JSON
	}
	
	def deleteTask(Long id) {
		render projectService.deleteTask(id) as JSON
	}
	def getMilestone(Long id) {
		render Milestone.findById(id) as JSON
	}
	
	def addMilestone(params) {
		render projectService.addMilestone(params) as JSON
	}
	
	def updateMilestone(params) {
		render projectService.updateMilestone(params) as JSON
	}
	
	def deleteMilestone(Long id) {
		render projectService.deleteMilestone(id) as JSON
	}
	
	def defaultExceptionHandler(Exception e){
		def result = [:]
		result['code'] = 'Failure'
		result['message'] = e.message
		render result as JSON
	}
	
}