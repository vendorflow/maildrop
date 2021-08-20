package co.vendorflow.oss.maildrop.sink.sendgrid;

import java.util.Map;

import co.vendorflow.oss.maildrop.api.MailMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameMapSendGridTemplateResolver implements SendGridTemplateResolver {

    private final Map<String, String> nameToId;

    public NameMapSendGridTemplateResolver(Map<String, String> mappings) {
        this.nameToId = Map.copyOf(mappings);
        log.info("registered {} template mappings", nameToId.size());
    }

    @Override
    public String resolveTemplateId(MailMessage message) {
        var id = nameToId.get(message.getTemplateName());
        log.debug("resolved {} -> {}", message.getTemplateName(), id);
        return id;
    }
}
