import com.quickpm.Role
import com.quickpm.RoleType
import com.quickpm.User
import org.apache.shiro.crypto.hash.Sha512Hash
import grails.converters.*

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
			new Role(name: 'ROLE_ADMIN').save(flush: true, failOnError: true)
			adminRole.addToPermissions("*:*")
			adminRole.save(flush: true)
		def pmRole = Role.findByName('ROLE_PM') ?:
			new Role(name: 'ROLE_PM').save(flush: true, failOnError: true)
			pmRole.addToPermissions("pm:*")
			pmRole.addToPermissions("tm:*")
			pmRole.save(flush: true)
		def tmRole = Role.findByName('ROLE_TM') ?:
			new Role(name: 'ROLE_TM').save(flush: true, failOnError: true)
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
						.addToRoles(pmRole)
						.addToRoles(tmRole)
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
			
    }
    def destroy = {
    }
}
