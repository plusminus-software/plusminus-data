package software.plusminus.audit.util;

import org.junit.Test;
import software.plusminus.audit.exception.AuditException;
import software.plusminus.audit.model.AuditLog;
import software.plusminus.data.event.CrudAction;

public class AuditLogUtilTest {

    @Test
    public void verifyPresentAuditLogOnCreatePassesWhenCreate() {
        AuditLogUtil.verifyPresentAuditLogOnCreate(auditLog(CrudAction.CREATE));
    }

    @Test(expected = AuditException.class)
    public void verifyPresentAuditLogOnCreateThrowsWhenNotCreate() {
        AuditLogUtil.verifyPresentAuditLogOnCreate(auditLog(CrudAction.UPDATE));
    }

    @Test
    public void verifyPresentAuditLogOnPatchPassesWhenNotDelete() {
        AuditLogUtil.verifyPresentAuditLogOnPatch(auditLog(CrudAction.UPDATE));
    }

    @Test(expected = AuditException.class)
    public void verifyPresentAuditLogOnPatchThrowsWhenDelete() {
        AuditLogUtil.verifyPresentAuditLogOnPatch(auditLog(CrudAction.DELETE));
    }

    private AuditLog<Object> auditLog(CrudAction action) {
        AuditLog<Object> auditLog = new AuditLog<>();
        auditLog.setEntity(new Object());
        auditLog.setAction(action);
        return auditLog;
    }
}
