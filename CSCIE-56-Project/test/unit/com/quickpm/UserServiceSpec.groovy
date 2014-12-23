package com.quickpm

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.subject.Subject
import spock.lang.Specification
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
@Mock([Role, User])
class UserServiceSpec extends Specification {

    def setup() {
		def subject = [ getPrincipal: { 'tm@test.com' },
			isAuthenticated: { true }, login: {UsernamePasswordToken authToken -> 
				if (authToken.username == 'tm@test.com' && authToken.password.toString() == 'password') {
					true
				} else {
					throw new AuthenticationException()
				}
			}
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
    }

    def cleanup() {
    }

    void 'test checkEmail'() {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm1@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm1@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def result = service.checkEmail('tm1@test.com')
		then:
		result == true
    }
	
	void 'test checkEmail invalid email'() {
		when:
		def result = service.checkEmail('tm@test.com')
		then:
		result == false
	}
	
	void 'test signUp' () {
		when:
		def pmRole = Role.findByName('ROLE_PM') ?:
		new Role(name: 'ROLE_PM', description: 'Project Manager').save(flush: true, failOnError: true)
		pmRole.addToPermissions("pm:*")
		pmRole.addToPermissions("tm:*")
		pmRole.save(flush: true)
		def params = [:]
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['password'] = 'password'
		params['active'] = true
		def result =  service.signUp(params)
		then:
		result['code'] == 'Success'
		result['message'] == "User '${params.firstName} ${params.lastName}' has been added as a Project Manager"
	}
	
	void 'test signUp existing user' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['password'] = 'password'
		params['active'] = true
		def result =  service.signUp(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "User already exists with the username '${params.username}'"
	}
	
	void 'test signUp with no role setup' () {
		when:
		def params = [:]
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['password'] = 'password'
		params['active'] = true
		def result =  service.signUp(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Unable to find a role for Project Manager. Please establish a ROLE_PM role."
	}
	
	void 'test activate' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def result =  service.activate(tmUser.id)
		then:
		result['code'] == 'Success'
		result['message'] == "User has been activated."
	}
	
	void 'test activate invalid user' () {
		when:
		def id = 2
		def result =  service.activate(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${id}' does not exist."
	}
	
	void 'test deactivate' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def result =  service.deactivate(tmUser.id)
		then:
		result['code'] == 'Success'
		result['message'] == "User has been deactivated."
	}
	
	void 'test deactivate invalid user' () {
		when:
		def id = 2
		def result =  service.deactivate(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${id}' does not exist."
	}
	
	void 'test changePassword' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['username'] = 'tm@test.com'
		params['oldPassword'] = 'password'
		params['password'] = 'password'
		def result =  service.changePassword(params)
		then:
		result['code'] == 'Success'
		result['message'] == 'The password has been successfully changed.'
	}
	
	void 'test changePassword invalid old password' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['username'] = 'tm@test.com'
		params['oldPassword'] = 'invalidPassword'
		params['password'] = 'password'
		def result =  service.changePassword(params)
		then:
		result['code'] == 'Failure'
		result['message'] == 'Invalid old password.'
	}
	
	void 'test changePassword invalid user' () {
		when:
		// Create an tm user
		def params = [:]
		params['username'] = 'tm@test.com'
		params['oldPassword'] = 'invalidPassword'
		params['password'] = 'password'
		def result =  service.changePassword(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "User with username - '${params.username}' does not exist."
	}
	
	void 'test resetPassword' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)
		def params = [:]
		params['id'] = tmUser.id
		params['oldPassword'] = 'password'
		params['password'] = 'password'
		def result =  service.resetPassword(params)
		then:
		result['code'] == 'Success'
		result['message'] == "The password has been changed."
	}
	
	void 'test restPassword invalid user' () {
		when:
		def params = [:]
		params['id'] = 4
		params['oldPassword'] = 'invalidPassword'
		params['password'] = 'password'
		def result =  service.resetPassword(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${params.id}' does not exist."
	}
	
	void 'test addUser' () {
		when:
		def adminRole = Role.findByName('ROLE_ADMIN') ?:
			new Role(name: 'ROLE_ADMIN', description: 'Administrator').save(flush: true, failOnError: true)
			adminRole.addToPermissions("*:*")
			adminRole.save(flush: true)

		def params = [:]
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['newUserPassword'] = 'password'
		params['role'] = adminRole.id
		def result =  service.addUser(params)
		then:
		result['code'] == 'Success'
		result['message'] == "User '${params.firstName} ${params.lastName}' has been added as a ${adminRole.description}"
	}
	
	void 'test addUser user already exists' () {
		when:
		def tmRole = Role.findByName('ROLE_TM') ?:
			new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
			tmRole.addToPermissions("tm:*")
			tmRole.save(flush: true)
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
		def params = [:]
		params['username'] = 'tm@test.com'
		def result =  service.addUser(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "User already exists with the username '${params.username}'"
	}
	
	void 'test addUser no role setup' () {
		when:

		def params = [:]
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['newUserPassword'] = 'password'
		params['role'] = 5
		def result =  service.addUser(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "Role with role id - '${params.role}' does not exist."
	}
	
	void 'test updateUser' () {
		when:
		def tmRole = Role.findByName('ROLE_TM') ?:
			new Role(name: 'ROLE_TM', description: 'Team Member').save(flush: true, failOnError: true)
			tmRole.addToPermissions("tm:*")
			tmRole.save(flush: true)
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
		
		def params = [:]
		params['user_id'] = tmUser.id
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['newUserPassword'] = 'password'
		params['role'] = tmRole.id
		def result =  service.updateUser(params)
		then:
		result['code'] == 'Success'
		result['message'] == "User '${params.firstName} ${params.lastName}' has been updated."
	}
	
	void 'test updateUser invalid user' () {
		when:
		def params = [:]
		params['user_id'] = 2

		def result =  service.updateUser(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "User '${params.firstName} ${params.lastName}' does not exist."
	}
	
	void 'test updateUser invalid role' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		
		def params = [:]
		params['user_id'] = tmUser.id
		params['firstName'] = 'TM'
		params['lastName'] = 'User'
		params['middleInitial'] = 'I'
		params['username'] = 'tm@test.com'
		params['newUserPassword'] = 'password'
		params['role'] = 3
		def result =  service.updateUser(params)
		then:
		result['code'] == 'Failure'
		result['message'] == "The selected role does not exist."
	}
	
	void 'test deleteUser' () {
		when:
		// Create an tm user
		def tmUser = User.findByUsername('tm@test.com') ?:
			new User(firstName: 'TM',
					lastName: 'User',
					middleInitial: 'I',
					username: 'tm@test.com',
					passwordHash: new Sha512Hash("password").toHex(),
					active: true)
					.save(flush: true, failOnError: true)

		def id = tmUser.id
		def result =  service.deleteUser(id)
		then:
		result['code'] == 'Success'
		result['message'] == "User has been deleted."
	}
	
	void 'test deleteUser invalid user' () {
		when:
		def id = 2
		def result =  service.deleteUser(id)
		then:
		result['code'] == 'Failure'
		result['message'] == "User with id - '${id}' does not exist."
	}
}
