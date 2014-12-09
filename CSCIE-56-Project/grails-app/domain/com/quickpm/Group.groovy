package com.quickpm

class Group {
	
	String groupName
	Date startDate
	Date endDate
	Integer percentageComplete
	Group parentGroup
	
	static hasMany = [tasks:Task, milestones:Milestone]
	
    static constraints = {
		parentGroup nullable: true, blank: true
    }
}
