package service.log;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

/**
 * CREATE KEYSPACE dsehealth WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
 * create table dsehealth.log (report_id text, node_id text, date timeuuid, level text, thread text, clazz text, line text, message text, primary key ((report_id, node_id), date))  WITH CLUSTERING ORDER BY (date ASC);
 */

@Table(keyspace = "dsehealth", name = "log", readConsistency = "ONE", writeConsistency = "ONE")
public class Log {

    @PartitionKey(0)
    @Column(name = "report_id")
    private String reportId;
    @PartitionKey(1)
    @Column(name = "node_id")
    private String nodeId;
    @ClusteringColumn
    private UUID date;
    private String level;
    private String thread;
    private String clazz;
    private String line;
    private String message;

    public Log() {
    }

    public Log(String reportId, String nodeId, UUID date, String level, String thread, String clazz, String line, String message) {
        this.reportId = reportId;
        this.nodeId = nodeId;
        this.date = date;
        this.level = level;
        this.thread = thread;
        this.clazz = clazz;
        this.line = line;
        this.message = message;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public UUID getDate() {
        return date;
    }

    public void setDate(UUID date) {
        this.date = date;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Log{" +
                "reportId='" + reportId + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", date=" + date +
                ", level='" + level + '\'' +
                ", thread='" + thread + '\'' +
                ", clazz='" + clazz + '\'' +
                ", line='" + line + '\'' +
                '}';
    }
}
