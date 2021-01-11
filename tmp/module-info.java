module ws.slink.statuspage {

    exports ws.slink.statuspage;
    exports ws.slink.statuspage.model;

    exports ws.slink.statuspage.type.converter to com.fasterxml.jackson.databind;
    opens   ws.slink.statuspage.model          to com.fasterxml.jackson.databind;

    requires transitive java.net.http;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires static     lombok;

}