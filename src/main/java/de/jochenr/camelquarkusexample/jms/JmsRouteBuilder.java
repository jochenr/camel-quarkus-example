/**
 *
 */
package de.jochenr.camelquarkusexample.jms;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.Function;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.TransactedDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.fasterxml.jackson.core.JsonParseException;



/**
 * @author Jochen Riedlinger
 *
 */
public class JmsRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// @formatter:off

        String logCategory = this.getClass().getCanonicalName() + "." + this.getContext().getName();        

        // Stream Caching global einschalten
        getContext().setStreamCaching(true);
        getContext().setAllowUseOriginalMessage(true);

        getContext().setTracing(false);
		
		
		restConfiguration().component("servlet").apiContextPath("apidoc").clientRequestValidation(true);



        onException(Exception.class)
	        .handled(false)
	        .markRollbackOnly()
	        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))	   
			.setBody(constant("FAILED"))     
	        ;

		
		rest("/jms")
            .get("/queuetest")
                .id("queuetest")
				.produces("text/plain")
                .to("direct:sendToAMQ");

       

        from("direct:sendToAMQ")

        	.transacted(TransactedDefinition.PROPAGATION_REQUIRED)
			
			.removeHeaders("*")

        	.log(LoggingLevel.INFO, logCategory, "\"direct:sendToAMQ\" was called!")
        	

			//TODO: testweise mal was an die Queue senden:
			.setBody(constant("Test JMS Message"))

			.to("jms:queue:Meine_Test_XXNMX_Queue?exchangePattern=InOnly&jmsMessageType=Text")
			.to("jms:queue:Meine_Test_Queue?exchangePattern=InOnly&jmsMessageType=Text")

			.setBody(constant("success"))					

			// .process(ex -> {throw new RuntimeException("absicht");})

        ;




        // @formatter:on

	}

}
