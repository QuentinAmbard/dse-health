package service.topology;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

/**
 * CREATE KEYSPACE dsehealth WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
 * create table dsehealth.node (report_id text, id uuid, ip text, dc text, rack text, primary key ((report_id), id));
 */

@Table(keyspace = "dsehealth", name = "node", readConsistency = "ONE", writeConsistency = "ONE")
public class Node {

    @PartitionKey
    @Column(name = "report_id")
    private String reportId;
    @ClusteringColumn
    private UUID id;
    private String ip;
    private String dc;
    private String rack;
    private Integer exception;
    private Float percentRepaired;

    public Node() {
    }

    public Node(String reportId, UUID id) {
        this.reportId = reportId;
        this.id = id;
    }


    public String getReportId() {
        return reportId;
    }

    public Node setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public UUID getId() {
        return id;
    }

    public String getDc() {
        return dc;
    }

    public Node setDc(String dc) {
        this.dc = dc;
        return this;
    }

    public String getRack() {
        return rack;
    }

    public Node setRack(String rack) {
        this.rack = rack;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Node setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Node setId(UUID id) {
        this.id = id;
        return this;
    }

    public Integer getException() {
        return exception;
    }

    public void setException(Integer exception) {
        this.exception = exception;
    }

    public Float getPercentRepaired() {
        return percentRepaired;
    }

    public void setPercentRepaired(Float percentRepaired) {
        this.percentRepaired = percentRepaired;
    }
}
