/**
 *
 */
package de.jochenr.camelquarkusexample.jms;


import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.messaginghub.pooled.jms.JmsPoolXAConnectionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import io.quarkus.artemis.core.runtime.ArtemisRuntimeConfig;

/**
 * Creates an instance of the Camel JmsComponent and configures it to support JMS transactions.
 *
 * https://github.com/quarkusio/quarkus/issues/14871#issuecomment-1140720699
 */
public class JmsComponentProducer {


    @Produces
    @Named("jmsxa")
    public JmsComponent createJmsComponent(@Named("pooledXaConnectionFactory") ConnectionFactory connectionFactory, @Named("jtaTransactionManager") PlatformTransactionManager transactionManager) {
        JmsComponent jmsComponentTransacted = JmsComponent.jmsComponentTransacted(connectionFactory, transactionManager);
        jmsComponentTransacted.setIncludeSentJMSMessageID(true);
        jmsComponentTransacted.setUseMessageIDAsCorrelationID(true);
        // disable local transactions as JTA TM will take care of enrolling
        jmsComponentTransacted.setTransacted(false);

        // caching does not work with distributed transactions
        jmsComponentTransacted.setCacheLevelName("CACHE_NONE");

		return jmsComponentTransacted;
    }


    @Produces
    @Named("xaConnectionFactory")
    public XAConnectionFactory getXAConnectionFactory(TransactionManager tm, ArtemisRuntimeConfig config) {
        XAConnectionFactory cf = new ActiveMQXAConnectionFactory(config.url, config.username.orElse(null), config.password.orElse(null));
        return cf;
    }


    // adapted from https://github.com/apache/camel-spring-boot-examples/blob/main/spring-boot-jta-jpa/src/main/resources/spring-camel.xml
    @Produces
    @Named("pooledXaConnectionFactory")
    public ConnectionFactory getPooledXAConnectionFactory(@Named("xaConnectionFactory") XAConnectionFactory connectionFactory, TransactionManager transactionManager) {
    	JmsPoolXAConnectionFactory pooledXaCf = new JmsPoolXAConnectionFactory();
    	pooledXaCf.setTransactionManager(transactionManager);
    	pooledXaCf.setConnectionFactory(connectionFactory);
    	pooledXaCf.setMaxConnections(1);
    	pooledXaCf.setMaxSessionsPerConnection(100);
    	pooledXaCf.setUseAnonymousProducers(false);
        return pooledXaCf;
    }


    @Produces
    @Named("jtaTransactionManager")
    PlatformTransactionManager createTransactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

}
