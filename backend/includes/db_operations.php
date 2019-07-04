<?php 

  class dbOperations{
  	private $con;
  	function __construct(){
  		require_once dirname(__FILE__).'/db_connect.php';


  		$db = new dbConnect();

  		$this->con = $db->connect();
	}

	public function createUser($email,$user,$pass,$first_name,$last_name,$prof,$gender)
	{
		if ($this->isUserExist($user,$email)) {
			return 0;
		}
		$password = md5($pass);
		$stmt = $this->con->prepare("INSERT INTO `user_info` (`id`,`email`, `username`, `password`, `first_name`, `last_name`, `profession`, `gender`,`facebook_id`,`twitter_id`,`skype_id`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?,NULL,NULL,NULL);");
		$stmt->bind_param("sssssss",$email,$user,$password,$first_name,$last_name,$prof,$gender);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 2;
		}
	}

	public function createClass($id,$class_name,$class_section,$class_room,$class_subject){

		$stmt = $this->con->prepare("INSERT INTO `class` (`id`,`class_name`, `class_section`, `class_room`, `class_subject`) VALUES (?, ?, ?, ?, ?);");
		$stmt->bind_param("sssss",$id,$class_name,$class_section,$class_room,$class_subject);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}

	}
		public function createAssignment($id,$ass_title,$ass_date,$ass_desc,$ass_attach_id,$ass_point,$posted_date){

		$stmt = $this->con->prepare("INSERT INTO `assignment` (`id`, `assignment_id`, `assignment_title`, `assignment_desc`, `assignment_points`, `assignment_date`, `attachment_id`, `posted_date`) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?);");
		$stmt->bind_param("sssssss",$id,$ass_title,$ass_desc,$ass_point,$ass_date,$ass_attach_id,$posted_date);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}

	}
	public function createAttachment($id,$url){
		$stmt = $this->con->prepare("INSERT INTO `attachment` (`id`, `attach_id`, `attach_url`) VALUES (NULL, ?, ?);");
		$stmt->bind_param("ss",$id,$url);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
	}

	public function setAnnounce($class_id,$class_name,$announce_title,$announce_desc,$announce_date){
		$stmt = $this->con->prepare("INSERT INTO `announcements` (`id`, `class_id`, `class_name`, `announce_title`, `announce_desc`, `announce_date`) VALUES (NULL, ?, ?, ?, ?, ?);");
		$stmt->bind_param("sssss",$class_id,$class_name,$announce_title,$announce_desc,$announce_date);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
	}

	public function setStudentMark($class_id,$std_id,$assignment_id,$mark){
		$stmt = $this->con->prepare("UPDATE `student_answer` SET `student_grade` = ? WHERE `student_id` = ? AND `assignment_id` = ? AND `class_id` = ?;");
		$stmt->bind_param("ssss",$mark,$std_id,$assignment_id,$class_id);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
	}


	public function setSocial($id,$f_id,$t_id,$s_id,$which){
		$stmt = $this->con->prepare("UPDATE `user_info` SET `facebook_id` = ? WHERE `id` = ?;");
		if ($which == "facebook"){
			$stmt = $this->con->prepare("UPDATE `user_info` SET `facebook_id` = ? WHERE `id` = ?;");
			$stmt->bind_param("ss",$f_id,$id);
		}elseif ($which == "twitter") {
			$stmt = $this->con->prepare("UPDATE `user_info` SET `twitter_id` = ? WHERE `id` = ?;");
			$stmt->bind_param("ss",$t_id,$id);
		}elseif ($which == "skype") {
			$stmt = $this->con->prepare("UPDATE `user_info` SET `skype_id` = ? WHERE `id` = ?;");
			$stmt->bind_param("ss",$s_id,$id);
		}

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
	}

	public function setAnswer($student_id,$assignment_id,$class_id,$attach_id,$answer_date,$answer_text,$grade,$name){
		$stmt = $this->con->prepare("INSERT INTO `student_answer`(`id`, `student_id`, `student_name`, `assignment_id`, `class_id`, `attach_id`, `answer_date`, `answer_text`, `student_grade`) VALUES (NULL, ?, ?, ?, ?, ?, ?,?,?);");
		$stmt->bind_param("ssssssss",$student_id,$name,$assignment_id,$class_id,$attach_id,$answer_date,$answer_text,$grade);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
	}

	public function joinClass($class_id,$user_id,$user_type,$name){

		if ($this->isUserjoined($class_id,$user_id)) {
			return 0;
		}
		if (!$this->checkClass($class_id)) {
			return 3;
		}

		$stmt = $this->con->prepare("INSERT INTO `user_classes`(`user_id`, `class_id`, `user_type`, `user_name`) VALUES (?, ?, ?, ?);");
		$stmt->bind_param("ssss",$user_id,$class_id,$user_type,$name);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 2;
		}
	}
	public function getJoinedUseres($id){
	$stmt = $this->con->prepare("SELECT * FROM `user_classes` WHERE class_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}

	public function getStdAnswer($std_id,$assignment_id,$class_id){
	$stmt = $this->con->prepare("SELECT * FROM `student_answer` WHERE class_id = ? and assignment_id = ? and student_id = ?");
	$stmt->bind_param("sss",$class_id,$assignment_id,$std_id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}
	public function getStdAnswer1($assignment_id,$class_id){
	$stmt = $this->con->prepare("SELECT * FROM `student_answer` WHERE class_id = ? and assignment_id = ?");
	$stmt->bind_param("ss",$class_id,$assignment_id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}

	public function getAssignments($id){
	$stmt = $this->con->prepare("SELECT * FROM `assignment` WHERE assignment_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}
	public function getAnnouncements($id){
    $stmt = $this->con->prepare("SELECT * FROM `announcements` WHERE class_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}
	public function getAttachments($id){
	$stmt = $this->con->prepare("SELECT * FROM `attachment` WHERE attach_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}

	private function isUserjoined($class_id,$user_id){
	$stmt = $this->con->prepare("SELECT * FROM `user_classes` WHERE user_id = ? AND class_id = ?");
  	$stmt->bind_param("ss",$class_id,$user_id);
  	$stmt->execute();
  	$stmt->store_result();
  	return $stmt->num_rows > 0;
	}


	public function checkClass($id){
	$stmt = $this->con->prepare("SELECT * FROM `class` WHERE id = ?");
  	$stmt->bind_param("s",$id);
  	$stmt->execute();
  	$stmt->store_result();
  	return $stmt->num_rows > 0;
	}

	public function userLogin($username,$pass){
	$password = md5($pass);
  	$stmt = $this->con->prepare("SELECT id FROM `user_info` WHERE username = ? AND password = ?");
	$stmt->bind_param("ss",$username,$password);
  	$stmt->execute();
  	$stmt->store_result();
  	return $stmt->num_rows > 0;
	}

	public function getUserbyUsername($username){
  	$stmt = $this->con->prepare("SELECT * FROM `user_info` WHERE username = ?");
	$stmt->bind_param("s",$username);
	$stmt->execute();
  	return $stmt->get_result()->fetch_assoc();
	}
	public function getUserClassesBy($id){
    $stmt = $this->con->prepare("SELECT * FROM `user_classes` WHERE user_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}
	public function getClassUsersBy($id){
	$stmt = $this->con->prepare("SELECT * FROM `user_classes` WHERE class_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
	}
	public function getClassBy($id){
	$stmt = $this->con->prepare("SELECT * FROM `class` WHERE id = ?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
  	return $stmt->get_result()->fetch_assoc();

}

	public function getUserBy($id){
	$stmt = $this->con->prepare("SELECT * FROM `user_info` WHERE id = ?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
  	return $stmt->get_result()->fetch_assoc();

}


  private function isUserExist($usernaem,$email){
  	$stmt = $this->con->prepare("SELECT id FROM `user_info` WHERE username = ? OR email = ?");
  	$stmt->bind_param("ss",$username,$email);
  	$stmt->execute();
  	$stmt->store_result();
  	return $stmt->num_rows > 0;
  }



  public function getPostContent($id){
  	$stmt = $this->con->prepare("SELECT * FROM `post_content` WHERE post_id = ?");
	$stmt->bind_param("s",$id);
	if ($stmt->execute()) {
		$json = array();
		$result = $stmt->get_result();
		while($row = $result->fetch_assoc()) {
            $json[] = $row;
        }
        $stmt->close();
        return $json;
	}else{
		return NULL;
	}
  }

  public function createPost($id,$name,$date,$text){
  		$stmt = $this->con->prepare("INSERT INTO `post_content` (`id`,`post_id`,`post_name`, `post_date`, `post_text`) VALUES (NULL,? , ?, ?, ?);");
		$stmt->bind_param("ssss",$id,$name,$date,$text);

		if ($stmt->execute()) {
			return 1;
		}else{
			return 0;
		}
  }
}

 ?>