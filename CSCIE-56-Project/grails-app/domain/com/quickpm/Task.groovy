package com.quickpm

class Task {
	
	String taskDesc
	Date startDate
	Date endDate
	Integer percentageComplete
	Status status

	User user
	
    static constraints = {
    }
}
