var $rows = $('#userList tbody tr');
$('#search').keyup(function() {
  var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();

  $rows.show().filter(function() {
    var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
    return !~text.indexOf(val);
  }).hide();
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

//$('.left-button').tooltip({
//  'placement' : 'bottom'
//});
//$('.right-button').tooltip({
//  'placement' : 'bottom'
//});
//$('.admin-icon').tooltip({
//  'placement' : 'top'
//});
//$('.pm-icon').tooltip({
//  'placement' : 'top'
//});
//$('.tm-icon').tooltip({
//  'placement' : 'top'
//});

$(function() {
  $('#addNewUser').on('click', function(e) {
    e.preventDefault();
    $('#addNewUserWindow #user_id').val('');
    $('#addNewUserWindow #first_name').val('');
    $('#addNewUserWindow #last_name').val('');
    $('#addNewUserWindow #email').val('');          
    $('#addNewUserWindow #confirm_email').val('');
    $('#addNewUserWindow #admin_access').prop("checked", true);
    $('#addNewUserModalLabel').text("Add New User");
    $('#alertAddNewUser').hide();
    $('#addNewUserWindow').modal('show');
  });
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
                url : '/users/deactivate/' + user_id,
                success : function(data) {
                  console.log(data);
                  bootbox.alert(data, function(result) {
                    window.location = "/admin/home";
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
  bootbox.confirm("Are you sure you to activate this user ?", function(result) {
    if (result) {
      $.ajax({
        type : 'post',
        url : '/users/activate/' + user_id,
        success : function(data) {
          console.log(data);
          bootbox.alert(data, function(result) {
            window.location = "/admin/home";
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
      var user_id = $(this).attr('id').substring(1);
      bootbox.confirm("Are you sure you want to reset the password ?",
          function(result) {
            if (result) {
              $.ajax({
                type : 'post',
                url : '/users/set_temp_token/' + user_id,
                success : function(data) {
                  console.log(data);
                  bootbox.alert(data, function(result) {
                    window.location = "/admin/home";
                  });
                }
              });
            }
          });
    });

//Edit user
$('.edit-user').on(
    'click',
    function(e) {
      e.preventDefault();
      var user_id = $(this).attr('id').substring(1);
      $.ajax({
        type : 'post',
        url : '/users/get_user/' + user_id,
        success : function(data) {
          var user = jQuery.parseJSON(data);
          $('#addNewUserWindow #user_id').val(user.user_id);
          $('#addNewUserWindow #first_name').val(user.first_name);
          $('#addNewUserWindow #last_name').val(user.last_name);
          $('#addNewUserWindow #email').val(user.email);          
          $('#addNewUserWindow #confirm_email').val(user.email);
          if (user.roles.indexOf("1") > 0)
            $('#addNewUserWindow #admin_access').prop("checked", true);
          else
            $('#addNewUserWindow #admin_access').prop("checked", false);
          $('#addNewUserModalLabel').text("Edit User");
          $('#alertAddNewUser').hide();
          $('#addNewUserWindow').modal('show');
        }
      });      
      
    });

$("#email").change(function() {
  $("#message").html("checking...");
  var email = $("#email").val();

  $.ajax({
    type : "post",
    url : "/users/check_email/" + email,
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
        var submit_url = '/users/p_add_user';
        if ($('#user_id').val() != "") {
          submit_url = '/users/p_update_user';
        }
        $.ajax({
          type : 'post',
          url : submit_url,
          data : formData,
          success : function(data) {
            console.log(data);
            bootbox.alert(data, function(result) {
              window.location = "/admin/home";
            });
          }
        });
        return false;
      }
    });