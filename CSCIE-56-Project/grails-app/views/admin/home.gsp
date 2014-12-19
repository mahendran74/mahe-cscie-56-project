<%@ page import="com.quickpm.User"%>
<%@ page import="com.quickpm.Status" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main" />

<title>Quick PM : Admin Home</title>
<asset:stylesheet src="qpm.css" />
<asset:stylesheet src="qpm-admin-theme.css" />
<asset:stylesheet src="datepicker.css" />
<style type="text/css">
.about-qpm {
	width: 597px;
	height: 600px;
	overflow: auto;
}

.qpm-center {
	text-align: center;
}

.datepicker{z-index:1151 !important;}
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
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
          <span class="sr-only">Toggle navigation</span> 
          <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="${createLink(controller:"admin", action: "home")}">
          Quick PM <small><small>Administrator</small></small>
        </a>
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
              <span class="glyphicon glyphicon-user"></span> ${(currentUser?.firstName)} ${(currentUser?.lastName)} <b class="caret"></b>
          </a>
            <ul class="dropdown-menu">
              <li class="dropdown-header">Switch Role To ...</li>
              <shiro:hasAnyRole in="['ROLE_ADMIN', 'ROLE_PM']">
                <li class="pm-interface">
                  <g:link controller="PM" action="home">
                    <span class="glyphicon glyphicon-user"></span>  Project Manager 
                  </g:link>
                </li>
              </shiro:hasAnyRole>
              <li class="tm-interface">
                <g:link controller="TM" action="home">
                  <span class="glyphicon glyphicon-user"></span>  Team Member
                </g:link>
              </li>
              <li>
                <a href="#" id="${currentUser?.username}" class="change-password" data-toggle="tooltip" title="Change Password"> 
                  <span class="glyphicon glyphicon-lock"></span> Change Password
                </a>
              </li>
              <li class="divider"></li>
              <li>
                <g:link controller="auth" action="signOut"><span class="glyphicon glyphicon-log-out"></span> Log Out 
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
    <ul id="admin-home-tabs" class="nav nav-pills">
      <li><a href="#users" data-toggle="tab">Users <span class="badge"> ${userList.size()}</span></a>
      </li>
      <li><a href="#projects" data-toggle="tab">Projects <span class="badge"> ${projectList.size()}</span></a>
      </li>
    </ul>
    <div class="tab-content">
      <div class="tab-pane ${userTabActive}" id="users">
        <div class="panel panel-default">
          <div class="panel-heading">
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <h4 class="panel-title">
                  Users
                </h4>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <div class="pull-right">
                  <a href="#" id="addNewUser"
                    class="btn btn-xs btn-success" data-toggle="tooltip"
                    title="Add New User"> 
                      <span class="glyphicon glyphicon-user"></span>
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
                  <th>${message(code: 'user.firstName.label', default: 'First Name')}</th>
                  <th>${message(code: 'user.lastName.label', default: 'Last Name')}</th>
                  <th>${message(code: 'user.email.label', default: 'Email')}</th>
                  <th>${message(code: 'user.active.label', default: 'Status')}</th>
                  <th>${message(code: 'user.roles.label', default: 'Roles')}</th>
                  <th>${message(code: 'user.actions.label', default: 'Actions')}</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${userList}" status="i" var="userInstance">
                  <tr>
                    <td>${fieldValue(bean: userInstance, field: "firstName")}</td>
                    <td>${fieldValue(bean: userInstance, field: "lastName")}</td>
                    <td>${fieldValue(bean: userInstance, field: "username")}</td>
                    <td>
                      <g:if test="${fieldValue(bean: userInstance, field: "active") == 'true'}">
                        <span class="label label-success">Active</span>
                      </g:if>
                      <g:else>
                        <span class="label label-danger">Inactive</span>
                      </g:else>
                    </td>
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
                      <a href="#" 
                        id="${userInstance?.id }" 
                        type="button"
                        class="btn btn-xs btn-primary right-button delete-user"
                        data-toggle="tooltip"
                        title="Delete User">
                          <span class="glyphicon glyphicon-trash"></span>
                      </a>
                    </td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="tab-pane ${projectTabActive}" id="projects">
      <div class="panel panel-default">
          <div class="panel-heading">
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <h4 class="panel-title">
                  Projects 
                </h4>
              </div>
            </div>
          </div>
          <div class="panel-body">
            <form>
              <label class="sr-only" for="search">Search</label> <input
                type="text" class="form-control" id="search"
                placeholder="Start typing to search...">
            </form>
            <table class="table table-hover" id="projectList">
              <thead>
                <tr>
                  <th>${message(code: 'project.name.label', default: 'Project Name')}</th>
                  <th>${message(code: 'project.start.date.label', default: 'Start Date')}</th>
                  <th>${message(code: 'project.end.date.label', default: 'End Date')}</th>
                  <th>${message(code: 'project.manager.label', default: 'Project Manager')}</th>
                  <th>${message(code: 'project.status.label', default: 'Status')}</th>
                  <th>${message(code: 'project.actions.label', default: 'Actions')}</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${projectList}" status="i" var="projectInstance">
                  <tr>
                    <td>
                      ${fieldValue(bean: projectInstance, field: "projectName")}
                    </td>
                    <td>
                      <g:formatDate format="MM/dd/yyyy" date="${projectInstance?.startDate}"/>
                    </td>
                    <td>
                      <g:formatDate format="MM/dd/yyyy" date="${projectInstance?.endDate}"/>
                    </td>
                    <td>
                      ${fieldValue(bean: projectInstance.projectManager, field: "firstName")} ${fieldValue(bean: projectInstance.projectManager, field: "lastName")}
                    </td>
                    <td>
                    <g:if test="${fieldValue(bean: projectInstance, field: "status") == Status.PLANNED.value}">
                      <span class="label label-primary">Planned</span>
                    </g:if>
                    <g:elseif test="${fieldValue(bean: projectInstance, field: "status") == Status.IN_PROGRESS.value}">
                      <span class="label label-info">In Progress</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: projectInstance, field: "status") == Status.ON_HOLD.value}">
                      <span class="label label-warning">On Hold</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: projectInstance, field: "status") == Status.COMPLETE.value}">
                      <span class="label label-success">Complete</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: projectInstance, field: "status") == Status.CANCELLED.value}">
                      <span class="label label-default">Cancelled</span>
                    </g:elseif>
                    </td>               
                    <td>
                      <a href="#" 
                        id="${projectInstance?.id }" 
                        type="button"
                        class="btn btn-xs btn-primary right-button admin-edit-project"
                        data-toggle="tooltip" 
                        title="Edit Project"> 
                        <span class="glyphicon glyphicon-edit"></span>
                      </a>
                      <a href="#" 
                        id="${projectInstance?.id }" 
                        type="button"
                        class="btn btn-xs btn-primary right-button delete-project"
                        data-toggle="tooltip"
                        title="Delete Project">
                        <span class="glyphicon glyphicon-trash"></span>
                      </a>
                    </td>
                  </tr>
                </g:each>
              </tbody>
            </table>
          </div>
        </div>
        
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
              <strong></strong>
            </div>
            <div class="form-group" id="oldPasswordDiv">
              <label for="oldPassword">Old Password</label> <input
                type="password" class="form-control" id="oldPassword"
                name="oldPassword" placeholder="Old password" value=""
                data-msg-required="Please enter the old password."
                data-msg-maxlength="Your password cannot be more than 20 characters."
                data-msg-minlength="Your password cannot be less than 5 characters."
                data-rule-required="true" data-rule-maxlength="20"
                data-rule-minlength="5" />
            </div>
            <div class="form-group">
              <label for="password">New Password</label> <input
                type="password" class="form-control" id="password"
                name="password" placeholder="Password" value=""
                data-msg-required="Please enter the new password."
                data-msg-maxlength="Your password cannot be more than 20 characters."
                data-msg-minlength="Your password cannot be less than 5 characters."
                data-rule-required="true" data-rule-maxlength="20"
                data-rule-minlength="5" />
            </div>
            <div class="form-group">
              <label for="confirmPassword">Confirm Password</label> <input
                type="password" class="form-control"
                id="confirmPassword" name="confirmPassword"
                placeholder="Comfirm password" value=""
                data-msg-required="Please confirm your new password."
                data-msg-maxlength="Your password cannot be more than 20 characters."
                data-msg-minlength="Your password cannot be less than 5 characters."
                data-msg-equalTo="The password confirmation has to match the password above."
                data-rule-required="true" data-rule-maxlength="20"
                data-rule-minlength="5" data-rule-equalTo="#password" />
            </div>
          </div>
          <div class="modal-footer">
            <input type="hidden" id="id" name="id"> <input
              type="hidden" id="username" name="username">
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
              <label for="firstName">First Name</label> <input
                type="text" class="form-control" id="firstName"
                name="firstName" placeholder="First Name" value=""
                data-msg-required="Please enter a first name."
                data-msg-maxlength="The first name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="last_name">Middle Initial</label> <input
                type="text" class="form-control" id="middleInitial"
                name="middleInitial" placeholder="MI" value=""
                data-msg-maxlength="The last name cannot be more than 1 characters."
                data-rule-required="false" data-rule-maxlength="1" />
            </div>
            <div class="form-group">
              <label for="lastName">Last Name</label> <input type="text"
                class="form-control" id="lastName" name="lastName"
                placeholder="Last Name" value=""
                data-msg-required="Please enter a last name."
                data-msg-maxlength="The last name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="email">Email Address</label> <input
                type="text" class="form-control" id="username"
                name="username" placeholder="Email" value=""
                data-msg-email="Please enter a valid email."
                data-msg-required="Please enter a valid email."
                data-msg-maxlength="Your email cannot be more than 255 characters"
                data-rule-email="true" data-rule-required="true"
                data-rule-maxlength="255" data-rule-notUsed="true" />
              <div id="message"></div>
            </div>
            <div class="form-group" id="passwordDiv">
              <label for="newUserPassword">New Password</label> <input
                type="password" class="form-control"
                id="newUserPassword" name="newUserPassword"
                placeholder="Password" value=""
                data-msg-required="Please enter the new password."
                data-msg-maxlength="Your password cannot be more than 20 characters."
                data-msg-minlength="Your password cannot be less than 5 characters."
                data-rule-required="true" data-rule-maxlength="20"
                data-rule-minlength="5" />
            </div>
            <div class="form-group" id="confirmPasswordDiv">
              <label for="newUserConfirmPassword">Confirm
                Password</label> <input type="password" class="form-control"
                id="newUserConfirmPassword"
                name="newUserConfirmPassword"
                placeholder="Comfirm password" value=""
                data-msg-required="Please confirm your new password."
                data-msg-maxlength="Your password cannot be more than 20 characters."
                data-msg-minlength="Your password cannot be less than 5 characters."
                data-msg-equalTo="The password confirmation has to match the password above."
                data-rule-required="true" data-rule-maxlength="20"
                data-rule-minlength="5"
                data-rule-equalTo="#newUserPassword" />
            </div>
            <div class="form-group">
              <label for="role">Role</label> 
              <select class="form-control" id="role" name="role">
                <option value="" selected disabled>Select role</option>
                <g:each in="${roleList}" status="i" var="roleInstance">
                <option value="${roleInstance?.id}">
                  ${roleInstance?.description}
                </option>
                </g:each>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <input type="hidden" id="user_id" name="user_id">
            <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-success btn-primary" id="addUserButton">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <!-- /END NEW USER MODAL -->
  
  <!-- START THE EDIT PROJECT MODAL -->

  <!-- Edit Project Modal -->
  <div class="modal fade" id="editProjectWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' action='' id="editProjectForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="editProjectModalLabel">Edit Project Details</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertEditProject">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="projectName">Project Name</label> <input
                type="text" class="form-control"
                id="projectName" name="projectName"
                placeholder="Name of the project" value="" autofocus
                data-msg-required="Please enter a project name."
                data-msg-maxlength="The project name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="projectDesc">Project Description</label> <input
                type="text" class="form-control"
                id="projectDesc" name="projectDesc"
                placeholder="Short description" value=""
                data-msg-maxlength="The project description cannot be more than 255 characters."
                data-rule-maxlength="255" />
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="startDate">Start Date</label> 
                  <input
                    type="text" class="form-control"
                    id="startDate" name="startDate" value=""
                    data-msg-date="Please enter a valid date as start date."
                    data-msg-required="Please enter a valid date as start date."
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="endDate">End Date</label> 
                  <input
                    type="text" class="form-control"
                    id="endDate" name="endDate" value=""
                    data-msg-date="Please enter a valid date as end date."
                    data-msg-required="Please enter a valid date as end date."
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
            </div>
            <div class="form-group">
              <label for="status">Status</label> 
              <g:select class="form-control" name="status" from="${Status.values()}" keys="${Status.values()*.name()}" />
            </div>
            <div class="form-group">
              <label for="projectManager">Project Manager</label> 
              <select class="form-control" id="projectManager" name="projectManager"
              data-rule-required="true" data-msg-required="Please select a Project Manager for this project.">
                <option value="" selected disabled>Select PM</option>
                <g:each in="${pmList}" status="i" var="pmInstance">
                <option value="${pmInstance?.id}">
                  ${pmInstance?.firstName} ${pmInstance?.lastName}
                </option>
                </g:each>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <input type="hidden" id="projectID" name="projectID">
            <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-success btn-primary"
              id="editProjectSaveButton">Save</button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- /END THE NEW PROJECT MODAL -->
  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
  <!-- We inline the values (ids, action links, resource links) we want to use in our JS file -->
    <asset:javascript src="application.js"/>
  <script>
    var gspVars = {
    checkEmailUrl: '${createLink(controller:"user", action: "checkEmail")}',
    homeUrl: '${createLink(controller:"admin", action: "home")}',
    altHomeUrl: '${createLink(controller:"admin", action: "home") + "?show=project"}' ,
    changePasswordUrl: '${createLink(controller:"admin", action: "changePassword")}',
    resetPasswordUrl: '${createLink(controller:"admin", action: "resetPassword")}' ,
    addUserUrl: '${createLink(controller:"admin", action: "addUser")}' ,
    updateUserUrl: '${createLink(controller:"admin", action: "updateUser")}',
    deleteUserUrl: '${createLink(controller:"admin", action: "deleteUser")}',
    activateUserUrl: '${createLink(controller:"admin", action: "activate")}',
    deactivateUserUrl: '${createLink(controller:"admin", action: "deactivate")}',
    getUserUrl: '${createLink(controller:"admin", action: "getUser")}',
    getProjectUrl: '${createLink(controller:"admin", action: "getProject")}',
    updateProjectUrl: '${createLink(controller:"admin", action: "updateProject")}',
    deleteProjectUrl: '${createLink(controller:"admin", action: "deleteProject")}'
			}
  </script>
</body>
</html>
