package com.quickpm

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import java.text.SimpleDateFormat
import spock.lang.Specification
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.SecurityUtils
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.subject.Subject

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TMController)
@Mock ([User, Role, Project, Task, TaskGroup, Milestone])
class TMControllerSpec extends Specification {

    def setup() {
		def subject = [ getPrincipal: { "tm@test.com" },
			isAuthenticated: { true }
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
    }

    def cleanup() {
    }

    void "test home from tasks tab "() {
		when:
		// Create the roles
		def tmRole = Role.findByName('ROLE_TM') ?:
		new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
		tmRole.addToPermissions("tm:*")
		tmRole.save(flush: true)
		// Create an pm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert tmUser.addToRoles(tmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : tmUser)
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
				assignedTo: tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
		params['show'] = 'tasks'
		def model = controller.home()
		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == tmUser
		model.taskList == [task]
		model.tmList == [tmUser]
		model.calendarTabActive == ''
		model.tasksTabActive == 'active'
		model.taskGroupList == [taskGroup]		
    }
	
	void "test home from calendar tab "() {
		when:
		// Create the roles
		def tmRole = Role.findByName('ROLE_TM') ?:
		new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
		tmRole.addToPermissions("tm:*")
		tmRole.save(flush: true)
		// Create an pm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert tmUser.addToRoles(tmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : tmUser)
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
				assignedTo: tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
		params['show'] = 'calendar'
		def model = controller.home()
		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == tmUser
		model.taskList == [task]
		model.tmList == [tmUser]
		model.calendarTabActive == 'active'
		model.tasksTabActive == ''
		model.taskGroupList == [taskGroup]
	}
	
	void 'test getTask' () {
		setup:
		// Create the roles
		def tmRole = Role.findByName('ROLE_TM') ?:
		new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
		tmRole.addToPermissions("tm:*")
		tmRole.save(flush: true)
		// Create an pm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert tmUser.addToRoles(tmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : tmUser)
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
				assignedTo: tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)
		when:
		controller.getTask(task.id)
		
		then:
		response.json.id == task.id
	}
	
	void 'test getTasks' () {
		setup:
		// Create the roles
		def tmRole = Role.findByName('ROLE_TM') ?:
		new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
		tmRole.addToPermissions("tm:*")
		tmRole.save(flush: true)
		// Create an pm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to the pm user
		assert tmUser.addToRoles(tmRole)
				.save(flush: true, failOnError: true)
		//Add Project
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : tmUser)
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
				assignedTo: tmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)
		when:
		controller.getTasks(task.id)
		
		then:
		response.contentAsString == '[{"id":"t1","title":"New Task","start":"12/10/2014","end":"02/01/2015","url":"#","color":"#46d6db","backgroundColor":"#46d6db","textColor":"#000000"}]'
	}
	
	def 'test updateTask' () {
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
}
