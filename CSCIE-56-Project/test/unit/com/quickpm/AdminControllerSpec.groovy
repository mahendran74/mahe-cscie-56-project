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
@TestFor(AdminController)
@Mock ([User, Role, Project, Task, TaskGroup, Milestone])
class AdminControllerSpec extends Specification {

    def setup() {

		def subject = [ getPrincipal: { "admin@test.com" },
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
		def adminRole = Role.findByName('ROLE_ADMIN') ?:
			new Role(name: 'ROLE_ADMIN', description: 'Administrator').save(flush: true, failOnError: true)
			adminRole.addToPermissions("*:*")
			adminRole.save(flush: true)
		def pmRole = Role.findByName('ROLE_PM') ?:
			new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
			pmRole.addToPermissions("pm:*")
			pmRole.addToPermissions("tm:*")
			pmRole.save(flush: true)
		def tmRole = Role.findByName('ROLE_TM') ?:
			new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
			tmRole.addToPermissions("tm:*")
			tmRole.save(flush: true)
		// Create an admin user
		def adminUser = User.findByUsername('admin@test.com') ?:
			new User(firstName: 'Admin', 
					lastName: 'User', 
					middleInitial: 'I',
					username: 'admin@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to  the admin user
		assert adminUser.addToRoles(adminRole)
				.save(flush: true, failOnError: true)
				
		// Create an admin user
		def adminUser1 = User.findByUsername('admin1@test.com') ?:
			new User(firstName: 'Admin1',
					lastName: 'User',
					middleInitial: 'I',
					username: 'admin1@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		
				// Add roles to the admin user
				assert adminUser1.addToRoles(adminRole)
						.save(flush: true, failOnError: true)
						
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
		
		// Create an tm user
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
		
		// Create an tm user disabled
		def tmUserDisabled = User.findByUsername('tmd@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'UserDisabled',
					middleInitial: 'I',
					username: 'tmd@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: false)
					.save(flush: true, failOnError: true)
		
		// Add roles to the pm user
		assert tmUserDisabled.addToRoles(tmRole)
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
		params['show'] = 'project'
		def model = controller.home()

		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == adminUser
		model.userList == [adminUser1, pmUser, tmUser, tmUserDisabled]
		model.roleList == [adminRole, pmRole, tmRole]
		model.projectList == [project]
		model.pmList == [pmUser, adminUser, adminUser1]
		model.projectTabActive == 'active'
		model.userTabActive == ''
    }
	
	void 'test home from the users tab'() {
		setup:
		// Create the roles
		def adminRole = Role.findByName('ROLE_ADMIN') ?:
			new Role(name: 'ROLE_ADMIN', description: 'Administrator').save(flush: true, failOnError: true)
			adminRole.addToPermissions("*:*")
			adminRole.save(flush: true)
		def pmRole = Role.findByName('ROLE_PM') ?:
			new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
			pmRole.addToPermissions("pm:*")
			pmRole.addToPermissions("tm:*")
			pmRole.save(flush: true)
		def tmRole = Role.findByName('ROLE_TM') ?:
			new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
			tmRole.addToPermissions("tm:*")
			tmRole.save(flush: true)
		// Create an admin user
		def adminUser = User.findByUsername('admin@test.com') ?:
			new User(firstName: 'Admin',
					lastName: 'User',
					middleInitial: 'I',
					username: 'admin@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		// Add roles to  the admin user
		assert adminUser.addToRoles(adminRole)
				.save(flush: true, failOnError: true)
				
		// Create an admin user
		def adminUser1 = User.findByUsername('admin1@test.com') ?:
			new User(firstName: 'Admin1',
					lastName: 'User',
					middleInitial: 'I',
					username: 'admin1@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		
				// Add roles to the admin user
				assert adminUser1.addToRoles(adminRole)
						.save(flush: true, failOnError: true)
						
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
		
		// Create an tm user
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
		
		// Create an tm user disabled
		def tmUserDisabled = User.findByUsername('tmd@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'UserDisabled',
					middleInitial: 'I',
					username: 'tmd@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: false)
					.save(flush: true, failOnError: true)
		
		// Add roles to the pm user
		assert tmUserDisabled.addToRoles(tmRole)
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
		params['show'] = 'users'
		def model = controller.home()

		then: 'returns the current user instance, list of projects and allowed role to be added.'
		model.currentUser == adminUser
		model.userList == [adminUser1, pmUser, tmUser, tmUserDisabled]
		model.roleList == [adminRole, pmRole, tmRole]
		model.projectList == [project]
		model.pmList == [pmUser, adminUser, adminUser1]
		model.projectTabActive == ''
		model.userTabActive == 'active'
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
	
	void 'test activate' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.activate  { Long id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User has been activated."
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.activate(1)
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test deactivate' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.deactivate  { Long id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User has been deactivated."
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.deactivate(1)
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test deleteUser' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.deleteUser  { Long id ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User has been deleted."
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.deleteUser(1)
		
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
	
	void 'test updateUser' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.updateUser  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User '${params.firstName} ${params.lastName}'"
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.updateUser()
		
		then:
		response.json.code == 'Success'
	}
	void 'test getUser' () {
		setup:
		// Create an pm user
		def pmUser = User.findByUsername('pm@test.com') ?:
			new User(firstName: 'PM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'pm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		
		when:
		controller.getUser(pmUser.id)
		
		then:
		response.json.id == pmUser.id
	}
	
	void 'test resetPassword' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.resetPassword  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User '${params.firstName} ${params.lastName}'"
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.resetPassword()
		
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
	
	void 'test deleteProject' () {
		setup:
		def projectServiceMock = mockFor(ProjectService)
		projectServiceMock.demand.deleteProject  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = 'Project has been deleted'
			return result
		}
		controller.projectService = projectServiceMock.createMock()
		
		when:
		controller.deleteProject()
		
		then:
		response.json.code == 'Success'
	}

}
