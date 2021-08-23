open module co.vendorflow.oss.maildrop.copy.sendgrid {
    exports com.sendgrid;
    exports com.sendgrid.helpers.eventwebhook;
    exports com.sendgrid.helpers.ips;
    exports com.sendgrid.helpers.mail;
    exports com.sendgrid.helpers.mail.objects;

    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.commons.codec;
    requires transitive com.fasterxml.jackson.databind;
}
