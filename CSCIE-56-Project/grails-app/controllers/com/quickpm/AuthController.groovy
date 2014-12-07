package com.quickpm

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils

class AuthController {
    def shiroSecurityManager

    def index = { redirect(action: "login", params: params) }

    def login = {
		flash.message = "Please login."
		redirect(uri: "/")
    }

    def signIn = {
        def authToken = new UsernamePasswordToken(params.username, params.password as String)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }
        
        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri ?: "/"
        
        // Handle requests saved by Shiro filters.
        SavedRequest savedRequest = WebUtils.getSavedRequest(request)
        if (savedRequest) {
            targetUri = savedRequest.requestURI - request.contextPath
            if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
        }
        
        try{
            // Perform the actual login. An AuthenticationException
            // will be thrown if the username is unrecognized or the
            // password is incorrect.
            SecurityUtils.subject.login(authToken)
			log.info "Authenticated"
			if (SecurityUtils.subject.hasRole(RoleType.ROLE_ADMIN.name())){
				redirect(controller: 'admin', action: 'home')
			} else if (SecurityUtils.subject.hasRole(RoleType.ROLE_PM.name())){
				redirect(controller: 'PM', action: 'home')
			} else if (SecurityUtils.subject.hasRole(RoleType.ROLE_TM.name()))
				redirect(controller: 'TM', action: 'home')

        }
        catch (AuthenticationException ex){
            // Authentication failed, so display the appropriate message
            // on the login page.
            log.info "Authentication failure for user '${params.username}'."
            flash.message = message(code: "login.failed")

            // Keep the username and "remember me" setting so that the
            // user doesn't have to enter them again.
            def m = [ email: params.username ]
            if (params.rememberMe) {
                m["rememberMe"] = true
            }
            // Now redirect back to the login page.
            redirect(uri:'/', params: m)
        }
    }

    def signOut = {
        // Log the user out of the application.
        SecurityUtils.subject?.logout()
        webRequest.getCurrentRequest().session = null

        // For now, redirect back to the home page.
        redirect(uri: "/")
    }

    def unauthorized = {
        flash.message = "Please login."
		redirect(uri: "/")
    }
}
