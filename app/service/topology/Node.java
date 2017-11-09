package service.topology;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

/**
 * CREATE KEYSPACE dsehealth WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
 * create table dsehealth.node (report_id text, node_id text, dc text, rack text, primary key ((report_id), node_id, date));
 */

@Table(keyspace = "dsehealth", name = "node", readConsistency = "ONE", writeConsistency = "ONE")
public class Node {

    @PartitionKey
    @Column(name = "report_id")
    private String reportId;
    @ClusteringColumn
    @Column(name = "external_ip")
    private String externalIp;
    @Column(name = "internal_ip")
    private String internalIp;
    private String dc;
    private String rack;
    private String id;

    public Node() {
    }

    public Node(String reportId, String externalIp) {
        this.reportId = reportId;
        this.externalIp = externalIp;
    }


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getInternalIp() {
        return internalIp;
    }

    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }
}
