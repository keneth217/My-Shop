package com.laptop.Laptop.configs;


import com.laptop.Laptop.audit.AuditLogListener;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class HibernateListenerConfigurer {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Autowired
    private AuditLogListener auditLogListener;


    @PostConstruct
    protected void init() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(auditLogListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(auditLogListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(auditLogListener);
    }

}
