package team.foodlog.backend.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhotoFormat {

    JPEG("image/jpeg", "jpg"),

    PNG("image/png", "png"),

    WEBP("image/webp", "webp");

    private final String contentType;

    private final String extension;

}
