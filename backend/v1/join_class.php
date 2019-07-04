<?php

require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['class_id']) and isset($_POST['user_id']) and isset($_POST['user_type'])) {
	$db = new dbOperations();
		$result = $db->joinClass(
			$_POST['class_id'],
			$_POST['user_id'],
			$_POST['user_type'],
			$_POST['user_name']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "class joined successfully";
		}elseif($result == 2){
			$response["error"] = true;
			$response["msg"] = "error while joining class";
		}elseif ($result == 0) {
			$response["error"] = true;
			$response["msg"] = "user already joined the class";
		}
		elseif ($result == 3) {
			$response["error"] = true;
			$response["msg"] = "class not found";
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
