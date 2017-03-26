package info.mourya.goiot.iotcontroller;

import info.mourya.goiot.database.DaoImplementation;
import info.mourya.goiot.dto.AddNewUserObject;
import info.mourya.goiot.dto.AdminDetailsObject;
import info.mourya.goiot.dto.DocDetailsObjecct;
import info.mourya.goiot.dto.DocPatientMapObject;
import info.mourya.goiot.dto.LoginObject;
import info.mourya.goiot.dto.MasterMedicineObject;
import info.mourya.goiot.dto.MedDetailsObject;
import info.mourya.goiot.dto.PatientDetailsObject;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;




// Init binder for session management
// properties editor 15 spring MVC
// @Size for form validation
// @Valid annotation JSR library 
@Controller

public class IotController {

	@RequestMapping(value="*", method = RequestMethod.GET)
	public ModelAndView getAdmissionForm() {

		ModelAndView model = new ModelAndView("LoginForm");

		return model;
	}
	
	@RequestMapping(value="/Logout.html", method = RequestMethod.GET)
	public ModelAndView getLogoutForm(HttpServletRequest request) {
		request.getSession().invalidate();
		ModelAndView model = new ModelAndView("LoginForm");

		return model;
	}
	
	@RequestMapping(value="/espTrigger/{patientId}/{medicineId}/{mae}", method = RequestMethod.GET)
	public ModelAndView getStationValue(@PathVariable("patientId") int patientid,
			@PathVariable("medicineId") int medicineId, @PathVariable("mae") String mae) {
		DaoImplementation daoImplement= new DaoImplementation();
		System.out.println("trigger");
		int trigger=daoImplement.getEspTriggerDetails(patientid,medicineId,mae);
		
		ModelAndView model = new ModelAndView("espTrigger");
		model.addObject("espMsg",trigger);
		return model;
		
	}
	
	@RequestMapping(value="/pillRemainingPercent/{patientId}/{medicineId}/{sensordata}", method = RequestMethod.GET)
	public ModelAndView getStationValue(@PathVariable("patientId") int patientid,
			@PathVariable("medicineId") int medicineId, @PathVariable("sensordata") int sensordata) {
		DaoImplementation daoImplement= new DaoImplementation();
	    boolean success;
	    success=daoImplement.updateSensorData(patientid,medicineId,sensordata);
		
		ModelAndView model = new ModelAndView("success");
		model.addObject("successMsg",success);
		return model;
		
	}
	
	@RequestMapping(value="/androidLoginDetails/{patientId}/{password}", method = RequestMethod.GET)
	public ModelAndView getStationValue(@PathVariable("patientId") String patientid,
			@PathVariable("password") String password) {
		try{
		DaoImplementation daoImplement= new DaoImplementation();
	    boolean success;
	    success=daoImplement.validateAndroidLogin(patientid,password);
		if(success){
		ModelAndView model = new ModelAndView("success");
		model.addObject("successMsg",1);
		return model;
		}else{
			ModelAndView model = new ModelAndView("success");
			model.addObject("successMsg",0);
			return model;
		}
		}catch(Exception e){
			ModelAndView model = new ModelAndView("success");
	 	     model.addObject("successMsg",0);
	 		 return model;
		}
		
	}
	
	@RequestMapping(value="/addMedicineMaster", method = RequestMethod.GET)
	public ModelAndView addMedicineMaster(HttpServletRequest request,@ModelAttribute("loginObject") LoginObject loginObject) {
		ModelAndView model=null;
		if(request.getSession().getAttribute("sessionUserId")!=null)
		{
		Integer sessionUserId=Integer.valueOf(request.getSession().getAttribute("sessionUserId").toString());
			System.out.println(sessionUserId );
			
		if(sessionUserId>999){	
		   model = new ModelAndView("addMedcineMaster");
		
		}
		return model;
		}
		else{
			model = new ModelAndView("LoginForm");

			return model;
		}
	}
	
	@RequestMapping(value="/addNewUser/{roleId}", method = RequestMethod.GET)
	public ModelAndView addDoctorLogin(HttpServletRequest request,@PathVariable("roleId") int roleId) {
		
		if(request.getSession().getAttribute("sessionUserId")!=null){
		ModelAndView model = new ModelAndView("addNewUser");
		model.addObject("roleId",roleId);
	/// only 1 admin SO for now  hardcoded
		
		model.addObject("adminUserId",1003);
		
		
		return model;
		}
		else{
			return getLogoutForm(request);
		}
		
	}
	
	@RequestMapping(value="/addNewUserToDb", method = RequestMethod.POST)
	public ModelAndView addDoctorLogin(HttpServletRequest request,@ModelAttribute("addNewUserObject") AddNewUserObject addNewUserObject) {
		if(request.getSession().getAttribute("sessionUserId")!=null){
		ModelAndView model=null;
		DaoImplementation daoImplementation=new DaoImplementation();
		boolean success=false;
		if(addNewUserObject.getAdminUserId()==1003)
		{
			success=daoImplementation.addNewUserDb(addNewUserObject);
			
			if(success){
				
				AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		    	
		    	adminDetailsObject=daoImplementation.getAdminDetailsDefault(1003,102);
		   	  
		   	   
		    	
		    	model = new ModelAndView("adminLoginHome");	
		    	model.addObject("adminDetailsObject",adminDetailsObject);
		    	model.addObject("adminUserId",1003) ;
		   		return model;
			}
			else{
				
				model = new ModelAndView("addNewUser");
				model.addObject("roleId",addNewUserObject.getRoleId());
			/// only 1 admin SO for now  hardcoded
				
				model.addObject("adminUserId",1003);
				
				
				return model;
				
			}
		
		}
		else{
			model = new ModelAndView("LoginForm");
        
		    model.addObject("failMsg", "please Login again");
		}
		return model;
		}
		else{
			return getLogoutForm(request);
		}
		
	}
	
	@RequestMapping(value="/tagDocPatient")
	public ModelAndView mapDocPatient(HttpServletRequest request) {
		if(request.getSession().getAttribute("sessionUserId")!=null){
		ModelAndView model=null;
		DaoImplementation daoImplementation=new DaoImplementation();
		AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		adminDetailsObject=daoImplementation.getMapDocPatient();
		model=new ModelAndView("tagDocPatient");
		model.addObject("adminDetailsObject",adminDetailsObject);
		
		return model;
		}else{
			return getLogoutForm(request);
		}
		
		
	}
	
	@RequestMapping(value="/mapDocPatient"  , method = RequestMethod.POST  )
	public ModelAndView mapDocPatientPostReq(HttpServletRequest request,@ModelAttribute("docPatientMapObject") DocPatientMapObject docPatientMapObject ) {
		if(request.getSession().getAttribute("sessionUserId")!=null){
		ModelAndView model=null;
		boolean success;
		DaoImplementation daoImplementation=new DaoImplementation();
		AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		success=daoImplementation.getMapDocPatientPostReq(docPatientMapObject);
		if(success)
		{
			
	    	
	    	adminDetailsObject=daoImplementation.getAdminDetailsDefault(1003,102);
	   	  
	   	   
	    	
	    	model = new ModelAndView("adminLoginHome");	
	    	model.addObject("adminDetailsObject",adminDetailsObject);
	    	model.addObject("adminUserId",1003) ;
	   		return model;
		}else
		{
			adminDetailsObject=daoImplementation.getMapDocPatient();
			model=new ModelAndView("tagDocPatient");
			model.addObject("failMsg","Please try again");
			model.addObject("adminDetailsObject",adminDetailsObject);
		}
		
		return model;
		}else{
			return getLogoutForm(request);
		}
		
		
	}
	
	
	
	
	@RequestMapping(value="/addPrescriptionDetails/{patientId}/{doctorId}")
	public ModelAndView addPrecriptionDetails(HttpServletRequest request,@ModelAttribute("medDetailsObject") MedDetailsObject medDetailsObject,
			@PathVariable("patientId") int patientId,@PathVariable("doctorId") int doctorId) {
		if(request.getSession().getAttribute("sessionUserId")!=null){
		ModelAndView model = null;
		List<MasterMedicineObject> masterMedicineObjectList=new ArrayList<MasterMedicineObject>();
		DaoImplementation daoImplement= new DaoImplementation();
		masterMedicineObjectList=daoImplement.getMasterMedicineDetails();
		PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
		System.out.println("addPrescriptionDetails**********");
		if(medDetailsObject.getMedicineId()>0)
		{
			boolean success=daoImplement.changeMedicinePrescriptionDetails(medDetailsObject,patientId,doctorId);
			if(success){
				 patientDetailsObject=daoImplement.getPrescriptionDetails(patientId,doctorId);
					
				   
			     model = new ModelAndView("modifyPrescriptionDetails");
				 model.addObject("patientDetailsObject",patientDetailsObject);
			}else
			{
				model = new ModelAndView("addPrescriptionDetails");
				model.addObject("patientId", patientId );
				model.addObject("doctorId", doctorId);
				model.addObject("failMsg", "Prescription already present");
				model.addObject("masterMedicineObjectList",masterMedicineObjectList);
			}
		}
		else{
		model = new ModelAndView("addPrescriptionDetails");
		model.addObject("patientId", patientId );
		model.addObject("doctorId", doctorId);
		model.addObject("masterMedicineObjectList",masterMedicineObjectList);
		}
		return model;
		}else
		{
			return getLogoutForm(request);
		}
		
	}
	
	@RequestMapping(value="/updateMedicineMaster/{medcineName}/{comment}", method = RequestMethod.GET)
	public ModelAndView modifyMedicineMaster(HttpServletRequest request,@PathVariable("medcineName") String medcineName, 
			@PathVariable("comment") String comment) {
		ModelAndView model = null;
		if(request.getSession().getAttribute("sessionUserId")!=null)
		{
		DaoImplementation daoImplement= new DaoImplementation();
		boolean success=daoImplement.updateMedicineMaster(medcineName,comment);
		
		if(success)
		{  Integer sessionUserId=Integer.valueOf(request.getSession().getAttribute("sessionUserId").toString());
            DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
			
			List<MasterMedicineObject> masterMedicineObjectList=new ArrayList<MasterMedicineObject>();
			
			docDetailsObject= daoImplement.getDocPatientDetails(sessionUserId,100);
			masterMedicineObjectList=daoImplement.getMasterMedicineDetails();
			model = new ModelAndView("doctorLoginHome");	
			
			;
            model.addObject("docDetailsObject",docDetailsObject);
            model.addObject("masterMedicineObjectList", masterMedicineObjectList);
    		return model;
		}
		if(!success)
		{
		model = new ModelAndView("addMedcineMaster");
		model.addObject("successMsg","Exists");
		return model;
		}
		}else
		{
			return getLogoutForm(request);
		}
		return model;
	}
	
	
	@RequestMapping(value="/getDoctorHome", method = RequestMethod.GET)
	public ModelAndView getDoctorHome(HttpServletRequest request) {
		ModelAndView model = null;
		if(request.getSession().getAttribute("sessionUserId")!=null)
		{
		DaoImplementation daoImplement= new DaoImplementation();
		
		
		
		    Integer sessionUserId=Integer.valueOf(request.getSession().getAttribute("sessionUserId").toString());
            DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
			
			List<MasterMedicineObject> masterMedicineObjectList=new ArrayList<MasterMedicineObject>();
			
			docDetailsObject= daoImplement.getDocPatientDetails(sessionUserId,100);
			masterMedicineObjectList=daoImplement.getMasterMedicineDetails();
			model = new ModelAndView("doctorLoginHome");	
            model.addObject("docDetailsObject",docDetailsObject);
            model.addObject("masterMedicineObjectList", masterMedicineObjectList);
    		return model;
		}else
		{
			return getLogoutForm(request);
		}
		
	}
	
	
	
	
	
	@RequestMapping(value="/prescriptions/{patientId}", method = RequestMethod.GET)
	public ModelAndView getPrescriptionsforLcd(@PathVariable("patientId") int patientid) {
		DaoImplementation daoImplement= new DaoImplementation();
		
		
		PatientDetailsObject patientDetailsObject= new PatientDetailsObject();
		patientDetailsObject=daoImplement.getPatientDetailsForDoc(patientid);
		
		ModelAndView model = new ModelAndView("showLCDDetails");
		if(patientDetailsObject.getRole()==0){
		model.addObject("lcdMsg","-1");
		}
		model.addObject("patientDetailsObject",patientDetailsObject);
		return model;
		
	}
	
	@RequestMapping(value="/alarmInput/{patientId}/{mae}", method = RequestMethod.GET)
	public ModelAndView getalarmDetails(@PathVariable("patientId") int patientid,@PathVariable("mae") String mae) {
		DaoImplementation daoImplement= new DaoImplementation();
		System.out.println( " alarmInput");
		List<String> medicineDetailsNTList=new ArrayList<String>();
		
	//	PatientDetailsObject patientDetailsObject= new PatientDetailsObject();
		medicineDetailsNTList=daoImplement.getalarmDetails(patientid,mae);
		
		
		if(medicineDetailsNTList.get(0).compareTo("1")==0){
			
			ModelAndView model = new ModelAndView("success");
		    model.addObject("successMsg",1);
			return model;
		}
         if(medicineDetailsNTList.get(0).compareTo("-1")==0){
        	 ModelAndView model = new ModelAndView("success");
     	    model.addObject("successMsg",-1);
     		return model;
			
		}
        else
		{
		
		ModelAndView model = new ModelAndView("showAlarmDetails");
	    model.addObject("medicineDetailsNTList",medicineDetailsNTList);
		return model;
	    }
		
		
	}
	
	@RequestMapping(value="/refillCheck/{patientId}", method = RequestMethod.GET)
	public ModelAndView getRefillCheck(@PathVariable("patientId") int patientid) {
		DaoImplementation daoImplement= new DaoImplementation();
		System.out.println( " alarmInput");
		List<String> medicineDetailsNTList=new ArrayList<String>();
		
	//	PatientDetailsObject patientDetailsObject= new PatientDetailsObject();
		medicineDetailsNTList=daoImplement.getRefillCheck(patientid);
		
		
		System.out.println(medicineDetailsNTList.size() + " size");
		if(medicineDetailsNTList.size()>0)
		{ModelAndView model = new ModelAndView("showAlarmDetails");
		 model.addObject("medicineDetailsNTList",medicineDetailsNTList);
		 return model;
		}
		else
		{   ModelAndView model = new ModelAndView("success");
		 
			model.addObject("successMsg",0);
			return model;
		}
		
		
	}
	
	
	@RequestMapping(value="/getPrescriptionDetails/{patientId}/{doctorId}/{medId}", method = RequestMethod.GET)
	public ModelAndView getPrescriptionDetails(HttpServletRequest request,@PathVariable("patientId") int patientid,@PathVariable("doctorId") int doctorId, 
			@PathVariable("medId") int medId) {
		if(request.getSession().getAttribute("sessionUserId")!=null)
		{
		DaoImplementation daoImplement= new DaoImplementation();
		boolean modify;
		
	    PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
	    if(medId>0)
	    {
	    	modify=daoImplement.modifyPrescriptionDetails(patientid,doctorId,medId);
	    	
	    }
	    
	    
	     patientDetailsObject=daoImplement.getPrescriptionDetails(patientid,doctorId);
		
	   
	     ModelAndView model = new ModelAndView("modifyPrescriptionDetails");
		 model.addObject("patientDetailsObject",patientDetailsObject);
		 
		return model;
		}
		else{
			return getLogoutForm(request);
		}
		
		
	}
	

	
	
	
	
	
	@RequestMapping(value="/adherenceUpdate/{patientId}/{medicineId}/{mae}/{update}", method = RequestMethod.GET)
	public ModelAndView setAdherenceDetails(@PathVariable("patientId") int patientid,@PathVariable("medicineId") int medicineId, 
			@PathVariable("mae") String mae, @PathVariable("update") int update) {
		DaoImplementation daoImplement= new DaoImplementation();
		
		boolean success=daoImplement.setAdherenceDetails(medicineId,patientid,mae,update);
		
		ModelAndView model = new ModelAndView("espTrigger");
		model.addObject("espMsg",success);
		return model;
		
		
	}
	
	
	//@RequestMapping("/submitAdmissionForm.html")
	//public ModelAndView submitAdmissionForm(@RequestParam("studentName") String name, @RequestParam("studentHobby") String hobby) {

//		ModelAndView model = new ModelAndView("AdmissionSuccess");
//		model.addObject("msg","Details submitted by you:: Name: "+name+ ", Hobby: " + hobby);

//		return model;
//	}
  //  @ModelAttribute for to send common object 
	
	@RequestMapping(value="/Login.html", method = RequestMethod.POST)
	public ModelAndView submitAdmissionForm(HttpServletRequest request,@ModelAttribute("loginObject") LoginObject loginObject,BindingResult result) {
		
		try{

	    int roleId;
	    
		DaoImplementation daoImplement= new DaoImplementation();  
		
		roleId=daoImplement.validateLogin(loginObject);
		System.out.println(roleId);
		if(roleId==0){
			
			ModelAndView model = new ModelAndView("LoginForm");
			model.addObject("failMsg","wrong usename/password");
    		return model;
			
		}
		
	    if(roleId==-1){
			
			System.out.println("wrong usename/password");
				
    	    
    	    ModelAndView model = new ModelAndView("LoginForm");
    	    model.addObject("failMsg","wrong usename/password");
    		return model;
			
		}
	    
   
       
       if(roleId==100){
			
			DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
			
			List<MasterMedicineObject> masterMedicineObjectList=new ArrayList<MasterMedicineObject>();
			
			docDetailsObject= daoImplement.getDocPatientDetails(Integer.valueOf(loginObject.getUserId()),roleId);
			masterMedicineObjectList=daoImplement.getMasterMedicineDetails();
			ModelAndView model = new ModelAndView("doctorLoginHome");	
			request.getSession().setAttribute("sessionUserId", loginObject.getUserId());
			model.addObject("loginObject",loginObject);
            model.addObject("docDetailsObject",docDetailsObject);
            model.addObject("masterMedicineObjectList", masterMedicineObjectList);
    		return model;
			
		}
       
       if(roleId==101){
    	   
    	    PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
    	    
    	    patientDetailsObject=daoImplement.getPatientDetails(Integer.valueOf(loginObject.getUserId()),roleId);
    	    request.getSession().setAttribute("sessionUserId", loginObject.getUserId());
    	    ModelAndView model = new ModelAndView("patientLoginHome");	
    	    model.addObject("loginObject",loginObject);
            model.addObject("patientDetailsObject",patientDetailsObject);
    		return model;
			
			
		}
       
       
       if(roleId==102){
    	   
    	AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
    	
    	adminDetailsObject=daoImplement.getAdminDetails(Integer.valueOf(loginObject.getUserId()),roleId);
   	  
    	request.getSession().setAttribute("sessionUserId", loginObject.getUserId());
    	
    	ModelAndView model = new ModelAndView("adminLoginHome");	
    	model.addObject("adminDetailsObject",adminDetailsObject);
    	model.addObject("adminUserId",loginObject.getUserId()) ;
   		return model;
			
			
		}
		
		 
	
		if(result.hasErrors())
		{
			ModelAndView model = new ModelAndView("LoginForm");
			return model;
			
		}
		
		
		return null;
		
		
		 

		
	}catch(Exception e)
		{
		 ModelAndView model = new ModelAndView("LoginForm");
 	     model.addObject("failMsg","wrong usename/password");
 		 return model;
		}
	}
	
	
}
