<?php 
require_once '../includes/db_operations.php';
$response = array();
if ($_SERVER['REQUEST_METHOD']== 'POST') {
	if (isset($_POST['std_id'])){
		$db = new dbOperations();
		
			$user['answers'] =  $db->getStdAnswer($_POST['std_id'],$_POST["assign_id"],$_POST['class_id']);
			if ($user == NULL) {
				$response["error"] = true;
				$response["msg"] = "NO class found for this user";
			}

	}else{
	$response["error"] = true;
	$response["msg"] = "required fields are missings";
	}




	}else{
	$response["error"] = true;
	$response["msg"] = "invalid REQUEST_METHOD";
	}


if ($user == []) {
	echo json_encode($response);
}else{
	echo json_encode($user);
}



 ?>