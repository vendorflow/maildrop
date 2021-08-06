module co.vendorflow.oss.maildrop.test.autoconfigure {
    /*exports*/ opens co.vendorflow.oss.maildrop.autoconfigure;

    requires transitive co.vendorflow.oss.maildrop.test;

    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
}
