package run.halo.lightgallery;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

@Component
public class LightGalleryHeadProcessor implements TemplateHeadProcessor {

    public LightGalleryHeadProcessor() {
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
                              IElementModelStructureHandler structureHandler) {
        return Mono.just(context.getTemplateData().getTemplate())
                .filter(this::isContentTemplate)
                .map(s -> {
                    IModelFactory modelFactory = context.getModelFactory();
                    model.add(modelFactory.createText(lightGalleryScript()));
                    return Mono.empty();
                })
                .then();
    }

    private String lightGalleryScript() {
        return """
                <!-- PluginLightGallery start -->
                <link href="/assets/PluginLightGallery/static/css/lightgallery.min.css" rel="stylesheet" />
                <script src="/assets/PluginLightGallery/static/js/lightgallery.min.js"></script>
                <script type="text/javascript">
                   const imageNodes = document.querySelectorAll("#content img");
                   imageNodes.forEach(function (node) {
                     if (node) {
                       node.dataset.src = node.src;
                     }
                   });
                               
                   lightGallery(document.getElementById("content"), {
                     selector: "img",
                   });
                </script>
                <!-- PluginLightGallery end -->
                """;
    }

    private boolean isContentTemplate(String template) {
        return "post".equals(template) || "page".equals(template);
    }
}
