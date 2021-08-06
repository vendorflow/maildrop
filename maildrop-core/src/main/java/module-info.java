module co.vendorflow.oss.maildrop.api {
    exports co.vendorflow.oss.maildrop.api;
    exports co.vendorflow.oss.maildrop.api.filter;
    exports co.vendorflow.oss.maildrop.impl;

    requires transitive jakarta.mail;

    requires io.vavr;

    requires static java.desktop;
    requires static java.validation;
    requires static lombok;
}
