<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id']) and isset($_POST['url'])) {
	$db = new dbOperations();
		$result = $db->createAttachment(
			$_POST['id'],
			$_POST['url']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "Attachment created successfully";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "error while creating Attachment";
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