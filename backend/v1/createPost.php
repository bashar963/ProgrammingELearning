<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id']) and isset($_POST['name']) and isset($_POST['date']) and isset($_POST['text'])) {
	$db = new dbOperations();
		$result = $db->createPost(
			$_POST['id'],
			$_POST['name'],
			$_POST['date'] ,
			$_POST['text']
		);
		if($result == 1){
		$response["error"] = false;
		$response["msg"] = "post created successfully";
		}elseif($result == 0){
			$response["error"] = true;
			$response["msg"] = "error while creating post";
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