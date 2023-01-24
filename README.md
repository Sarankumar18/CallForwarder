# CallForwarder

ONCALL DIVERSION AUTOMATION
 
	Flow: Scheduler will make a call to the API and API fetch the respective shift person from the DB and it will update to the parameter
	Flow: [Oncall Forwarder] a mobile application it will be installed on the mobile which will the service to check for the parameter and get the value through API to the mobile application.
	Flow: If we want to divert the oncall manually, which can be done via web application.
Scheduler Job:
•	If it a weekdays, an API will be called every eight hour once at the time of [7AM, 3PM, 11PM] and if it a weekend, an API will be called every twelve hours once at the time of [7AM AND 7PM] and it will fetch the respective shift person and it will update to the parameter.

                                                   

                                
