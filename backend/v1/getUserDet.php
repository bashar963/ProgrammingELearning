<?php
require_once '../includes/db_operations.php';
$response = array();
if ($_SERVER['REQUEST_METHOD']== 'POST') {
	
	if (isset($_POST['id'])){
		$db = new dbOperations();
		
			$user =  $db->getUserBy($_POST['id']);
		


	}else{
	$response["error"] = true;
	$response["msg"] = "required fields are missings";
	}




	}else{
	$response["error"] = true;
	$response["msg"] = "invalid REQUEST_METHOD";
	}


	echo json_encode($user);




?>