package run.halo.lightgallery;

import static org.assertj.core.api.Assertions.assertThat;

class BackdropStyleTest {
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"#000000", "#80808099", "#ABCDEFff"})
    void acceptsHexBackdropColors(String color) {
        assertThat(LightGalleryHeadProcessor.backdropStyle(color)).contains("background-color: " + color);
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.NullAndEmptySource
    @org.junit.jupiter.params.provider.ValueSource(strings = {"red", "#12345", "</style><script>alert(1)</script>"})
    void fallsBackForMissingOrInvalidBackdropColors(String color) {
        assertThat(LightGalleryHeadProcessor.backdropStyle(color))
            .isEqualTo("<style>.lg-backdrop { background-color: #000000ff; }</style>");
    }


}
