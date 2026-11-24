CREATE TABLE meal_entry_items(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    meal_entry_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    grams NUMERIC(7, 2) NOT NULL,
    kcal_per_100g NUMERIC(5, 2),
    carbs_per_100g NUMERIC(5, 2),
    protein_per_100g NUMERIC(5, 2),
    fat_per_100g NUMERIC(5, 2),
    status VARCHAR(32) NOT NULL,

    CONSTRAINT fk_meal_entry_items_meal_entry_id
        FOREIGN KEY (meal_entry_id)
        REFERENCES meal_entries (id)
        ON DELETE CASCADE,

    CONSTRAINT kcal_per_100g_non_negative
        CHECK (kcal_per_100g >= 0),

    CONSTRAINT carbs_per_100g_non_negative
        CHECK (carbs_per_100g >= 0),

    CONSTRAINT protein_per_100g_non_negative
        CHECK (protein_per_100g >= 0),

    CONSTRAINT fat_per_100g_non_negative
        CHECK (fat_per_100g >= 0),

    CONSTRAINT chk_status
        CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))

);

CREATE INDEX idx_meal_entry_items_meal_entry_id
    ON meal_entry_items (meal_entry_id);
