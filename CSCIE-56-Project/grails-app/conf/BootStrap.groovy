import com.quickpm.Milestone
import com.quickpm.Project
import com.quickpm.Role
import com.quickpm.RoleType
import com.quickpm.Task
import com.quickpm.TaskGroup
import com.quickpm.User
import com.quickpm.Status

import org.apache.shiro.crypto.hash.Sha512Hash

import grails.converters.*

import java.text.SimpleDateFormat
import java.util.Date;

class BootStrap {
    def init = { servletContext ->
		
//		def adminRole = new Role(roleName: RoleType.ROLE_ADMIN).save(flush: true)
//		def userRole = new Role(roleName: RoleType.ROLE_USER).save(flush: true)
//  
//		def testUser = new User(firstName: 'Test', 
//								lastName: 'User',
//								middleIntitial: 'I',
//								email: 'test@test.com',
//								password: 'test',
//								token: 'dfsdf',
//								active: true)
//		testUser.addToRoles(adminRole)
//		testUser.save(flush: true)
		
		JSON.registerObjectMarshaller(User) {
			def output = [:]
			output['id'] = it.id
			output['firstName'] = it.firstName
			output['lastName'] = it.lastName
			output['middleInitial'] = it.middleInitial
			output['active'] = it.active
			output['username'] = it.username
			output['roles'] = it.roles
			return output;
		}
		JSON.registerObjectMarshaller(Role) {
			def output = [:]
			output['id'] = it.id
			output['name'] = it.name
			return output;
		}
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
				status : Status.GOOD,
				projectManager : pmUser)
			.save(flush: true, failOnError: true)
		//Add Task Group
		def taskGroup = TaskGroup.findByGroupName('Group of Tasks') ?:
			new TaskGroup(groupName : 'Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2014'),
				percentageComplete: 0)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup)
			.save(flush: true, failOnError: true)
			
		//Add Task
		def task = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('2/1/2014'),
				percentageComplete: 0,
				status : Status.GOOD,
				assignedTo: tmUser,
				taskGroup: taskGroup )
			.save(flush: true, failOnError: true)
		taskGroup.addToTasks(task)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'New Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2014'),
				assignedTo:tmUser,
				taskGroup: taskGroup)
			.save(flush: true, failOnError: true)
		taskGroup.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
    }
    def destroy = {
    }
}
