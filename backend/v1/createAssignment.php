<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id']) and isset($_POST['title']) and isset($_POST['date']) and isset($_POST['attach_id']) and isset($_POST['point'])) {
	$db = new dbOperations();
		$result = $db->createAssignment(
			$_POST['id'],
			$_POST['title'],
			$_POST['date'] ,
			$_POST['desc'],
			$_POST['attach_id'],
			$_POST['point'],
			$_POST['posted_date']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "assignment created successfully";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "error while creating assignment";
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