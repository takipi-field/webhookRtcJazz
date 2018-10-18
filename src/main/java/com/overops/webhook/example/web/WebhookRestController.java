package com.overops.webhook.example.web;

import com.overops.webhook.example.data.Event;
import com.overops.webhook.example.integrations.RtcjazzService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
public class WebhookRestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private RtcjazzService rtcjazzService;
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public WebhookRestController(
    		RtcjazzService rtcjazzService,
    		RequestMappingHandlerMapping handlerMapping) {
        this.rtcjazzService = rtcjazzService;
        this.handlerMapping = handlerMapping;
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public ResponseEntity<String> show(Model model) {
     model.addAttribute("handlerMethods", this.handlerMapping.getHandlerMethods());
     return new ResponseEntity<String>(model.toString(), HttpStatus.OK);
    } 
    
    
    
    
    
    @PostMapping(value = "/wh/simple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity simple(@RequestBody Event event) {

        if (Event.Type.ALERT.equals(event.getType())) {

            log.debug("OverOps event posted to /simple via WebHook integration: {}", event.toString());

            // add your custom logic here to do something with the Event...

        }

        return ResponseEntity.ok(HttpStatus.OK);
    }




   
    @PostMapping(value = "/wh/rtcjazz", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity rtcjazzTracker(@RequestBody Event event) {

        if (Event.Type.ALERT.equals(event.getType())) {

            log.debug("OverOps event posted to /wh/rtcjazz via WebHook integration: {}", event.toString());

            ResponseEntity<String> response = rtcjazzService.createEntity(event);

            log.debug("/wh/rtcjazz response: {}", response.toString());

            return response;

        }

        return ResponseEntity.ok(HttpStatus.OK);
    }


}
