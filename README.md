CSCI E-56 Final Project - Quick Project Management (QPM)
The QPM is a project management tool. It helps users manage small projects like a Agile
sprint. It has 3 different user interfaces for the 3 different roles that it supports.
Project Manager Interface
This is the default interface. The user signing in to the website will be given this role by
default. This interface provides the user with the following privileges.
1. Create new projects.
2. Edit projects details like
a. Project Description
b. Start and End date
c. Project Status - Planned/In Progress/Completed
3. View the Gantt chart of the project
4. Add, edit and delete tasks, task groups and milestones.
5. Add team members for the projects.
Administrator Interface
The admin interface allows the admin to manage all the users of QPM. All users who sign up
has the Project Manager access. But the Admin can create Team Member users and other
admins. The admin can also provide and revoke admin access to other users. Admin can
work as a Project Manager and a Team Member. The admin user either has to be inserted
directly using the database. The Bootstrap config creates the following users.
Name User name Password Role Status
Admin User admin@test.com password Administrator Active
PM User pm@test.com password Project Manager Active
TM User tm@test.com password Team Member ActiveTM UserDisabled tmd@test.com password Team Member Inactive
Team Member Interface
This is interface allows users to
1. View task list
2. Edit task details
3. Change task status/start date and end date
Here are the main features of this site
1. Dynamic Gantt chart
The PM interface allows the user to see a dynamic Gantt Chart of the project. When the
user creates a project, QPM creates a Gantt chart for the project with a root task group with
the same name and parameters of the project. The root task group cannot be edited. The
PM can then add, edit, and delete the tasks, milestones and task groups. Once a task
group is deleted, all the tasks and milestones in that group is also deleted.
2. Calendar view task list
The TM interface has a interactive calendar view of the task list along with a regular task list.
The calendar interface allows the user to edit the task details by clicking on the task on the
calendar. They can also drag and drop the task to change the task dates and also resize the
task to change it's duration.
Multiple User Interfaces
As it's already mentioned there are 3 different interfaces for the 3 different roles that QPM
supports. These interfaces are easily distinguishable since there are captioned and color
coded differently from each other. QPM also lets the user switch between the roles without
additional logins.
