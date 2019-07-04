<?php 
require_once '../includes/db_operations.php';
$response = array();
if ($_SERVER['REQUEST_METHOD']== 'POST') {
	if (isset($_POST['class_id'])){
		$db = new dbOperations();
		
			$announcements['announcements'] =  $db->getAnnouncements($_POST['class_id']);
			if ($announcements == NULL) {
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


if ($announcements == []) {
	echo json_encode($response);
}else{
	echo json_encode($announcements);
}



 ?>