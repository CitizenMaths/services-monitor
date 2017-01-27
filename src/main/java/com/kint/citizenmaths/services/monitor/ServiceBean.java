package com.kint.citizenmaths.services.monitor;

public class ServiceBean
{
	private String name;
	private String url;
	private boolean live;
	private String additionalEmail;
	private String configKey;
	
	public ServiceBean()
	{
		additionalEmail = null;
	}
	
	private ServiceBean(String the_name, String the_url, boolean is_live, String the_additional_email, String the_config_key)
	{
		name = the_name;
		url = the_url;
		live = is_live;
		additionalEmail = the_additional_email;
		configKey = the_config_key;
	}
	
	public static ServiceBean create(String the_name, String the_url, boolean is_live, String the_additional_email, String the_config_key)
	{
		return new ServiceBean(the_name, the_url, is_live, the_additional_email, the_config_key);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public boolean isLive()
	{
		return live;
	}
	
	public void setLive(boolean live)
	{
		this.live = live;
	}
	
	public String getAdditionalEmail()
	{
		return additionalEmail;
	}
	
	public void setAdditionalEmail(String additionalEmail)
	{
		this.additionalEmail = additionalEmail;
	}
	
	public String getConfigKey() 
	{
		return configKey;
	}
	
	public void setConfigKey(String configKey)
	{
		this.configKey = configKey;
	}
}