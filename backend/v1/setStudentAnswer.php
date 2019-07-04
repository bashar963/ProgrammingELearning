<?php 
require_once '../includes/db_operations.php';
$response = array();

if ($_SERVER['REQUEST_METHOD']== 'POST') {

if (isset($_POST['std_id'])) {
	$db = new dbOperations();
		$result = $db->setAnswer(
			$_POST['std_id'],
			$_POST['assignment_id'],
			$_POST['class_id'] ,
			$_POST['attach_id'],
			$_POST['answer_date'],
			$_POST['answer_text'],
			$_POST['std_grade'],
			$_POST["std_name"]
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