var $rows = $('#userList tbody tr');
$('#search').keyup(function() {
  var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();

  $rows.show().filter(function() {
    var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
    return !~text.indexOf(val);
  }).hide();
});

$(document).ready(function(){
//	  e.preventDefault();
	if($.trim($('#alertLogin').text()) != "") {
	  $('#alertLogin').show();
	  $('#loginWindow').modal('show');
	}
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

// Deactivate user
$('.deactivate-user').on(
    'click',
    function(e) {
      e.preventDefault();
      var user_id = $(this).attr('id');
      bootbox.confirm("Are you sure you want to deactivate this user ?",
          function(result) {
            if (result) {
              $.ajax({
                type : 'post',
                url : gspVars.deactivateUserUrl + "/" + user_id,
                success : function(data) {
                  bootbox.alert(data.message, function(result) {
                    window.location = gspVars.homeUrl;
                  });
                }
              });
            }
          });
    });

// Activate user
$('.activate-user').on('click', function(e) {
  e.preventDefault();
  var user_id = $(this).attr('id');
  bootbox.confirm("Are you sure you want to activate this user ?", function(result) {
    if (result) {
      $.ajax({
        type : 'post',
        url : gspVars.activateUserUrl + "/" + user_id,
        success : function(data) {
          bootbox.alert(data.message, function(result) {
            window.location = gspVars.homeUrl;
          });
        }
      });
    }
  });
});

//Delete user
$('.delete-user').on('click', function(e) {
  e.preventDefault();
  var user_id = $(this).attr('id');
  bootbox.confirm("Are you sure you want to delete this user ? All projects and tasks associated with this user will be deleted.", function(result) {
    if (result) {
      $.ajax({
        type : 'post',
        url : gspVars.deleteUserUrl + "/" + user_id,
        success : function(data) {
          bootbox.alert(data.message, function(result) {
            window.location = gspVars.homeUrl;
          });
        }
      });
    }
  });
});

//Delete project
$('.delete-project').on('click', function(e) {
  e.preventDefault();
  var user_id = $(this).attr('id');
  bootbox.confirm("Are you sure you want to delete this project ? All groups, tasks and milestones associated with this project will be deleted.", function(result) {
    if (result) {
      $.ajax({
        type : 'post',
        url : gspVars.deleteProjectUrl + "/" + user_id,
        success : function(data) {
          bootbox.alert(data.message, function(result) {
            window.location = gspVars.altHomeUrl;
          });
        }
      });
    }
  });
});

// Reset password
$('.reset-password').on('click', function(e) {
	e.preventDefault();
	$('#changePasswordWindow #id').val($(this).attr('id'));
	$('#changePasswordWindow #username').val('');
	$('#changePasswordWindow #password').val('');
	$('#changePasswordWindow #confirmPassword').val('');          
	$('#changePasswordWindow #oldPasswordDiv').hide();
	$('#changePasswordLabel').text("Reset Password");
	$('#alertChangePassword').hide();
	$('#changePasswordWindow').modal('show');
});

$('.change-password').on('click', function(e) {
	e.preventDefault();
	$('#changePasswordWindow #id').val('');
	$('#changePasswordWindow #username').val($(this).attr('id'));
	$('#changePasswordWindow #password').val('');
	$('#changePasswordWindow #confirmPassword').val('');
	$('#changePasswordWindow #oldPassword').val('');
	$('#changePasswordWindow #oldPasswordDiv').show();
	$('#changePasswordLabel').text("Change Password");
	$('#alertChangePassword').hide();
	$('#changePasswordWindow').modal('show');
});

function checkForRole(data, value) {
    var found = false;
    for (var i = 0; i < data.length; i++) {
        var element = data[i];
        if (element.name == value) {
           found = true;
       } 
    }
    return found;
}

// Add User click
$('#addNewUser').on('click', function(e) {
	e.preventDefault();
	$('#addNewUserWindow #user_id').val('');
	$('#addNewUserWindow #firstName').val('');
	$('#addNewUserWindow #middleInitial').val('');
	$('#addNewUserWindow #lastName').val('');
	$('#addNewUserWindow #email').val('');          
	$('#addNewUserWindow #confirm_email').val('');
	$('#addNewUserWindow #passwordDiv').show();
	$('#addNewUserWindow #confirmPasswordDiv').show();    
	$('#addNewUserWindow #role').val('');
	$('#addNewUserModalLabel').text("Add New User");
	$('#alertAddNewUser').hide();
	$('#addNewUserWindow').modal('show');
});

//Edit user click
$('.edit-user').on('click', function(e) {
	e.preventDefault();
	var user_id = $(this).attr('id');
	$.ajax({
		type : 'post',
		datatype : 'json',
		url : gspVars.getUserUrl + "/" + user_id,
		success : function(data) {
			$('#addNewUserWindow #user_id').val(data.id);
			$('#addNewUserWindow #firstName').val(data.firstName);
			$('#addNewUserWindow #middleInitial').val(data.middleInitial);
			$('#addNewUserWindow #lastName').val(data.lastName);
			$('#addNewUserWindow #username').val(data.username);          
			$('#addNewUserWindow #role').val(data.roles[0].id); 
			$('#addNewUserWindow #passwordDiv').hide();
			$('#addNewUserWindow #confirmPasswordDiv').hide();
			$('#addNewUserModalLabel').text("Edit User");
			$('#alertAddNewUser').hide();
			$('#addNewUserWindow').modal('show');
		}
	});
});

//Edit Project click
$('.admin-edit-project').on('click', function(e) {
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
$('#editProjectWindow #startDate').datepicker({
  autoclose : true
});
$('#editProjectWindow #endDate').datepicker({
  autoclose : true
});

//Add/Edit Project submit
$("#editProjectForm").validate(
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
		var submit_url = gspVars.updateProjectUrl;
		if ($('#projectID').val() == "") {
			submit_url = gspVars.addProjectUrl; // Add project
		}
		$.ajax({
			type : 'post',
			url : submit_url,
			data : formData,
			success : function(data) {
				if (data.code == 'Success') {
					bootbox.alert(data.message, function(result) {
						window.location = gspVars.altHomeUrl;
					});
				} else {
					$('#alertEditProject').text(data.message);
	              	$('#alertEditProject').show();
				}          
			}
		});
		return false;
	}
});

// Change password submit
$("#changePasswordForm").validate(
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
		var submit_url = gspVars.changePasswordUrl; // change password
		if ($('#id').val() != "" && $('#username').val() == "") {
			submit_url = gspVars.resetPasswordUrl; // reset password
		}
        $.ajax({
        	type : 'post',
        	url : submit_url,
        	data : formData,
        	success : function(data) {
        		if (data.code == 'Success') {
        			bootbox.alert(data.message, function(result) {
        				window.location = gspVars.homeUrl;
        			});
        		} else {
        			$('#alertChangePassword').text(data.message);
        			$('#alertChangePassword').show();
        		}          
        	}
        });
        return false;
	}
});

// New user submit
$("#addNewUserForm").validate(
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
        var submit_url = gspVars.addUserUrl;
        if ($('#user_id').val() != "") {
          submit_url = gspVars.updateUserUrl;
        }
        $.ajax({
          type : 'post',
          url : submit_url,
          data : formData,
          success : function(data) {
            console.log(data);
            if (data.code == 'Success') {
            	bootbox.alert(data.message, function(result) {
            		window.location = gspVars.homeUrl;
                });
            } else {
              	$('#alertAddNewUser').text(data.message);
              	$('#alertAddNewUser').show();
            }          
          }
        });
        return false;
      }
    });