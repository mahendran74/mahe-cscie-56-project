<%@ page import="com.quickpm.Status" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main"/>

<title>Quick PM : PM Home</title>

<asset:stylesheet src="qpm.css"/>
<asset:stylesheet src="qpm-pm-theme.css"/>
<asset:stylesheet src="datepicker.css" />
<asset:stylesheet src="jsgantt.css" />
<asset:stylesheet src="jquery-simplecolorpicker.css" />
<asset:stylesheet src="jquery.simplecolorpicker-glyphicons.css" />
<asset:stylesheet src="slider.css" />
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
.simplecolorpicker{z-index:1151 !important;}
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
          <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand"  href="${createLink(controller:"PM", action: "home")}">
          Quick PM <small><small>Project Manager</small></small>
        </a>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="${createLink(controller:"PM", action: "home")}">Home</a></li>
          <li><a href="#about" id="about">About</a></li>
          <li><a href="#contact" id="contact">Contact</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> ${(currentUser?.firstName)}  ${(currentUser?.lastName)} <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li class="dropdown-header">Switch Role To ...</li>
              <shiro:hasAnyRole in="['ROLE_ADMIN']">
              <li class="admin-interface"><g:link controller="admin" action="home"><span class="glyphicon glyphicon-user"></span> Administrator </g:link></li>
              </shiro:hasAnyRole>
              <li class="tm-interface"><g:link controller="TM" action="home"><span class="glyphicon glyphicon-user"></span> Team Member</g:link></li>
              <li class="divider"></li>
              <li>
                <a href="#" id="${currentUser?.username}" class="change-password" data-toggle="tooltip" title="Change Password">
                  <span class="glyphicon glyphicon-lock"></span> Change Password 
                </a>
              </li>
              <li><g:link controller="auth" action="signOut"><span class="glyphicon glyphicon-log-out"></span> Log Out </g:link></li>
            </ul></li>
        </ul>
      </div>
      <!--/.nav-collapse -->
    </div>
  </div>

  <div class="container">
    <div class="row">
      <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        <div class="panel panel-default">
          <div class="panel-heading">
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <h3 class="panel-title">${project?.projectName}</h3>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <div class="pull-right">
                  <a id="addNewGroup" class="btn btn-xs btn-success right-button" data-toggle="tooltip" title="New Group">
                    <span class="glyphicon glyphicon-bookmark"></span>
                  </a>
                  <a id="addNewTask" class="btn btn-xs btn-success right-button" data-toggle="tooltip" title="New Task">
                    <span class="glyphicon glyphicon-minus"></span>
                  </a>
                  <a id="addNewMilestone" class="btn btn-xs btn-success right-button" data-toggle="tooltip" title="New Milestone">
                    <span class="glyphicon glyphicon-map-marker"></span>
                  </a>
                  <a id="addNewUser" class="btn btn-xs btn-success right-button" data-toggle="tooltip" title="New User">
                    <span class="glyphicon glyphicon-user"></span>
                  </a>
                </div>
              </div>
            </div>
          </div>
          <div class="panel-body">
            <div class="gantt" id="GanttChartDIV"></div>
            <input type="hidden" id="project_id" value="${project?.id}" />
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
            <h4 class="modal-title" id="addNewUserModalLabel">Add
              New User</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertAddNewUser">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="firstName">First Name</label> 
              <input
                type="text" class="form-control" id="firstName"
                name="firstName" placeholder="First Name" value=""
                data-msg-required="Please enter a first name."
                data-msg-maxlength="The first name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="last_name">Middle Initial</label> 
              <input
                type="text" class="form-control" id="middleInitial"
                name="middleInitial" placeholder="MI" value=""
                data-msg-maxlength="The last name cannot be more than 1 characters."
                data-rule-required="false" data-rule-maxlength="1" />
            </div>
            <div class="form-group">
              <label for="lastName">Last Name</label> 
              <input type="text"
                class="form-control" id="lastName" name="lastName"
                placeholder="Last Name" value=""
                data-msg-required="Please enter a last name."
                data-msg-maxlength="The last name cannot be more than 255 characters."
                data-rule-required="true" data-rule-maxlength="255" />
            </div>
            <div class="form-group">
              <label for="email">Email Address</label> 
              <input
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
              <label for="newUserPassword">New Password</label> 
              <input
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
              <label for="newUserConfirmPassword">Confirm Password</label> 
               <input type="password" class="form-control"
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
          </div>
          <div class="modal-footer">
            <input type="hidden" id="role" name="role" value="${allowedRole}">
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

  <!-- START NEW TASK MODAL -->

  <!-- New Task Modal -->
  <div class="modal fade" id="addNewTaskWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' id="addNewTaskForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="addNewTaskModalLabel">Add New Task</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertAddNewTask">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="taskDesc">Task Description</label>
              <input type="text" class="form-control" id="taskDesc" name="taskDesc" placeholder="Task Description" 
                data-msg-required="Please enter the task description." 
                data-msg-maxlength="The task description cannot be more than 255 characters." 
                data-rule-required="true" 
                data-rule-maxlength=255 />
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="startDate">Start Date</label>
                  <input type="text" class="form-control" id="startDate" name="startDate" 
                    data-msg-date="Please enter a valid date as start date." 
                    data-msg-required="Please enter a valid date as start date." 
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="endDate">End Date</label>
                  <input type="text" class="form-control" id="endDate" name="endDate"  
                    data-msg-date="Please enter a valid date as end date." 
                    data-msg-required="Please enter a valid date as end date." 
                    data-msg-greaterThan="The end date must fall after the start date." 
                    data-rule-date="true" 
                    data-rule-required="true"
                    data-rule-greaterThan="#task_start_date" />
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="status">Status</label> 
                  <g:select class="form-control" name="status" from="${Status.values()}" keys="${Status.values()*.name()}" />
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="color">Color</label> <br>
                  <select name="color" id="color">
                    <option value="#7bd148">Green</option>
                    <option value="#5484ed">Bold blue</option>
                    <option value="#a4bdfc">Blue</option>
                    <option value="#46d6db">Turquoise</option>
                    <option value="#7ae7bf">Light green</option>
                    <option value="#51b749">Bold green</option>
                    <option value="#fbd75b">Yellow</option>
                    <option value="#ffb878">Orange</option>
                    <option value="#ff887c">Red</option>
                    <option value="#dc2127">Bold red</option>
                    <option value="#dbadff">Purple</option>
                    <option value="#e1e1e1">Gray</option>
                  </select>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label for="assignedTo">Task Assigned To</label>
              <select class="form-control" id="assignedTo" name="assignedTo">
                <option value="" selected disabled>Select User</option>
                <g:each in="${tmList}" status="i" var="userInstance">
                <option value="${userInstance?.id}">
                  ${userInstance?.firstName} ${userInstance?.lastName}
                </option>
                </g:each>
              </select>
            </div>
            <div class="form-group">
              <label for="taskGroup ">Task Group</label> 
              <select class="form-control" id="taskGroup" name="taskGroup">
                <option value="" selected disabled>Select Task Group</option>
                <g:each in="${project.taskGroups}" status="i" var="groupInstance">
                <option value="${groupInstance?.id}">
                  ${groupInstance?.groupName}
                </option>
                </g:each>
              </select>
            </div>
            <div class="form-group">
              <label for="dependsOn ">Depends On</label> 
              <select class="form-control" id="dependsOn" name="dependsOn">
                <option value="" selected disabled>Select Task</option>
                <g:each in="${taskList}" status="i" var="taskInstance">
                <option value="${taskInstance?.id}">
                  ${taskInstance?.taskDesc}
                </option>
                </g:each>
              </select>
            </div>
            <div class="form-group">
              <label for="per_complete">Percentage Complete</label>
              <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
              <input type="text" value="0" id="percentageCompleteSlide" name="percentageCompleteSlide" 
              data-slider-min="0" 
              data-slider-max="100" 
              data-slider-value="0" 
              data-slider-step="1"/>
              <input type="hidden" id="percentageComplete" name="percentageComplete" />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <div id="addTaskAction">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-primary" id="taskSaveButton">Save task</button>
            </div>
            <div id="editTaskAction">
              <input type="hidden" id="taskID" name="taskID" />
              <input type="hidden" name="projectID" id="projectID" value="${project?.id}" />
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="button" class="btn btn-danger" id="taskDelButton">Delete task</button>
              <button type="submit" class="btn btn-primary" id="taskEditButton">Save task</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- /END NEW TASK MODAL -->
  
<!-- START NEW GROUP MODAL -->

  <!-- New Group Modal -->
  <div class="modal fade" id="addNewGroupWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' id="addNewGroupForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="addNewGroupModalLabel">Add New Group</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertAddNewGroup">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="groupName">Group Name</label>
              <input type="text" class="form-control" id="groupName" name="groupName" placeholder="Group Name" value="" 
                data-msg-required="Please enter the group name." 
                data-msg-maxlength="The group name cannot be more than 255 characters." 
                data-rule-required="true" 
                data-rule-maxlength="255" />
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="startDate">Start Date</label>
                  <input type="text" class="form-control" id="startDate" name="startDate" value="" 
                    data-msg-date="Please enter a valid date as start date." 
                    data-msg-required="Please enter a valid date as start date." 
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="endDate">End Date</label>
                  <input type="text" class="form-control" id="endDate" name="endDate" value="" 
                    data-msg-date="Please enter a valid date as end date." 
                    data-msg-required="Please enter a valid date as end date." 
                    data-msg-greaterThan="The end date must fall after the start date." 
                    data-rule-date="true" 
                    data-rule-required="true"
                    data-rule-greaterThan="#group_start_date" />
                </div>
              </div>
            </div>
            <div class="form-group">
              <label for="parentGroup">Parent Group</label> 
              <select class="form-control" id="parentGroup" name="parentGroup">
                <option value="" selected disabled>Select Parent Group</option>
                <g:each in="${project.taskGroups}" status="i" var="groupInstance">
                <option value="${groupInstance?.id}">
                  ${groupInstance?.groupName}
                </option>
                </g:each>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <div id="addGroupAction">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-primary" id="groupSaveButton">Save group</button>
            </div>
            <div id="editGroupAction">
              <input type="hidden" name="projectID" id="projectID" value="${project?.id}" />
              <input type="hidden" id="groupID" name="groupID" />
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="button" class="btn btn-danger" id="groupDelButton">Delete group</button>
              <button type="submit" class="btn btn-primary" id="groupEditButton">Save group</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- /END NEW GROUP MODAL -->
  
  <!-- START NEW MILESTONE MODAL -->

  <!-- New Milestone Modal -->
  <div class="modal fade" id="addNewMilestoneWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' id="addNewMilestoneForm">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="addNewMilestoneModalLabel">Add New Milestone</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertAddNewMilestone">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="milestoneDesc">Milestone Description</label>
              <input type="text" class="form-control" id="milestoneDesc" name="milestoneDesc" placeholder="Milestone Description" value="" 
                data-msg-required="Please enter the milestone description." 
                data-msg-maxlength="The milestone description cannot be more than 255 characters." 
                data-rule-required="true" 
                data-rule-maxlength="255" />
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="milestoneDate">Milestone Date</label>
                  <input type="text" class="form-control" id="milestoneDate" name="milestoneDate" value="" 
                    data-msg-date="Please enter a valid date as milestone date." 
                    data-msg-required="Please enter a valid date as milestone date." 
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
            </div>
            <div class="form-group">
              <label for="assignedTo">Milestone Assigned To</label>
              <select class="form-control" id="assignedTo" name="assignedTo">
                <option value="" selected disabled>Select User</option>
                <g:each in="${tmList}" status="i" var="userInstance">
                <option value="${userInstance?.id}">
                  ${userInstance?.firstName} ${userInstance?.lastName}
                </option>
                </g:each>
              </select>
            </div>
            <div class="form-group">
              <label for="taskGroup ">Task Group</label> 
              <select class="form-control" id="taskGroup" name="taskGroup">
                <option value="" selected disabled>Select Task Group</option>
                <g:each in="${project.taskGroups}" status="i" var="groupInstance">
                <option value="${groupInstance?.id}">
                  ${groupInstance?.groupName}
                </option>
                </g:each>
              </select>
            </div>
          </div>
          <div class="modal-footer">
            <div id="addMilestoneAction">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-primary" id="milestoneSaveButton">Save milestone</button>
            </div>
            <div id="editMilestoneAction">
              <input type="hidden" id="milestoneID" name="milestoneID" />
              <input type="hidden" name="projectID" id="projectID" value="${project?.id}" />
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="button" class="btn btn-danger" id="milestoneDelButton">Delete milestone</button>
              <button type="submit" class="btn btn-primary" id="milestoneEditButton">Save milestone</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
  <!-- /END NEW MILESTONE MODAL -->
  
  
  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
  <asset:javascript src="application.js"/>
  <script type="text/javascript">
  var gspVars = {
		    checkEmailUrl: '${createLink(controller:"user", action: "checkEmail")}',
		    homeUrl: '${createLink(controller:"PM", action: "gantt")}' + '/' + '${project?.id}',
		    altHomeUrl: '${createLink(controller:"PM", action: "gantt")}' + '/' + '${project?.id}',
		    changePasswordUrl: '${createLink(controller:"PM", action: "changePassword")}',
		    addUserUrl: '${createLink(controller:"PM", action: "addUser")}' ,
		    getProjectUrl: '${createLink(controller:"PM", action: "getProject")}',
		    getGroupUrl: '${createLink(controller:"PM", action: "getGroup")}',
		    addGroupUrl: '${createLink(controller:"PM", action: "addGroup")}',    
		    updateGroupUrl: '${createLink(controller:"PM", action: "updateGroup")}',
		    deleteGroupUrl: '${createLink(controller:"PM", action: "deleteGroup")}',
		    getTaskUrl: '${createLink(controller:"PM", action: "getTask")}',
	      addTaskUrl: '${createLink(controller:"PM", action: "addTask")}',    
	      updateTaskUrl: '${createLink(controller:"PM", action: "updateTask")}',
	      deleteTaskUrl: '${createLink(controller:"PM", action: "deleteTask")}',
	      getMilestoneUrl: '${createLink(controller:"PM", action: "getMilestone")}',
	      addMilestoneUrl: '${createLink(controller:"PM", action: "addMilestone")}',    
	      updateMilestoneUrl: '${createLink(controller:"PM", action: "updateMilestone")}',
	      deleteMilestoneUrl: '${createLink(controller:"PM", action: "deleteMilestone")}',
		    currentUserId: '${currentUser?.id}',
        projectId: '${project?.id}'
		    }
    var d = "${raw(projectXMLString)}";
    var resList = '';
    var groupList = '';
    var dependList = '';
    var vGanttChart = new JSGantt.GanttChart('vGanttChart', document
        .getElementById('GanttChartDIV'), 'day');
    vGanttChart.setShowRes(1);
    vGanttChart.setShowDur(1);
    vGanttChart.setShowComp(1);
    vGanttChart.setCaptionType('Resource');
    if (vGanttChart) {
      JSGantt.loadXMLString(d, vGanttChart);
      vGanttChart.Draw();
      vGanttChart.DrawDependencies();
    } else {
      alert("not defined");
    }
  </script>
</body>
</html>
