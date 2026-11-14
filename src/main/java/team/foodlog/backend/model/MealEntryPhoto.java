package team.foodlog.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import team.foodlog.backend.model.enums.MealEntryPhotoSlot;
import team.foodlog.backend.model.enums.PhotoFormat;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meal_entry_photos")
public class MealEntryPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_entry_id", nullable = false)
    private MealEntry mealEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false, length = 32)
    private MealEntryPhotoSlot slot;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 32)
    private PhotoFormat format;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public MealEntryPhoto(
            MealEntry mealEntry,
            MealEntryPhotoSlot slot,
            String storageKey,
            PhotoFormat format,
            long sizeBytes
    ) {
        this.mealEntry = mealEntry;
        this.slot = slot;
        this.storageKey = storageKey;
        this.format = format;
        this.sizeBytes = sizeBytes;
    }

}
