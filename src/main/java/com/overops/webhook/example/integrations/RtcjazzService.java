package com.overops.webhook.example.integrations;

import com.overops.webhook.example.data.Event;
import com.overops.webhook.example.data.NewEventPayload;
import com.overops.webhook.example.data.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.team.foundation.common.text.XMLString;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemOperation;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.model.ICategoryHandle;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;

@Service
public class RtcjazzService extends TemplateService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public RtcjazzService(SpringTemplateEngine templateEngine) {
        super(templateEngine);
    	
    }
    
    

    @Value("${webhook.rtcjazz.api.url}")
    private String rtcUrl;

    @Value("${webhook.rtcjazz.api.username}")
    private String rtcUser;

    @Value("${webhook.rtcjazz.api.password}")
    private String rtcPassword;
    
    @Value("${webhook.rtcjazz.api.projectarea}")
    private String rtcProjectArea;
    
    @Value("${webhook.rtcjazz.api.category}")
    private String rtcCategory;

    
    

    

    IWorkItemHandle handle;
    IWorkItem workItem = null;
    IProjectArea projectArea = null;
	ICategoryHandle category = null;
    
    @Override
    public ResponseEntity createEntity(Event event) {

        if (StringUtils.isEmpty(rtcUrl) || StringUtils.isEmpty(rtcUser) || StringUtils.isEmpty(rtcPassword)) {
            throw new IllegalArgumentException("missing required fields");
        }

        
        if(!TeamPlatform.isStarted()) {
			TeamPlatform.startup();}
        
        // create rtc task from OverOps event    	
    	
        
        IProgressMonitor progressMonitor = new NullProgressMonitor();
        ITeamRepository teamRepository = TeamPlatform.getTeamRepositoryService().getTeamRepository(rtcUrl);
		teamRepository.registerLoginHandler(getLoginHandler(rtcUser, rtcPassword));
		URI uri = URI.create(rtcProjectArea.replaceAll(" ", "%20"));
		String summary = event.getData().getSummary();
		String description = getDescription(event); 
		
		
		try {
			teamRepository.login(progressMonitor);
		} catch (TeamRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Login success: [" + teamRepository.loggedIn() + "]");
		
		IProcessClientService processClient= (IProcessClientService) teamRepository.getClientLibrary(IProcessClientService.class);
		IAuditableClient auditableClient= (IAuditableClient) teamRepository.getClientLibrary(IAuditableClient.class);
		IWorkItemClient workItemClient= (IWorkItemClient) teamRepository.getClientLibrary(IWorkItemClient.class);
		
		
		try {
			projectArea = (IProjectArea) processClient.findProcessArea(uri, null, null);
		} catch (TeamRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (projectArea == null) {
			System.out.println("Project area not found.");
		return null;	
		}
		
		List path= Arrays.asList(rtcCategory.split("/"));
	
		try {
			category = workItemClient.findCategoryByNamePath(projectArea, path, null);
		} catch (TeamRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (category == null) {
			System.out.println("Category not found.");
			return null;
		}
			
			IWorkItemType workItemType = null;
			try {
				workItemType = workItemClient.findWorkItemType(projectArea, "defect", null);
			} catch (TeamRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (workItemType == null) {
				System.out.println("Work item type not found.");
				return null;
			}
		
		
		WorkItemInitialization operation= new WorkItemInitialization(summary, description, category);
		
		try {
			handle = operation.run(workItemType, null);
		} catch (TeamRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			workItem = auditableClient.resolveAuditable(handle, IWorkItem.FULL_PROFILE, null);
		} catch (TeamRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Created work item " + workItem.getId() + ".");
		
		
//		teamRepository.logout();
//		System.out.println("Logout success: [" + teamRepository.loggedIn() + "]");
//		TeamPlatform.shutdown();
		return ResponseEntity.ok(workItem.getId());
      
    
    }
    
    public static ILoginHandler2 getLoginHandler(final String user, final String password) {
		return new ILoginHandler2() {
			public ILoginInfo2 challenge(ITeamRepository repo) {
				return new UsernameAndPasswordLoginInfo(user, password);
			}
		};
    }
    
private static class WorkItemInitialization extends WorkItemOperation {
		
		private String fSummary;
		String fdescription;
		private ICategoryHandle fCategory;
		
		public WorkItemInitialization(String summary, String description, ICategoryHandle category) {
			super("Initializing Work Item");
			fSummary= summary;
			fCategory= category;
			fdescription = description;
		}
		
		@Override
		protected void execute(WorkItemWorkingCopy workingCopy, IProgressMonitor monitor) throws TeamRepositoryException {
			IWorkItem workItem= workingCopy.getWorkItem();
			workItem.setHTMLSummary(XMLString.createFromPlainText(fSummary));
			workItem.setCategory(fCategory);
			workItem.setHTMLDescription(XMLString.createFromPlainText(fdescription));
		}
	}
    
}
