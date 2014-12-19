$(document).ready(function(){
//	  e.preventDefault();
	if($.trim($('#alertLogin').text()) != "") {
	  $('#alertLogin').show();
	  $('#loginWindow').modal('show');
	}
});

// Sign Up click
$('#signUp').on('click', function(e) {
  e.preventDefault();
  $('#alertSignUp').hide();
  $('#signUpWindow #firstName').val('');
  $('#signUpWindow #middleInitial').val('');
  $('#signUpWindow #lastName').val('');
  $('#signUpWindow #username').val('');
  $('#signUpWindow #password').val('');
  $('#signUpWindow #confirm_password').val('');
  $('#signUpWindow').modal('show');
});

//Login click
$('#login').on('click', function(e) {
  e.preventDefault();
  $('#alertLogin').hide();
  $('#loginWindow').modal('show');
});

//About click
$('#about').on('click', function(e) {
  e.preventDefault();
  $('#aboutWindow').modal('show');
});

jQuery.validator.addMethod("notUsed", function(value, element, params) {
   var response;
   $.ajax({
     type : "post",
     url : gspVars.checkEmailUrl + "?email=" + value,
     async: false,
     success : function(data) {
    	   response = data;
     }
   });
   if (response.code == "Success")
     return true;
   else
     return false;
 },'Email already used. Please provide another email.');

// Log In submit
$("#loginForm").validate(
    {
      showErrors : function(errorMap, errorList) {
        $.each(this.validElements(), function(index, element) {
          var $element = $(element);
          $element.data("title", "").removeClass("has-error")
              .tooltip("destroy");
        });
        $.each(errorList, function(index, error) {
          var $element = $(error.element);
          $element.addClass("has-error");
          $element.tooltip("destroy").data("title", error.message).addClass(
              "has-error").tooltip({
            'placement' : 'bottom'
          });
        });
      },
      submitHandler : function(form) {
        form.submit();
        return false;
      }
    });
// Sign Up submit
$("#signUpForm").validate(
    {
      showErrors : function(errorMap, errorList) {
        $.each(this.validElements(), function(index, element) {
          var $element = $(element);
          $element.data("title", "").removeClass("has-error")
              .tooltip("destroy");
        });
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
        var submit_url = gspVars.signUpUserUrl; 
        $.ajax({
          type : 'post',
          url : submit_url,
          data : formData,
          success : function(data) {
            if (data.code == 'Success') {
                bootbox.alert(data.message, function(result) {
                	$('#signUpWindow').modal('hide');
                });
            } else {
            	$('#alertSignUp').value(data.message);
            	$('#alertSignUp').show();
            }
          }
        });
        return false;
      }
    });
//Password reset
$("#passwordResetForm").validate(
    {
      showErrors : function(errorMap, errorList) {
        $.each(this.validElements(), function(index, element) {
          var $element = $(element);
          $element.data("title", "").removeClass("has-error")
              .tooltip("destroy");
        });
        $.each(errorList, function(index, error) {
          var $element = $(error.element);
          $element.tooltip("destroy").data("title", error.message).addClass(
              "has-error").tooltip({
            'placement' : 'bottom'
          });
        });
      },
      submitHandler : function(form) {
        form.submit();
        return false;
      }
    });
