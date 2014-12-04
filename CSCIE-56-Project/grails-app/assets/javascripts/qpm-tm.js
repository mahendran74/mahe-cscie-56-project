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

$(document).ready(
    function() {
      $('#calendar').fullCalendar(
          {
            editable : true,
            events : "/tm/get_tasks_json/" + $("#user_id").val(),
            eventDrop : function(event, delta, revertFunc) {
              bootbox.confirm("Are you sure you want make this change ?",
                  function(result) {
                    if (result) {
                      var submit_url = '/tm/change_task_time/'
                          + event.id.substring(1) + "/start_date/" + delta;
                      if (event.end == null)
                        submit_url = '/tm/change_milestone_time/'
                            + event.id.substring(1) + "/milestone_date/"
                            + delta;
                      $.ajax({
                        type : 'post',
                        url : submit_url,
                        success : function(data) {
                          console.log(data);
                          bootbox.alert(data, function(result) {
                            window.location = "/tm/home";
                          });
                        }
                      });
                    } else
                      revertFunc();
                  });
            },
            eventResize : function(event, dayDelta, minuteDelta, revertFunc) {
              bootbox.confirm("Are you sure you want make this change ?",
                  function(result) {
                    if (result) {
                      if (event.id.charAt(0) == 'm') {
                        bootbox.alert(
                            "You cannot extend a milestone beyond a day.",
                            function(result) {
                              revertFunc();
                            });
                      } else {
                        $.ajax({
                          type : 'post',
                          url : '/tm/change_task_time/' + event.id.substring(1)
                              + "/end_date/" + dayDelta,
                          success : function(data) {
                            console.log(data);
                            bootbox.alert(data, function(result) {
                              window.location = "/tm/home";
                            });
                          }
                        });
                      }
                    } else
                      revertFunc();
                  });
            },
            eventClick : function(calEvent, jsEvent, view) {
              $.ajax({
                type : 'post',
                url : '/tm/get_task_details/' + calEvent.id.substring(1),
                success : function(data) {
                  var task = jQuery.parseJSON(data);
                  $('#editTaskWindow #task_id').val(task.task_id);
                  $('#editTaskWindow #task_task_desc').text(task.task_desc);
                  $('#editTaskWindow #task_start_date').val(changeDateFormat(task.start_date));
                  $('#editTaskWindow #task_end_date').val(changeDateFormat(task.end_date));
                  if (task.status == "red") {
                    $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#dc2127');
                  } else if (task.status == "yellow") {
                    $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#ffb878');
                  } else {
                    $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#7bd148');
                  }
                  $('#editTaskWindow #task_color').simplecolorpicker('selectColor', task.color);
                  $('#editTaskWindow #per_complete_slide').slider('setValue', task.per_complete);
                  $('#editTaskWindow #task_per_complete').val(task.per_complete);
                  $('#alertEditTask').hide();
                  $('#editTaskWindow').modal('show');
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

$('#task_start_date').datepicker();
$('#task_end_date').datepicker();
//Color picker
$('#task_status').simplecolorpicker({
  picker : true,
  theme : 'glyphicons'
});
$('#task_color').simplecolorpicker({
  picker : true,
  theme : 'glyphicons'
});
$('#per_complete_slide').slider({
  formater : function(value) {
    return 'Percentage complete : ' + value + ' %';
  }
});
//Edit Task
$('.edit-task').on(
    'click',
    function(e) {
      e.preventDefault();
      var task_id = $(this).attr('id');

      $.ajax({
        type : 'post',
        url : '/tm/get_task_details/' + task_id,
        success : function(data) {
          var task = jQuery.parseJSON(data);
          $('#editTaskWindow #task_id').val(task.task_id);
          $('#editTaskWindow #task_task_desc').text(task.task_desc);
          $('#editTaskWindow #task_start_date').val(changeDateFormat(task.start_date));
          $('#editTaskWindow #task_end_date').val(changeDateFormat(task.end_date));
          if (task.status == "red") {
            $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#dc2127');
          } else if (task.status == "yellow") {
            $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#ffb878');
          } else {
            $('#editTaskWindow #task_status').simplecolorpicker('selectColor', '#7bd148');
          }
          $('#editTaskWindow #task_color').simplecolorpicker('selectColor', task.color);
          $('#editTaskWindow #per_complete_slide').slider('setValue', task.per_complete);
          $('#editTaskWindow #task_per_complete').val(task.per_complete);
          $('#alertEditTask').hide();
          $('#editTaskWindow').modal('show');
        }
      });
    });


$("#editTaskWindow #task_per_complete").on('slide', function(slideEvt) {
  $("#task_per_complete").val(slideEvt.value);
});


// Edit task submit
$("#editTaskForm").validate(
    {
      showErrors : function(errorMap, errorList) {

        // Clean up any tooltips for valid elements
        $.each(this.validElements(), function(index, element) {
          var $element = $(element);
          $element.data("title", "").removeClass("has-error")
              .tooltip("destroy");
        });

        // Create new tooltips for invalid elements
        $.each(errorList, function(index, error) {
          var $element = $(error.element);
          $element.tooltip("destroy").data("title", error.message).addClass(
              "has-error").tooltip({
            'placement' : 'bottom'
          });
        });
      },

      submitHandler : function(form) {
        var formData = $(form).serialize();
        console.log(formData);
        $.ajax({
          type : 'post',
          url : '/tm/p_update_task',
          data : formData,
          success : function(data) {
            //window.location = '/tm/home';
            $('#pm-home-tabs a:last').tab('show');
            //$('#editTaskWindow').modal('hide');
            bootbox.alert(data, function(result) {

            });
          }
        });
        return false;
      }
    });