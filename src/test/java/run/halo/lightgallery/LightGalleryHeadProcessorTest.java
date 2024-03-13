package run.halo.lightgallery;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LightGalleryHeadProcessorTest {

    @Test
    void lightGalleryScript() {
        Set<String> set = new LinkedHashSet<>();
        set.add(".content");
        set.add(".moment");
        String result = LightGalleryHeadProcessor.lightGalleryScript(set);
        assertThat(result).isEqualToIgnoringWhitespace("""
                <!-- PluginLightGallery start -->
                <link
                  href="/plugins/PluginLightGallery/assets/static/css/lightgallery.min.css"
                  rel="stylesheet"
                />
                <script
                  defer
                  src="/plugins/PluginLightGallery/assets/static/js/lightgallery.min.js"
                ></script>
                <!-- PluginLightGallery zoom plugin -->
                <script
                  defer
                  src="/plugins/PluginLightGallery/assets/static/js/plugins/zoom/lg-zoom.min.js"
                ></script>
                <script type="text/javascript">
                  document.addEventListener("DOMContentLoaded", function () {
                    document.querySelectorAll(`.content img`)?.forEach(function (node) {
                      if (node) {
                        node.dataset.src = node.src;
                      }
                          
                      const galleries = document.querySelectorAll(`.content`);
                          
                      if (galleries.length > 0) {
                        galleries.forEach(function (node) {
                          lightGallery(node, {
                            selector: "img",
                          });
                        });
                      }
                    });
                          
                    document.querySelectorAll(`.moment img`)?.forEach(function (node) {
                      if (node) {
                        node.dataset.src = node.src;
                      }
                          
                      const galleries = document.querySelectorAll(`.moment`);
                          
                      if (galleries.length > 0) {
                        galleries.forEach(function (node) {
                          lightGallery(node, {
                            selector: "img",
                          });
                        });
                      }
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
        assertThat(result).isEqualToIgnoringWhitespace("""
                document.querySelectorAll(`.content img`)?.forEach(function (node) {
                   if (node) {
                     node.dataset.src = node.src;
                   }

                   const galleries = document.querySelectorAll(`.content`);

                   if (galleries.length > 0) {
                     galleries.forEach(function (node) {
                       lightGallery(node, {
                         selector: "img",
                       });
                     });
                   }
                 });

                 document.querySelectorAll(`.moment img`)?.forEach(function (node) {
                   if (node) {
                     node.dataset.src = node.src;
                   }

                   const galleries = document.querySelectorAll(`.moment`);

                   if (galleries.length > 0) {
                     galleries.forEach(function (node) {
                       lightGallery(node, {
                         selector: "img",
                       });
                     });
                   }
                });
                """);
    }
}
