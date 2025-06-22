package dev.ofekmalka.core.tag.maven.elements.enums;

public enum EmailSuffix {
	GMAIL("@gmail.com"), //
	YAHOO("@yahoo.com"), //
	OUTLOOK("@outlook.com"), //
	HOTMAIL("@hotmail.com"), //
	AOL("@aol.com"), //
	ICLOUD("@icloud.com"), //
	PROTONMAIL("@protonmail.com"), //
	ZOHO("@zoho.com"), //
	YANDEX("@yandex.com"), //
	MAIL("@mail.com"), //
	GMX("@gmx.com"), //
	TUTA("@tuta.io"), //
	FASTMAIL("@fastmail.com"), //
	DISPOSTABLE("@dispostable.com"), //
	EXAMPLE("@example.com"), // // Placeholder for custom or example domain
	WALLA("@walla.com"), //
	YNET("@ynet.com"), //
	NETVISION("@netvision.net.il"), //
	YOUR_DOMAIN("@your_domain.com"); // Placeholder
										// for
										// additional
										// custom
										// domains

	private final String value;

	EmailSuffix(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
