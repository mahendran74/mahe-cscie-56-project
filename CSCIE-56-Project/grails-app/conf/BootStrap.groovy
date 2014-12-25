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
		
////////////////////// Registering Custom Object Marhallers ////////////////////////////////////
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
		JSON.registerObjectMarshaller(Project) {
			def output = [:]
			output['id'] = it.id
			output['projectName'] = it.projectName
			output['projectDesc'] = it.projectDesc
			output['startDate'] = it.startDate.format('MM/dd/yyyy')
			output['endDate'] = it.endDate.format('MM/dd/yyyy')
			output['status'] = it.status
			output['projectManager'] = ["id": it.projectManager.id, "name": it.projectManager.getFullName()]

			return output;
		}
		JSON.registerObjectMarshaller(TaskGroup) {
			def output = [:]
			output['id'] = it.id
			output['groupName'] = it.groupName
			output['startDate'] = it.startDate.format('MM/dd/yyyy')
			output['endDate'] = it.endDate.format('MM/dd/yyyy')
			output['parentGroup'] = it.parentGroup.id
			return output;
		}
		JSON.registerObjectMarshaller(Task) {
			def output = [:]
			output['id'] = it.id
			output['taskDesc'] = it.taskDesc
			output['startDate'] = it.startDate.format('MM/dd/yyyy')
			output['endDate'] = it.endDate.format('MM/dd/yyyy')
			output['percentageComplete'] = it.percentageComplete
			output['status'] = it.status
			if (it.dependsOn)
				output['dependsOn'] = it.dependsOn.id
			output['assignedTo'] = it.assignedTo.id
			if (it.taskGroup)
				output['taskGroup'] = it.taskGroup.id
			output['project'] = it.project.id
			output['color'] = it.color
			return output;
		}
		JSON.registerObjectMarshaller(Milestone) {
			def output = [:]
			output['id'] = it.id
			output['milestoneDesc'] = it.milestoneDesc
			output['milestoneDate'] = it.milestoneDate.format('MM/dd/yyyy')
			output['assignedTo'] = it.assignedTo.id
			output['taskGroup'] = it.taskGroup.id
			output['project'] = it.project.id
			return output;
		}
		
///////////////////////// Setting up the data ///////////////////////////////////////
//		// Create the roles
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
				startDate : new SimpleDateFormat('MM/dd/yyyy').parse('12/01/2014'),
				endDate : new SimpleDateFormat('MM/dd/yyyy').parse('01/31/2015'),
				status : Status.PLANNED,
				projectManager : pmUser)
			.save(flush: true, failOnError: true)
			
		//Add Task Group
		def taskGroup1 = TaskGroup.findByGroupName('First Group of Tasks') ?:
			new TaskGroup(groupName : 'First Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/01/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/31/2014'),
				percentageComplete: 0, project: project)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup1)
			.save(flush: true, failOnError: true)
			
		//Add Task
		def task1 = new Task(taskDesc: 'Task 1',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/01/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#7bd148',
				assignedTo: tmUser,
				taskGroup: taskGroup1,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup1.addToTasks(task1)
			.save(flush: true, failOnError: true)

		//Add Task
		def task2 = new Task(taskDesc: 'Task 2',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/10/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/20/2014'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#5484ed',
				assignedTo: pmUser,
				taskGroup: taskGroup1,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup1.addToTasks(task2)
			.save(flush: true, failOnError: true)
				
		//Add Task
		def task3 = new Task(taskDesc: 'Task 3',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/20/2014'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/30/2014'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#a4bdfc',
				assignedTo: adminUser,
				taskGroup: taskGroup1,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup1.addToTasks(task3)
			.save(flush: true, failOnError: true)

		//Add Milestone
		def milestone = new Milestone(milestoneDesc: 'First Milestone',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('12/31/2014'),
				assignedTo:pmUser,
				taskGroup: taskGroup1,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup1.addToMilestones(milestone)
			.save(flush: true, failOnError: true)
				
		//Add Task Group
		def taskGroup2 = TaskGroup.findByGroupName('Second Group of Tasks') ?:
			new TaskGroup(groupName : 'Second Group of Tasks',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/01/2015'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/31/2015'),
				percentageComplete: 0, project: project)
			.save(flush: true, failOnError: true)
		project.addToTaskGroups(taskGroup2)
			.save(flush: true, failOnError: true)

		//Add Task
		def task4 = new Task(taskDesc: 'Task 4',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/01/2015'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#46d6db',
				assignedTo: tmUser,
				taskGroup: taskGroup2,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup2.addToTasks(task4)
			.save(flush: true, failOnError: true)
						
		//Add Task
		def task5 = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/10/2015'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/20/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#7ae7bf',
				assignedTo: pmUser,
				taskGroup: taskGroup2,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup2.addToTasks(task5)
			.save(flush: true, failOnError: true)

		//Add Task
		def task6 = new Task(taskDesc: 'New Task',
				startDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/20/2015'),
				endDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/30/2015'),
				percentageComplete: 0,
				status : Status.PLANNED,
				color : '#ffb878',
				assignedTo: adminUser,
				taskGroup: taskGroup2,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup2.addToTasks(task6)
			.save(flush: true, failOnError: true)
							
							
		//Add Milestone
		def milestone2 = new Milestone(milestoneDesc: 'Milestone 2',
				milestoneDate: new SimpleDateFormat('MM/dd/yyyy').parse('01/31/2015'),
				assignedTo:pmUser,
				taskGroup: taskGroup2,
				project: project)
			.save(flush: true, failOnError: true)
		taskGroup2.addToMilestones(milestone2)
			.save(flush: true, failOnError: true)
    }
    def destroy = {
    }
}
