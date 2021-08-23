package co.vendorflow.oss.maildrop.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sendgrid.SendGridAPI;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SendGridAPI.class)
@AutoConfigureAfter(SendGridAutoConfiguration.class)
@AutoConfigureBefore(MaildropInMemoryAutoConfiguration.class)
@EnableConfigurationProperties(MaildropSendGridDynamicTemplateProperties.class)
// Sequence these imports manually due to Spring Boot's undocumented rules.
@Import({
    MaildropSendGridConfiguration.TemplateResolverConfiguration.class,
    MaildropSendGridConfiguration.MailSinkConfiguration.class,
})
public class MaildropSendGridAutoConfiguration {
}
