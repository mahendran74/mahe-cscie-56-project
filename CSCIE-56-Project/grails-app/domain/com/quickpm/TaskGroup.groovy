package com.quickpm

class TaskGroup {
	
	String groupName 
	Date startDate
	Date endDate
	Integer percentageComplete
	TaskGroup parentGroup
	Project project
	
	static hasMany = [tasks:Task, milestones:Milestone]
	
    static constraints = {
		parentGroup nullable: true, blank: true
		percentageComplete nullable: true, blank: true
		project nullable: true, blank: true
    }
	
	static mapping = {
		tasks cascade: 'all-delete-orphan'
		milestones cascade: 'all-delete-orphan'
	}
}
