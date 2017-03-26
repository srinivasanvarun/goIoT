package info.mourya.testcontroller;
import info.mourya.goiot.dto.LoginObject;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StudentAdmissionController {
	@RequestMapping(value="/admissionForm.html", method = RequestMethod.GET)
	public ModelAndView getAdmissionForm() {

		ModelAndView model = new ModelAndView("AdmissionForm");

		return model;
	}
	
	//@RequestMapping("/submitAdmissionForm.html")
	//public ModelAndView submitAdmissionForm(@RequestParam("studentName") String name, @RequestParam("studentHobby") String hobby) {

//		ModelAndView model = new ModelAndView("AdmissionSuccess");
//		model.addObject("msg","Details submitted by you:: Name: "+name+ ", Hobby: " + hobby);

//		return model;
//	}

	@RequestMapping(value="/patientLogin.html", method = RequestMethod.POST)
	public ModelAndView submitAdmissionForm(@RequestParam Map<String,String> reqPar) {

		String userId= reqPar.get("userId");
	   String password = reqPar.get("password");
	  
	   
	   LoginObject loginobject=new LoginObject();
	   loginobject.setUserId(userId);
	   loginobject.setPassword(password);
		
		

		ModelAndView model = new ModelAndView("AdmissionSuccess");
		model.addObject("msg","Details submitted by you:: Name: "+userId+ ", Hobby: " + password);
        model.addObject("loginObject",loginobject);
		return model;
	}
}
