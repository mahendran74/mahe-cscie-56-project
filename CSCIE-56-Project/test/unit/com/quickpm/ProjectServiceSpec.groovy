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
@Mock([Role, User, Project, TaskGroup])
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
}
