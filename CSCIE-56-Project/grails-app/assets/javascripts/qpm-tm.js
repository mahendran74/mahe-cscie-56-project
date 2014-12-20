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

$('#addNewTaskWindow #startDate').datepicker();
$('#addNewTaskWindow #endDate').datepicker();
$('#color').simplecolorpicker({
	  picker : true,
	  theme : 'glyphicons'
	});
$('#taskcolor').simplecolorpicker({
	  picker : true,
	  theme : 'glyphicons'
	});
$('#addNewTaskWindow #percentageCompleteSlide').slider({
	  formater : function(value) {
	    return 'Percentage complete : ' + value + ' %';
	  }
});
$("#addNewTaskWindow #percentageCompleteSlide").on('slide', function(slideEvt) {
	  $("#percentageComplete").val(slideEvt.value);
});

$(document).ready( function() {
      $('#calendar').fullCalendar({
            editable : true,
            events : gspVars.getTasksUrl + '/' + $("#userID").val(),
            eventClick : function(calEvent, jsEvent, view) {
              $.ajax({
                type : 'post',
                url : gspVars.getTaskUrl + '/' +calEvent.id.substring(1),
                success : function(data) {
                  	$('#addNewTaskWindow #projectID').val(data.project);
                  	$('#addNewTaskWindow #taskID').val(data.id);
                  	$('#addNewTaskWindow #dtaskDesc').val(data.taskDesc);
                  	$('#addNewTaskWindow #taskDesc').val(data.taskDesc);
                  	$('#addNewTaskWindow #startDate').val(data.startDate);
                  	$('#addNewTaskWindow #endDate').val(data.endDate);
                  	$('#addNewTaskWindow #status').val(data.status.name);
                  	$('#addNewTaskWindow #dassignedTo').val(data.assignedTo);
                  	$('#addNewTaskWindow #dtaskGroup').val(data.taskGroup);
                  	$('#addNewTaskWindow #ddependsOn').val(data.dependsOn);
                  	$('#addNewTaskWindow #assignedTo').val(data.assignedTo);
                  	$('#addNewTaskWindow #taskGroup').val(data.taskGroup);
                  	$('#addNewTaskWindow #dependsOn').val(data.dependsOn);
                  	$('#addNewTaskWindow #color').simplecolorpicker('selectColor', data.color);
                    $('#addNewTaskWindow #percentageCompleteSlide').slider('setValue', data.percentageComplete);
                    $('#addNewTaskWindow #percentageComplete').val(data.percentageComplete);
                    $('#addNewTaskWindow #source').val('calendar');
                  	$('#addNewTaskModalLabel').text("Edit Task");
                  	$('#addTaskAction').hide();
                  	$('#editTaskAction').show();
                  	$('#alertAddNewTask').hide();
                  	$('#addNewTaskWindow').modal('show');
                }
              });
            },
            loading : function(bool) {
              if (bool)
                $('#loading').show();
              else
                $('#loading').hide();
            },

          });
    });

function changeDateFormat(date_string) {
  var dateVar = new Date(date_string);
  var dayVar = dateVar.getDate();
  var monthVar = dateVar.getMonth();
  monthVar++;
  var yearVar = dateVar.getFullYear();
  return (monthVar + "/" + dayVar + "/" + yearVar);
}

//Edit Task
$('.edit-task').on('click', function(e) {
      e.preventDefault();
      var task_id = $(this).attr('id');
      $.ajax({
          type : 'post',
          url : gspVars.getTaskUrl + '/' + task_id,
          success : function(data) {
        	  console.log(data)
          	$('#addNewTaskWindow #projectID').val(data.project);
          	$('#addNewTaskWindow #taskID').val(data.id);
          	$('#addNewTaskWindow #dtaskDesc').val(data.taskDesc);
          	$('#addNewTaskWindow #taskDesc').val(data.taskDesc);
          	$('#addNewTaskWindow #startDate').val(data.startDate);
          	$('#addNewTaskWindow #endDate').val(data.endDate);
          	$('#addNewTaskWindow #status').val(data.status.name);
          	$('#addNewTaskWindow #dassignedTo').val(data.assignedTo);
          	$('#addNewTaskWindow #dtaskGroup').val(data.taskGroup);
          	$('#addNewTaskWindow #ddependsOn').val(data.dependsOn);
          	$('#addNewTaskWindow #assignedTo').val(data.assignedTo);
          	$('#addNewTaskWindow #taskGroup').val(data.taskGroup);
          	$('#addNewTaskWindow #dependsOn').val(data.dependsOn);
          	$('#addNewTaskWindow #color').simplecolorpicker('selectColor', data.color);
            $('#addNewTaskWindow #percentageCompleteSlide').slider('setValue', data.percentageComplete);
            $('#addNewTaskWindow #percentageComplete').val(data.percentageComplete);
            $('#addNewTaskWindow #source').val('tasks');
          	$('#addNewTaskModalLabel').text("Edit Task");
          	$('#addTaskAction').hide();
          	$('#editTaskAction').show();
          	$('#alertAddNewTask').hide();
          	$('#addNewTaskWindow').modal('show');
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
		var home_url = gspVars.homeUrl;
		alert (home_url);
		if ($('#source').val() == "tasks") {
			home_url = gspVars.altHomeUrl; 
			alert (home_url);
		}
		console.log(formData);
		console.log(submit_url);
		$.ajax({
			type : 'post',
			url : submit_url,
			data : formData,
			success : function(data) {
				console.log(data)
				console.log(home_url)
				if (data.code == 'Success') {
					bootbox.alert(data.message, function(result) {
						window.location = home_url;
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