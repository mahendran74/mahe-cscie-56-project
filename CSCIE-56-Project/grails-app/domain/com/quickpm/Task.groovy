package com.quickpm

class Task {
	
	String taskDesc
	Date startDate
	Date endDate
	Integer percentageComplete
	Status status

	User assignedTo
	
    static constraints = {
    }
}
