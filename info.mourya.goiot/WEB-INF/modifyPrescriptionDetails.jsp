<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<link rel="icon" type="image/png" href="/info.mourya.goiot/resources/headlogo.png">
<title>Home</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-pink.css">
<style>
html,body,h1,h2,h3,h4,h5 {font-family: "Raleway", sans-serif}
.navbuttons{
  width: 100%;
  padding-top:10px;
  padding-bottom:10px;
  padding-left: 16px;
  text-align:left;
  border: none;
  background: none;
  -webkit-transition-duration: 0.6s; /* Safari */
  transition-duration: 0.6s;
}
.navbuttons:hover{
    background-color: #4d94ff; /* Green */
    color: white;
}
.hidden{
  display: none;
}
</style>

<script type="text/javascript">
  divSelected = "";
  var divs = ["d1", "d2", "d3"];
  var visibleDivId = null;
  function toggleVisibility(divId) {
    if(divSelected == undefined || divSelected == ""){
      divSelected = divId;
    }
    else{
      if(divSelected !== divId){
        if(visibleDivId === divId) {
          visibleDivId = null;
        } else {
          visibleDivId = divId;
        }
        hideNonVisibleDivs();
      }
    }
  }

  function hideNonVisibleDivs() {
    var i, divId, div;
    for(i = 0; i < divs.length; i++) {
      divId = divs[i];
      div = document.getElementById(divId);
      if(visibleDivId === divId) {
        div.style.display = "block";
      } else {
        div.style.display = "none";
      }
    }
  }

  
 function functionEditUserMed(medicineId){
	  
	  
	  var patientUserId=document.getElementById("patientUserId").value;
	  var doctorUserId=document.getElementById("doctorUserId").value;
	  document.location.href="/info.mourya.goiot/getPrescriptionDetails/"+patientUserId+"/"+doctorUserId+"/"+medicineId;
	  
	  
  }
  

  
    function functionAddUseMed(){
	  var patientUserId=document.getElementById("patientUserId").value;
	  var doctorUserId=document.getElementById("doctorUserId").value;
	  document.location.href="/info.mourya.goiot/addPrescriptionDetails/"+patientUserId+"/"+doctorUserId;
  }
    
    function functionGoHome(){
  	  document.location.href="/info.mourya.goiot/getDoctorHome";
    }
    
</script>

<body>
<!-- Top container -->
<div class="w3-container w3-top w3-theme-dark w3-large w3-padding" style="z-index:4">
  <button class="w3-btn w3-padding-0 w3-theme-dark w3-hover-text-grey" onclick="w3_open()"><i class="fa fa-bars"></i>&nbsp;</button>
  <span class="w3-right"><img src="/info.mourya.goiot/resources/headlogo.png" style="height:30px;"></span>
</div>
<!-- Sidenav/menu -->


<!-- Overlay effect when opening sidenav on small screens -->
<!--<div class="w3-overlay w3-hide-large w3-animate-opacity" onclick="w3_close()" style="cursor:pointer" title="close side menu" id="myOverlay"></div>-->

<!-- !PAGE CONTENT! -->
<div id="maincontent" class="w3-main" style="margin-top:43px;">
  <div class="w3-container">
    <br><br>
    <header class="w3-container" style="padding-top:22px">
      <h5><b><i class="fa fa-medkit"></i> ${patientDetailsObject.patientLName}</b></h5>
    </header>
      <input type="hidden" id="patientUserId" value="${patientDetailsObject.userId}" >   
      <input type="hidden" id="doctorUserId" value="${patientDetailsObject.doctorUserId}" > 
    <form method="" action="" class="w3-container w3-card-4">
      <div class="w3-row-padding">
        <div id="med">
        <c:forEach items="${patientDetailsObject.medDetails}" var="medDetailsObject"> 
          <div class="w3-half w3-group">   
            <div> 
            <input class="w3-input" type="text" value="${medDetailsObject.pillName}" readonly>
            <label class="w3-label w3-validate">Medicine Name</label>
            </div>
          </div>
          <div class="w3-half w3-row-padding">      
            <div class="w3-quarter w3-group">      
              <input class="w3-input" type="text" value="${medDetailsObject.morning}" readonly>
              <label class="w3-label w3-validate">Morning</label>
            </div>
            <div class="w3-quarter w3-group">      
              <input class="w3-input" type="text" value="${medDetailsObject.afternoon}" readonly>
              <label class="w3-label w3-validate">Afternoon</label>
            </div>
            <div class="w3-quarter w3-group">      
              <input class="w3-input" type="text" value="${medDetailsObject.evening}" readonly>
              <label class="w3-label w3-validate">Night</label>
            </div>
            <div class="w3-quarter w3-group">      
			  <input type="button" onClick="functionEditUserMed(${medDetailsObject.medicineId})" value="Delete">
            </div>
          </div>
         </c:forEach> 
        </div>
      
        <div class="w3-group w3-row-padding">      
          <input class="w3-hover-text-blue" type="button" value="Add"  onclick="functionAddUseMed()">
              
          
        </div>
      </div>
    </form>
    <button onclick="history.back()">Go Back</button>
    <button onclick="functionGoHome()">Home</button>
  </div>
  <br><br>

  <!-- Footer -->
  <footer class="w3-container w3-bottom w3-theme-dark" style="height:50px;padding-left:15px;">
    <p>Developed and powered by <b>goIoT</b> Project Team, <a href="https://scu.edu" target="_blank">Santa Clara University</a></p>
  </footer>

  <!-- End page content -->
</div>
</body>
</html>