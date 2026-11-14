CREATE TABLE meal_entries(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    analysis_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    ai_confidence VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meal_entries_user_id
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_analysis_status
        CHECK (analysis_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),

    CONSTRAINT chk_ai_confidence
        CHECK (ai_confidence IN ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN')),

    CONSTRAINT chk_analysis_status_ai_confidence
        CHECK (
            (analysis_status IN ('PENDING', 'PROCESSING', 'FAILED')
                AND ai_confidence = 'UNKNOWN')
                OR
            (analysis_status = 'COMPLETED'
                AND ai_confidence IN ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN'))
        )
);

CREATE INDEX idx_meal_entries_user_id_created_at
    ON meal_entries (user_id, created_at DESC);
