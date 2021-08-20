package co.vendorflow.oss.maildrop.sink.sendgrid;

import co.vendorflow.oss.maildrop.api.sink.MaildropDeliveryException;
import lombok.Getter;

public class TemplateNotResolvedException extends MaildropDeliveryException {
    @Getter
    private final String templateName;

    public TemplateNotResolvedException(String templateName) {
        super("could not resolve template '" + templateName + "'");
        this.templateName = templateName;
    }
}
