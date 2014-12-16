package com.quickpm

import grails.transaction.Transactional

@Transactional
class ProjectService {

    def addProject(params) {
		System.println (params)
		def result = [:]
		def user = User.findById(params.projectManager)
		if (user) {
			if (user.roles.any({ it.name == 'ROLE_PM' || it.name == 'ROLE_ADMIN' })) {
				if (validateDate(new Date().parse("MM/dd/yyyy", params.startDate), new Date().parse("MM/dd/yyyy", params.endDate))) {
					result['code'] = 'Failure'
					result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
				} else {
					def project = new Project(
						projectName: params.projectName,
						projectDesc: params.projectDesc,
						startDate: new Date().parse("MM/dd/yyyy", params.startDate),
						endDate: new Date().parse("MM/dd/yyyy", params.endDate),
						status: Status.PLANNED,
						projectManager: user).save(flush: true, failOnError: true)
						result['code'] = 'Success'
						result['message'] = "The project has been created with '${user.firstName} ${user.lastName}' as the Project Manager"
				}
			} else {
				result['code'] = 'Failure'
				result['message'] = "User with id - '${params.id}' is not a Project Manager."
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' does not exist."
		}
		result
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
		if (project) {
			def user = User.findById(params.projectManager)
			if (user) {
				if (user.roles.any({ it.name == 'ROLE_PM' || it.name == 'ROLE_ADMIN' })) {
					if (validateDate(new Date().parse("MM/dd/yyyy", params.startDate), new Date().parse("MM/dd/yyyy", params.endDate))) {
						result['code'] = 'Failure'
						result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
					} else {
						project.projectName = params.projectName
						project.projectDesc = params.projectDesc
						project.startDate = new Date().parse("MM/dd/yyyy", params.startDate)
						project.endDate = new Date().parse("MM/dd/yyyy", params.endDate)
						project.status = params.status
						project.projectManager = user
						project.save(flush: true, failOnError: true)
						result['code'] = 'Success'
						result['message'] = "The project has been updated."
					}
				} else {
					result['code'] = 'Failure'
					result['message'] = "User with id - '${params.id}' is not a Project Manager."
				}
			} else {
				result['code'] = 'Failure'
				result['message'] = "User with id - '${params.projectManager}' does not exist."
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "Project with id - '${params.projectID}' does not exist."
		}
		result
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
	
	def addTask() {
		def result = [:]
		def user = User.findById(params.userId)
		if (user) { 
			if (validateDate(new Date().parse("MM/dd/yyyy", params.startDate), new Date().parse("MM/dd/yyyy", params.endDate))) {
				result['code'] = 'Failure'
				result['message'] = "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
			} else {
				def taskGroup = TaskGroup.findById(params.taskGroup)
				def task = new Task(
					taskDesc: params.taskDesc,
					startDate: new Date().parse("MM/dd/yyyy", params.startDate),
					endDate: new Date().parse("MM/dd/yyyy", params.endDate),
					percentageComplete: params.percentageComplete,
					status : params.status,
					assignedTo: user,
					taskGroup: taskGroup).save(flush: true, failOnError: true)
					result['code'] = 'Success'
					result['message'] = "The task has been created."
			}
		} else {
			result['code'] = 'Failure'
			result['message'] = "User with id - '${params.id}' does not exist."
		}
		result
	}
	
	def updateTask() {
		
	}
	
	def deleteTask() {
		
	}
	
	def adddMilestone() {
		
	}
	
	def updateMilestone() {
		
	}
	
	def deleteMilestone() {
		
	}
	
	def addGroup() {
		
	}
	
	def updateGroup() {
		
	}
	
	def deleteGroup() {
		
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
		  def returnXMLString = '<task><pID>' << group.id << '</pID>'
		  returnXMLString << '<pName>' << group.groupName << '</pName>'
		  returnXMLString << '<pStart>' << group.startDate.format('MM/dd/yy') << '</pStart>'
		  returnXMLString << '<pEnd>' << group.endDate.format('MM/dd/yy') << '</pEnd>'
		  returnXMLString << '<pColor>0000ff</pColor><pLink /><pMile>0</pMile><pRes/>'
		  returnXMLString << '<pComp>' << group.percentageComplete << '</pComp>'
		  if (group.parentGroup){
		  	returnXMLString << '<pParent>' << group.parentGroup.id << '</pParent>'
		  } else {
		  	returnXMLString << '<pParent>0</pParent>'
		  }
		  returnXMLString << '<pOpen>1</pOpen><pDepend/></task>'
		  returnXMLString
	  }
	  
	  def getMilestoneXMLString(Milestone milestone) {
		  def returnXMLString = '<task><pID>' << milestone.id << '</pID>'
		  returnXMLString << '<pName>' << milestone.milestoneDesc << '</pName>'
		  returnXMLString << '<pStart>' << milestone.milestoneDate.format('MM/dd/yy') << '</pStart>'
		  returnXMLString << '<pEnd>' << milestone.milestoneDate.format('MM/dd/yy') << '</pEnd>'
		  returnXMLString << '<pColor>0000ff</pColor><pLink /><pMile>1</pMile>'
		  if (milestone.assignedTo)
		  	returnXMLString << '<pRes>' << milestone.assignedTo.getFullName() << '</pRes>'
		  returnXMLString << '<pComp/><pGroup>0</pGroup>'
		  if (milestone.taskGroup){
			  returnXMLString << '<pParent>' << milestone.taskGroup.id << '</pParent>'
		  } else {
			  returnXMLString << '<pParent>0</pParent>'
		  }
		  returnXMLString << '<pOpen>0</pOpen><pDepend/></task>'
		  returnXMLString
	  }
	  
	  def getTaskXMLString(Task task) {
		  def returnXMLString = '<task><pID>' << task.id << '</pID>'
		  returnXMLString << '<pName>' << task.taskDesc << '</pName>'
		  returnXMLString << '<pStart>' << task.startDate.format('MM/dd/yy') << '</pStart>'
		  returnXMLString << '<pEnd>' << task.endDate.format('MM/dd/yy') << '</pEnd>'
		  returnXMLString << '<pColor>' << task.color << '</pColor><pLink /><pMile>0</pMile>'
		  if (task.assignedTo)
		  	returnXMLString << '<pRes>' << task.assignedTo.getFullName() << '</pRes>'
		  returnXMLString << '<pComp>' << task.percentageComplete << '</pComp>'
		  if (task.taskGroup) {
			returnXMLString << '<pParent>' << task.taskGroup.id << '</pParent>'
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
