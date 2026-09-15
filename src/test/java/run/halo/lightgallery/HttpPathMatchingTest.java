package run.halo.lightgallery;

import static org.assertj.core.api.Assertions.assertThat;

class HttpPathMatchingTest {

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "/archives/**, /archives/demo, true",
        "/archives/**, /archives/demo.html, true",
        "/archives/**, /archives/a/b.html, true",
        "/archives/*.html, /archives/demo.html, true",
        "/archives/*.html, /archives/a/b.html, false",
        "/archives/**, /moments/demo.html, false",
        "/archives/**, /archives/a%20b.html, true"
    })
    void matchesHttpPaths(String pattern, String path, boolean expected) {
        var matcher = LightGalleryHeadProcessor.createRouteMatcher();
        assertThat(matcher.match(pattern, matcher.parseRoute(path))).isEqualTo(expected);
    }


}
