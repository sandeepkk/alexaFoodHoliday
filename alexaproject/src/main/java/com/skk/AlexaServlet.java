package com.skk;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class AlexaServlet  extends SpeechletServlet {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 5519685430199887724L;
	
	
	public AlexaServlet() {
		    this.setSpeechlet(new FoodHolidaySpeechlet());
		  }
}
