package com.quickpm

class TaskGroup {
	
	String groupName
	Date startDate
	Date endDate
	Integer percentageComplete
	TaskGroup parentGroup
	
	static hasMany = [tasks:Task, milestones:Milestone]
	
    static constraints = {
		parentGroup nullable: true, blank: true
    }
	
	static mapping = {
		tasks cascade: 'all-delete-orphan'
		milestones cascade: 'all-delete-orphan'
	}
}
