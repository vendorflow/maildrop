package co.vendorflow.oss.maildrop.autoconfigure;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.Maildrop;
import co.vendorflow.oss.maildrop.api.Recipient;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilter;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilterException;
import co.vendorflow.oss.maildrop.impl.MaildropFilterChain;
import co.vendorflow.oss.maildrop.impl.MessageMutatingFilter;
import co.vendorflow.oss.maildrop.impl.MutableMailMessage;
import co.vendorflow.oss.maildrop.test.InMemoryMailSink;
import jakarta.mail.internet.InternetAddress;

public class MaildropAutoConfigurationTests {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MaildropAutoConfiguration.class));


    @Test
    public void nestedAutoConfigTriggers() {
        runner.run(ctx -> {
            assertThat(ctx).hasSingleBean(MaildropFilterChain.class);
            assertThat(ctx.getBean(MaildropFilterChain.class).getFilters()).isEmpty();
        });
    }


    @Test
    public void canCollectFilters() {
        runner
                .withBean(FilterMiddle.class)
                .withBean(FilterLast.class)
                .withBean(FilterFirst.class)
                .run(ctx -> {
                    assertThat(ctx).getBeans(MaildropFilter.class).hasSize(4);

                    assertThat(ctx).hasSingleBean(MaildropFilterChain.class);

                    assertThat(ctx).doesNotHaveBean(Maildrop.class);

                    assertThat(
                            ctx.getBean(MaildropFilterChain.class)
                                    .getFilters().stream()
                                    .map(Object::getClass)
                                    .collect(toList())
                    ).isEqualTo(List.of(FilterFirst.class, FilterMiddle.class, FilterLast.class));
                });
    }


    @Test
    public void canProvideCustomFilterChain() {
        runner.withUserConfiguration(TestFiltersConfiguration.class)
                .withBean(
                        MaildropFilterChain.class,
                        () -> new MaildropFilterChain(List.of(discarded -> List.of(new SentinelMailMessage())))
                )
                .run(ctx -> {
                    assertThat(ctx).getBeans(MaildropFilter.class).hasSize(4);

                    assertThat(ctx).hasSingleBean(MaildropFilterChain.class);

                    assertThat(ctx).doesNotHaveBean(Maildrop.class);

                    assertThat(
                            ctx.getBean(MaildropFilterChain.class)
                                    .filter(new MutableMailMessage())
                                    .get(0)
                    ).isInstanceOf(SentinelMailMessage.class);
                });
    }


    @Test
    public void fullMaildrop() {
        var originalSubs = Map.of(
                "foo", "bar",
                "baz", "quux"
        );

        var sent = new MutableMailMessage();
        sent.setTemplateName("mact");
        sent.setSubstitutions(originalSubs);

        var expectedSubs = Map.of(
                "foo", "replaced",
                "baz", "quux",
                "newValue", "inserted"
        );

        runner
                .withBean(FooOverridingMailFilter.class)
                .withBean(InMemoryMailSink.class)
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(Maildrop.class);

                    ctx.getBean(Maildrop.class).enqueue(sent);

                    var outQueue = ctx.getBean(InMemoryMailSink.class).drainQueue();
                    assertThat(outQueue).hasSize(1);
                    var delivered = outQueue.get(0);
                    assertThat(delivered.getTemplateName()).isEqualTo("mact");
                    assertThat(delivered.getSubstitutions()).isEqualTo(expectedSubs);
                });
    }


    @Configuration
    @Import({
        FilterMiddle.class,
        FilterFirst.class,
        FilterLast.class,
    })
    public static class TestFiltersConfiguration {
    }


    @Order(-100)
    public static class FilterFirst implements MaildropFilter {
        @Override
        public List<MailMessage> filter(MailMessage message) throws MaildropFilterException {
            return List.of(message);
        }
    }


    @Order(0)
    public static class FilterMiddle implements MaildropFilter {
        @Override
        public List<MailMessage> filter(MailMessage message) throws MaildropFilterException {
            return List.of(message);
        }
    }


    @Order(100)
    public static class FilterLast implements MaildropFilter {
        @Override
        public List<MailMessage> filter(MailMessage message) throws MaildropFilterException {
            return List.of(message);
        }
    }


    public static class SentinelMailMessage implements MailMessage {
        @Override
        public String getTemplateName() {
            return null;
        }

        @Override
        public InternetAddress getFrom() {
            return null;
        }

        @Override
        public InternetAddress getReplyTo() {
            return null;
        }

        @Override
        public Collection<Recipient> getRecipients() {
            return null;
        }
    }


    public static class FooOverridingMailFilter extends MessageMutatingFilter {
        @Override
        protected void mutate(MutableMailMessage mutable) {
            mutable.getSubstitutions().put("foo", "replaced");
            mutable.getSubstitutions().put("newValue", "inserted");
        }
    }
}
