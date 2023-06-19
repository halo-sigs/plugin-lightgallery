package run.halo.lightgallery;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LightGalleryHeadProcessorTest {

    @Test
    void lightGalleryScript() {
        Set<String> set = new LinkedHashSet<>();
        set.add(".content");
        set.add(".moment");
        String result = LightGalleryHeadProcessor.lightGalleryScript(set);
        assertThat(result).isEqualTo("""
                <!-- PluginLightGallery start -->
                <link href="/plugins/PluginLightGallery/assets/static/css/lightgallery.min.css" rel="stylesheet" />
                <script defer src="/plugins/PluginLightGallery/assets/static/js/lightgallery.min.js"></script>
                <script type="text/javascript">
                    document.addEventListener("DOMContentLoaded", function () {
                       document.querySelectorAll(`.content img`)?.forEach(function (node) {
                  if (node) {
                    node.dataset.src = node.src;
                  }
                });
                lightGallery(document.querySelector(".content"), {
                  selector: "img",
                });
                                
                document.querySelectorAll(`.moment img`)?.forEach(function (node) {
                  if (node) {
                    node.dataset.src = node.src;
                  }
                });
                lightGallery(document.querySelector(".moment"), {
                  selector: "img",
                });
                                
                    });
                </script>
                <!-- PluginLightGallery end -->
                """);
    }

    @Test
    void instantiateGallery() {
        Set<String> set = new LinkedHashSet<>();
        set.add(".content");
        set.add(".moment");
        String result = LightGalleryHeadProcessor.instantiateGallery(set);
        assertThat(result).isEqualTo("""
                document.querySelectorAll(`.content img`)?.forEach(function (node) {
                  if (node) {
                    node.dataset.src = node.src;
                  }
                });
                lightGallery(document.querySelector(".content"), {
                  selector: "img",
                });

                document.querySelectorAll(`.moment img`)?.forEach(function (node) {
                  if (node) {
                    node.dataset.src = node.src;
                  }
                });
                lightGallery(document.querySelector(".moment"), {
                  selector: "img",
                });
                """);
    }
}
