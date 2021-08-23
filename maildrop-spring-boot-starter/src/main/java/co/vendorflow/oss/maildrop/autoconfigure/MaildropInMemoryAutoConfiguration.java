package co.vendorflow.oss.maildrop.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.vendorflow.oss.maildrop.api.sink.MailSink;
import co.vendorflow.oss.maildrop.test.InMemoryMailSink;

@Configuration
@ConditionalOnClass(InMemoryMailSink.class)
public class MaildropInMemoryAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "maildrop.sink.in-memory.enabled", matchIfMissing = true)
    @ConditionalOnMissingBean
    public MailSink inMemoryMailSink() {
        return new InMemoryMailSink();
    }
}
