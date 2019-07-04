<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id']) and isset($_POST['class_name']) and isset($_POST['class_section']) and isset($_POST['class_room']) and isset($_POST['class_subject'])) {
	$db = new dbOperations();
		$result = $db->createClass(
			$_POST['id'],
			$_POST['class_name'] ,
			$_POST['class_section'] ,
			$_POST['class_room'] ,
			$_POST['class_subject']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "class created successfully";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "error while creating class";
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