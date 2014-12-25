            <h1>CSCI E-56 Final Project - Quick Project Management (QPM)</h1>
            <p>The QPM is a project management tool. It helps users manage small projects like a Agile sprint. It has 3 different user interfaces for the 3 different roles that it supports.</p>
            <h2>Project Manager Interface</h2>
            <p>This is the default interface. The user signing in to the website will be given this role by default. This interface provides the user with the following privileges.</p>
            <ol>
                <li>Create new projects. </li>
                <li>Edit projects details like</li>
                <ol type="a">
                    <li>Project Description</li>
                    <li>Start and End date</li>
                    <li>Project Status - Planned/In Progress/Completed</li>
                </ol>
                <li>View the Gantt chart of the project</li>
                <li>Add, edit and delete tasks, task groups and milestones.</li>
                <li>Add team members for the projects.</li>
            </ol>
            <h2>Administrator Interface</h2>
          <p>The admin interface allows the admin to manage all the users of QPM. All users who sign up has the Project Manager access. 
            But the Admin can create Team Member users and other admins. The admin can also provide and revoke admin access to other users.
            Admin can work as a Project Manager and a Team Member. 
            The admin user either has to be inserted directly using the database. 
            The Bootstrap config creates the following users.</p>
            <table class="table table-striped">
            	<tr>
            		<th>Name</th>
            		<th>User name</th>
            		<th>Password</th>
            		<th>Role</th>
            		<th>Status</th>
            	</tr>
            	<tr>
            		<td>Admin User</td>
            		<td>admin@test.com</td>
            		<td>password</td>
            		<td>Administrator</td>
            		<td>Active</td>
            	</tr>
            	<tr>
            		<td>PM User</td>
            		<td>pm@test.com</td>
            		<td>password</td>
            		<td>Project Manager</td>
            		<td>Active</td>
            	</tr>
           		<tr>
            		<td>TM User</td>
            		<td>tm@test.com</td>
            		<td>password</td>
            		<td>Team Member</td>
            		<td>Active</td>
            	</tr>
            	<tr>
            		<td>TM UserDisabled</td>
            		<td>tmd@test.com</td>
            		<td>password</td>
            		<td>Team Member</td>
            		<td>Inactive</td>
            	</tr>
            </table>
            </ol>
            <h2>Team Member Interface</h2>
            <p>This is interface allows users to</p>
            <ol>
                <li>View task list</li>
                <li>Edit task details</li>
                <li>Change task status/start date and end date</li>
            </ol>
            <p>Here are the main features of this site</p>
            
            <h3>1. Dynamic Gantt chart</h3>
            <p>The PM interface allows the user to see a dynamic Gantt Chart of the project. When the user creates a project, QPM creates a Gantt chart for the project with a root task group with the same name and parameters of the project. The root task group cannot be edited. The PM can then add, edit, and delete the tasks, milestones and task groups. Once a task group is deleted, all the tasks and milestones in that group is also deleted.</p>

            <h3>2. Calendar view task list</h3>
            <p>The TM interface has a interactive calendar view of the task list along with a regular task list. The calendar interface allows the user to edit the task details by clicking on the task on the calendar. They can also drag and drop the task to change the task dates and also resize the task to change it's duration.</p>
            
            <h3>Multiple User Interfaces</h3>
            <p>As it's already mentioned there are 3 different interfaces for the 3 different roles that QPM supports. 
            These interfaces are easily distinguishable since there are captioned and color coded differently from each other. 
            QPM also lets the user switch between the roles without additional logins.</p>
          </div>
