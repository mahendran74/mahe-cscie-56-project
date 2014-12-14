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
}
