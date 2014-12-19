<%@ page import="com.quickpm.Status" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main"/>

<title>Quick PM : TM Home</title>

<asset:stylesheet src="qpm.css"/>
<asset:stylesheet src="qpm-tm-theme.css"/>
<asset:stylesheet src="fullcalendar.css"/>
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
        <a class="navbar-brand"  href="${createLink(controller:"TM", action: "home")}">Quick PM <small><small>Team Member</small></small></a>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="${createLink(controller:"TM", action: "home")}">Home</a></li>
          <li><a href="#about" id="about">About</a></li>
          <li><a href="#contact" id="contact">Contact</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> ${(currentUser?.firstName)}  ${(currentUser?.lastName)} <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <shiro:hasAnyRole in="['ROLE_ADMIN', 'ROLE_PM']">
                <li class="dropdown-header">Switch Role To ...</li>
                <shiro:hasAnyRole in="['ROLE_ADMIN']">
                  <li class="admin-interface"><g:link controller="admin" action="home"><span class="glyphicon glyphicon-user"></span> Administrator </g:link></li>
                </shiro:hasAnyRole>
                <shiro:hasAnyRole in="['ROLE_ADMIN', 'ROLE_PM']">
                  <li class="pm-interface"><g:link controller="PM" action="home"><span class="glyphicon glyphicon-user"></span> Project Manager </g:link></li>
                </shiro:hasAnyRole>
              </shiro:hasAnyRole>
              <li>
                <a href="#" id="${currentUser?.username}" class="change-password" data-toggle="tooltip" title="Change Password"> 
                  <span class="glyphicon glyphicon-lock"></span> Change Password
                </a>
              </li>
              <li class="divider"></li>
              <li><g:link controller="auth" action="signOut"><span class="glyphicon glyphicon-log-out"></span> Log Out</g:link></li>
            </ul></li>
        </ul>
      </div>
      <!--/.nav-collapse -->
    </div>
  </div>
  <div class="container">
    <ul id="pm-home-tabs" class="nav nav-pills">
      <li><a href="#calendar" data-toggle="tab">Calendar</a></li>
      <li><a href="#tasks" data-toggle="tab">Tasks <span class="badge"> ${taskList.size()}</span></a></li>
    </ul>
    <div class="tab-content">
      <input type="hidden" id="userID" value="${(currentUser?.id)}" />
      <div class="tab-pane ${calendarTabActive}" id="calendar">
        <div id='calendar'></div>
      </div>
      <div class="tab-pane ${tasksTabActive}" id="tasks">
        <div class="panel panel-default">
          <div class="panel-heading">
            <h3 class="panel-title">Tasks</h3>
          </div>
          <div class="panel-body">
            <table class="table table-hover table-condensed" id="taskList">
              <thead>
                <tr>
                  <th>${message(code: 'task.name.label', default: 'Task Desc')}</th>
                  <th>${message(code: 'task.start.date.label', default: 'Start Date')}</th>
                  <th>${message(code: 'task.end.date.label', default: 'Due Date')}</th>
                  <th>${message(code: 'task.status.label', default: 'Status')}</th>
                  <th>${message(code: 'task.percentageComplete.label', default: 'Progress')}</th>
                  <th>${message(code: 'task.actions.label', default: 'Actions')}</th>
                </tr>
              </thead>
              <tbody>
              <g:each in="${taskList}" status="i" var="taskInstance">
                <tr>
                  <td>${taskInstance?.taskDesc }</td>
                  <td><g:formatDate format="MM/dd/yyyy" date="${taskInstance?.startDate}"/></td>
                  <td><g:formatDate format="MM/dd/yyyy" date="${taskInstance?.endDate}"/></td>
                  <td>
                    <g:if test="${fieldValue(bean: taskInstance, field: "status") == Status.PLANNED.value}">
                      <span class="label label-primary">Planned</span>
                    </g:if>
                    <g:elseif test="${fieldValue(bean: taskInstance, field: "status") == Status.IN_PROGRESS.value}">
                      <span class="label label-info">In Progress</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: taskInstance, field: "status") == Status.ON_HOLD.value}">
                      <span class="label label-warning">On Hold</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: taskInstance, field: "status") == Status.COMPLETE.value}">
                      <span class="label label-success">Complete</span>
                    </g:elseif>
                    <g:elseif test="${fieldValue(bean: taskInstance, field: "status") == Status.CANCELLED.value}">
                      <span class="label label-default">Cancelled</span>
                    </g:elseif>
                  </td>
                  <td>
                    <div class="progress">
                      <div class="progress-bar" style="width: ${taskInstance?.percentageComplete }%;">
                        <span class="sr-only">${taskInstance?.percentageComplete}% Complete</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <a href="#" id="${taskInstance?.id }" type="button" class="btn btn-xs btn-primary left-button edit-task" data-toggle="tooltip" title="Edit Task Details">
                      <span class="glyphicon glyphicon-edit"></span>
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
  
  <!-- START EDIT TASK MODAL -->

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
              <label for="dtaskDesc">Task Description</label>
              <input type="text" class="form-control" id="dtaskDesc" name="dtaskDesc" placeholder="Task Description" disabled/>
              <input type="hidden" id="taskDesc" name="taskDesc" />
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
              <label for="dassignedTo">Task Assigned To</label>
              <select class="form-control" id="dassignedTo" name="dassignedTo" disabled>
                <option value="" selected disabled>Select User</option>
                <g:each in="${tmList}" status="i" var="userInstance">
                <option value="${userInstance?.id}">
                  ${userInstance?.firstName} ${userInstance?.lastName}
                </option>
                </g:each>
              </select>
              <input type="hidden" id="assignedTo" name="assignedTo" />
            </div>
            <div class="form-group">
              <label for="dtaskGroup ">Task Group</label> 
              <select class="form-control" id="dtaskGroup" name="dtaskGroup" disabled>
                <option value="" selected disabled>Select Task Group</option>
                <g:each in="${taskGroupList}" status="i" var="groupInstance">
                <option value="${groupInstance?.id}">
                  ${groupInstance?.groupName}
                </option>
                </g:each>
              </select>
              <input type="hidden" id="taskGroup" name="taskGroup" />
            </div>
            <div class="form-group">
              <label for="ddependsOn ">Depends On</label> 
              <select class="form-control" id="ddependsOn" name="ddependsOn" disabled>
                <option value="" selected disabled>Select Task</option>
                <g:each in="${taskList}" status="i" var="taskInstance">
                <option value="${taskInstance?.id}">
                  ${taskInstance?.taskDesc}
                </option>
                </g:each>
              </select>
              <input type="hidden" id="dependsOn" name="dependsOn" />
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
              <input type="hidden" id="source" name="source" />
              <input type="hidden" name="projectID" id="projectID"/>
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

  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
      <asset:javascript src="application_tm.js"/>
      <asset:javascript src="bootbox.js"/>
      <asset:javascript src="bootstrap-datepicker.js"/>
      <asset:javascript src="bootstrap-slider.js"/>
      <asset:javascript src="moment.js"/>
      <asset:javascript src="fullcalendar.js"/>
      <asset:javascript src="jquery.simplecolorpicker.js"/>
      <asset:javascript src="jquery.validate.js"/>
      <asset:javascript src="qpm-admin.js"/>
      <asset:javascript src="qpm-home.js"/>
      <asset:javascript src="qpm-pm.js"/>
      <asset:javascript src="qpm-tm.js"/>
      
    <script type="text/javascript">
    var gspVars = {
      homeUrl: '${createLink(controller:"TM", action: "home")}',
      altHomeUrl: '${createLink(controller:"TM", action: "home") + "?show=tasks"}', 
      changePasswordUrl: '${createLink(controller:"TM", action: "changePassword")}',
      getTasksUrl: '${createLink(controller:"TM", action: "getTasks")}',
      getTaskUrl: '${createLink(controller:"TM", action: "getTask")}',
      updateTaskUrl: '${createLink(controller:"TM", action: "updateTask")}',
      currentUserId: '${currentUser?.id}'
    }
  </script>


</body>
</html>
