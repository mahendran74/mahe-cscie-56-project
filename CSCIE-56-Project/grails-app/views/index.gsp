<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Quick PM</title>
        <asset:stylesheet src="carousel.css"/>
    </head>
<body>
    <div class="navbar-wrapper">
      <div class="container">

        <nav class="navbar navbar-inverse navbar-static-top" role="navigation">
          <div class="container">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" href="#">Quick PM</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
              <ul class="nav navbar-nav">
                <li class="active"><a href="/">Home</a></li>
              <li><a href="#signup" id="signUp">Sign Up</a></li>
              <li><a href="#login" id="login">Login</a></li>
              <li><a href="#about" id="about">About</a></li>
              </ul>
            </div>
          </div>
        </nav>

      </div>
    </div>
  <!-- Carousel
    ================================================== -->
  <div id="myCarousel" class="carousel slide" data-ride="carousel">
    <!-- Indicators -->
    <ol class="carousel-indicators">
      <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
      <li data-target="#myCarousel" data-slide-to="1"></li>
      <li data-target="#myCarousel" data-slide-to="2"></li>
    </ol>
    <div class="carousel-inner" role="listbox">
      <div class="item active">
        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEUsPlC8g8jXAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="First slide">
        <div class="container">
          <div class="carousel-caption">
            <h1>Quick Project Management.</h1>
            <p>
              QPM, lets you manage short project with very little prep time. Ideal for short Agile sprints.
            </p>
          </div>
        </div>
      </div>
      <div class="item">
        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEU3bmw5JKWVAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="Second slide">
        <div class="container">
          <div class="carousel-caption">
            <h1>Interactive Gantt Charts</h1>
            <p>Dynamically generated Gantt charts for projects to give you a visual interface to the project status.</p>
          </div>
        </div>
      </div>
      <div class="item">
        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEVuLFC3aeGqAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="Third slide">
        <div class="container">
          <div class="carousel-caption">
            <h1>Multiple roles and multiple interfaces</h1>
            <p>Unique color-coded interface for Administrator, Project Manager and Team Member.</p>
          </div>
        </div>
      </div>
    </div>
      <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
        <span class="sr-only">Previous</span>
      </a>
      <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
        <span class="sr-only">Next</span>
      </a>
    </div><!-- /.carousel -->



  <div class="container marketing">

    <!-- Three columns of text below the carousel -->
    <div class="row">
      <div class="col-lg-4">
        <img class="img-circle" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEUsPlC8g8jXAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="Generic placeholder image" style="width: 140px; height: 140px;"/>
        <h2>Administrator</h2>
         <p>
            1. Add new users. <br>
            2. Edit user details like <br>
            	a. First name <br>
            	b. Last name <br>
            	c. Email address. <br>
            2.Change user's privileges and provide users with administrator access. <br>
            3. Activate/deactivate users. <br>
            4. Reset passwords of users.
        </p>
      </div>
      <!-- /.col-lg-4 -->
      <div class="col-lg-4">
        <img class="img-circle" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEU3bmw5JKWVAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="Generic placeholder image" style="width: 140px; height: 140px;"/>
        <h2>Project Manager</h2>
        <p>
            1. Create new projects. 2. Edit projects details like a. Project Description b. Start and End date c. Project Status - Green/Yellow/Red 
            3. View the Gantt chart of the project 4. Add, edit and delete tasks, task groups and milestones. 5. Add team members for the projects.
        </p>
      </div>
      <!-- /.col-lg-4 -->
      <div class="col-lg-4">
        <img class="img-circle" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAADIAQMAAAAwS4omAAAAA1BMVEVuLFC3aeGqAAAAG0lEQVRIie3BMQEAAADCoPVPbQwfoAAAAIC3AQ+gAAEq5xQCAAAAAElFTkSuQmCC" alt="Generic placeholder image" style="width: 140px; height: 140px;"/>
        <h2>Team Member</h2>
            1. View calendar 2. View task list 3. Edit task details 4. Change task status/start date and end date
      </div>
      <!-- /.col-lg-4 -->
    </div>
    <!-- /.row -->

    <!-- START THE LOGIN MODAL -->

    <!-- Login Modal -->
    <div class="modal fade" id="loginWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <g:form controller="auth" action="signIn" method="post">
            <div class="modal-header modal-header-success">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title" id="loginModalLabel">Login</h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" id="alertLogin">
                <strong>${flash.message}</strong>
              </div>
              <label for="inputEmail" class="sr-only">Email address</label>
              <input type="email" id="inputEmail" 
                name="username" value="${username}" 
                class="form-control" placeholder="Email address" 
                required autofocus>
              <label for="inputPassword" class="sr-only">Password</label>
              <input type="password" id="inputPassword" 
                name="password" value="" class="form-control" 
                placeholder="Password" required>
              <div class="checkbox">
                <label>
                  <g:checkBox name="rememberMe" value="${rememberMe}" /> Remember me
                </label>
              </div>
              <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </div>
          </g:form>
        </div>
      </div>
    </div>

    <!-- /END THE LOGIN MODAL -->

    <!-- START THE SIGN UP MODAL -->

    <!-- Change Project Name Modal -->
    <div class="modal fade" id="signUpWindow" tabindex="-1" data-backdrop="static" data-keyboard="false">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method='POST' action='' id="signUpForm">
            <div class="modal-header modal-header-success">
              <button type="button" class="close" data-dismiss="modal">&times;</button>
              <h4 class="modal-title" id="signUpModalLabel">Sign Up</h4>
            </div>
            <div class="modal-body">
              <div class="alert alert-danger" id="alertSignUp">
                <strong>Oh snap!</strong>
              </div>

              <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" class="form-control" id="firstName" name="firstName" placeholder="First Name" value=""
                 data-msg-required="Please enter your first name." 
                 data-msg-maxlength="Your first name cannot be more than 255 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="255" />
              </div>
              <div class="form-group">
                <label for="middleIntitial">Middle Initial</label>
                <input type="text" class="form-control" id="middleInitial" name="middleInitial" placeholder="MI" value=""
                 data-msg-required="Please enter your middle initial." 
                 data-msg-maxlength="Your first name cannot be more than 1 character." 
                 data-rule-required="false" 
                 data-rule-maxlength="1" />
              </div>
              <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Last Name" value="" 
                 data-msg-required="Please enter your last name." 
                 data-msg-maxlength="Your last name cannot be more than 255 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="255" />
              </div>
              <div class="form-group">
                <label for="username">Email Address</label>
                <input type="text" class="form-control" id="username" name="username" placeholder="Email" value=""  
                 data-msg-email="Please enter a valid email." 
                 data-msg-required="Please enter a valid email." 
                 data-msg-maxlength="Your email cannot be more than 255 characters" 
                 data-rule-email="true" 
                 data-rule-required="true" 
                 data-rule-maxlength="255" 
                 data-rule-notUsed="true"/>
                <div id="message"></div>
              </div>
              <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Password" value=""
                 data-msg-required="Please enter the password." 
                 data-msg-maxlength="Your password cannot be more than 20 characters." 
                 data-msg-minlength="Your password cannot be less than 5 characters." 
                 data-rule-required="true" 
                 data-rule-maxlength="20"
                 data-rule-minlength="5" />
              </div>
              <div class="form-group">
                <label for="confirm_password">Confirm Password</label>
                <input type="password" class="form-control" id="confirm_password" name="confirm_password" placeholder="Comfirm password" value=""
                 data-msg-required="Please confirm your password." 
                 data-msg-maxlength="Your password cannot be more than 20 characters." 
                 data-msg-minlength="Your password cannot be less than 5 characters."
                 data-msg-equalTo="The password confirmation has to match the password above."
                 data-rule-required="true" 
                 data-rule-maxlength="20"
                 data-rule-minlength="5"
                 data-rule-equalTo="#password" />
              </div>

            </div>
            <div class="modal-footer">
              <button type="reset" class="btn btn-default" data-dismiss="modal">Close</button>
              <button type="submit" class="btn btn-success btn-primary" id="saveButton">Sign Up</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- /END THE SIGN UP MODAL -->

    <!-- FOOTER -->
    <footer>
      <p class="pull-right">
        <a href="#">Back to top</a>
      </p>
      <p>
        CSCIE-14 P4. Submitted by <a href="mailto:mahendran.sreedevi@gmail.com">Mahendran Sreedevi</a>
      </p>
    </footer>

  </div>
  <!-- /.container -->
  <!-- We inline the values (ids, action links, resource links) we want to use in our JS file -->
  <script>
    var gspVars = {
	  checkEmailUrl: '${createLink(controller:"user", action: "checkEmail")}',
    signUpUserUrl: '${createLink(controller:"user", action: "signUp")}'
    }
  </script>
  <asset:javascript src="qpm-home.js"/>
  <asset:javascript src="jquery-validate.js"/>
</body>
</html>