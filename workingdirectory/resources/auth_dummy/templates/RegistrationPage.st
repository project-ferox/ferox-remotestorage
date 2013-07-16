RegistrationPage(alert) ::= <<
	<!DOCTYPE html>
	<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link rel="stylesheet" type="text/css" href="static/css/bootstrap.min.css" />
	</head>
	<body>
		<div class="modal">
  			<div class="modal-header">
    			<h3>Register</h3>
  			</div>
  			<div class="alert">
  				{alert}
  			</div>
  			
  			<div class="modal-body">
  				<form class="form-horizontal" method="post">
  					<div class="control-group">
    					<label class="control-label" for="inputUsername">username</label>
    					<div class="controls">
      						<input type="text" name="username" id="inputUsername" placeholder="username">
    					</div>
  					</div>
  					<div class="control-group">
    					<label class="control-label" for="inputPassword">Password</label>
    					<div class="controls">
      						<input type="password" name="password" id="inputPassword" placeholder="Password">
    					</div>
  					</div>
  					<div class="control-group">
    					<div class="controls">
      						<button type="submit" class="btn btn-primary">Register</button>
    					</div>
  					</div>
				</form>
  			</div>
  			
  			<div class="modal-footer">
  				<a href="registrations/clear">Clear Users</a> | <a href="dialog">Login</a>
  			</div>
		</div>
	</body>
	</html>
>>