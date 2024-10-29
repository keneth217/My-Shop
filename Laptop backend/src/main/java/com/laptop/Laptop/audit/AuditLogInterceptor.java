package com.laptop.Laptop.audit;

import com.laptop.Laptop.dto.AuditTrailDTO;
import com.laptop.Laptop.enums.Enums;
import com.laptop.Laptop.services.AuditLogService;
import com.laptop.Laptop.util.ApplicationContextProvider;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuditLogInterceptor extends EmptyInterceptor {

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "SYSTEM";
    }

    private String getComputerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UNKNOWN";
        }
    }

    private String getComputerAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "UNKNOWN";
        }
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider
                    .getApplicationContext().getBean("auditLogService");

            String modifiedBy = getCurrentUser();
            String computerName = getComputerName();
            String computerAddress = getComputerAddress();

            for (int i = 0; i < currentState.length; i++) {
                if (!previousState[i].equals(currentState[i])) {
                    auditTrailDTOList.add(
                            new AuditTrailDTO(
                                    entity.getClass().getCanonicalName(),
                                    id.toString(),
                                    Enums.AuditEvent.UPDATE.name(),
                                    propertyNames[i],
                                    previousState[i].toString(),
                                    currentState[i].toString(),
                                    modifiedBy,
                                    computerName,
                                    computerAddress
                            )
                    );
                }
            }
            auditTrailDTOList.forEach(auditLogService::save);
        }
        return true;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider
                    .getApplicationContext().getBean("auditLogService");

            String modifiedBy = getCurrentUser();
            String computerName = getComputerName();
            String computerAddress = getComputerAddress();

            for (int i = 0; i < propertyNames.length; i++) {
                auditTrailDTOList.add(
                        new AuditTrailDTO(
                                entity.getClass().getCanonicalName(),
                                id.toString(),
                                Enums.AuditEvent.INSERT.name(),
                                propertyNames[i],
                                null,
                                state[i].toString(),
                                modifiedBy,
                                computerName,
                                computerAddress
                        )
                );
            }
            auditTrailDTOList.forEach(auditLogService::save);
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof AuditAware) {
            List<AuditTrailDTO> auditTrailDTOList = new ArrayList<>();
            AuditLogService auditLogService = (AuditLogService) ApplicationContextProvider
                    .getApplicationContext().getBean("auditLogService");

            String modifiedBy = getCurrentUser();
            String computerName = getComputerName();
            String computerAddress = getComputerAddress();

            for (int i = 0; i < propertyNames.length; i++) {
                auditTrailDTOList.add(
                        new AuditTrailDTO(
                                entity.getClass().getCanonicalName(),
                                id.toString(),
                                Enums.AuditEvent.DELETE.name(),
                                propertyNames[i],
                                state[i].toString(),
                                null,
                                modifiedBy,
                                computerName,
                                computerAddress
                        )
                );
            }
            auditTrailDTOList.forEach(auditLogService::save);
        }
    }
}