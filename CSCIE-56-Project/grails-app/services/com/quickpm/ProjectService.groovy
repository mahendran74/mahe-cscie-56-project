package com.quickpm

import grails.transaction.Transactional
import java.text.SimpleDateFormat

@Transactional
class ProjectService {
	
    def addProject(params) {
		System.println (params)
		def result = [:]
		def user = User.findById(params.projectManager)
		if (!user) {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' does not exist."
			return result
		}
		if (user.roles.any({ it.name == 'ROLE_PM' || it.name == 'ROLE_ADMIN' })) {
			Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
			Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
			if (validateDate(startDate, endDate)) {
				result['code'] = 'Failure'
				result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
				return result
			} else {
				def project = new Project(
					projectName: params.projectName,
					projectDesc: params.projectDesc,
					startDate: startDate,
					endDate: endDate,
					status: Status.PLANNED,
					projectManager: user)
				.save(flush: true, failOnError: true)
				def taskGroup = new TaskGroup(
					groupName: params.projectName + " Task Group",
					startDate: startDate,
					endDate: endDate,
					parentGroup: null,
					percentageComplete: 0,
					project: project)
					.save(flush: true, failOnError: true)
				project.addToTaskGroups(taskGroup)
					.save(flush: true, failOnError: true)
				result['code'] = 'Success'
				result['message'] = "The project has been created with '${user.firstName} ${user.lastName}' as the Project Manager"
				return result
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' is not a Project Manager."
			return result
		}
    }
	
	def validateDate(Date startDate, Date endDate) {
		if (endDate.before(startDate))
			true
		else
			false
	}
	
	def updateProject(params) {
		def result = [:]
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		def user = User.findById(params.projectManager)
		if (!user) {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.projectManager}' does not exist."
			return result
		}
		if (user.roles.any({ it.name == 'ROLE_PM' || it.name == 'ROLE_ADMIN' })) {
			Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
			Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
			if (validateDate(startDate, endDate)) {
				result['code'] = 'Failure'
				result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
				return result
			} else {
				project.projectName = params.projectName
				project.projectDesc = params.projectDesc
				project.startDate = startDate
				project.endDate = endDate
				project.status = params.status 
				project.projectManager = user
				project.save(flush: true, failOnError: true)
				result['code'] = 'Success'
				result['message'] = "The project has been updated."
				return result
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' is not a Project Manager."
			return result
		}
	}
	
	def deleteProject(id) {
		def result = [:]
		def project = Project.findById(id)
		if (project) {
			project.delete(flush: true)
			result['code'] = 'Success'
			result['message'] = "The project has been deleted."
		} else {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${id}' does not exist."
		}
		result
	}
	
	def addTask(params) {
		def result = [:]
		def assignedTo = User.findById(params.assignedTo)
		if (!assignedTo) { 
			result['code'] = 'Failure'
			result['message'] = "The assigned to user with id - '${params.assignedTo}' does not exist."
			return result
		}
		def taskGroup
		if (params.taskGroup) {
			taskGroup = TaskGroup.findById(params.taskGroup)
			if (!taskGroup) {
				result['code'] = 'Failure'
				result['message'] = "Task Group with id - '${params.taskGroup}', to which this task belongs to, does not exist."
				return result
			}
		}
		def dependsOn 
		if (params.dependsOn) {
			dependsOn = Task.findById(params.dependsOn)
			if (!dependsOn) {
				result['code'] = 'Failure'
				result['message'] = "The task with id - '${params.dependsOn}', to which this task depends on, does not exist."
				return result
			}
		}
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
		if (validateDate(startDate, endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
			return result
		} 
		if (startDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The task start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		}
		if (endDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The task end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		def percentageComplete = 0
		if (params.percentageComplete)
			percentageComplete = params.percentageComplete.toInteger()
		def task = new Task(
			taskDesc: params.taskDesc,
			startDate: startDate,
			endDate: endDate,
			percentageComplete: percentageComplete,
			status : params.status,
			assignedTo: assignedTo,
			taskGroup: taskGroup,
			dependsOn: dependsOn,
			color: params.color,
			project:project)
		.save(flush: true, failOnError: true)
		result['code'] = 'Success'
		result['message'] = "The task has been created."
		return result
	}
	
	def updateTask(params ) {
		def result = [:]
		def task = Task.findById(params.taskID)
		if (!task) {
			result['code'] = 'Failure'
			result['message'] = "Task with id - '${params.taskID}' does not exist."
			return result
		}
		def assignedTo = User.findById(params.assignedTo)
		if (!assignedTo) {
			result['code'] = 'Failure'
			result['message'] = "The assigned to user with id - '${params.assignedTo}' does not exist."
			return result
		}
		def taskGroup
		if (params.taskGroup) {
			taskGroup = TaskGroup.findById(params.taskGroup)
			if (!taskGroup) {
				result['code'] = 'Failure'
				result['message'] = "Task Group with id - '${params.taskGroup}', to which this task belongs to, does not exist."
				return result
			}
		}
		def dependsOn
		if (params.dependsOn) {
			dependsOn = Task.findById(params.dependsOn)
			if (!dependsOn) {
				result['code'] = 'Failure'
				result['message'] = "The task with id - '${params.dependsOn}', to which this task depends on, does not exist."
				return result
			}
			if (task.equals(dependsOn)) {
				result['code'] = 'Failure'
				result['message'] = "A task cannot depend on itself."
				return result
			}
		}
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
		if (validateDate(startDate, endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
			return result
		}
		if (startDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The task start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		}
		if (endDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The task end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		task.taskDesc = params.taskDesc
		task.startDate = startDate
		task.endDate = endDate
		task.percentageComplete = params.percentageComplete.toInteger()
		task.status = params.status
		task.assignedTo = assignedTo
		task.taskGroup = taskGroup
		task.dependsOn = dependsOn
		task.color = params.color
		task.save(flush: true, failOnError: true)

		result['code'] = 'Success'
		result['message'] = "The task has been updated."
		return result
	}
	
	def deleteTask(id) {
		def result = [:]
		def task = Task.findById(id)
		if (task) {
			task.delete(flush: true)
			result['code'] = 'Success'
			result['message'] = "The task has been deleted."
		} else {
			result['code'] = 'Failure'
			result['message'] = "Task with id - '${id}' does not exist."
		}
		result
	}
	
	def addMilestone(params) {
		def result = [:]
		def assignedTo = User.findById(params.assignedTo)
		if (!assignedTo) {
			result['code'] = 'Failure'
			result['message'] = "The assigned to user with id - '${params.assignedTo}' does not exist."
			return result
		}
		def taskGroup
		if (params.taskGroup) {
			taskGroup = TaskGroup.findById(params.taskGroup)
			if (!taskGroup) {
				result['code'] = 'Failure'
				result['message'] = "Task Group with id - '${params.taskGroup}', to which this milestone belongs to, does not exist."
				return result
			}
		}
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date milestoneDate = new Date().parse("MM/dd/yyyy", params.milestoneDate)
		if (milestoneDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The milestone date - ${params.milestoneDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		}
		if (milestoneDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The milestone date - ${params.milestoneDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		def milestone = new Milestone(
			milestoneDesc: params.milestoneDesc,
			milestoneDate: milestoneDate,
			assignedTo: assignedTo,
			taskGroup: taskGroup,
			project:project)
		.save(flush: true, failOnError: true)
		result['code'] = 'Success'
		result['message'] = "The milestone has been created."
		return result
	}
	
	def updateMilestone(params) {
		def result = [:]
		def milestone = Milestone.findById(params.milestoneID)
		if (!task) {
			result['code'] = 'Failure'
			result['message'] = "Milestone with id - '${params.milestoneID}' does not exist."
			return result
		}
		def assignedTo = User.findById(params.assignedTo)
		if (!assignedTo) {
			result['code'] = 'Failure'
			result['message'] = "The assigned to user with id - '${params.assignedTo}' does not exist."
			return result
		}
		def taskGroup
		if (params.taskGroup) {
			taskGroup = TaskGroup.findById(params.taskGroup)
			if (!taskGroup) {
				result['code'] = 'Failure'
				result['message'] = "Task Group with id - '${params.taskGroup}', to which this milestone belongs to, does not exist."
				return result
			}
		}
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date milestoneDate = new Date().parse("MM/dd/yyyy", params.milestoneDate)
		if (milestoneDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The milestone date - ${params.milestoneDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		}
		if (milestoneDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The milestone date - ${params.milestoneDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		milestone.milestoneDesc = params.milestoneDesc
		milestone.milestoneDate = milestoneDate
		milestone.assignedTo = assignedTo
		milestone.taskGroup = taskGroup
		milestone.save(flush: true, failOnError: true)

		result['code'] = 'Success'
		result['message'] = "The task has been updated."
		return result
	}
	
	def deleteMilestone(id) {
		def result = [:]
		def milestone = Milestone.findById(id)
		if (milestone) {
			milestone.delete(flush: true)
			result['code'] = 'Success'
			result['message'] = "The milestone has been deleted."
		} else {
			result['code'] = 'Failure'
			result['message'] = "Milestone with id - '${id}' does not exist."
		}
		result
	}
	
	def addGroup(params) {
		System.println ("addGroup - " + params)
		def result = [:]
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
		if (validateDate(startDate, endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
			return result
		}
		if (startDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The group start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		} 
		if (endDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The group end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		def parentGroup
		if (params.parentGroup) {
			parentGroup = TaskGroup.findById(params.parentGroup)
		}
		def taskGroup = new TaskGroup(
			groupName: params.groupName,
			startDate: startDate,
			endDate: endDate,
			parentGroup: parentGroup,
			percentageComplete: params.percentageComplete,
			project: project)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup)
			.save(flush: true, failOnError: true)
		result['code'] = 'Success'
		result['message'] = "The task group has been created."
		return result
	}
	
	def updateGroup(params) {
		System.println ("updateGroup - " + params)
		def result = [:]
		def group = TaskGroup.findById(params.groupID)
		if (!group) {
			result['code'] = 'Failure'
			result['message'] = "Task Group with id - '${params.groupID}' does not exist."
			return result
		}
		def parentGroup = TaskGroup.findById(params.parentGroup)
		if (!parentGroup) {
			result['code'] = 'Failure'
			result['message'] = "Task Group with id - '${params.parentGroup}' which was set as the parent group, does not exist."
			return result
		}
		if (group.equals(parentGroup)) {
			result['code'] = 'Failure'
			result['message'] = "A task group cannot be its own parent."
			return result
		}
		def project = Project.findById(params.projectID)
		if (!project) {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
			return result
		}
		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.startDate)
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(params.endDate)
		if (validateDate(startDate, endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
			return result
		}
		if (startDate.before(project.startDate)) {
			result['code'] = 'Failure'
			result['message'] = "The group start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
			return result
		} 
		if (endDate.after(project.endDate)) {
			result['code'] = 'Failure'
			result['message'] = "The group end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
			return result
		}
		group.groupName = params.groupName
		group.startDate = startDate
		group.endDate = endDate
		group.save(flush: true, failOnError: true)
		
		result['code'] = 'Success'
		result['message'] = "The project has been updated."
		return result
	}
	
	def deleteGroup(id) {
		def result = [:]
		def group = TaskGroup.findById(id)
		if (group) { 
			group.delete(flush: true)
			result['code'] = 'Success'
			result['message'] = "The task group has been deleted."
		} else {
			result['code'] = 'Failure'
			result['message'] = "Task group with id - '${id}' does not exist."
		}
		result
	}
	
	def getProjectXMLString(Project project) {
		def returnXMLString = '<project>'
		for (group in project.taskGroups){
			def groupXMLString = getGroupXMLString(group)
			returnXMLString = returnXMLString + groupXMLString
			for (task in group.tasks){
				def taskXMLString = getTaskXMLString(task)
				returnXMLString = returnXMLString + taskXMLString
			}
			for (milestone in group.milestones){
				def milestoneXMLString = getMilestoneXMLString(milestone)
				returnXMLString = returnXMLString + milestoneXMLString
			}
		}
		returnXMLString = returnXMLString + '</project>'
		returnXMLString
	  }
	  
	def getGroupXMLString(TaskGroup group) {
		def returnXMLString = '<task><pID>' << group.id + 1000 << '</pID>'
		returnXMLString << '<pName>' << group.groupName << '</pName>'
		returnXMLString << '<pStart>' << group.startDate.format('MM/dd/yyyy') << '</pStart>'
		returnXMLString << '<pEnd>' << group.endDate.format('MM/dd/yyyy') << '</pEnd>'
		returnXMLString << '<pColor>0000ff</pColor><pLink /><pMile>0</pMile><pRes/>'
		returnXMLString << '<pComp>' << group.percentageComplete << '</pComp><pGroup>1</pGroup>'
		if (group.parentGroup){
			returnXMLString << '<pParent>' << group.parentGroup.id + 1000 << '</pParent>'
		} else {
			returnXMLString << '<pParent>0</pParent>'
		}
		returnXMLString << '<pOpen>1</pOpen><pDepend/></task>'
		returnXMLString
	}
	  
	def getMilestoneXMLString(Milestone milestone) {
		def returnXMLString = '<task><pID>' << milestone.id + 2000 << '</pID>'
		returnXMLString << '<pName>' << milestone.milestoneDesc << '</pName>'
		returnXMLString << '<pStart>' << milestone.milestoneDate.format('MM/dd/yyyy') << '</pStart>'
		returnXMLString << '<pEnd>' << milestone.milestoneDate.format('MM/dd/yyyy') << '</pEnd>'
		returnXMLString << '<pColor>0000ff</pColor><pLink/><pMile>1</pMile>'
		if (milestone.assignedTo)
			returnXMLString << '<pRes>' << milestone.assignedTo.getFullName() << '</pRes>'
		returnXMLString << '<pComp/><pGroup>0</pGroup>'
		if (milestone.taskGroup){
			returnXMLString << '<pParent>' << milestone.taskGroup.id + 1000 << '</pParent>'
		} else {
			returnXMLString << '<pParent>0</pParent>'
		}
		returnXMLString << '<pOpen>0</pOpen><pDepend/></task>'
		returnXMLString
	}
	  
	def getTaskXMLString(Task task) {
		def returnXMLString = '<task><pID>' << task.id + 3000 << '</pID>'
		returnXMLString << '<pName>' << task.taskDesc << '</pName>'
		returnXMLString << '<pStart>' << task.startDate.format('MM/dd/yyyy') << '</pStart>'
		returnXMLString << '<pEnd>' << task.endDate.format('MM/dd/yyyy') << '</pEnd>'
		returnXMLString << '<pColor>' << task.color << '</pColor><pLink /><pMile>0</pMile>'
		if (task.assignedTo)
			returnXMLString << '<pRes>' << task.assignedTo.getFullName() << '</pRes>'
		returnXMLString << '<pComp>' << task.percentageComplete << '</pComp><pGroup>0</pGroup>'
		if (task.taskGroup) {
			returnXMLString << '<pParent>' << task.taskGroup.id + 1000 << '</pParent>'
		} else {
			returnXMLString << '<pParent>0</pParent>'
		}
		returnXMLString << '<pOpen>1</pOpen>'
		if (task.dependsOn) {
			returnXMLString << '<pDepend>' << task.dependsOn.id << '</pDepend>'
		} else {
			returnXMLString << '<pDepend/>'
		}
		returnXMLString << '</task>'
		returnXMLString
	}
}
