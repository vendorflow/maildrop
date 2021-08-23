module co.vendorflow.oss.maildrop.sink.sendgrid {
    exports co.vendorflow.oss.maildrop.sink.sendgrid;

    requires transitive co.vendorflow.oss.maildrop.api;

    requires transitive co.vendorflow.oss.maildrop.copy.sendgrid;
    requires org.slf4j;

    requires static lombok;
    requires static java.desktop;
}
