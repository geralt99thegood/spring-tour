package java.com.example.springtour.config.audit;

public final class AuditContextHolder {

    private static final ThreadLocal<AuditEntry> auditContext = new ThreadLocal<>();

    private AuditContextHolder(){}


    public static void setAuditEntry(AuditEntry entry) {
        auditContext.set(entry);
    }


    public static AuditEntry getAuditEntry() {
        return auditContext.get();
    }

    public static void clear(){
        auditContext.remove();
    }


}
