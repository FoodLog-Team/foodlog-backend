CREATE TABLE nutrition_targets(
    user_id BIGINT NOT NULL,
    kcal INT NOT NULL,
    carbs INT NOT NULL,
    protein INT NOT NULL,
    fats INT NOT NULL,
    target_mode VARCHAR(32) NOT NULL DEFAULT 'RECOMMENDED',
    formula_type VARCHAR(32) NOT NULL DEFAULT 'MIFFLIN_ST_JEOR',
    calculation_type VARCHAR(32) NOT NULL DEFAULT 'BASIC',
    version BIGINT NOT NULL,

    CONSTRAINT pk_nutrition_targets
        PRIMARY KEY (user_id),

    CONSTRAINT fk_nutrition_targets_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_nutrition_target_mode
        CHECK (target_mode IN ('RECOMMENDED', 'CUSTOM')),

    CONSTRAINT chk_formula
        CHECK (formula_type IN ('MIFFLIN_ST_JEOR')),

    CONSTRAINT chk_calculation_type
        CHECK (calculation_type IN ('BASIC')),

    CONSTRAINT chk_recommended_calculation_config
        CHECK (
            target_mode != 'RECOMMENDED'
                OR (
                formula_type IS NOT NULL
                    AND calculation_type IS NOT NULL
                )
            ),

    CONSTRAINT chk_nutrition_targets_kcal
        CHECK (
            kcal IS NULL
                OR kcal BETWEEN 800 AND 8000
            ),

    CONSTRAINT chk_nutrition_targets_carbs
        CHECK (
            carbs IS NULL
                OR carbs BETWEEN 1 AND 1000
            ),

    CONSTRAINT chk_nutrition_targets_protein
        CHECK (
            protein IS NULL
                OR protein BETWEEN 1 AND 500
            ),

    CONSTRAINT chk_nutrition_targets_fats
        CHECK (
            fats IS NULL
                OR fats BETWEEN 1 AND 500
            ),

    CONSTRAINT chk_nutrition_targets_macros_consistency
        CHECK (
            kcal IS NULL
                OR
            20 * ABS(
                    kcal::BIGINT
                        - (
                        carbs::BIGINT * 4
                            + protein::BIGINT * 4
                            + fats::BIGINT * 9
                        )
                 ) <= kcal::BIGINT
            )
);
