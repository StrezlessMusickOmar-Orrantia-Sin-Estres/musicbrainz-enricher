package org.felixrilling.musicbrainzenricher.history;

import org.felixrilling.musicbrainzenricher.DataType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "history_entry",
        uniqueConstraints = @UniqueConstraint(
                name = "history_entry_unique",
                columnNames = {"data_type", "mbid"}
        )
)
class HistoryEntry {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "data_type", nullable = false)
    @NotNull
    private DataType dataType;

    @Column(name = "mbid", nullable = false)
    @NotNull
    private String mbid;

    @Column(name = "last_checked", nullable = false)
    @NotNull
    private ZonedDateTime lastChecked;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @NotNull DataType getDataType() {
        return dataType;
    }

    public void setDataType(@NotNull DataType dataType) {
        this.dataType = dataType;
    }

    public @NotNull String getMbid() {
        return mbid;
    }

    public void setMbid(@NotNull String mbid) {
        this.mbid = mbid;
    }

    public @NotNull ZonedDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(@NotNull ZonedDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoryEntry that = (HistoryEntry) o;
        return id == that.id &&
                dataType == that.dataType &&
                mbid.equals(that.mbid) &&
                lastChecked.equals(that.lastChecked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataType, mbid, lastChecked);
    }

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "id=" + id +
                ", dataType=" + dataType +
                ", mbid='" + mbid + '\'' +
                ", lastChecked=" + lastChecked +
                '}';
    }
}
