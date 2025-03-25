package apiandui;

interface EnvConfig extends org.aeonbits.owner.Config {
    @Key("base.url")
    @DefaultValue("https://the-internet.herokuapp.com")
    String baseUrl();
}