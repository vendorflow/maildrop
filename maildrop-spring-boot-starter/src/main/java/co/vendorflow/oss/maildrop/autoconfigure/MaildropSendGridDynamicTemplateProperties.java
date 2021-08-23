package co.vendorflow.oss.maildrop.autoconfigure;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties(prefix = MaildropSendGridDynamicTemplateProperties.PREFIX)
@Getter
@Setter
@ToString
@Validated
class MaildropSendGridDynamicTemplateProperties {
    static final String PREFIX = "maildrop.sink.sendgrid.dynamic-template";

    @NotNull
    Resource mappings;
}
