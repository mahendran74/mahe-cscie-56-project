package com.quickpm

class Task {
	
	String taskDesc
	Date startDate
	Date endDate
	Integer percentageComplete
	Status status
	Task dependsOn
	User assignedTo
	TaskGroup taskGroup
	Project project
	String color
	
    static constraints = {
		taskGroup nullable: true, blank: true
		dependsOn nullable: true, blank: true
		assignedTo nullable: true, blank: true
		project nullable: true, blank: true
    }
}
