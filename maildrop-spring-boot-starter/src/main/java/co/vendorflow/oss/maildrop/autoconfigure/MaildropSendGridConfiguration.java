package co.vendorflow.oss.maildrop.autoconfigure;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.sendgrid.SendGridAPI;

import co.vendorflow.oss.maildrop.api.sink.MailSink;
import co.vendorflow.oss.maildrop.sink.sendgrid.NameMapSendGridTemplateResolver;
import co.vendorflow.oss.maildrop.sink.sendgrid.SendGridDynamicTemplateMailSink;
import co.vendorflow.oss.maildrop.sink.sendgrid.SendGridTemplateResolver;

class MaildropSendGridConfiguration {
    @Configuration(proxyBeanMethods = false)
    static class TemplateResolverConfiguration {
        @Bean
        @ConditionalOnProperty(prefix = MaildropSendGridDynamicTemplateProperties.PREFIX, name = "mappings")
        public SendGridTemplateResolver sendGridTemplateResolver(MaildropSendGridDynamicTemplateProperties props) throws IOException {
            var mappings = PropertiesLoaderUtils.loadProperties(props.getMappings()).entrySet().stream()
                    .collect(toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
            return new NameMapSendGridTemplateResolver(mappings);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class MailSinkConfiguration {
        @Bean
        @ConditionalOnProperty(name = "maildrop.sink.sendgrid.dynamic-template.enabled", matchIfMissing = true)
        @ConditionalOnBean({
                SendGridAPI.class,
                SendGridTemplateResolver.class,
        })
        @ConditionalOnMissingBean
        public MailSink sendGridDynamicTemplateMailSink(SendGridAPI sg, SendGridTemplateResolver resolver) {
            return new SendGridDynamicTemplateMailSink(sg, resolver);
        }
    }
}