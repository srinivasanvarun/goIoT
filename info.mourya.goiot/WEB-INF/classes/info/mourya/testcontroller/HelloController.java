package info.mourya.testcontroller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
 
  
 
	@Controller
	@RequestMapping("/greet")
	public class HelloController {

		@RequestMapping("/welcome/country/{name}")
		public ModelAndView helloWorld(@PathVariable("name") String name) {

			ModelAndView model = new ModelAndView("HelloPage");
			model.addObject("msg","hello world");

			return model;
		}
		
		public static void main(String[] args){
			Calendar cal = Calendar.getInstance();
			for(int i=0 ;i<20;i++){
				cal.add(Calendar.DAY_OF_MONTH, i);
				Date entryDate = cal.getTime();
				java.sql.Date sqlDate = new java.sql.Date(entryDate.getTime());
				System.out.println(sqlDate);
			}
			
		}
		
		@RequestMapping("/hi")
		public ModelAndView hiWorld() {

			ModelAndView model = new ModelAndView("HelloPage");
			model.addObject("msg","kasi world");

			return model;
		}

	}
