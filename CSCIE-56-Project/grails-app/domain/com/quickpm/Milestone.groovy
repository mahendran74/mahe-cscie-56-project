package com.quickpm

class Milestone {

	String milestoneDesc
	Date milestoneDate
	User assignedTo
	TaskGroup taskGroup
	Project project
	
    static constraints = {
		taskGroup nullable: true, blank: true
		assignedTo nullable: true, blank: true
		project nullable: true, blank: true
    }
}
