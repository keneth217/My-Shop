package com.laptop.Laptop.audit;

import com.laptop.Laptop.controller.ProductController;
import com.laptop.Laptop.dto.AuditTrailDTO;
import com.laptop.Laptop.entity.AuditTrail;
import com.laptop.Laptop.enums.Enums;
import com.laptop.Laptop.services.AuditLogService;
import com.laptop.Laptop.util.ApplicationContextProvider;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AuditLogListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private AuditLogService getAuditLogService() {
        return ApplicationContextProvider.getApplicationContext().getBean(AuditLogService.class);
    }
    private static final Logger logger = LoggerFactory.getLogger(AuditLogListener.class);
    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();

        // Prevent infinite recursion
        if (entity instanceof AuditTrail) {
            return; // Skip auditing if it's an audit trail entity
        }

        // Ensure the entity is of the correct type
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] states = event.getState();

            logger.debug("Processing entity: " + entity.getClass().getSimpleName() + " with ID: " + event.getId());

            for (int i = 0; i < propertyNames.length; i++) {
                String propertyName = propertyNames[i];
                Object newState = states[i];

                logger.debug("Inside On Save ===>>> " + propertyName);
                auditTrailDTOList.add(new AuditTrailDTO(
                        entity.getClass().getCanonicalName(),
                        event.getId().toString(),
                        Enums.AuditEvent.INSERT.name(),
                        propertyName,
                        null, // old value is null for new insertions
                        Objects.toString(newState, "null")
                ));
            }

            // Save audit trails; consider batch saving if necessary
            for (AuditTrailDTO auditTrail : auditTrailDTOList) {
                try {
                    getAuditLogService().save(auditTrail);
                } catch (Exception e) {
                    logger.error("Error saving audit trail: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] currentState = event.getState();
            Object[] previousState = event.getOldState();

            for (int i = 0; i < currentState.length; i++) {
                if (!Objects.equals(previousState[i], currentState[i])) {
                    String propertyName = propertyNames[i];

                    System.out.println("Inside On Flush Dirty ===>>> " + propertyName);

                    auditTrailDTOList.add(new AuditTrailDTO(
                            entity.getClass().getCanonicalName(),
                            event.getId().toString(),
                            Enums.AuditEvent.UPDATE.name(),
                            propertyName,
                            Objects.toString(previousState[i], "null"),
                            Objects.toString(currentState[i], "null")
                    ));
                }
            }
            auditTrailDTOList.forEach(getAuditLogService()::save);
        }
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        Object entity = event.getEntity();
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            String[] propertyNames = event.getPersister().getPropertyNames();
            Object[] state = event.getDeletedState();

            for (int i = 0; i < propertyNames.length; i++) {
                String propertyName = propertyNames[i];

                System.out.println("Inside On Delete ===>>> " + propertyName);

                auditTrailDTOList.add(new AuditTrailDTO(
                        entity.getClass().getCanonicalName(),
                        event.getId().toString(),
                        Enums.AuditEvent.DELETE.name(),
                        propertyName,
                        Objects.toString(state[i], "null"),
                        null
                ));
            }
            auditTrailDTOList.forEach(getAuditLogService()::save);
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
        return false;
    }
}