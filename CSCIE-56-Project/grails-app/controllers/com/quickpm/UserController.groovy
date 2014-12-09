package com.quickpm

class UserController {

    def signUp() {
		
	}
	
	def checkEmail(String email) {
		User user = User.findAllByUsername(email)
		if (user == null)
			render "Email not used"
		else
			render "Email is available"
		
	}

}
