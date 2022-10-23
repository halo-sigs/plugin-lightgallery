package run.halo.lightgallery;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

/**
 * @author ryanwang
 */
@Component
public class LightGalleryHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public LightGalleryHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
                              IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch("basic", BasicConfig.class)
                .map(basicConfig -> {
                    final IModelFactory modelFactory = context.getModelFactory();
                    model.add(modelFactory.createText(lightGalleryScript(basicConfig.getDom_selector())));
                    return Mono.empty();
                }).orElse(Mono.empty()).then();
    }

    private String lightGalleryScript(String domSelector) {
        return """
                <!-- PluginLightGallery start -->
                <link href="/plugins/PluginLightGallery/assets/static/css/lightgallery.min.css" rel="stylesheet" />
                <script src="/plugins/PluginLightGallery/assets/static/js/lightgallery.min.js"></script>
                <script type="text/javascript">
                    document.addEventListener("DOMContentLoaded", function () {
                      const imageNodes = document.querySelectorAll(`%s img`);
                      imageNodes.forEach(function (node) {
                        if (node) {
                          node.dataset.src = node.src;
                        }
                      });
                      lightGallery(document.querySelector("%s"), {
                        selector: "img",
                      });
                    });
                </script>
                <!-- PluginLightGallery end -->
                """.formatted(domSelector, domSelector);
    }

    @Data
    public static class BasicConfig {
        String dom_selector;
    }
}
