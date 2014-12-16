// New Project
$('#addNewProject').on('click', function(e) {
  e.preventDefault();
  $('#alertNewProject').hide();
  $('#newProjectWindow').modal('show');
});

//About click
$('#about').on('click', function(e) {
  e.preventDefault();
  $('#aboutWindow').modal('show');
});

//Contact click
$('#contact').on('click', function(e) {
  e.preventDefault();
  $('#contactWindow').modal('show');
});

// Edit Project
//Edit Project click
$('.pm-edit-project').on('click', function(e) {
	e.preventDefault();
	var id = $(this).attr('id');
	$.ajax({
		type : 'post',
		datatype : 'json',
		url : gspVars.getProjectUrl + "/" + id,
		success : function(data) {
			$('#editProjectWindow #projectID').val(data.id);
			$('#editProjectWindow #projectName').val(data.projectName);
			$('#editProjectWindow #projectDesc').val(data.projectDesc);
			$('#editProjectWindow #startDate').val(data.startDate);
			$('#editProjectWindow #endDate').val(data.endDate);          
			$('#editProjectWindow #status').val(data.status.name); 
			$('#editProjectWindow #projectManager').val(data.projectManager.id); 
			$('#editProjectModalLabel').text("Edit Project Details");
			$('#alertEditProject').hide();
			$('#editProjectWindow').modal('show');
		}
	});
});

$('#start_date').datepicker();
$('#end_date').datepicker();


//Add User click
$('#addNewProject').on('click', function(e) {
	e.preventDefault();
	$('#editProjectWindow #projectManager').val(gspVars.currentUserId);
	$('#editProjectWindow #projectName').val('');
	$('#editProjectWindow #projectDesc').val('');
	$('#editProjectWindow #startDate').val('');
	$('#editProjectWindow #endDate').val('');          
	$('#editProjectWindow #status').val('');
	$('#editProjectModalLabel').text("Add New Project");
	$('#alertEditProject').hide();
	$('#editProjectWindow').modal('show');
});

//Edit Project click
$('.pm-edit-project').on('click', function(e) {
	e.preventDefault();
	var id = $(this).attr('id');
	$.ajax({
		type : 'post',
		datatype : 'json',
		url : gspVars.getProjectUrl + "/" + id,
		success : function(data) {
			$('#editProjectWindow #projectID').val(data.id);
			$('#editProjectWindow #projectName').val(data.projectName);
	        $('#editProjectWindow #projectDesc').val(data.projectDesc);
	        $('#editProjectWindow #startDate').val(data.startDate);
	        $('#editProjectWindow #endDate').val(data.endDate);          
	        $('#editProjectWindow #status').val(data.status.name); 
	        $('#editProjectWindow #projectManager').val(data.projectManager.id); 
	        $('#editProjectModalLabel').text("Edit Project Details");
	        $('#alertEditProject').hide();
	        $('#editProjectWindow').modal('show');
		}
	});
});
$('#editProjectWindow #startDate').datepicker();
$('#editProjectWindow #endDate').datepicker();