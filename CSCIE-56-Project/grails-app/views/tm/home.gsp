
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main"/>

<title>Quick PM : TM Home</title>

<asset:stylesheet src="qpm.css"/>
<asset:stylesheet src="qpm-tm-theme.css"/>
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
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
          <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/tm/home">Quick PM <small><small>Team Member</small></small></a>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="/tm/home">Home</a></li>
          <li><a href="#about" id="about">About</a></li>
          <li><a href="#contact" id="contact">Contact</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> ${(userInstance?.firstName)}  ${(userInstance?.lastName)} <b class="caret"></b></a>
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
      <li><a href="#tasks" data-toggle="tab">Tasks <span class="badge"><?=count($tasks)?></span></a></li>
    </ul>
    <div class="tab-content">
      <input type="hidden" id="user_id" value="<?=$user->user_id?>" />
      <div class="tab-pane active" id="calendar">
        <div id='calendar'></div>
      </div>
      <div class="tab-pane" id="tasks">
        <div class="panel panel-default">
          <div class="panel-heading">
            <h3 class="panel-title">Tasks</h3>
          </div>
          <div class="panel-body">
            <table class="table table-hover table-condensed" id="userList">
              <thead>
                <tr>
                  <th>Task Desc</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th>% Complete</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
              <?php foreach($tasks as $task): ?>
                <tr>
                  <td><?=$task['task_desc']?></td>
                  <td><?=date('m/d/Y', strtotime($task['end_date']))?></td>
                  <td>
                    <div class="progress">
                      <div class="progress-bar" style="width: <?=$task['per_complete']?>%;">
                        <span class="sr-only"><?=$task['per_complete']?>% Complete</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <?php if($task['status'] == 'green'): ?>
                    <span class="label label-success">Green</span>
                    <?php elseif($task['status'] == 'yellow'): ?>
                    <span class="label label-warning">Yellow</span>
                    <?php elseif($task['status'] == 'red'): ?>
                    <span class="label label-danger">Red</span>
                    <?php endif;?>
                  </td>
                  <td>
                    <a href="#" id="<?=$task['task_id']?>" type="button" class="btn btn-xs btn-primary left-button edit-task" data-toggle="tooltip" title="Edit Task Details">
                      <span class="glyphicon glyphicon-edit"></span>
                    </a>
                  </td>
                </tr>
               <?php endforeach; ?>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- /container -->

  <!-- START EDIT TASK MODAL -->

 <!-- New Task Modal -->
  <div class="modal fade" id="editTaskWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
      <div class="modal-content">
        <form method='POST' action='' id="editTaskForm">
        <input type="hidden" id="task_id" name="task_id"/>
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title" id="editTaskModalLabel">Edit Task</h4>
          </div>
          <div class="modal-body">
            <div class="alert alert-danger" id="alertEditTask">
              <strong>Oh snap!</strong>
            </div>
            <div class="form-group">
              <label for="task_desc">Task Description</label>
              <p id="task_task_desc" name="task_desc" class="form-control-static">12/03/2012</p>
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="start_date">Start Date</label>
                  <input type="text" class="form-control" id="task_start_date" name="start_date" 
                    data-msg-date="Please enter a valid date as start date." 
                    data-msg-required="Please enter a valid date as start date." 
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="end_date">End Date</label>
                  <input type="text" class="form-control" id="task_end_date" name="end_date"  
                    data-msg-date="Please enter a valid date as end date." 
                    data-msg-required="Please enter a valid date as end date." 
                    data-rule-date="true" 
                    data-rule-required="true" />
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="status">Status</label> 
                  <select name="status" id="task_status">
                    <option value="#7bd148">Green</option>
                    <option value="#ffb878">Yellow</option>
                    <option value="#dc2127">Red</option>
                  </select>
                </div>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                <div class="form-group">
                  <label for="color">Color</label> 
                  <select name="color" id="task_color">
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
              <label for="per_complete">Percentage Complete</label>
              <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                <input type="hidden" id="per_complete_slide" name="per_complete_slide" 
                  data-slider-min="0" 
                  data-slider-max="100" 
                  data-slider-value="0" 
                  data-slider-step="1"/>
                <input type="hidden" name="per_complete" id="task_per_complete"/>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <div id="editTaskAction">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-primary" id="taskSaveButton">Save task</button>
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
  <asset:javascript src="application.js"/>
  <script type="text/javascript">
var $rows = $('#userList tbody tr');
$('#search').keyup(function() {
    var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
    
    $rows.show().filter(function() {
        var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
        return !~text.indexOf(val);
    }).hide();
});

    </script>
</body>
</html>
