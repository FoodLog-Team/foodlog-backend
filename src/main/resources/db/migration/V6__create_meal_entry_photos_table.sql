CREATE TABLE meal_entry_photos(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    meal_entry_id BIGINT NOT NULL,
    slot VARCHAR(32) NOT NULL,
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    format VARCHAR(32) NOT NULL,
    size_bytes BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meal_entry_photos_meal_entry_id
        FOREIGN KEY (meal_entry_id)
        REFERENCES meal_entries (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_slot
        CHECK (slot IN ('PRIMARY', 'SECONDARY')),

    CONSTRAINT uq_meal_entry_photos_meal_entry_id_slot
        UNIQUE (meal_entry_id, slot),

    CONSTRAINT chk_content_type
        CHECK (format IN ('JPEG', 'PNG', 'WEBP')),

    CONSTRAINT chk_size_bytes_limit
        CHECK (size_bytes BETWEEN 1 AND 10 * 1024 * 1024)
);
