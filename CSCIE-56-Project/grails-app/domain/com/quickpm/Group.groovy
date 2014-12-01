package com.quickpm

class Group {
	
	String groupName
	Date startDate
	Date endDate
	Integer percentageComplete
	
	static hasMany = [tasks:Task, milestones:Milestone]
	
    static constraints = {
    }
}
