package com.quickpm

import grails.converters.JSON
import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import java.text.SimpleDateFormat
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.subject.Subject
import spock.lang.Specification
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.SecurityUtils

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(PMController)
@Mock ([User, Role, Project, Task, TaskGroup, Milestone])
class PMControllerSpec extends Specification {

    def setup() {

		def subject = [ getPrincipal: { "pm@test.com" },
			isAuthenticated: { true }
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
    }

    def cleanup() {
    }

    void 'test home'() {
		setup:
		// Create the roles
		def tmRole = Role.findByName('ROLE_TM') ?:
		new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
		tmRole.addToPermissions("tm:*")
		tmRole.save(flush: true)
		def pmRole = new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
		pmRole.addToPermissions("pm:*")
		pmRole.addToPermissions("tm:*")
		pmRole.save(flush: true)
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert pmUser.addToRoles(pmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
			.save(flush: true, failOnError: true)
		when: 'a PM user is logged in'
		def model = controller.home()

		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == pmUser
		model.projectList == [project]
		model.allowedRole == Role.findByName('ROLE_TM').id
    }
	
	void 'test gantt'() {
		setup:
		// Create the roles
		def pmRole = new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
		pmRole.addToPermissions("pm:*")
		pmRole.addToPermissions("tm:*")
		pmRole.save(flush: true)
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert pmUser.addToRoles(pmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
			.save(flush: true, failOnError: true)
		//Add Task Group
		def taskGroup = TaskGroup.findByGroupName('Group of Tasks') ?:
			new TaskGroup(groupName : 'Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0, project: project)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup)
			.save(flush: true, failOnError: true)
			
		//Add Task
		def task = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#46d6db',
				assignedTo: pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo: pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)

		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.getProjectXMLString { Project testProject -> return '' }
		controller.projectService = projectServiceMock.createMock()
			
		
		when: 'a PM user is logged in'
		def model = controller.gantt(project.id)

		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == pmUser
		model.project == project
		model.tmList == [pmUser]
		model.taskList == [task]
		model.projectXMLString == ''
	}
	
	void 'test gantt a user is logged in who is not the PM of this project'() {
		setup:
		// Create the roles
		def pmRole = new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
		pmRole.addToPermissions("pm:*")
		pmRole.addToPermissions("tm:*")
		pmRole.save(flush: true)
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert pmUser.addToRoles(pmRole)
				.save(flush: true, failOnError: true)
				// Create an pm user
		def pmUser1 = User.findByUsername('pm1@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm1@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert pmUser1.addToRoles(pmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser1)
			.save(flush: true, failOnError: true)
		//Add Task Group
		def taskGroup = TaskGroup.findByGroupName('Group of Tasks') ?:
			new TaskGroup(groupName : 'Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0, project: project)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup)
			.save(flush: true, failOnError: true)
			
		//Add Task
		def task = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#46d6db',
				assignedTo: pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo: pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)

		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.getProjectXMLString { Project testProject -> return '' }
		controller.projectService = projectServiceMock.createMock()
			
		
		when: 'a user is logged in who is not the PM of this project'
		def model = controller.gantt(project.id)

		then: 'returns the current user instance, list of projects and allowed role to be added.'
		response.redirectedUrl == '/PM/home'
	}
	
	void 'test changePassword' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.changePassword  { Map params -> 
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'The password has been successfully changed.'
			return result 
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.changePassword()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test addUser' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.addUser  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User '${params.firstName} ${params.lastName}'"
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.addUser()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test addProject' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.addProject  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Project has been added'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.addProject()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test getProject' () {
		setup:
		// Create the roles
		def pmRole = new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
		pmRole.addToPermissions("pm:*")
		pmRole.addToPermissions("tm:*")
		pmRole.save(flush: true)
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert pmUser.addToRoles(pmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
			.save(flush: true, failOnError: true)
		
		when:
		controller.getProject(project.id)
		
		then:
		response.json.id == project.id
	}
	
	void 'test updateProject' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.updateProject  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Project has been updated'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.updateProject()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test getGroup' () {
		setup:
		def taskGroup = TaskGroup.findByGroupName('Group of Tasks') ?:
			new TaskGroup(groupName : 'Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0, project: null)
			.save(flush: true, failOnError: true)
		
		when:
		controller.getGroup(taskGroup.id)
		
		then:
		response.json.id == taskGroup.id
	}
	
	void 'test addGroup' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.addGroup  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task Group has been added'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.addGroup()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test updateGroup' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.updateGroup  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task Group has been updated'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.updateGroup()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test deleteGroup' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.deleteGroup  { Object id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task Group has been deleted'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.deleteGroup(1)
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test getTask' () {
		setup:
		//Add Task
		def task = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#46d6db',
				assignedTo: null,
				taskGroup: null,
				project: null)
			.save(flush: true, failOnError: true)
		
		when:
		controller.getTask(task.id)
		
		then:
		response.json.id == task.id
	}
	
	void 'test addTask' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.addTask  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task has been added'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.addTask()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test updateTask' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.updateTask  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task has been updated'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.updateTask()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test deleteTask' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.deleteTask  { Object id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Task has been deleted'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.deleteTask(1)
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test getMilestone' () {
		setup:
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:null,
				taskGroup: null,
				project: null)
			.save(flush: true, failOnError: true)
		
		when:
		controller.getMilestone(milestone.id)
		
		then:
		response.json.id == milestone.id
	}
	
	void 'test addMilestone' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.addMilestone  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Milestone has been added'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.addMilestone()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test updateMilestone' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.updateMilestone  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Milestone has been updated'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.updateMilestone()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test deleteMilestone' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.deleteMilestone  { Object id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Milestone has been deleted'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.deleteMilestone(1)
		
		then:
		response.json.code == 'Success'
	}
}
