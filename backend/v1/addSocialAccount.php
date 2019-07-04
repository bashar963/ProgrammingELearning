<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id'])) {
	$db = new dbOperations();
		$result = $db->setSocial(
			$_POST['id'],
			$_POST['f_id'],
			$_POST['t_id'] ,
			$_POST['s_id'],
			$_POST['which']
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