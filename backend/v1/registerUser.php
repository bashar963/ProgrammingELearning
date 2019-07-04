<?php 

require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {
	
	if (isset($_POST['email']) and isset($_POST['username']) and isset($_POST['password']) and isset($_POST['first_name']) and isset($_POST['last_name']) and isset($_POST['prof']) and isset($_POST['gender'])) {
		$db = new dbOperations();
		$result = $db->createUser(
			$_POST['email'],
			$_POST['username'] ,
			$_POST['password'] ,
			$_POST['first_name'] ,
			$_POST['last_name'] ,
			$_POST['prof'] ,
			$_POST['gender']
		);
			if($result == 1){
		$response["error"] = false;
		$response["msg"] = "user registered successfully";
		}elseif ($result == 2) {
			$response["error"] = true;
			$response["msg"] = "error while registering user";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "the user is already exist";
		}
		
		
	}else{
	$response["error"] = true;
	$response["msg"] = "required fields are missings";
	}

}else{

	$response["error"] = true;
	$response["msg"] = "invalid REQUEST_METHOD";
}


echo json_encode($response);

 ?>