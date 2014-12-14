package com.quickpm

import grails.transaction.Transactional

@Transactional
class ProjectService {

	def getProjects(id) {
		User pm = User.findById(id)
		if(pm) {
			def projectList = Project.findAllByProjectManager(pm)
		} else {
			def output = [:]
		}
	}
    def addProject(params) {
		def user = User.findById(params.userId)
		def project = new Project(
			projectName: params.projectName,
			projectDesc: params.projectDesc,
			startDate: params.startDate,
			endDate: params.endDate,
			status: Status.GOOD,
			projectManager: user)
    }
	
	def editProject() {
	
	}
	
	def deleteProject() {
	
	}
	
	def addTask() {
	
	}
	
	def updateTask() {
		
	}
	
	def deleteTask() {
		
	}
	
	def adddMilestone() {
		
	}
	
	def updateMilestone() {
		
	}
	
	def deleteMilestone() {
		
	}
	
	def addGroup() {
		
	}
	
	def updateGroup() {
		
	}
	
	def deleteGroup() {
		
	}
}
