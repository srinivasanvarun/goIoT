<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<title>goIoT Login Page</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css">
<link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-pink.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<body>

<!-- Header -->
<header class="w3-container w3-padding" id="myHeader">
  <div class="w3-center">
  <img src="/info.mourya.goiot/resources/mainlogo.jpg" alt="goIoT" style="width:25%">
  <p class="w3-animate-opacity">An IoT solution for smart pill management</p>
  </div>
</header>

<!--Login form-->
<div class="w3-container w3-padding w3-row-padding" style="background:url(/info.mourya.goiot/resources/pills.jpg);background-attachment:fixed;">
<div class="w3-row-padding w3-center w3-margin-top">
<div class="w3-third">
  <div class="w3-card-1 w3-padding-top">
  </div>
</div>
<div class="w3-third">
  <div class="w3-card-4 w3-padding-top w3-theme-dark" style="min-height:300px">
         ${failMsg}
    <h1><i class="fa fa-user w3-text" style="font-size:50px"></i>&emsp;Login</h1>
    <form action="/info.mourya.goiot/Login.html" method="post" class="w3-container">
      <div class="w3-group">
        <input class="w3-input" type="text" placeholder="UserId" name="userId" required>   
      </div>
      <div class="w3-group"> 
        <input class="w3-input" type="password" placeholder="Password" name="password" required> 
      </div>
      <div class="w3-group">   
        <input class="w3-button" type="submit" value="Submit">
      </div>
    </form>
  </div>
  <br>
</div>
<div class="w3-third">
  <div class="w3-card-1 w3-padding-top">
  </div>
</div>
</div>
</div>

<!--General Statuses-->
<div class="w3-container">
   <div class="w3-main">
   <div class="w3-container">
    <span class="w3-center w3-padding"><h2><b>goIoT Project Group</b></h2></span><hr>
   </div>
  <div id="d1" class="w3-container" style="margin-left:10%;margin-right:10%;margin-top:43px;">
    <br><br>
    <div class="w3-container">
    <h3>Varun Srinivasan</h3>
    <div class="w3-row">
      <div class="w3-col m2 text-center">
        <img class="w3-circle" src="/info.mourya.goiot/resources/varun.jpeg" alt="Varun" style="width:96px;height:96px">
      </div>
      <div class="w3-col m10 w3-container">
        <h4>Software Developer</h4>
        <p>A Computer Science Engineering Graduate student at Santa Clara University. A code enthusiasist, and an ardent carnatic music singer &amp; listener. Stays at Santa Clara, California. Basically from Chennai, Tamil Nadu, India.
        </p><br>
      </div>
    </div>
    <hr>
    <h3>Lakshmi Shankarrao</h3>
    <div class="w3-row">
      <div class="w3-col m2 text-center">
        <img class="w3-circle" src="/info.mourya.goiot/resources/lakshmi.jpg" alt="Lakshmi" style="width:96px;height:96px">
      </div>
      <div class="w3-col m10 w3-container">
        <h4>Software Engineer</h4>
        <p>A Computer Science Engineering Graduate student at Santa Clara University. A fast learner and coder. Stays at Sunnyvale, California. Basically from Bangalore, Karnataka, India.</p><br>
      </div>
    </div>
    <hr>
    <h3>Sri Mourya Dommeti</h3>
    <div class="w3-row">
      <div class="w3-col m2 text-center">
        <img class="w3-circle" src="/info.mourya.goiot/resources/mourya.jpg" alt="Mourya" style="width:96px;height:96px">
      </div>
      <div class="w3-col m10 w3-container">
        <h4>Software Engineer</h4>
        <p>A Computer Science Engineering Graduate student at Santa Clara University. Stays at Foster City, California. Basically from Hyderabad, Andra Pradesh, India.</p><br>
      </div>
    </div>
  </div>
  </div>
</div>
<!-- Footer -->
<footer class="w3-container w3-theme-dark">
  <p>Developed and powered by <b>goIoT</b> Project Team, <a href="https://scu.edu" target="_blank">Santa Clara University</a></p>
</footer>

</body>
</html>


