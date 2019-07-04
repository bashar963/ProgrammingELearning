<?php 



require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['id'])) {
	$db = new dbOperations();
		$result = $db->checkClass(
			$_POST['id']
		);
		if($result){
		$response["error"] = true;
		$response["msg"] = "class is exist";
		}else{
			$response["error"] = false;
			$response["msg"] = "class is not exist";
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
