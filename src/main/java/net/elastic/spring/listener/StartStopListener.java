package net.elastic.spring.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import net.elastic.spring.recipe.ElastiSearchService;

@WebListener
public class StartStopListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
	
	ElastiSearchService elastiSearchService = null;
	elastiSearchService = ElastiSearchService.getInstance();

	elastiSearchService.createPipeline();

	elastiSearchService.RestClientBuilder();
        
        System.out.println("Gaurav Servlet has been started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

	ElastiSearchService elastiSearchService = null;
	elastiSearchService = ElastiSearchService.getInstance();
	elastiSearchService.RestClientCleanUp();

        System.out.println("Cleaning up..............");
        System.out.println("Servlet has been stopped.");
    }

}
