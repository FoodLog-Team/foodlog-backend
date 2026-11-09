CREATE TABLE nutrition_targets
(
    user_id BIGINT NOT NULL,

    recommended_kcal     INT NOT NULL,
    recommended_carbs    INT NOT NULL,
    recommended_protein  INT NOT NULL,
    recommended_fats     INT NOT NULL,

    mode VARCHAR(32) NOT NULL DEFAULT 'RECOMMENDED',

    custom_kcal     INT,
    custom_carbs    INT,
    custom_protein  INT,
    custom_fats     INT,

    version BIGINT NOT NULL,

    CONSTRAINT pk_nutrition_targets
        PRIMARY KEY (user_id),

    CONSTRAINT fk_nutrition_targets_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_nutrition_targets_mode
        CHECK (mode IN ('RECOMMENDED', 'CUSTOM')),

    CONSTRAINT chk_nutrition_targets_custom_kcal
        CHECK (
            custom_kcal IS NULL
                OR custom_kcal BETWEEN 800 AND 8000
            ),

    CONSTRAINT chk_nutrition_targets_custom_carbs
        CHECK (
            custom_carbs IS NULL
                OR custom_carbs BETWEEN 1 AND 1000
            ),

    CONSTRAINT chk_nutrition_targets_custom_protein
        CHECK (
            custom_protein IS NULL
                OR custom_protein BETWEEN 1 AND 500
            ),

    CONSTRAINT chk_nutrition_targets_custom_fats
        CHECK (
            custom_fats IS NULL
                OR custom_fats BETWEEN 1 AND 500
            ),

    CONSTRAINT chk_nutrition_targets_custom_values_complete
        CHECK (
            (
                custom_kcal IS NULL
                AND custom_carbs IS NULL
                AND custom_protein IS NULL
                AND custom_fats IS NULL
            )
                OR
            (
                custom_kcal IS NOT NULL
                AND custom_carbs IS NOT NULL
                AND custom_protein IS NOT NULL
                AND custom_fats IS NOT NULL
            )
        ),

    CONSTRAINT chk_nutrition_targets_custom_mode_configured
        CHECK (
            mode <> 'CUSTOM'
                OR custom_kcal IS NOT NULL
            ),

    CONSTRAINT chk_nutrition_targets_custom_macros_consistency
        CHECK (
            custom_kcal IS NULL
                OR
            20 * ABS(
                    custom_kcal::BIGINT
                        - (
                        custom_carbs::BIGINT * 4
                            + custom_protein::BIGINT * 4
                            + custom_fats::BIGINT * 9
                        )
                 ) <= custom_kcal::BIGINT
            )
);
