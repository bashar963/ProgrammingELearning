package com.devbashar.programminglearning.helperClasses

 class Constants{

    val APP_ID="6482065064294608896"
    val acceptedExtList = arrayOf("txt","java","py","cpp","cs","js","kt","mysql","php","c","m")
    private val URL_ROOT ="http://178.62.87.100/project/v1/"
    val URL_REGISTER="${URL_ROOT}registerUser.php"
    val URL_LOGIN = "${URL_ROOT}userLogin.php"
    val URL_CREATE_CLASS= "${URL_ROOT}createClass.php"
    val URL_JOIN_CLASS= "${URL_ROOT}join_class.php"
    val URL_CHECK_CLASS= "${URL_ROOT}checkClassID.php"
    val URL_USER_CLASSES= "${URL_ROOT}getUserClasses.php"
    val URL_CLASS_DET= "${URL_ROOT}getClassDet.php"
    val URL_GET_POST = "${URL_ROOT}getPostContent.php"
    val URL_SET_POST = "${URL_ROOT}createPost.php"
    val URL_UPLOAD_FILE = "${URL_ROOT}upload_file.php"
    val URL_CREATE_ASSIGNMENT = "${URL_ROOT}createAssignment.php"
    val URL_CREATE_ATTACHMENT = "${URL_ROOT}createAttachment.php"
    val URL_GET_ASSIGNMENT = "${URL_ROOT}getAssignments.php"
    val URL_GET_ATTACHMENT = "${URL_ROOT}getAttachment.php"
    val URL_SET_ANSWER = "${URL_ROOT}setStudentAnswer.php"
    val URL_GET_ANSWER = "${URL_ROOT}getStudentAnswer.php"
    val URL_GET_CLASS_USER = "${URL_ROOT}getClassUser.php"
    val URL_GET_HANDED = "${URL_ROOT}getUserHanded.php"
    val URL_SET_STUDENT_MARK = "${URL_ROOT}setStudentMark.php"
    val URL_GET_USER_DET="${URL_ROOT}getUserDet.php"
     val URL_ADD_SOCIAL="${URL_ROOT}addSocialAccount.php"
     val URL_CREATE_ANNOUNC = "${URL_ROOT}createAnnounce.php"
     val URL_GET_ANNOUNCE = "${URL_ROOT}getAnnounce.php"

    val URL_COMPILER_API_CREATE = "http://api.paiza.io:80/runners/create"
    val URL_COMPILER_API_DETAILS = "http://api.paiza.io:80/runners/get_details"
    val URL_COMPILER_API_STATUS = "http://api.paiza.io:80/runners/get_status"
}