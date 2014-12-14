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
                  console.log(data);
                  bootbox.alert(data, function(result) {
                    window.location = gspVars.adminHomeUrl;
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
          console.log(data);
          bootbox.alert(data, function(result) {
            window.location = gspVars.adminHomeUrl;
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
  bootbox.confirm("Are you sure you want to delete this user ? \nAll projects and tasks associated with this user will be deleted.", function(result) {
    if (result) {
      $.ajax({
        type : 'post',
        url : gspVars.deleteUserUrl + "/" + user_id,
        success : function(data) {
          console.log(data);
          bootbox.alert(data, function(result) {
            window.location = gspVars.adminHomeUrl;
          });
        }
      });
    }
  });
});

// Reset password
$('.reset-password').on(
    'click',
    function(e) {
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

$('.change-password').on(
	    'click',
	    function(e) {
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
$(function() {
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
	});

//Edit user click
$('.edit-user').on(
    'click',
    function(e) {
      e.preventDefault();
      var user_id = $(this).attr('id');
      $.ajax({
        type : 'post',
        datatype : 'json',
        url : gspVars.getUserUrl + "/" + user_id,
        success : function(data) {
        	console.log(data)
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

$("#username").change(function() {
  $("#message").html("checking...");
  var email = $("#email").val();

  $.ajax({
    type : "post",
    url : gspVars.checkEmailUrl + "/" + email,
    success : function(data) {
      // alert(data);
      if (data == 1) {
        $("#message").html("Email available");
        $("saveButton").disabled = false;
      } else {
        $("#message").html("Email already taken");
        $("#email").focus();
        $("saveButton").disabled = true;
      }
    }
  });
});


jQuery.validator.addMethod("notUsed", 
   function(value, element, params) {
  var response;
  $.ajax({
    type : "post",
    url : "/users/check_email/" + value + "/" + $("#user_id").val(),
    async: false,
    success : function(data) {
      response = data;
    }
  });
  if (response == "Email is available")
    return true;
  else
    return false;
},'Email already used. Please provide another email.');

// Change password submit
$("#changePasswordForm").validate(
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
	        var submit_url = gspVars.changePasswordUrl; // change password
	        if ($('#id').val() != "" && $('#username').val() == "") {
	          submit_url = gspVars.resetPasswordUrl; // reset password
	        }
	        $.ajax({
	          type : 'post',
	          url : submit_url,
	          data : formData,
	          success : function(data) {
	            console.log(data);
	            bootbox.alert(data, function(result) {
	              window.location = gspVars.adminHomeUrl;
	            });
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
            bootbox.alert(data, function(result) {
              window.location = gspVars.adminHomeUrl;
            });
          }
        });
        return false;
      }
    });