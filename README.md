# OverOps Rational Team Concert JAZZ CCM Webhook

For a detailed explanation of the OverOps WebHook functionality please visit https://doc.overops.com/docs/outgoing-webhook

This project provides a webhook service using SpringBoot to create defects in Rational Team Concert Jazz CCM.  This webhook is based on the fancy shmancy webhook framework create by Tim Veil.  Read more about it here:
https://github.com/timveil/oo-webhook-example

## Getting Started inside your IDE

To get started simply run the following command or use your IDE of choice.

```bash
./mvnw spring-boot:run
``` 

## Getting Started stand-alone Jar file
download bin/webhook-rtcjazz-x.x.jar  and bin/application.properties.  Place in same directory.
update application.properties 

java -jar webhook-rtcjazz-x.x.jar


By default, this will start up the provided examples running inside an embedded tomcat instance listening on port `8080`.  This can be easily changed by modifying `application.properties`, etc.

To begin receiving events, enable "Webhook" alerts on any OverOps View.  You should provide one of the following URL's to OverOps.

```
http://<your host name or ip>:8080/wh/rtcjazz
```

Keep in mind, these need to be accessible from the OverOps server (SaaS or On-prem).  You can also visit my Docker Demos repo for an complete on-prem example: https://github.com/timveil/docker-demos/tree/master/onprem/webhook-example

If you're using Docker for Mac, like I am for most testing, you can use the following base URLs.

```
http://host.docker.internal:8090/wh/rtcjazz
```
## RTC Jazz settings

Rational Team Concert Jazz CCM create defect webhook.

```properties
webhook.rtcjazz.api.url=https://jazz.net/xxxxx-ccm
webhook.rtcjazz.api.username=
webhook.rtcjazz.api.password=
webhook.rtcjazz.api.projectarea=<Project Area full name>
webhook.rtcjazz.api.category=<Full category path>
```

