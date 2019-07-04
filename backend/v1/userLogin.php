<?php

require_once '../includes/db_operations.php';
$response = array();
if ($_SERVER['REQUEST_METHOD']== 'POST') {
	if (isset($_POST['username']) and isset($_POST['password'])){
		$db = new dbOperations();
		if ($db->userLogin($_POST['username'],$_POST['password'])) {
			$user =  $db->getUserbyUsername($_POST['username']);
			$response["error"] = false;
			$response["msg"] = "Login successfully";
			$response["id"] = $user["id"];
			$response["email"] = $user["email"];
			$response["username"] = $user["username"];
			$response["first_name"] = $user["first_name"];
			$response["last_name"] = $user["last_name"];
			$response["profession"] = $user["profession"];
			$response["gender"] = $user["gender"];
		}
		else{
			$response["error"] = true;
			$response["msg"] = "wrong username or password";
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