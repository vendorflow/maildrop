package co.vendorflow.oss.maildrop.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGridAPI;

import co.vendorflow.oss.maildrop.api.sink.MailSink;
import co.vendorflow.oss.maildrop.sink.sendgrid.SendGridDynamicTemplateMailSink;
import co.vendorflow.oss.maildrop.sink.sendgrid.SendGridTemplateResolver;

@Configuration
@ConditionalOnClass(SendGridAPI.class)
@AutoConfigureAfter(SendGridAutoConfiguration.class)
@AutoConfigureBefore(MaildropInMemoryAutoConfiguration.class)
public class MaildropSendGridAutoConfiguration {

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
