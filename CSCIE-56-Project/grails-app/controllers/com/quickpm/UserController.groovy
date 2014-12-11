package com.quickpm

class UserController {

	UserService userService
	
    def signUp() {
		render userService.signUp(params)
	}
	
	def checkEmail(String email) {
		if (userService.checkEmail(email))
			render "Email is available"
		else
			render "Email is already used"
	}

}
