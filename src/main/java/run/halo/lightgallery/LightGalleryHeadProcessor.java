package run.halo.lightgallery;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
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
                        model.add(modelFactory.createText(lightGalleryScript(Set.of(domSelector))));
                    }

                    MatchResult matchResult = isRequestPathMatchingRoute(context, basicConfig);
                    if (!matchResult.matched()) {
                        return;
                    }
                    model.add(modelFactory.createText(lightGalleryScript(matchResult.domSelectors())));
                })
                .onErrorResume(e -> {
                    log.error("LightGalleryHeadProcessor process failed", e);
                    return Mono.empty();
                })
                .then();
    }

    static String lightGalleryScript(Set<String> domSelectors) {
        return """
                <!-- PluginLightGallery start -->
                <link href="/plugins/PluginLightGallery/assets/static/css/lightgallery.min.css" rel="stylesheet" />
                <script defer src="/plugins/PluginLightGallery/assets/static/js/lightgallery.min.js"></script>
                <script type="text/javascript">
                    document.addEventListener("DOMContentLoaded", function () {
                       %s
                    });
                </script>
                <!-- PluginLightGallery end -->
                """.formatted(instantiateGallery(domSelectors));
    }

    static String instantiateGallery(Set<String> domSelectors) {
        return domSelectors.stream()
                .map(domSelector -> """
                        document.querySelectorAll(`%s img`)?.forEach(function (node) {
                          if (node) {
                            node.dataset.src = node.src;
                          }
                        });
                        lightGallery(document.querySelector("%s"), {
                          selector: "img",
                        });
                        """.formatted(domSelector, domSelector)
                )
                .collect(Collectors.joining("\n"));
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

        Set<String> selectors = basicConfig.nullSafeRules()
                .stream()
                .filter(rule -> isMatchedRoute(requestRoute, rule))
                .map(rule -> defaultIfBlank(rule.getDomSelector(), "body"))
                .collect(Collectors.toSet());
        return selectors.size() > 0
                ? new MatchResult(true, selectors)
                : MatchResult.mismatch();
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

    record MatchResult(boolean matched, Set<String> domSelectors) {
        public static MatchResult mismatch() {
            return new MatchResult(false, Set.of());
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
