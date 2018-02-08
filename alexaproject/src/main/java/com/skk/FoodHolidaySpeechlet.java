package com.skk;





import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This sample shows how to create a simple speechlet for handling speechlet
 * requests.
 */
public class FoodHolidaySpeechlet implements Speechlet {
	private static final Logger log = Logger.getLogger(FoodHolidaySpeechlet.class);
	private static final String NO_MATCH_FOUND= "Sorry, Could not find any Food Holiday matching your criteria";
	private static final String WELCOME= "Welcome to the Alexa Food Holiday Skill, You can ask me , What food holiday is today ? , Or , What "
			+ "Food holiday is on January tenth?";
	private static final String INTENT_SLOT_DATE= "Date";
	private static final String JSON_FILE_DATE= "Date";
	private static final String JSON_FILE_HOLIDAY= "Holiday";
	
	private static final String INTENT_TITLE= "Food Holiday";
	private static final String INTENT_NAME_HOLIDAY= "FoodHolidyIntent";
	private static final String INTENT_NAME_HELP= "AMAZON.HelpIntent";

	@Override
	public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={} " +  request.getRequestId() +  session.getSessionId());
		  // mLoggers.put(Thread.currentThread(), session.getLogger());
		// any initialization logic goes here
	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request, final Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}" +  request.getRequestId() +  session.getSessionId());
		return getWelcomeResponse();
	}

	@Override
	public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}" + request.getRequestId()+ session.getSessionId());

		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;

		if (INTENT_NAME_HOLIDAY.equals(intentName)) {
			return getResponse(intent);
		} else if (INTENT_NAME_HELP.equals(intentName)) {
			return getHelpResponse();
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}

	@Override
	public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
	 	log.info("onSessionEnded requestId={}, sessionId={}"+ request.getRequestId()+ session.getSessionId());
	 	log.debug("Session Ended");
		// any cleanup logic goes here
	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getWelcomeResponse() {
		
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle(INTENT_TITLE);
		card.setContent(WELCOME);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(WELCOME);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the hello intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getResponse(Intent intent) {
		log.debug("Request received. ");
		String speechText = NO_MATCH_FOUND;
		Slot dateSlot = intent.getSlot(INTENT_SLOT_DATE);
        if (dateSlot != null && null != dateSlot.getValue()  && !dateSlot.getValue().isEmpty()) {
        	speechText = readFromJason(dateSlot.getValue());
        }
        
		

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle(INTENT_TITLE);
		card.setContent(speechText);
		

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

	/**
	 * Creates a {@code SpeechletResponse} for the help intent.
	 *
	 * @return SpeechletResponse spoken and visual response for the given intent
	 */
	private SpeechletResponse getHelpResponse() {
		String speechText = "You can say hello to me!";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("HelloWorld");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}
	
	
	private String readFromJason(final String date) {
		log.debug("trying to read from Json : " + date);
		JSONParser parser = new JSONParser();
		JSONArray array = null;
		String parseVal = date.substring(5);
		//String todaysFoodHoliday = NO_MATCH_FOUND;
		StringBuffer todaysFoodHoliday = new StringBuffer(1000);
		
		
			
		try {
			array = (JSONArray) parser.parse(new FileReader("C:\\foodholiday.json"));
		} catch (FileNotFoundException e) {
		    log.error("Error occured : No JSON  file Found ");
		} catch (IOException e) {
		
			log.error("Error occured : Reading JSON  file ");
		} catch (ParseException e) {
		
			log.error("Error occured : Parsing JSON  file ");
		}
	
	
	   log.debug("Key value " + parseVal);
       if (null != array ) {
    	  for (Object o : array)
		  {
		    JSONObject holiday = (JSONObject) o;
		  
		   if ( parseVal.equals((String) holiday.get(JSON_FILE_DATE))) {
			  
			   log.debug("month" +date.substring(5,7));
			   switch (date.substring(5,7)) {
			case "01":
				todaysFoodHoliday .append("Januray");
				break;
			case "02":
				todaysFoodHoliday .append("February");
				break;
			case "03":
				todaysFoodHoliday .append("March");
				break;
			case "04":
				todaysFoodHoliday .append("April");
				break;
			case "05":
				todaysFoodHoliday .append("May");
				break;
			case "06":
				todaysFoodHoliday .append("june");
				break;
			case "07":
				todaysFoodHoliday .append("july");
				break;
			case "08":
				todaysFoodHoliday .append("august");
				break;
			case "09":
				todaysFoodHoliday .append("september");
				break;
			case "10":
				todaysFoodHoliday .append("october");
				break;
			case "11":
				todaysFoodHoliday .append("november");
				break;
			case "12":
				todaysFoodHoliday .append("december");
				break;
			

			default:
				break;
			}
			  
			   
			   todaysFoodHoliday.append(" ");
			   log.debug("Day " + date.substring(8,10));
			   	if (date.charAt(8)=='0') {
			   		todaysFoodHoliday.append(date.substring(9,10));
			   } else {
				   todaysFoodHoliday.append(date.substring(8,10));
			   }
			  
			   
			   todaysFoodHoliday.append(" is "); 
			   todaysFoodHoliday.append(holiday.get(JSON_FILE_HOLIDAY));
		       break;
		   }
          }
		  
		 
		  }
         log.debug("Done Reading .. ");
         if (todaysFoodHoliday.length() == 0) {
        	 todaysFoodHoliday.append(NO_MATCH_FOUND);
         } 
         
		  return todaysFoodHoliday.toString();
	}
}
