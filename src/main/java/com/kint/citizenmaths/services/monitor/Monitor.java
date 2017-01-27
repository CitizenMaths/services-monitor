package com.kint.citizenmaths.services.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sendgrid.SendGridException;

public class Monitor
{
	private List<ServiceBean> services;
	
	private String apikey;
	private String email_from;
	private String email_to;
	
	private static final String CONFIG_FILE = "/monitor-config.properties";
	
	private static final String KEY_SERVICES = "services";
	private static final String KEY_NAME = KEY_SERVICES + ".name.";
	private static final String KEY_URL = KEY_SERVICES + ".url.";
	private static final String KEY_LIVE = KEY_SERVICES + ".live.";
	private static final String KEY_ADDITIONAL_EMAIL = KEY_SERVICES + ".additional.email.";
	
	private static final String KEY_SENDGRID_API_KEY = "monitor.sendgrid.apikey";
	private static final String KEY_SENDGRID_FROM = "monitor.sendgrid.from";
	private static final String KEY_SENDGRID_TO = "monitor.sendgrid.to";
	
	private final Log log = LogFactory.getLog(Monitor.class);
	
	//command line options
	private static final String CONFIG_SHORT = "cf";
	private static final String CONFIG_LONG = "config";
	private static final String CONFIG_DESC="location and name of the configuration file to use";
	
	public Monitor() throws Exception
	{
		
	}
	
	private void init(PropertiesConfiguration the_config)
	{
		services = new ArrayList<ServiceBean>();
		String[] service_keys = the_config.getStringArray(KEY_SERVICES);
		
		for (String k: service_keys)
		{
			String add_email = null;
			if (the_config.containsKey(KEY_ADDITIONAL_EMAIL + k))
			{
				add_email = the_config.getString(KEY_ADDITIONAL_EMAIL + k);
			}
			
			services.add(ServiceBean.create(the_config.getString(KEY_NAME + k),
											the_config.getString(KEY_URL + k),
											the_config.getBoolean(KEY_LIVE + k),
											add_email,
											k));
		}
		
		apikey = the_config.getString(KEY_SENDGRID_API_KEY);
		email_from = the_config.getString(KEY_SENDGRID_FROM);
		email_to = the_config.getString(KEY_SENDGRID_TO);
	}
	
	private Options setUpCommandLineOptions()
	{
		Options opts = new Options();
		opts.addOption(CONFIG_SHORT, CONFIG_LONG, true, CONFIG_DESC);
		return opts;
	}
	
	private void showHelp(Options the_options)
	{
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("Monitor", the_options);
	}
	
	public void run(String[] the_args)
	{
		Options the_options = setUpCommandLineOptions();
		CommandLineParser parser = new PosixParser();
		try
		{
			CommandLine cl = parser.parse(the_options, the_args);
			
			String config_location = cl.getOptionValue(CONFIG_SHORT);
			
			if (config_location == null)
			{
				throw new Exception("Configuration location argument was null");
			}
			
			File cf = new File(config_location);
			if (cf.exists()
				&& cf.isFile())
			{
				PropertiesConfiguration the_config = new PropertiesConfiguration(cf);
				runMonitor(the_config);
			}
			else
			{
				throw new FileNotFoundException("The configuration file cannot be found at: " + cf.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			e.printStackTrace();
			showHelp(the_options);
		}
	}
	
	private void runMonitor(PropertiesConfiguration the_config)
	{
		init(the_config);
		Mailer m = new Mailer();
		for (ServiceBean s: services)
		{
			boolean ok = connectToService(s.getUrl());
			
			if ((s.isLive() && !ok)
				|| (!s.isLive() && ok))
			{
				try
				{
					m.sendMail(apikey, email_from, email_to, s.getName(), s.getUrl(), ok);
					if ((s.getAdditionalEmail() != null)
						&& (!s.getAdditionalEmail().trim().equalsIgnoreCase("")))
					{
						m.sendMail(apikey, email_from, s.getAdditionalEmail().trim(), s.getName(), s.getUrl(), ok);
					}
					the_config.setProperty(KEY_LIVE + s.getConfigKey(), !s.isLive());
					the_config.save();
				}
				catch (SendGridException sge)
				{
					log.error(sge.getMessage(), sge);
					sge.printStackTrace(System.out);
				}
				catch (ConfigurationException cfe)
				{
					log.error(cfe.getMessage(), cfe);
					cfe.printStackTrace(System.out);
				}
			}
		}
	}
	
	private boolean connectToService(String the_service_url)
	{
		boolean ok = false;
		
		try
		{
			URL url = new URL(the_service_url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.connect();
			
			int code = conn.getResponseCode();
			log.info("checking: " + the_service_url);
			log.info("rc: " + code);
			if (code == HttpURLConnection.HTTP_OK
				|| code == HttpURLConnection.HTTP_FORBIDDEN
				|| code == 302)
			{
				ok = true;
			}
		}
		catch (MalformedURLException mue)
		{
			log.error(mue.getMessage(), mue);
			mue.printStackTrace(System.out);
		}
		catch (UnknownHostException uhe)
		{
			log.error(uhe.getMessage(), uhe);
			uhe.printStackTrace(System.out);
		}
		catch (IOException ioe)
		{
			log.error(ioe.getMessage(), ioe);
			ioe.printStackTrace(System.out);
		}
		
		return ok;
	}
	
	public static void main(String[] args) throws Exception
	{
		Monitor m = new Monitor();
		m.run(args);
	}
}