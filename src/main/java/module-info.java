module com.gregorgott.ekspgpmessengerclient {
    requires javafx.controls;
    //requires pgpainless.sop;
    requires pgpainless.core;
    requires com.google.gson;
    requires com.google.common;
    requires java.rmi;
    requires org.bouncycastle.pg;
    requires org.bouncycastle.provider;
    // development imports
    requires cssfx;
    requires java.logging;

    exports com.gregorgott.occultus;
    exports com.gregorgott.occultus.Encryption;
    exports com.gregorgott.occultus.ChatData;
    exports com.gregorgott.occultus.JsonHandler;
    exports com.gregorgott.occultus.FileHandler;
    exports com.gregorgott.occultus.ServerHandler;
}