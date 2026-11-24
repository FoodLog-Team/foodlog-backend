CREATE TABLE meal_entries(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plate_size VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
    analysis_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    ai_confidence VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN',
    processing_error VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meal_entries_user_id
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_plate_size
        CHECK (plate_size IN ('SMALL', 'MEDIUM', 'LARGE')),

    CONSTRAINT chk_analysis_status
        CHECK (analysis_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),

    CONSTRAINT chk_ai_confidence
        CHECK (ai_confidence IN ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN')),

    CONSTRAINT chk_analysis_status_ai_confidence
        CHECK (
            analysis_status != 'PENDING'
                OR ai_confidence = 'UNKNOWN'
            ),

    CONSTRAINT chk_processing_error
        CHECK (processing_error IS NULL
            OR processing_error IN (
                                    'RECOGNITION_FAILED',
                                    'NO_FOOD_DETECTED',
                                    'FOOD_NOT_RECOGNIZED',
                                    'NUTRITION_FETCHING_FAILED')
            ),

    CONSTRAINT chk_analysis_status_processing_error
        CHECK (
            (analysis_status = 'FAILED' AND processing_error IS NOT NULL)
                OR
            (analysis_status != 'FAILED' AND processing_error IS NULL)
        )

);

CREATE INDEX idx_meal_entries_user_id_created_at
    ON meal_entries (user_id, created_at DESC);
