<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['class_id'])) {
	$db = new dbOperations();
		$result = $db->setAnnounce(
			$_POST['class_id'],
			$_POST['class_name'] ,
			$_POST['announce_title'],
			$_POST['announce_desc'],
			$_POST['announce_date']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "answer created successfully";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "error while creating answer";
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