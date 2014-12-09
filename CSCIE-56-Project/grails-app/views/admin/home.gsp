<%@ page import="com.quickpm.User"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main" />

<title>Quick PM : Admin Home</title>
<asset:stylesheet src="qpm.css" />
<asset:stylesheet src="qpm-admin-theme.css" />
<style type="text/css">
.about-qpm {
	width: 597px;
	height: 600px;
	overflow: auto;
}

.qpm-center {
	text-align: center;
}
</style>
<!-- Just for debugging purposes. Don't actually copy this line! -->
<!--[if lt IE 9]><script src="../../docs-assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>

<body>

  <!-- Fixed navbar -->
  <div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle"
          data-toggle="collapse" data-target=".navbar-collapse">
          <span class="sr-only">Toggle navigation</span> <span
            class="icon-bar"></span> <span class="icon-bar"></span> <span
            class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="${createLink(controller:"admin", action: "home")}">Quick PM <small><small>Administrator</small></small></a>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="${createLink(controller:"admin", action: "home")}">Home</a></li>
          <li><a href="#about" id="about">About</a></li>
          <li><a href="#contact" id="contact">Contact</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <span class="glyphicon glyphicon-user"></span> ${(currentUser?.firstName)} ${(currentUser?.lastName)} 
              <b class="caret"></b>
            </a>
            <ul class="dropdown-menu">
              <li>
                <a href="#" id="${userInstance?.username }" class="change-password" data-toggle="tooltip" title="Change Password">
                  <span class="glyphicon glyphicon-lock"></span> Change Password 
                </a>
              </li>
              <li class="dropdown-header">Switch Role To ...</li>
              <shiro:hasAnyRole in="['ROLE_ADMIN', 'ROLE_PM']">
                <li class="pm-interface">
                  <g:link controller="PM" action="home">
                    <span class="glyphicon glyphicon-user"></span> Project Manager 
                  </g:link>
                </li>
              </shiro:hasAnyRole>
              <li class="tm-interface">
                <g:link controller="TM" action="home">
                  <span class="glyphicon glyphicon-user"></span> Team Member
                </g:link>
              </li>
              <li class="divider"></li>
              <li>
                <g:link controller="auth" action="signOut">
                  <span class="glyphicon glyphicon-log-out"></span> Log Out 
                </g:link>
              </li>
            </ul>
          </li>
        </ul>
      </div>
      <!-- /.nav-collapse -->
    </div>
  </div>

  <div class="container">
    <div class="panel panel-default">
      <div class="panel-heading">
        <div class="row">
          <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
            <h4 class="panel-title">
              Users <span class="badge">${userList.size()}</span>
            </h4>
          </div>
          <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
            <div class="pull-right">
              <a href="#" id="addNewUser" class="btn btn-xs btn-success"
                data-toggle="tooltip" title="Add New User"> <span
                class="glyphicon glyphicon-user"></span>
              </a>
            </div>
          </div>
        </div>
      </div>
      <div class="panel-body">
        <form>
          <label class="sr-only" for="search">Search</label> <input
            type="text" class="form-control" id="search"
            placeholder="Start typing to search...">
        </form>
        <table class="table table-hover" id="userList">
          <thead>
            <tr>
              <th>
                ${message(code: 'user.firstName.label', default: 'First Name')}
              </th>
              <th>
                ${message(code: 'user.lastName.label', default: 'Last Name')}
              </th>
              <th>
                ${message(code: 'user.email.label', default: 'Email')}
              </th>
              <th>
                ${message(code: 'user.active.label', default: 'Status')}
              </th>
              <th>
                ${message(code: 'user.roles.label', default: 'Roles')}
              </th>
              <th>
                ${message(code: 'user.actions.label', default: 'Actions')}
              </th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${userList}" status="i" var="userInstance">
              <tr>
                <td>
                  ${fieldValue(bean: userInstance, field: "firstName")}
                </td>
                <td>
                  ${fieldValue(bean: userInstance, field: "lastName")}
                </td>
                <td>
                  ${fieldValue(bean: userInstance, field: "username")}
                </td>
                <g:if test="${fieldValue(bean: userInstance, field: "active") == 'true'}">
                  <td><span class="label label-success">Active</span></td>
                </g:if>
                <g:else>
                  <td><span class="label label-danger">Inactive</span></td>
                </g:else>
                <td>
                  <g:if test="${(userInstance?.roles?.any { it.name == 'ROLE_ADMIN' })}">
                    <span class="glyphicon glyphicon-user admin-icon" data-toggle="tooltip" title="Administrator"></span>
                  </g:if> 
                  <g:if test="${(userInstance?.roles?.any { it.name == 'ROLE_PM' })}">
                    <span class="glyphicon glyphicon-user pm-icon" data-toggle="tooltip" title="Project Manager"></span>
                  </g:if>
                  <g:if test="${(userInstance?.roles?.any { it.name == 'ROLE_TM' })}">
                    <span class="glyphicon glyphicon-user tm-icon" data-toggle="tooltip" title="Team Member"></span>
                  </g:if>
                </td>
                <td>
                  <g:if test="${fieldValue(bean: userInstance, field: "active") == 'true'}">
                    <a href="#" 
                      id="${userInstance?.id }" 
                      type="button"
                      class="btn btn-xs btn-danger left-button deactivate-user"
                      data-toggle="tooltip" 
                      title="Deactivate"> 
                      <span class="glyphicon glyphicon-thumbs-down"></span>
                    </a>
                  </g:if> 
                  <g:else>
                    <a href="#" 
                      id="${userInstance?.id }" 
                      type="button"
                      class="btn btn-xs btn-success left-button activate-user"
                      data-toggle="tooltip" 
                      title="Activate">
                      <span class="glyphicon glyphicon-thumbs-up"></span>
                    </a>
                  </g:else> 
                  <a href="#" 
                    id="${userInstance?.id }" 
                    type="button"
                    class="btn btn-xs btn-primary right-button reset-password"
                    data-toggle="tooltip" 
                    title="Reset Password"> 
                    <span class="glyphicon glyphicon-lock"></span>
                  </a>
                  <a href="#" 
                    id="${userInstance?.id }" 
                    type="button"
                    class="btn btn-xs btn-primary right-button edit-user"
                    data-toggle="tooltip"
                    title="Edit User">
                      <span class="glyphicon glyphicon-user"></span>
                  </a>
                </td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </div>

  </div>
  <!-- /container -->

  <!-- START CHANGE PASSWORD MODAL -->

  <!-- Change Password Modal -->
  <div class="modal fade" id="changePasswordWindow" tabindex="-1"
    data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' action='' id="changePasswordForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="changePasswordLabel">Change Password</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertChangePassword">
              <strong>Oh snap!</strong>
            </div>
              <div class="form-group" id="oldPasswordDiv">
                <label for="oldPassword">Old Password</label>
                <input type="password" class="form-control" id="oldPassword" name="OldPassword" placeholder="Old password" value=""
                 data-msg-required="Please enter the old password." 
                 data-msg-maxlength="Your password cannot be more than 20 characters." 
                 data-msg-minlength="Your password cannot be less than 5 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="20"
                 data-rule-minlength="5" />
              </div>
              <div class="form-group">
                <label for="password">New Password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Password" value=""
                 data-msg-required="Please enter the new password." 
                 data-msg-maxlength="Your password cannot be more than 20 characters." 
                 data-msg-minlength="Your password cannot be less than 5 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="20"
                 data-rule-minlength="5" />
              </div>
              <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="Comfirm password" value=""
                 data-msg-required="Please confirm your new password." 
                 data-msg-maxlength="Your password cannot be more than 20 characters." 
                 data-msg-minlength="Your password cannot be less than 5 characters."
                 data-msg-equalTo="The password confirmation has to match the password above."
                 data-rule-required="true" 
                 data-rule-maxlength="20"
                 data-rule-minlength="5"
                 data-rule-equalTo="#password" />
              </div>
          </div>
          <div class="modal-footer">
            <input type="hidden" id="id" name="id">
            <input type="hidden" id="username" name="username">
            <button type="reset" class="btn btn-default"
              data-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-success btn-primary"
              id="addUserButton">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- /END CHANGE PASSWORD MODAL -->

  <!-- START NEW USER MODAL -->

  <!-- New User Modal -->
  <div class="modal fade" id="addNewUserWindow" tabindex="-1"
    data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' action='' id="addNewUserForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="addNewUserModalLabel">Add New User</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertAddNewUser">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="first_name">First Name</label> 
              <input
                type="text" class="form-control" id="first_name"
                name="first_name" placeholder="First Name" value=""
                data-msg-required="Please enter a first name."
                data-msg-maxlength="The first name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="last_name">Last Name</label> 
              <input
                type="text" class="form-control" id="last_name"
                name="last_name" placeholder="Last Name" value=""
                data-msg-required="Please enter a last name."
                data-msg-maxlength="The last name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="email">Email Address</label> 
              <input
                type="text" class="form-control" id="email" name="email"
                placeholder="Email" value=""
                data-msg-email="Please enter a valid email."
                data-msg-required="Please enter a valid email."
                data-msg-maxlength="Your email cannot be more than 255 characters"
                data-rule-email="true" data-rule-required="true"
                data-rule-maxlength="255" data-rule-notUsed="true" />
              <div id="message"></div>
            </div>
            <div class="form-group">
              <label for="confirm_email">Confirm Email Address</label> 
              <input
                type="text" class="form-control" id="confirm_email"
                name="confirm_email" placeholder="Confirm Email"
                value=""
                data-msg-email="Please confirm the email above."
                data-msg-required="Please confirm the email above."
                data-msg-maxlength="Your email cannot be more than 255 characters"
                data-msg-equalTo="The email confirmation has to match the email above."
                data-rule-email="true" data-rule-required="true"
                data-rule-maxlength="255" data-rule-equalTo="#email" />
            </div>
            <div class="form-group">
              <div class="checkbox">
                <label for="admin_access"> 
                  <input type="checkbox" id="admin_access" name="admin_access">Administrator access 
                </label>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <input type="hidden" id="user_id" name="user_id">
            <button type="reset" class="btn btn-default"
              data-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-success btn-primary"
              id="addUserButton">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- /END NEW USER MODAL -->

  <!-- About Modal -->
  <div class="modal fade" id="aboutWindow" tabindex="-1"
    data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title" id="aboutWindowModalLabel">About</h4>
        </div>
        <div class="modal-body about-qpm">
          <h1>
            <a href="http://p4.mahe-cscie-15.biz">CSCIE-15 P4 -
              Quick Project Management (QPM)</a>
          </h1>
          <p>The QPM is a project management tool. It helps users
            manage small projects like a Agile sprint. It has 3
            different user interfaces for the 3 different roles that it
            supports.</p>
          <h2>Project Manager Interface</h2>
          <p>This is the default interface. The user signing in to
            the website will be given this role by default. This
            interface provides the user with the following privileges.</p>
          <ol>
            <li>Create new projects.</li>
            <li>Edit projects details like</li>
            <ol type="a">
              <li>Project Description</li>
              <li>Start and End date</li>
              <li>Project Status - Green/Yellow/Red</li>
            </ol>
            <li>View the Gantt chart of the project</li>
            <li>Add, edit and delete tasks, task groups and
              milestones.</li>
            <li>Add team members for the projects.</li>
          </ol>
          <h2>Administrator Interface</h2>
          <p>This is the administrator's interface. This lets users
            to</p>
          <ol>
            <li>Add new users.</li>
            <li>Edit user details like</li>
            <ol type="a">
              <li>First name</li>
              <li>Last name</li>
              <li>Email address.</li>
            </ol>
            <li>Change user's privileges and provide users with
              administrator access.</li>
            <li>Activate/deactivate users.</li>
            <li>Reset passwords of users.</li>
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
          <p>The PM interface allows the user to see a dynamic Gantt
            Chart of the project. When the user creates a project, QPM
            creates a Gantt chart for the project with a root task group
            with the same name and parameters of the project. The root
            task group cannot be edited. The PM can then add, edit, and
            delete the tasks, milestones and task groups. Once a task
            group is deleted, all the tasks and milestones in that group
            is also deleted.</p>

          <h3>2. Email notifications</h3>
          <p>QPM sends out email notifications when a user is
            activated or deactivated. Those emails are just
            notifications that the user receives. QPM also sends out
            email notification when the password is reset. That email
            contains a URL that the user can use to reset his/her
            password.</p>

          <h3>3. Calendar view task list</h3>
          <p>The TM interface has a interactive calendar view of the
            task list along with a regular task list. The calendar
            interface allows the user to edit the task details by
            clicking on the task on the calendar. They can also drag and
            drop the task to change the task dates and also resize the
            task to change it's duration.</p>

          <h3>4. Admin interface</h3>
          <p>The admin interface allows the admin to manage all the
            users of QPM. All users who sign up has the Project Manager
            access. But the Admin can create Team Member users and other
            admins. The admin can also provide and revoke admin access
            to other users. There is no single admin user and the admin
            user cannot created using the UI. The admin user either has
            to be inserted directly using the database or using one of
            the following user ids. The password for all of them are
            'password'.</p>
          <ul>
            <li>barney.stinson@sharklasers.com</li>
            <li>bettywhite@sharklasers.com</li>
            <li>jerryseinfeld@sharklasers.com</li>
            <li>mahendran.nair@gmail.com</li>
            <li>vimal.nair@sharklasers.com</li>
          </ul>
          <p>The email notifications send out to the
            'sharklasers.com' email addresses can be viewed at the
            www.guerillamail.com</p>

          <h3>Multiple User Interfaces</h3>
          <p>As it's already mentioned there are 3 different
            interfaces for the 3 different roles that QPM supports.
            These interfaces are easily distinguishable since there are
            captioned and color coded differently from eah other. QPM
            also lets the user switch between the roles without
            additional logins.</p>
        </div>
        <div class="modal-footer">
          <button type="reset" class="btn btn-default"
            data-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Contact Modal -->
  <div class="modal fade" id="contactWindow" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title" id="contatModalLabel">About Quick
            Gantt</h4>
        </div>
        <div class="modal-body">
          <div class="jumbotron qpm-center">
            <h1>QPM</h1>
            <p class="lead">Quick Project Management</p>
          </div>
          <div class="row marketing">
            <div class="col-lg-12">
              <p>
                Developed as part of CSCIE-15 - P4 by <a
                  href="mailto:mahendran.sreedevi@gmail.com">Mahendran
                  Sreedevi</a>
              </p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default"
            data-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
    <!-- We inline the values (ids, action links, resource links) we want to use in our JS file -->
  <script>
    var gspVars = {
    checkEmailUrl: '${createLink(controller:"user", action: "checkEmail")}',
    activateUrl: '${createLink(controller:"user", action: "activate")}',
    deactivateUrl: '${createLink(controller:"user", action: "deactivate")}',
    getUserUrl: '${createLink(controller:"user", action: "getUser")}',
    adminHomeUrl: '${createLink(controller:"admin", action: "home")}'
    }
  </script>
</body>
</html>
