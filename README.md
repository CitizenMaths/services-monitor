# services-monitor
Java application that is run as a cron job to check whether certain sites that Citizen Maths use are running or down, and email someone to let them know of a sites status.

This is a maven project, so you will need that installed to build it. You will need to use the maven assembly command to build it also.

The project uses SendGrid (https://sendgrid.com/) as the email delivery system, so you would need to have an account on there and create an API Key which needs to be set in the configuration file.
You can open a SendGrid account for free, and you can send so many (12000) for free each month.

To configure the application:

In src/main/resources you will find the monitor-config.properties file.
services is a list of the key names for each individual service that needs to be monitored.
For each of these key names (e.g. servicekey1), there are three mandatory values that need to be set, and one optional one.
The mandatory values need to be set using the following key structure (using the example key name servicekey1)
  - services.name.servicekey1= The name of the service here.
  - services.url.servicekey1= The URL of the service for the monitor to check.
  - services.live.servicekey1= The status of whether this service is currently live or down. It is a boolean value so true or false. By default, set this to true.

The optional value uses the following key structure, again using the example key name servicekey1
  - services.additional.email.servicekey1= When this monitor needs to send the email notification to another email address besides the main email address. This other email address goes here.

Lastly, these values need to be set
  - monitor.sendgrid.apikey= The api key created from your SendGrid account here.
  - monitor.sendgrid.from= The email address that it is from.
  - monitor.sendgrid.to= The main email address where the monitor should send these messages to.
