module co.vendorflow.oss.maildrop.test.autoconfigure {
    /*exports*/ opens co.vendorflow.oss.maildrop.autoconfigure;

    requires co.vendorflow.oss.maildrop.test;
    requires co.vendorflow.oss.maildrop.sink.sendgrid;

    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
}
