module co.vendorflow.oss.maildrop.api {
    exports co.vendorflow.oss.maildrop.api;
    exports co.vendorflow.oss.maildrop.api.filter;
    exports co.vendorflow.oss.maildrop.api.sink;
    exports co.vendorflow.oss.maildrop.impl;
    opens co.vendorflow.oss.maildrop.impl;
    exports co.vendorflow.oss.maildrop.jackson;

    requires transitive jakarta.mail;

    ////
    requires transitive com.fasterxml.jackson.databind;
    ////


    requires io.vavr;

    requires static java.desktop;
    requires static java.validation;
    requires static lombok;
}
