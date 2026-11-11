package team.foodlog.backend.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User's biological sex, used in nutrition formula calculations (e.g., BMR).")
public enum Sex {

    @Schema(description = "Male")
    MALE,

    @Schema(description = "Female")
    FEMALE;

}
