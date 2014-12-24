package com.quickpm

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UserController)
class UserControllerSpec extends Specification {

    def setup() {

    }

    def cleanup() {
    }

	void 'test signUp' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.signUp  { Map params ->
			def result = [:]
			result['code'] = 'Success'
			result['message'] = "User signed up"
			return result
		}
		controller.userService = userServiceMock.createMock()
		
		when:
		controller.signUp()
		
		then:
		response.json.code == 'Success'
	}
	
	void 'test checkEmail valid email' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.checkEmail  { String email ->
			if (email == 'admin@test.com')
				false
			else
				true
		}
		controller.userService = userServiceMock.createMock()
		when:
		controller.checkEmail('admin@test.com')
		
		then:
		response.json.code == 'Success'
		response.json.message == 'Email is available'
	}
	
	void 'test checkEmail invalid email' () {
		setup:
		def userServiceMock = mockFor(UserService)
		userServiceMock.demand.checkEmail  { String email ->
			if (email == 'admin@test.com')
				false
			else
				true
		}
		controller.userService = userServiceMock.createMock()
		when:
		controller.checkEmail('invalid@test.com')
		
		then:
		response.json.code == 'Failure'
		response.json.message == 'This email is already used. Please provide another email address.'
	}
}
