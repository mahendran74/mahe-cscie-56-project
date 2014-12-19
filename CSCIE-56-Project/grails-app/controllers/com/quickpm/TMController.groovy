package com.quickpm

import org.apache.shiro.subject.Subject
import org.apache.shiro.SecurityUtils

import static org.springframework.http.HttpStatus.*
import grails.converters.JSON

class TMController {
	UserService userService
	ProjectService projectService
	
	def home() {
		Subject subject = SecurityUtils.getSubject()
		def loggedInUsername = subject.principal
		User currentUser = User.findByUsername(loggedInUsername)
		def taskList = Task.findAllByAssignedTo(currentUser)
		
		def calendarTabActive, tasksTabActive
		if (params.show == 'tasks'){
			calendarTabActive = ''
			tasksTabActive = 'active'
		} else {
			calendarTabActive = 'active'
			tasksTabActive = ''
		}
		def userList = User.list()
		def taskGroupList = TaskGroup.list()
		[currentUser: currentUser, taskList: taskList, calendarTabActive: calendarTabActive, tasksTabActive: tasksTabActive, tmList: userList, taskGroupList: taskGroupList]
		
	}
	
	def getTasks(Integer id) {
		def user = User.findById(id)
		def taskList = Task.findAllByAssignedTo(user)
		def retList = []
		for (task in taskList) {
			def retArray = [:]
			retArray['id'] = 't' + task.id
			retArray['title'] = task.taskDesc
			retArray['start'] = task.startDate.format('MM/dd/yyyy')
			retArray['end'] = task.endDate.format('MM/dd/yyyy')
			retArray['url'] = '#'
			retArray['color'] = task.color
			retArray['backgroundColor'] = task.color
			retArray['textColor'] = '#000000'
			retList.add(retArray)
		}
		def milestoneList = Milestone.findAllByAssignedTo(user)
		for (milestone in milestoneList) {
			def retArray = [:]
			retArray['id'] = 'm' + milestone.id
			retArray['title'] = milestone.milestoneDesc
			retArray['start'] = milestone.milestoneDate.format('MM/dd/yyyy')
			retArray['url'] = '#'
			retArray['color'] = '#f83a22'
			retArray['backgroundColor'] = '#f83a22'
			retArray['textColor'] = '#000000'
			retList.add(retArray)
		}
		render retList as JSON
	}
	def getTask(Integer id) {
		render Task.findById(id) as JSON
	}
	def updateTask()
	{
		System.println params
		render projectService.updateTask(params) as JSON
	}
	
	def changePassword() {
		render userService.changePassword(params) as JSON
	}
//	def defaultExceptionHandler(Exception e){
//		def result = [:]
//		result['code'] = 'Failure'
//		result['message'] = e.message
//		render result as JSON
//	}
}