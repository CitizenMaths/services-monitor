package com.kint.citizenmaths.services.monitor;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;

public class Mailer
{
	private static final String SUBJECT_OFFLINE = "{service_name} is Offline";
	private static final String SUBJECT_ONLINE = "{service_name} is back Online";
	
	private static final String TOKEN_SERVICE_NAME = "{service_name}";
	
	public Mailer()
	{
		
	}
	
	private String replaceToken(String the_token, String the_string, String the_value)
	{
		return the_string.replace(the_token, the_value);
	}
	
	public void sendMail(String the_api_key, String the_from_email, String the_to_email, String the_service_name, String the_service_url, boolean is_online_email) throws SendGridException
	{
		SendGrid sg = new SendGrid(the_api_key);
		
		Email email = new Email();
		email.addTo(the_to_email);
		email.setFrom(the_from_email);
		
		if (is_online_email)
		{
			makeOnlineMail(email, the_service_name, the_service_url);
		}
		else
		{
			makeOfflineMail(email, the_service_name, the_service_url);
		}

		sg.send(email);
	}
	
	private void makeOfflineMail(Email the_email, String the_service_name, String the_service_url)
	{
		the_email.setSubject(replaceToken(TOKEN_SERVICE_NAME, SUBJECT_OFFLINE, the_service_name));
		
		StringBuffer sb = new StringBuffer();
		sb.append(the_service_name);
		sb.append(" (" + the_service_url + ") ");
		sb.append("is offline currently.");
		
		the_email.setText(sb.toString());
	}
	
	private void makeOnlineMail(Email the_email, String the_service_name, String the_service_url)
	{
		the_email.setSubject(replaceToken(TOKEN_SERVICE_NAME, SUBJECT_ONLINE, the_service_name));
		
		StringBuffer sb = new StringBuffer();
		sb.append(the_service_name);
		sb.append(" (" + the_service_url + ") ");
		sb.append("is back online.");
		
		the_email.setText(sb.toString());
	}
}