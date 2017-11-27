package service.topology;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

/**
 * CREATE KEYSPACE dsehealth WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
 * create table dsehealth.node_status (report_id text, id uuid, target_id uuid, target_ip text, dc text, rack text, status text, state text, load float, load_unit text, effective float, token int, primary key ((report_id), id, target_id));
 */

@Table(keyspace = "dsehealth", name = "node_status", readConsistency = "ONE", writeConsistency = "ONE")
public class NodeStatus {

    @PartitionKey
    @Column(name = "report_id")
    private String reportId;
    @ClusteringColumn
    private UUID id;
    @ClusteringColumn
    @Column(name = "target_id")
    private UUID targetId;
    @Column(name = "target_ip")
    private String targetIp;
    private String dc;
    private String rack;
    private String status;
    private String state;
    private Float load;
    @Column(name = "load_unit")
    private String loadUnit;
    private float effective;
    private Integer token;

    public NodeStatus() {
    }

    public NodeStatus(String reportId, UUID id) {
        this.reportId = reportId;
        this.id = id;
    }


    public String getReportId() {
        return reportId;
    }

    public NodeStatus setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public UUID getId() {
        return id;
    }

    public String getDc() {
        return dc;
    }

    public NodeStatus setDc(String dc) {
        this.dc = dc;
        return this;
    }

    public String getRack() {
        return rack;
    }

    public NodeStatus setRack(String rack) {
        this.rack = rack;
        return this;
    }

    public NodeStatus setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public NodeStatus setStatus(String status) {
        this.status = status;
        return this;
    }

    public Float getLoad() {
        return load;
    }

    public NodeStatus setLoad(Float load) {
        this.load = load;
        return this;
    }

    public String getLoadUnit() {
        return loadUnit;
    }

    public NodeStatus setLoadUnit(String loadUnit) {
        this.loadUnit = loadUnit;
        return this;
    }

    public float getEffective() {
        return effective;
    }

    public NodeStatus setEffective(float effective) {
        this.effective = effective;
        return this;
    }

    public String getState() {
        return state;
    }

    public NodeStatus setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getToken() {
        return token;
    }

    public NodeStatus setToken(Integer token) {
        this.token = token;
        return this;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public NodeStatus setTargetId(UUID targetId) {
        this.targetId = targetId;
        return this;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public NodeStatus setTargetIp(String targetIp) {
        this.targetIp = targetIp;
        return this;
    }
}
