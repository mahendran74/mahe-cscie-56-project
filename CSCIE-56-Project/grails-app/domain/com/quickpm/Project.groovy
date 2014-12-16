package com.quickpm

class Project {

	String projectName
	String projectDesc
	Date startDate
	Date endDate
	Status status
	User projectManager
	
	static hasMany = [taskGroups:TaskGroup, tasks:Task, milestones:Milestone]
	
    static constraints = {
		projectName nullable: false, blank: false, unique: true
		projectDesc nullable: true, blank: true
		startDate nullable: false, blank: false
		endDate nullable: false, blank: false
		status nullable: false, blank: false
		projectManager nullable: false, blank: false
    }
	
	static mapping = {
		taskGroups cascade: 'all-delete-orphan'
		tasks cascade: 'all-delete-orphan'
		milestones cascade: 'all-delete-orphan'
	}
}
