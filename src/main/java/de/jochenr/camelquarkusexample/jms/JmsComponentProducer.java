// /**
//  *
//  */
// package de.jochenr.camelquarkusexample.jms;

// import javax.enterprise.inject.Produces;
// import javax.inject.Named;
// import javax.jms.ConnectionFactory;
// import javax.jms.XAConnectionFactory;
// import javax.transaction.TransactionManager;
// import javax.transaction.UserTransaction;

// import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
// import org.apache.camel.component.jms.JmsComponent;
// import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
// import org.jboss.narayana.jta.jms.TransactionHelperImpl;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.jta.JtaTransactionManager;

// import io.quarkus.artemis.core.runtime.ArtemisRuntimeConfig;

// /**
//  * Creates an instance of the Camel JmsComponent and configures it to support
//  * JMS transactions.
//  *
//  * Hinweis:
//  * laut https://access.redhat.com/articles/310603 im Abschnitt "Supported
//  * Messaging Clients"
//  * muss für AMQ 7.1 die "camel-jms" oder "camel-sjms" Komponente verwendet
//  * werden (nicht die "activemq" Komponente).
//  */
// public class JmsComponentProducer {

//     @Produces
//     @Named("jmsxa")
//     public JmsComponent createJmsComponent(@Named("xaConnectionFactory") ConnectionFactory connectionFactory,
//             @Named("jtaTransactionManager") PlatformTransactionManager transactionManager) {
//         JmsComponent jmsComponentTransacted = JmsComponent.jmsComponentTransacted(connectionFactory,
//                 transactionManager);
//         jmsComponentTransacted.setIncludeSentJMSMessageID(true);
//         jmsComponentTransacted.setUseMessageIDAsCorrelationID(true);
//         jmsComponentTransacted.setTransacted(true);
//         return jmsComponentTransacted;
//     }

//     // wegen: // https://github.com/apache/camel-quarkus/issues/2815
//     // Die Methode ist aus
//     // https://github.com/apache/camel-quarkus/blob/main/integration-tests/jta/src/main/java/org/apache/camel/quarkus/component/jta/it/XAConnectionFactoryConfiguration.java#L41
//     // geklaut

//     // This class should be remove if
//     // https://github.com/quarkusio/quarkus/issues/14871 resolved
//     // And the ConnectionFactory could be integrated with TransactionManager
//     @Produces
//     @Named("xaConnectionFactory")
//     public ConnectionFactory getXAConnectionFactory(TransactionManager tm, ArtemisRuntimeConfig config) {
//         XAConnectionFactory cf = new ActiveMQXAConnectionFactory(config.url.orElse(null), config.username.orElse(null),
//                 config.password.orElse(null));
//         return new ConnectionFactoryProxy(cf, new TransactionHelperImpl(tm));

//     }

//     @Produces
//     @Named("jtaTransactionManager")
//     PlatformTransactionManager createTransactionManager(UserTransaction userTransaction,
//             TransactionManager transactionManager) {
//         return new JtaTransactionManager(userTransaction, transactionManager);
//     }

// }
