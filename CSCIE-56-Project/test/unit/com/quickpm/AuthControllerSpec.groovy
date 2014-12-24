package com.quickpm

import grails.converters.JSON
import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.subject.Subject
import spock.lang.Specification
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.SecurityUtils
import org.apache.shiro.web.util.WebUtils

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(AuthController)
@Mock ([User, Role, Project, Task, TaskGroup, Milestone])
class AuthControllerSpec extends Specification {

    def setup() {

    }

    def cleanup() {
    }

    void 'test index'() {
		when: 
		controller.index()

		then: 
		response.redirectedUrl == '/auth/login'
    }
	
	void 'test login'() {
		when:
		controller.login()

		then:
		response.redirectedUrl == '/'
	}

	void 'test signIn as Admin'() {
		setup:
		def subject = [
			getPrincipal: { 'admin@test.com' },
			isAuthenticated: { true },
			login: { UsernamePasswordToken authToken ->
				if (authToken.username == 'admin@test.com' && authToken.password.toString() == 'password') {
					true
				} else {
					throw new AuthenticationException()
				}
			},
			hasRole: { String roleName ->
				if (roleName == 'ROLE_ADMIN')
					true
				else
					false
			}
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = {HttpServletRequest request -> null }
		when:
		params['username'] = 'admin@test.com'
		params['password'] = 'password'
		controller.signIn()

		then:
		response.redirectedUrl == '/admin/home'
	}
	
	void 'test signIn as PM'() {
		setup:
		def subject = [
			getPrincipal: { 'pm@test.com' },
			isAuthenticated: { true },
			login: { UsernamePasswordToken authToken ->
				if (authToken.username == 'pm@test.com' && authToken.password.toString() == 'password') {
					true
				} else {
					throw new AuthenticationException()
				}
			},
			hasRole: { String roleName ->
				if (roleName == 'ROLE_PM')
					true
				else
					false
			}
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = {HttpServletRequest request -> null }
		when:
		params['username'] = 'pm@test.com'
		params['password'] = 'password'
		controller.signIn()

		then:
		response.redirectedUrl == '/PM/home'
	}
	
	void 'test signIn as TM'() {
		setup:
		def subject = [
			getPrincipal: { 'tm@test.com' },
			isAuthenticated: { true },
			login: { UsernamePasswordToken authToken ->
				if (authToken.username == 'tm@test.com' && authToken.password.toString() == 'password') {
					true
				} else {
					throw new AuthenticationException()
				}
			},
			hasRole: { String roleName ->
				if (roleName == 'ROLE_TM')
					true
				else
					false
			}
		  ] as Subject

		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
						[ getSubject: { subject } ] as SecurityManager )
		
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = {HttpServletRequest request -> null }
		when:
		params['username'] = 'tm@test.com'
		params['password'] = 'password'
		controller.signIn()

		then:
		response.redirectedUrl == '/TM/home'
	}
}
