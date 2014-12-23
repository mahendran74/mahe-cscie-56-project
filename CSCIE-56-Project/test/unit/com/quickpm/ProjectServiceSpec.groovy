package com.quickpm

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import java.text.SimpleDateFormat
import org.apache.shiro.crypto.hash.Sha512Hash
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ProjectService)
@Mock([Role, User, Project, TaskGroup, Task, Milestone])
class ProjectServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void 'test addProject'() {
		when:
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

			
		def params =[:]
		params['projectName'] = 'My New Project'
		params['projectDesc'] = 'This is the first project'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '02/10/2015'
		params['status'] = 'Planned'
		params['projectManager'] = pmUser.id
		def result = service.addProject(params)
		
		then:
		result['code'] == 'Success'
		result['message'] == "The project has been created with '${pmUser.firstName} ${pmUser.lastName}' as the Project Manager"
    }
	
	void 'test addProject with invalid user'() {
		when:
		def params =[:]
		params['projectManager'] = 2
		def result = service.addProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${params.projectManager}' does not exist."
	}
	
	void 'test addProject with user who is not a PM or ADMIN'() {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params =[:]
		params['projectManager'] = pmUser.id
		def result = service.addProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${params.projectManager}' is not a Project Manager."
	}
	
	void 'test addProject with invalid dates'() {
		when:
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

			
		def params =[:]
		params['projectName'] = 'My New Project'
		params['projectDesc'] = 'This is the first project'
		params['startDate'] = '02/10/2015'
		params['endDate'] = '12/10/2014'
		params['status'] = 'Planned'
		params['projectManager'] = pmUser.id
		def result = service.addProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	void 'test updateProject' () {
		when:
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
			
		def params =[:]
		params['projectID'] = project.id
		params['projectName'] = 'My New Project'
		params['projectDesc'] = 'This is the first project'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '02/10/2015'
		params['status'] = Status.PLANNED
		params['projectManager'] = pmUser.id
		def result = service.updateProject(params)
		
		then:
		result['code'] == 'Success'
		result['message'] == "The project has been updated."
	}
	
	void 'test updateProject with invalid project' () {
		when:
			
		def params =[:]
		params['projectID'] = 2
		def result = service.updateProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	void 'test updateProject with invalid user' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
		.save(flush: true, failOnError: true)
		
		def params =[:]
		params['projectID'] = project.id
		params['projectManager'] = 3
		def result = service.updateProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${params.projectManager}' does not exist."
	}
	
	void 'test updateProject with valid user who is not a PM' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def project = Project.findByProjectName('My New Project') ?:
			new Project(projectName : 'My New Project',
				projectDesc : 'This is the first project',
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('02/10/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
		.save(flush: true, failOnError: true)
		
		def params =[:]
		params['projectID'] = project.id
		params['projectManager'] = 1
		def result = service.updateProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${params.projectManager}' is not a Project Manager."
	}
	
	void 'test updateProject with invalid dates' () {
		when:
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
			
		def params =[:]
		params['projectID'] = project.id
		params['projectName'] = 'My New Project'
		params['projectDesc'] = 'This is the first project'
		params['startDate'] = '02/10/2015'
		params['endDate'] = '12/10/2014'
		params['status'] = Status.PLANNED
		params['projectManager'] = pmUser.id
		def result = service.updateProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	void 'test deleteProject' () {
		when:
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
			
		def params = project.id
		def result = service.deleteProject(params)
		
		then:
		result['code'] == 'Success'
		result['message'] == "The project has been deleted."
	}
	
	void 'test deleteProject with invalid project' () {
		when:
		def params = 1
		def result = service.deleteProject(params)
		
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params}' does not exist."
	}
	
	void 'test addGroup'() {
		when:
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

		def params = [:]
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.addGroup(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The task group has been created."
	}
	
	void 'test addGroup with invalid project' () {
		when:
		def params = [:]
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = 1
		def result = service.addGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	void 'test addGroup with invalid dates' () {
		when:
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

		def params = [:]
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '2/1/2015'
		params['endDate'] = '12/10/2014'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.addGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	void 'test addGroup with invalid start date' () {
		when:
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

		def params = [:]
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/1/2014'
		params['endDate'] = '12/10/2014'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.addGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The group start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	void 'test addGroup with invalid end date' () {
		when:
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

		def params = [:]
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '2/1/2015'
		params['endDate'] = '02/30/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.addGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The group end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	void 'test updateGroup'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The project has been updated."
	}
	
	void 'test updateGroup with invalid task group'() {
		when:
				
		def params = [:]
		params['groupID'] = 2

		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.groupID}' does not exist."
	}
	
	void 'test updateGroup with invalid parent task group'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['parentGroup'] = 23
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.parentGroup}' which was set as the parent group, does not exist."
	}
	
	void 'test updateGroup with the current group as its own parent task group'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['parentGroup'] = taskGroup.id
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] ==  "A task group cannot be its own parent."
	}
	
	void 'test updateGroup with invalid project'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = 4
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	void 'test updateGroup with invalid dates'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '2/10/2015'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	void 'test updateGroup with start date before project start date'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/1/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The group start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	void 'test updateGroup with end date after project end date'() {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/11/2014'
		params['endDate'] = '2/11/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.updateGroup(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The group end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	def 'test deleteGroup' () {
		when:
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
				
		def params = [:]
		params['groupID'] = taskGroup.id
		params['groupName'] = 'Group of Tasks'
		params['startDate'] = '12/11/2014'
		params['endDate'] = '2/11/2015'
		params['percentageComplete'] = 0
		params['projectID'] = project.id
		def result = service.deleteGroup(taskGroup.id)
		then:
		result['code'] == 'Success'
		result['message'] == "The task group has been deleted."
	}
	
	def 'test deleteGroup with invalid task group id' () {
		when:
		def id = 1
		def result = service.deleteGroup(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task group with id - '${id}' does not exist."
	}
	
	def 'test addTask' () {
		when:
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
			
		def params = [:]
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The task has been created."
	}
	
	def 'test addTask with invalid assignedTo ID' () {
		when:
			
		def params = [:]
		params['assignedTo'] = 3
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The assigned to user with id - '${params.assignedTo}' does not exist."
	}
	
	def 'test addTask with invalid task group' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

			
		def params = [:]
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = 3
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.taskGroup}', to which this task belongs to, does not exist."
	}
	
	def 'test addTask with invalid dependsOn' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

			
		def params = [:]
		params['assignedTo'] = pmUser.id
		params['dependsOn'] = 3
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task with id - '${params.dependsOn}', to which this task depends on, does not exist."
	}
	
	def 'test addTask with invalid project ID' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

			
		def params = [:]
		params['assignedTo'] = pmUser.id
		params['projectID'] = 3
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	def 'test addTask with invalid dates' () {
		when:
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
			
		def params = [:]
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2015'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	def 'test addTask with start date before project start date' () {
		when:
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
			
		def params = [:]
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/1/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	def 'test addTask with end date after project end date' () {
		when:
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
			
		def params = [:]
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/15/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	def 'test updateTask' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The task has been updated."
	}
	
	def 'test updateTask with invalid taskID' () {
		when:
		def params = [:]
		params['taskID'] = 3
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task with id - '${params.taskID}' does not exist."
	}
	
	def 'test updateTask with invalid assignedTo' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['assignedTo'] = 3
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The assigned to user with id - '${params.assignedTo}' does not exist."
	}
	
	def 'test updateTask with invalid task group' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = 3
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.taskGroup}', to which this task belongs to, does not exist."
	}
	
	def 'test updateTask with invalid dependsOn' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['assignedTo'] = pmUser.id
		params['dependsOn'] = 3
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task with id - '${params.dependsOn}', to which this task depends on, does not exist."
	}
	
	def 'test updateTask with invalid dependsOn itself' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['assignedTo'] = pmUser.id
		params['dependsOn'] = task.id
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "A task cannot depend on itself."
	}
	
	def 'test updateTask with invalid projectID' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['assignedTo'] = pmUser.id
		params['projectID'] = 3
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	def 'test updateTask with end date after project end date' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2014'
		params['endDate'] = '2/15/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task end date - ${params.endDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	def 'test updateTask with invalid dates' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/10/2015'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The end date - ${params.endDate} should fall after start date - ${params.startDate}."
	}
	
	def 'test updateTask with start date before project start date' () {
		when:
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
		def params = [:]
		params['taskID'] = task.id
		params['taskDesc'] = 'New Task'
		params['startDate'] = '12/1/2014'
		params['endDate'] = '2/1/2015'
		params['percentageComplete'] = 0
		params['status'] = Status.PLANNED
		params['color'] = '#46d6db'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateTask(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The task start date - ${params.startDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	def 'test deleteTask' () {
		when:
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
		def id = task.id
		def result = service.deleteTask(id)
		then:
		result['code'] == 'Success'
		result['message'] == "The task has been deleted."
	}
	
	def 'test deleteTask with invalid Id' () {
		when:
		def id = 3
		def result = service.deleteTask(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task with id - '${id}' does not exist."
	}
	
	def 'test addMilestone' () {
		when:
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
			
		def params = [:]
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '12/10/2014'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The milestone has been created."
	}
	
	def 'test addMilestone invalid assignedTo' () {
		when:
		def params = [:]
		params['assignedTo'] = 3

		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The assigned to user with id - '${params.assignedTo}' does not exist."
	}
	
	def 'test addMilestone invalid taskGroup' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = 2

		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.taskGroup}', to which this milestone belongs to, does not exist."
	}
	
	def 'test addMilestone invalid project' () {
		when:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['assignedTo'] = pmUser.id
		params['project'] = 2

		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	def 'test addMilestone milestone date before project start date' () {
		when:
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
			
		def params = [:]
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '12/1/2014'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The milestone date - ${params.milestoneDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	def 'test addMilestone milestone date after project end date' () {
		when:
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
			
		def params = [:]
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '02/15/2015'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.addMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The milestone date - ${params.milestoneDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	def 'test updateMilestone' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
			
		def params = [:]
		params['milestoneID'] = milestone.id
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '12/10/2014'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The milestone has been updated."
	}
	
	def 'test updateMilestone invalid milestoneID' () {
		when:
			
		def params = [:]
		params['milestoneID'] = 2
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Milestone with id - '${params.milestoneID}' does not exist."
	}
	
	def 'test updateMilestone invalid assignedTo' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
		def params = [:]
		params['milestoneID'] = milestone.id
		params['assignedTo'] = 3
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The assigned to user with id - '${params.assignedTo}' does not exist."
	}

	def 'test updateMilestone invalid taskGroup' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
		def params = [:]
		params['milestoneID'] = milestone.id
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = 4
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Task Group with id - '${params.taskGroup}', to which this milestone belongs to, does not exist."
	}
	
	def 'test updateMilestone invalid project' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
		def params = [:]
		params['milestoneID'] = milestone.id
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = 3
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Project with id - '${params.projectID}' does not exist."
	}
	
	def 'test updateMilestone milestone date before project start date' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
			
		def params = [:]
		params['milestoneID'] = milestone.id
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '12/1/2014'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The milestone date - ${params.milestoneDate} should not be before the project start date - ${project.startDate.format('MM/dd/yyyy')}."
	}
	
	def 'test updateMilestone milestone date after project end date' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
			
		def params = [:]
		params['milestoneID'] = milestone.id
		params['milestoneDesc'] = 'New Milestone'
		params['milestoneDate'] = '12/10/2015'
		params['assignedTo'] = pmUser.id
		params['taskGroup'] = taskGroup.id
		params['projectID'] = project.id
		
		def result = service.updateMilestone(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The milestone date - ${params.milestoneDate} should not be after the project end date - ${project.endDate.format('MM/dd/yyyy')}."
	}
	
	def 'test deleteMilestone' () {
		when:
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
		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)

		def result = service.deleteMilestone(milestone.id)
		then:
		result['code'] == 'Success'
		result['message'] == "The milestone has been deleted."
	}
	
	def 'test deleteMilestone invalid project ID' () {
		when:
		def id = 2
		def result = service.deleteMilestone(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "Milestone with id - '${id}' does not exist."
	}
	
	def 'test getProjectXMLString' () {
		when:
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
				assignedTo:pmUser,
				taskGroup: taskGroup,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
			
		def result = service.getProjectXMLString(project)
		then:
		result == "<project><task><pID>1001</pID><pName>Group of Tasks</pName><pStart>12/10/2014</pStart><pEnd>02/01/2015</pEnd><pColor>0000ff</pColor><pLink /><pMile>0</pMile><pRes/><pComp>0</pComp><pGroup>1</pGroup><pParent>0</pParent><pOpen>1</pOpen><pDepend/></task><task><pID>3001</pID><pName>New Task</pName><pStart>12/10/2014</pStart><pEnd>02/01/2015</pEnd><pColor>#46d6db</pColor><pLink /><pMile>0</pMile><pRes>PM User</pRes><pComp>0</pComp><pGroup>0</pGroup><pParent>1001</pParent><pOpen>1</pOpen><pDepend/></task><task><pID>2001</pID><pName>New Milestone</pName><pStart>01/10/2015</pStart><pEnd>01/10/2015</pEnd><pColor>0000ff</pColor><pLink/><pMile>1</pMile><pRes>PM User</pRes><pComp/><pGroup>0</pGroup><pParent>1001</pParent><pOpen>0</pOpen><pDepend/></task></project>"
	}
}
