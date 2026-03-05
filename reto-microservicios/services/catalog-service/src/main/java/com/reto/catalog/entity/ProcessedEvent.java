package com.reto.catalog.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false, unique = true, length = 255)
    private String eventId;
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
    @PrePersist
    protected void onCreate() {
        processedAt = Instant.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}