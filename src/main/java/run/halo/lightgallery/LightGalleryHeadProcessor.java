package run.halo.lightgallery;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.List;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.RouteMatcher;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import org.springframework.web.util.pattern.PatternParseException;
import org.thymeleaf.context.Contexts;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.web.IWebRequest;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

/**
 * @author ryanwang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LightGalleryHeadProcessor implements TemplateHeadProcessor {
    private static final String TEMPLATE_ID_VARIABLE = "_templateId";
    private final ReactiveSettingFetcher reactiveSettingFetcher;
    private final PathPatternRouteMatcher routeMatcher = new PathPatternRouteMatcher();

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
                              IElementModelStructureHandler structureHandler) {
        return reactiveSettingFetcher.fetch("basic", BasicConfig.class)
                .doOnNext(basicConfig -> {
                    final IModelFactory modelFactory = context.getModelFactory();
                    String domSelector = basicConfig.getDom_selector();
                    if (StringUtils.isNotBlank(domSelector) && isContentTemplate(context)) {
                        model.add(modelFactory.createText(lightGalleryScript(domSelector)));
                    }

                    MatchResult matchResult = isRequestPathMatchingRoute(context, basicConfig);
                    if (!matchResult.matched()) {
                        return;
                    }

                    model.add(modelFactory.createText(lightGalleryScript(matchResult.domSelector())));
                })
                .onErrorContinue((throwable, o) -> log.warn("LightGalleryHeadProcessor process failed", throwable))
                .then();
    }

    private String lightGalleryScript(String domSelector) {
        return """
                <!-- PluginLightGallery start -->
                <link href="/plugins/PluginLightGallery/assets/static/css/lightgallery.min.css" rel="stylesheet" />
                <script defer src="/plugins/PluginLightGallery/assets/static/js/lightgallery.min.js"></script>
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

    public boolean isContentTemplate(ITemplateContext context) {
        return "post".equals(context.getVariable(TEMPLATE_ID_VARIABLE))
                || "page".equals(context.getVariable(TEMPLATE_ID_VARIABLE));
    }

    public MatchResult isRequestPathMatchingRoute(ITemplateContext context, BasicConfig basicConfig) {
        if (!Contexts.isWebContext(context)) {
            return MatchResult.mismatch();
        }
        IWebRequest request = Contexts.asWebContext(context).getExchange().getRequest();
        String requestPath = request.getRequestPath();
        RouteMatcher.Route requestRoute = routeMatcher.parseRoute(requestPath);

        return basicConfig.nullSafeRules()
                .stream()
                .filter(rule -> isMatchedRoute(requestRoute, rule))
                .findFirst()
                .map(rule -> new MatchResult(true, defaultIfBlank(rule.getDomSelector(), "body")))
                .orElse(MatchResult.mismatch());
    }

    private boolean isMatchedRoute(RouteMatcher.Route requestRoute, PathMatchRule rule) {
        try {
            return routeMatcher.match(rule.getPathPattern(), requestRoute);
        } catch (PatternParseException e) {
            // ignore
            log.warn("Parse route pattern [{}] failed", rule.getPathPattern(), e);
        }
        return false;
    }

    record MatchResult(boolean matched, String domSelector) {
        public static MatchResult mismatch() {
            return new MatchResult(false, null);
        }
    }

    @Data
    public static class BasicConfig {
        String dom_selector;
        List<PathMatchRule> rules;

        public List<PathMatchRule> nullSafeRules() {
            return ObjectUtils.defaultIfNull(rules, List.of());
        }
    }

    @Data
    public static class PathMatchRule {
        private String pathPattern;
        private String domSelector;
    }
}
