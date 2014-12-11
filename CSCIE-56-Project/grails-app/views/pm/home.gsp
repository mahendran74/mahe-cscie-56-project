
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="layout" content="main"/>

<title>Quick PM : PM Home</title>

<asset:stylesheet src="qpm.css"/>
<asset:stylesheet src="qpm-pm-theme.css"/>
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
        <a class="navbar-brand" href="/pm/home">Quick PM <small><small>Project Manager</small></small></a>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="/pm/home">Home</a></li>
          <li><a href="#about" id="about">About</a></li>
          <li><a href="#contact" id="contact">Contact</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
          <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> ${(userInstance?.firstName)}  ${(userInstance?.lastName)} <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li class="dropdown-header">Switch Role To ...</li>
              <shiro:hasAnyRole in="['ROLE_ADMIN']">
              <li class="admin-interface"><g:link controller="admin" action="home"><span class="glyphicon glyphicon-user"></span> Administrator </g:link></li>
              </shiro:hasAnyRole>
              <li class="tm-interface"><g:link controller="TM" action="home"><span class="glyphicon glyphicon-user"></span> Team Member</g:link></li>
              <li class="divider"></li>
              <li>
                <a href="#" id="${userInstance?.username}" class="change-password" data-toggle="tooltip" title="Change Password">
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
        <div class="panel panel-default qpm-panel">
          <div class="panel-heading">
            <div class="row">
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <h4 class="panel-title">Projects</h4>
              </div>
              <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <div class="pull-right">
                  <a href="#" id="addNewProject" class="btn btn-xs btn-success" data-toggle="tooltip" title="Add New Project"> 
                    <span class="glyphicon glyphicon-tasks"></span>
                  </a>
                  <a id="addNewUser" class="btn btn-xs btn-success right-button" data-toggle="tooltip" title="New User">
                    <span class="glyphicon glyphicon-user"></span>
                  </a>
                </div>
              </div>
            </div>
          </div>
          <div class="panel-body">
            <table class="table table-hover table-condensed" id="userList">
              <thead>
                <tr>
                  <th>Project Name</th>
                  <th>Start Date</th>
                  <th>End Date</th>                 
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <?php foreach($projects as $project): ?>
                <tr>
                  <td><?=$project['project_name']?></td>
                  <td><?=date('m/d/Y', strtotime($project['start_date']))?></td>
                  <td><?=date('m/d/Y', strtotime($project['end_date']))?></td>
                  <?php if($project['status'] == 'green'): ?>
                  <td><span class="label label-success">Green</span></td>
                  <?php elseif($project['status'] == 'yellow'): ?>
                  <td><span class="label label-warning">Yellow</span></td>
                  <?php elseif($project['status'] == 'red'): ?>
                  <td><span class="label label-danger">Red</span></td>
                  <?php endif;?>
                  <td>
                    <a href="#" id="<?=$project['project_id']?>" class="btn btn-xs btn-primary left-button edit-project-details" data-toggle="tooltip" title="Edit Project Details">
                      <span class="glyphicon glyphicon-edit"></span>
                    </a>
                    <a href="/pm/project_details/<?=$project['project_id']?>"  class="btn btn-xs btn-primary left-button" data-toggle="tooltip" title="View Gantt Chart">
                      <span class="glyphicon glyphicon-tasks"></span>
                    </a>
                  </td>
                </tr>
                <?php endforeach; ?>
              </tbody>
            </table>
          </div>
        </div>

  </div>
  <!-- /container -->

    <!-- START THE NEW PROJECT MODAL -->

    <!-- New Project Modal -->
    <div class="modal fade" id="newProjectWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method='POST' action='/pm/p_newproject' id="newProjectForm">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title" id="newProjectModalLabel">Add New Project</h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" id="alertNewProject">
                <strong>Oh snap!</strong>
              </div>
              <div class="form-group">
                <label for="project_name">Project Name</label>
                <input type="text" class="form-control" id="project_name" name="project_name" placeholder="Name of the project" value="" autofocus
                 data-msg-required="Please enter a project name." 
                 data-msg-maxlength="Your project name cannot be more than 255 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="255" />
              </div>
              <div class="form-group">
                <label for="project_desc">Project Description</label>
                <input type="text" class="form-control" id="project_desc" name="project_desc" placeholder="Short description" value="" 
                 data-msg-maxlength="Your last name cannot be more than 255 characters." 
                 data-rule-maxlength="255" />
              </div>
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label for="start_date">Start Date</label>
                    <input type="text" class="form-control" id="start_date" name="start_date" value=""  
                     data-msg-date="Please enter a valid date as start date." 
                     data-msg-required="Please enter a valid date as start date." 
                     data-rule-date="true" 
                     data-rule-required="true"/>
                  </div>
                 </div>
                 <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label for="end_date">End Date</label>
                    <input type="text" class="form-control" id="end_date" name="end_date" value=""
                     data-msg-date="Please enter a valid date as end date." 
                     data-msg-required="Please enter a valid date as end date." 
                     data-rule-date="true" 
                     data-rule-required="true"/>
                  </div>
                 </div>
                </div>
              </div>
            <div class="modal-footer">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-success btn-primary" id="addProjectSaveButton">Save</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- /END THE NEW PROJECT MODAL -->
  
    <!-- START THE EDIT PROJECT MODAL -->

    <!-- Edit Project Modal -->
    <div class="modal fade" id="editProjectWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method='POST' action='/pm/p_updateproject' id="editProjectForm">
            <input type="hidden" id="project_project_id" name="project_id"/>
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title" id="editProjectModalLabel">Edit Project Details</h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" id="alertEditProject">
                <strong>Oh snap!</strong>
              </div>
              <div class="form-group">
                <label for="project_name">Project Name</label>
                <input type="text" class="form-control" id="project_project_name" name="project_name" placeholder="Name of the project" value="" autofocus
                 data-msg-required="Please enter a project name." 
                 data-msg-maxlength="Your project name cannot be more than 255 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="255" />
              </div>
              <div class="form-group">
                <label for="project_desc">Project Description</label>
                <input type="text" class="form-control" id="project_project_desc" name="project_desc" placeholder="Short description" value="" 
                 data-msg-maxlength="Your last name cannot be more than 255 characters." 
                 data-rule-maxlength="255" />
              </div>
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label>Actual Start Date</label>
                    <p id="project_actual_start_date" class="form-control-static">12/03/2012</p>
                  </div>
                 </div>
                 <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label>Actual End Date</label>
                    <p id="project_actual_end_date" class="form-control-static">12/03/2012</p>
                  </div>
                 </div>
                </div>
              <div class="row">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label for="start_date">Start Date</label>
                    <input type="text" class="form-control" id="project_start_date" name="start_date" value=""  
                     data-msg-date="Please enter a valid date as start date." 
                     data-msg-required="Please enter a valid date as start date." 
                     data-rule-date="true" 
                     data-rule-required="true"/>
                  </div>
                 </div>
                 <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
                  <div class="form-group">
                    <label for="end_date">End Date</label>
                    <input type="text" class="form-control" id="project_end_date" name="end_date" value=""
                     data-msg-date="Please enter a valid date as end date." 
                     data-msg-required="Please enter a valid date as end date." 
                     data-rule-date="true" 
                     data-rule-required="true"/>
                  </div>
                 </div>
                </div>
                <div class="form-group">
                 <label for="project_status">Status</label>
                 <select name="status" id="project_status">
                  <option value="#7bd148">Green</option>
                  <option value="#ffb878">Orange</option>
                  <option value="#dc2127">Red</option>
                 </select>
                </div>
              </div>
            <div class="modal-footer">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-success btn-primary" id="editProjectSaveButton">Save</button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <!-- /END THE NEW PROJECT MODAL -->
  
  
  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->

  <script type="text/javascript">
var $rows = $('#userList tbody tr');
$('#search').keyup(function() {
    var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
    
    $rows.show().filter(function() {
        var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
        return !~text.indexOf(val);
    }).hide();
});
$('.left-button').tooltip({
  'placement':'bottom'
});
$('.right-button').tooltip({
  'placement':'bottom'
});

    </script>
</body>
</html>
