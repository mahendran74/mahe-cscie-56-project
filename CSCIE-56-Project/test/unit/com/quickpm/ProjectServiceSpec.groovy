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
}
