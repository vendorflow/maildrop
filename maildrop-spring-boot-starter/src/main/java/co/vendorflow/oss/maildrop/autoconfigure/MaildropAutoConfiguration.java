package co.vendorflow.oss.maildrop.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.vendorflow.oss.maildrop.api.Maildrop;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilter;
import co.vendorflow.oss.maildrop.api.sink.MailSink;
import co.vendorflow.oss.maildrop.impl.FilteringMaildrop;
import co.vendorflow.oss.maildrop.impl.MaildropFilterChain;

@Configuration
@AutoConfigureAfter({
    MaildropInMemoryAutoConfiguration.class,
})
public class MaildropAutoConfiguration {

    @Configuration
    @AutoConfigureBefore(MaildropAutoConfiguration.class)
    public static class MaildropFilterAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public MaildropFilterChain defaultMaildropFilterChain(List<MaildropFilter> filters) {
            return new MaildropFilterChain(filters);
        }
    }



    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({
        MailSink.class,
        MaildropFilterChain.class,
    })
    public Maildrop maildrop(MaildropFilterChain filterChain, MailSink sink) {
        return FilteringMaildrop.builder()
                .filterChain(filterChain)
                .sink(sink)
                .build();
    }
}
