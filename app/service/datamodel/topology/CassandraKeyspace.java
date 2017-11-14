package service.datamodel.topology;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;

@com.datastax.driver.mapping.annotations.Table(keyspace = "dsehealth", name = "cassandra_keyspace", readConsistency = "ONE", writeConsistency = "ONE")
public class CassandraKeyspace {

    @PartitionKey
    @Column(name = "report_id")
    private String reportId;
    @ClusteringColumn
    private String name;
    private String replication;
    private String durableWrites;

    private Long readCount;
    private Integer readLatency;
    private Long writeCount;
    private Integer writeLatency;
    private Integer pendingFlushes;

    public String getReportId() {
        return reportId;
    }

    public CassandraKeyspace setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CassandraKeyspace setName(String name) {
        this.name = name;
        return this;
    }

    public String getReplication() {
        return replication;
    }

    public CassandraKeyspace setReplication(String replication) {
        this.replication = replication;
        return this;
    }

    public String getDurableWrites() {
        return durableWrites;
    }

    public CassandraKeyspace setDurableWrites(String durableWrites) {
        this.durableWrites = durableWrites;
        return this;
    }

    public Long getReadCount() {
        return readCount;
    }

    public CassandraKeyspace setReadCount(Long readCount) {
        this.readCount = readCount;
        return this;
    }

    public Integer getReadLatency() {
        return readLatency;
    }

    public CassandraKeyspace setReadLatency(Integer readLatency) {
        this.readLatency = readLatency;
        return this;
    }

    public Long getWriteCount() {
        return writeCount;
    }

    public CassandraKeyspace setWriteCount(Long writeCount) {
        this.writeCount = writeCount;
        return this;
    }

    public Integer getWriteLatency() {
        return writeLatency;
    }

    public CassandraKeyspace setWriteLatency(Integer writeLatency) {
        this.writeLatency = writeLatency;
        return this;
    }

    public Integer getPendingFlushes() {
        return pendingFlushes;
    }

    public CassandraKeyspace setPendingFlushes(Integer pendingFlushes) {
        this.pendingFlushes = pendingFlushes;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CassandraKeyspace that = (CassandraKeyspace) o;

        if (!reportId.equals(that.reportId)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = reportId.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
