package dev.ofekmalka.core.tag.maven.elements.enums;

public enum UrlScheme {
	    HTTP("http://"),//
	    HTTPS("https://"),//
	    FTP("ftp://"),//
	    FILE("file://"),//
	    MAILTO("mailto:"),//
	    DATA("data:"),//
	    JAVASCRIPT("javascript:"),//
	    tel("tel:"),//
	    NEWS("news:"),//
	    TELNET("telnet://"),//
	    LDAP("ldap://"),//
	    GOPHER("gopher://"),//
	    TFTP("tftp://"),//
	    SMB("smb://"),//
	    RTSP("rtsp://"),//
	    MMS("mms://"),//
	    WSS("wss://"),//
	    WS("ws://"),//
	    URN("urn:"),//
	    POP("pop:"),//
	    IMAP("imap:"),//
	    SIP("sip:"),//
	    SIPS("sips:"),//
	    NFS("nfs://");//

	    private final String value;

	    UrlScheme(String value) {
	        this.value = value;
	    }

	    public String getValue() {
	        return value;
	    }
	}


