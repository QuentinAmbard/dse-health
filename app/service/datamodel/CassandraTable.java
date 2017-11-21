package service.datamodel;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;

/**
 * CREATE KEYSPACE dsehealth WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
 * create name dsehealth.tablestat (
 * report_id text,
 * ks text,
 * name text,
 * sstableCount text,
 * spaceUsedLive text,
 * spaceUsedTotal text,
 * spaceUsedSnapshot text,
 * compressionRation float,
 * keyNumber bigint,
 * memtableCellCount bigint,
 * memtableDataSize bigint,
 * memtableOffHeapUsed bigint,
 * memtableSwitchCount bigint,
 * localReadCount bigint,
 * localReadLatency float,
 * localWriteCount bigint,
 * localWriteLatency float,
 * pendingFlushes bigint,
 * bloomFilterFalsePositive bigint,
 * bloomFilterFalseRatio float,
 * bloomFilterSpaceUsed bigint,
 * bloomFilterOffHeapUsed bigint,
 * indexSummaryOffHeapUsed bigint,
 * compressionMetadataOffHeapUsed bigint,
 * partitionMinBytes bigint,
 * partitionMaxBytes bigint,
 * partitionMeanBytes bigint,
 * averageLiveCellPerSlice float,
 * maximumLiveCellPerSlice bigint,
 * averageTombstonePerSlice float,
 * maximumTombstonePerSlice bigint,
 * primary key ((report_id), ks, tbl));
 */

@com.datastax.driver.mapping.annotations.Table(keyspace = "dsehealth", name = "cassandra_table", readConsistency = "ONE", writeConsistency = "ONE")
public class CassandraTable {

    @PartitionKey
    @Column(name = "report_id")
    private String reportId;
    @ClusteringColumn(0)
    @Column(name = "ks")
    private String keyspace;
    @ClusteringColumn(1)
    private String name;
    private boolean index;
    private Long sstableCount;
    private Long spaceUsedLive;
    private Long spaceUsedTotal;
    private Long spaceUsedSnapshot;
    private Long offHeapMemoryUsed;
    private Float compressionRation;
    private Long keyNumber;
    private Long memtableCellCount;
    private Long memtableDataSize;
    private Long memtableOffHeapUsed;
    private Long memtableSwitchCount;
    private Long localReadCount;
    private Float localReadLatency;
    private Long localWriteCount;
    private Float localWriteLatency;
    private Long pendingFlushes;
    private Float percentRepaired;
    private Long bloomFilterFalsePositive;
    private Float bloomFilterFalseRatio;
    private Long bloomFilterSpaceUsed;
    private Long bloomFilterOffHeapUsed;
    private Long indexSummaryOffHeapUsed;
    private Long compressionMetadataOffHeapUsed;
    private Long partitionMinBytes;
    private Long partitionMaxBytes;
    private Long partitionMeanBytes;
    private Float averageLiveCellPerSlice;
    private Long maximumLiveCellPerSlice;
    private Float averageTombstonePerSlice;
    private Long maximumTombstonePerSlice;
    private Long droppedMutation;


    public CassandraTable() {
    }

    public boolean isIndex() {
        return index;
    }

    public CassandraTable setIndex(boolean index) {
        this.index = index;
        return this;
    }

    public String getReportId() {
        return reportId;
    }

    public CassandraTable setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public CassandraTable setKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    public Long getSstableCount() {
        return sstableCount;
    }

    public CassandraTable setSstableCount(Long sstableCount) {
        this.sstableCount = sstableCount;
        return this;
    }

    public Long getSpaceUsedLive() {
        return spaceUsedLive;
    }

    public CassandraTable setSpaceUsedLive(Long spaceUsedLive) {
        this.spaceUsedLive = spaceUsedLive;
        return this;
    }

    public Long getSpaceUsedTotal() {
        return spaceUsedTotal;
    }

    public CassandraTable setSpaceUsedTotal(Long spaceUsedTotal) {
        this.spaceUsedTotal = spaceUsedTotal;
        return this;
    }

    public Long getSpaceUsedSnapshot() {
        return spaceUsedSnapshot;
    }

    public CassandraTable setSpaceUsedSnapshot(Long spaceUsedSnapshot) {
        this.spaceUsedSnapshot = spaceUsedSnapshot;
        return this;
    }

    public Long getOffHeapMemoryUsed() {
        return offHeapMemoryUsed;
    }

    public CassandraTable setOffHeapMemoryUsed(Long offHeapMemoryUsed) {
        this.offHeapMemoryUsed = offHeapMemoryUsed;
        return this;
    }

    public Float getCompressionRation() {
        return compressionRation;
    }

    public CassandraTable setCompressionRation(Float compressionRation) {
        this.compressionRation = compressionRation;
        return this;
    }

    public Long getKeyNumber() {
        return keyNumber;
    }

    public CassandraTable setKeyNumber(Long keyNumber) {
        this.keyNumber = keyNumber;
        return this;
    }

    public Long getMemtableCellCount() {
        return memtableCellCount;
    }

    public CassandraTable setMemtableCellCount(Long memtableCellCount) {
        this.memtableCellCount = memtableCellCount;
        return this;
    }

    public Long getMemtableDataSize() {
        return memtableDataSize;
    }

    public CassandraTable setMemtableDataSize(Long memtableDataSize) {
        this.memtableDataSize = memtableDataSize;
        return this;
    }

    public Long getMemtableOffHeapUsed() {
        return memtableOffHeapUsed;
    }

    public CassandraTable setMemtableOffHeapUsed(Long memtableOffHeapUsed) {
        this.memtableOffHeapUsed = memtableOffHeapUsed;
        return this;
    }

    public Long getMemtableSwitchCount() {
        return memtableSwitchCount;
    }

    public CassandraTable setMemtableSwitchCount(Long memtableSwitchCount) {
        this.memtableSwitchCount = memtableSwitchCount;
        return this;
    }

    public Long getLocalReadCount() {
        return localReadCount;
    }

    public CassandraTable setLocalReadCount(Long localReadCount) {
        this.localReadCount = localReadCount;
        return this;
    }

    public Float getLocalReadLatency() {
        return localReadLatency;
    }

    public CassandraTable setLocalReadLatency(Float localReadLatency) {
        this.localReadLatency = localReadLatency;
        return this;
    }

    public Long getLocalWriteCount() {
        return localWriteCount;
    }

    public CassandraTable setLocalWriteCount(Long localWriteCount) {
        this.localWriteCount = localWriteCount;
        return this;
    }

    public Float getLocalWriteLatency() {
        return localWriteLatency;
    }

    public CassandraTable setLocalWriteLatency(Float localWriteLatency) {
        this.localWriteLatency = localWriteLatency;
        return this;
    }

    public Long getPendingFlushes() {
        return pendingFlushes;
    }

    public CassandraTable setPendingFlushes(Long pendingFlushes) {
        this.pendingFlushes = pendingFlushes;
        return this;
    }

    public Float getPercentRepaired() {
        return percentRepaired;
    }

    public CassandraTable setPercentRepaired(Float percentRepaired) {
        this.percentRepaired = percentRepaired;
        return this;
    }

    public Long getBloomFilterFalsePositive() {
        return bloomFilterFalsePositive;
    }

    public CassandraTable setBloomFilterFalsePositive(Long bloomFilterFalsePositive) {
        this.bloomFilterFalsePositive = bloomFilterFalsePositive;
        return this;
    }

    public Float getBloomFilterFalseRatio() {
        return bloomFilterFalseRatio;
    }

    public CassandraTable setBloomFilterFalseRatio(Float bloomFilterFalseRatio) {
        this.bloomFilterFalseRatio = bloomFilterFalseRatio;
        return this;
    }

    public Long getBloomFilterSpaceUsed() {
        return bloomFilterSpaceUsed;
    }

    public CassandraTable setBloomFilterSpaceUsed(Long bloomFilterSpaceUsed) {
        this.bloomFilterSpaceUsed = bloomFilterSpaceUsed;
        return this;
    }

    public Long getBloomFilterOffHeapUsed() {
        return bloomFilterOffHeapUsed;
    }

    public CassandraTable setBloomFilterOffHeapUsed(Long bloomFilterOffHeapUsed) {
        this.bloomFilterOffHeapUsed = bloomFilterOffHeapUsed;
        return this;
    }

    public Long getIndexSummaryOffHeapUsed() {
        return indexSummaryOffHeapUsed;
    }

    public CassandraTable setIndexSummaryOffHeapUsed(Long indexSummaryOffHeapUsed) {
        this.indexSummaryOffHeapUsed = indexSummaryOffHeapUsed;
        return this;
    }

    public Long getCompressionMetadataOffHeapUsed() {
        return compressionMetadataOffHeapUsed;
    }

    public CassandraTable setCompressionMetadataOffHeapUsed(Long compressionMetadataOffHeapUsed) {
        this.compressionMetadataOffHeapUsed = compressionMetadataOffHeapUsed;
        return this;
    }

    public Long getPartitionMinBytes() {
        return partitionMinBytes;
    }

    public CassandraTable setPartitionMinBytes(Long partitionMinBytes) {
        this.partitionMinBytes = partitionMinBytes;
        return this;
    }

    public Long getPartitionMaxBytes() {
        return partitionMaxBytes;
    }

    public CassandraTable setPartitionMaxBytes(Long partitionMaxBytes) {
        this.partitionMaxBytes = partitionMaxBytes;
        return this;
    }

    public Long getPartitionMeanBytes() {
        return partitionMeanBytes;
    }

    public CassandraTable setPartitionMeanBytes(Long partitionMeanBytes) {
        this.partitionMeanBytes = partitionMeanBytes;
        return this;
    }

    public Float getAverageLiveCellPerSlice() {
        return averageLiveCellPerSlice;
    }

    public CassandraTable setAverageLiveCellPerSlice(Float averageLiveCellPerSlice) {
        this.averageLiveCellPerSlice = averageLiveCellPerSlice;
        return this;
    }

    public Long getMaximumLiveCellPerSlice() {
        return maximumLiveCellPerSlice;
    }

    public CassandraTable setMaximumLiveCellPerSlice(Long maximumLiveCellPerSlice) {
        this.maximumLiveCellPerSlice = maximumLiveCellPerSlice;
        return this;
    }

    public Float getAverageTombstonePerSlice() {
        return averageTombstonePerSlice;
    }

    public CassandraTable setAverageTombstonePerSlice(Float averageTombstonePerSlice) {
        this.averageTombstonePerSlice = averageTombstonePerSlice;
        return this;
    }

    public Long getMaximumTombstonePerSlice() {
        return maximumTombstonePerSlice;
    }

    public CassandraTable setMaximumTombstonePerSlice(Long maximumTombstonePerSlice) {
        this.maximumTombstonePerSlice = maximumTombstonePerSlice;
        return this;
    }

    public String getName() {
        return name;
    }

    public CassandraTable setName(String name) {
        this.name = name;
        return this;
    }

    public Long getDroppedMutation() {
        return droppedMutation;
    }

    public CassandraTable setDroppedMutation(Long droppedMutation) {
        this.droppedMutation = droppedMutation;
        return this;
    }

}
