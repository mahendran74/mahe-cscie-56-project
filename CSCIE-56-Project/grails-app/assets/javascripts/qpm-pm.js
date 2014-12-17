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

///////////////////////////////P R O J E C T/////////////////////////////////
//Edit Project click
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

//Edit Project submit
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

///////////////////////////////G R O U P/////////////////////////////////

$('#startDate').datepicker();
$('#endDate').datepicker();

//Add Group click
$('#addNewGroup').on('click', function(e) {
	e.preventDefault();
	$('#addNewGroupWindow #projectID').val(gspVars.projectId);
	$('#addNewGroupWindow #groupName').val('');
	$('#addNewGroupWindow #startDate').val('');
	$('#addNewGroupWindow #endDate').val('');    
	$('#addNewGroupWindow #parentGroup').val(''); 
	$('#addNewGroupModalLabel').text("Add New Task Group");
	$('#addGroupAction').show();
	$('#editGroupAction').hide();
	$('#alertAddNewGroup').hide();
	$('#addNewGroupWindow').modal('show');
});

//Edit Group click
function editGroupItem(group_id) {
  $.ajax({
    type : 'post',
    datatype : 'json',
    url : gspVars.getGroupUrl + '/' + (group_id - 1000),
    success : function(data) {
    	console.log(data);
      $('#addNewGroupWindow #groupID').val(data.id);
      $('#addNewGroupWindow #groupName').val(data.groupName);
      $('#addNewGroupWindow #startDate').val(data.startDate);
      $('#addNewGroupWindow #endDate').val(data.endDate);
      $('#addNewGroupWindow #parentGroup').val(data.parentGroup);
      $('#alertAddNewGroup').hide();
      $('#addGroupAction').hide();
      $('#editGroupAction').show();
      $('#addNewGroupModalLabel').text("Edit Task Group");
      $('#addNewGroupWindow').modal('show');
    }
  });
}

//Delete Group submit
$('#groupDelButton').on('click',function(e) {
	e.preventDefault();
	var group_id = $('#groupID').val();
	var project_id = $('#projectID').val();
	bootbox.confirm("Are you sure you want to delete this group ? All associated tasks groups, tasks, and milestones will be deleted.", function(result) {
        if (result) {
        	$.ajax({
        		type : 'post',
        		url : gspVars.deleteGroupUrl + '/' + group_id,
        		success : function(data) {
        			console.log(data);
        			bootbox.alert(data.message, function(data) {
        				window.location = gspVars.homeUrl;
        			});
        		}
        	});
        }
	});
});

//Add/Edit Group submit
$("#addNewGroupForm").validate(
{
	showErrors : function(errorMap, errorList) {
		$.each(this.validElements(), function(index, element) {
			var $element = $(element);
			$element.data("title", "")
				.removeClass("has-error")
				.tooltip("destroy");
		});
		$.each(errorList, function(index, error) {
			var $element = $(error.element);
			$element.tooltip("destroy")
				.data("title", error.message)
				.addClass("has-error")
				.tooltip({
					'placement' : 'bottom'
				});
		});
	},
	submitHandler : function(form) {
		var formData = $(form).serialize();
		var submit_url = gspVars.updateGroupUrl; // Update group
		if ($('#groupID').val() == "") {
			submit_url = gspVars.addGroupUrl; // Add group
		}
		console.log(submit_url);
		$.ajax({
			type : 'post',
			url : submit_url,
			data : formData,
			success : function(data) {
				console.log(data)
				if (data.code == 'Success') {
					bootbox.alert(data.message, function(result) {
						window.location = gspVars.altHomeUrl;
					});
				} else {
					$('#alertAddNewGroup').text(data.message);
	              	$('#alertAddNewGroup').show();
				}          
			}
		});
		return false;
	}
});

///////////////////////////////T A S K////////////////////////////////////////
$('#color').simplecolorpicker({
	  picker : true,
	  theme : 'glyphicons'
	});
$('#percentageCompleteSlide').slider({
	  formater : function(value) {
	    return 'Percentage complete : ' + value + ' %';
	  }
});
$("#addNewTaskWindow #percentageCompleteSlide").on('slide', function(slideEvt) {
	  $("#percentageComplete").val(slideEvt.value);
});

//Add Task click
$('#addNewTask').on('click', function(e) {
	e.preventDefault();
	$('#addNewTaskWindow #projectID').val(gspVars.projectId);
	$('#addNewTaskWindow #groupName').val('');
	$('#addNewTaskWindow #startDate').val('');
	$('#addNewTaskWindow #endDate').val('');
	$('#addNewTaskWindow #status').val('');
	$('#addNewTaskWindow #assignedTo').val(''); 
	$('#addNewTaskWindow #taskGroup').val(''); 
	$('#addNewTaskWindow #dependsOn').val(''); 
	$('#addNewTaskWindow #color').simplecolorpicker('selectColor', '#7bd148');
    $('#addNewTaskWindow #percentageCompleteSlide').slider('setValue', '');
    $('#addNewTaskWindow #percentageComplete').val('');
	$('#addNewTaskModalLabel').text("Add New Task");
	$('#addTaskAction').show();
	$('#editTaskAction').hide();
	$('#alertAddNewTask').hide();
	$('#addNewTaskWindow').modal('show');
});

//Edit Task click
function editTaskItem(task_id) {
  $.ajax({
    type : 'post',
    datatype : 'json',
    url : gspVars.getTaskUrl + '/' + (task_id - 3000),
    success : function(data) {
    	console.log(data);
    	$('#addNewTaskWindow #projectID').val(gspVars.projectId);
    	$('#addNewTaskWindow #taskID').val(data.id);
    	$('#addNewTaskWindow #taskDesc').val(data.taskDesc);
    	$('#addNewTaskWindow #startDate').val(data.startDate);
    	$('#addNewTaskWindow #endDate').val(data.endDate);
    	$('#addNewTaskWindow #status').val(data.status.name);
    	$('#addNewTaskWindow #assignedTo').val(data.assignedTo);
    	$('#addNewTaskWindow #taskGroup').val(data.taskGroup);
    	$('#addNewTaskWindow #dependsOn').val(data.dependsOn);
    	$('#addNewTaskWindow #color').simplecolorpicker('selectColor', '#7bd148');
        $('#addNewTaskWindow #percentageCompleteSlide').slider('setValue', '');
        $('#addNewTaskWindow #percentageComplete').val('');
    	$('#addNewTaskModalLabel').text("Edit Task");
    	$('#addTaskAction').hide();
    	$('#editTaskAction').show();
    	$('#alertAddNewTask').hide();
    	$('#addNewTaskWindow').modal('show');
    }
  });
}

//Delete Task click
$('#taskDelButton').on('click',function(e) {
	e.preventDefault();
	var task_id = $('#taskID').val();
	var project_id = $('#projectID').val();
	bootbox.confirm("Are you sure you want to delete this task ? All associated tasks will be deleted.", function(result) {
        if (result) {
        	$.ajax({
        		type : 'post',
        		url : gspVars.deleteTaskUrl + '/' + task_id,
        		success : function(data) {
        			console.log(data);
        			bootbox.alert(data.message, function(data) {
        				window.location = gspVars.homeUrl;
        			});
        		}
        	});
        }
	});
});

//Add/Edit Task submit
$("#addNewTaskForm").validate(
{
	showErrors : function(errorMap, errorList) {
		$.each(this.validElements(), function(index, element) {
			var $element = $(element);
			$element.data("title", "")
				.removeClass("has-error")
				.tooltip("destroy");
		});
		$.each(errorList, function(index, error) {
			var $element = $(error.element);
			$element.tooltip("destroy")
				.data("title", error.message)
				.addClass("has-error")
				.tooltip({
					'placement' : 'bottom'
				});
		});
	},
	submitHandler : function(form) {
		var formData = $(form).serialize();
		var submit_url = gspVars.updateTaskUrl; // Update task
		if ($('#taskID').val() == "") {
			submit_url = gspVars.addTaskUrl; // Add task
		}
		console.log(submit_url);
		$.ajax({
			type : 'post',
			url : submit_url,
			data : formData,
			success : function(data) {
				console.log(data)
				if (data.code == 'Success') {
					bootbox.alert(data.message, function(result) {
						window.location = gspVars.homeUrl;
					});
				} else {
					$('#alertAddNewTask').text(data.message);
	              	$('#alertAddNewTask').show();
				}          
			}
		});
		return false;
	}
});

