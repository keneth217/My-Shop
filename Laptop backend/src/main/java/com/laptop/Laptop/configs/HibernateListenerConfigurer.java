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

    private final AuditLogListener auditLogListener;

    @Autowired
    public HibernateListenerConfigurer(AuditLogListener auditLogListener) {
        this.auditLogListener = auditLogListener;
    }

    @PostConstruct
    protected void init() {
        // Unwrap the Hibernate SessionFactory from the EntityManagerFactory
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);

        // Retrieve the EventListenerRegistry from the SessionFactory service registry
        EventListenerRegistry registry = sessionFactory.getServiceRegistry()
                .getService(EventListenerRegistry.class);

        // Register the audit listener for insert, update, and delete events
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(auditLogListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(auditLogListener);
        registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(auditLogListener);
    }
}