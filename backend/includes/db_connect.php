<?php 

class dbConnect {
	private $con;

	function __construct(){

	}
	function connect()
	{
		
		include_once dirname(__FILE__).'/constants.php';
		$this->con = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME);

		if (mysqli_connect_errno()) {
			echo "Faild to connect";
		}
		return $this->con;
	}
}


 ?>