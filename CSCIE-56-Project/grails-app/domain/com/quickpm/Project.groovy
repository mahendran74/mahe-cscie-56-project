package com.quickpm

class Project {

	String projectName
	String projectDesc
	Date startDate
	Date endDate
	Status status
	
	User projectManager
	
	static hasMany = [groups:Group]
	
    static constraints = {
    }
}
