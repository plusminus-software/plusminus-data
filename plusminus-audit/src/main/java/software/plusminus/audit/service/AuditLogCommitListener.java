package software.plusminus.audit.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import software.plusminus.audit.model.AuditLog;
import software.plusminus.audit.repository.AuditLogRepository;

@AllArgsConstructor
@Component
public class AuditLogCommitListener {

    private AuditLogRepository repository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(AuditLog<?> auditLog) {
        repository.setCurrentFalseOnAllOldAuditLogs(
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getNumber()
        );
    }
}
