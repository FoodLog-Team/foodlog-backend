CREATE TABLE user_profiles (
    user_id BIGINT PRIMARY KEY,
    date_of_birth DATE NOT NULL,
    height INT NOT NULL,
    weight NUMERIC(5, 2) NOT NULL,
    sex VARCHAR(32) NOT NULL,
    activity_level VARCHAR(32) NOT NULL,
    weight_goal VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_profile_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_profiles_date_of_birth
        CHECK (date_of_birth <= CURRENT_DATE),

    CONSTRAINT chk_user_profiles_minimum_age
        CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years'),

    CONSTRAINT chk_user_profiles_height_range
        CHECK (height >= 120 AND height <= 230),

    CONSTRAINT chk_user_profiles_weight_range
        CHECK (weight >= 30 AND weight <= 300),

    CONSTRAINT chk_user_profiles_sex
        CHECK (sex IN ('MALE', 'FEMALE')),

    CONSTRAINT chk_user_profiles_activity_level
        CHECK (activity_level IN (
            'SEDENTARY',
            'LIGHTLY_ACTIVE',
            'MODERATELY_ACTIVE',
            'VERY_ACTIVE',
            'EXTRA_ACTIVE'
        )),

    CONSTRAINT chk_user_profiles_weight_goal
        CHECK (weight_goal IN (
            'LOSE_WEIGHT',
            'MAINTAIN_WEIGHT',
            'GAIN_WEIGHT'
        ))
);
