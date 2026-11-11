package team.foodlog.backend.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Nutrition target mode —
        determines whether targets are auto-calculated or manually set.""")
public enum TargetMode {

    @Schema(description = "Auto-calculated targets based on the user's nutrition profile")
    RECOMMENDED,

    @Schema(description = "Manually set custom targets defined by the user")
    CUSTOM

}
