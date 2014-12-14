package com.quickpm

import grails.converters.JSON

class UserController {

	UserService userService
	
    def signUp() {
		render userService.signUp(params) as JSON
	}
	
	def checkEmail(String email) {
		//println "Here is request.JSON: ${request.JSON as JSON}"
		println "Here is params: $params"
		def result = [:]
		if (userService.checkEmail(email)) {
			result['code'] = 'Failure'
			result['message'] = 'This email is already used. Please provide another email address.'
		} else {
			result['code'] = 'Success'
			result['message'] = 'Email is available'
		}
		render result['message'] // as JSON
	}

}
