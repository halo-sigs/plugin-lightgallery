package run.halo.lightgallery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IText;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

class LightGalleryHeadProcessorTest {
    @Test
    void injectsResourcesOnceWhenLegacyAndPageRulesBothMatch() {
        var settings = mock(ReactiveSettingFetcher.class);
        var config = new LightGalleryHeadProcessor.BasicConfig();
        config.setDom_selector(".content");
        when(settings.fetch("basic", LightGalleryHeadProcessor.BasicConfig.class))
            .thenReturn(Mono.just(config));
        var processor = spy(new LightGalleryHeadProcessor(settings));
        var context = mock(ITemplateContext.class);
        var factory = mock(IModelFactory.class);
        var model = mock(IModel.class);
        when(context.getModelFactory()).thenReturn(factory);
        when(context.getVariable("_templateId")).thenReturn("post");
        when(factory.createText(anyString())).thenReturn(mock(IText.class));
        doReturn(new LightGalleryHeadProcessor.MatchResult(true, Set.of(".content", ".other")))
            .when(processor).isRequestPathMatchingRoute(context, config);
        processor.process(context, model, null).block();
        verify(factory, times(1)).createText(anyString());
        verify(model, times(1)).add(any(IText.class));
    }
}
