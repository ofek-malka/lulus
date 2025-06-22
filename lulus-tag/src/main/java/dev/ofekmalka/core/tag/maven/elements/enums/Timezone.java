package dev.ofekmalka.core.tag.maven.elements.enums;

import java.util.TimeZone;

import dev.ofekmalka.core.data_structure.list.List;

public enum Timezone {
	NO_COUNTRY("No Country", "No Timezone", 0), //
	UTC("UTC", "Coordinated Universal Time", 0), //
	AMERICA_MONTREAL("America/Montreal", "Eastern Time (UTC-05:00)", -5), //
	EUROPE_PARIS("Europe/Paris", "Central European Time (UTC+01:00)", 1);

	private final String zoneId;
	private final String description;
	private final int offset;

	Timezone(final String zoneId, final String description, final int offset) {
		this.zoneId = zoneId;
		this.description = description;
		this.offset = offset;
	}

	public String getZoneId() {
		return zoneId;
	}

	public String getDescription() {
		return description;
	}

	public int getOffset() {
		return offset;
	}

	// You can add methods or additional information as needed

	public static Timezone getDefault() {
		return NO_COUNTRY;
	}

	public static Timezone getByZoneId(final String zoneId) {

		return List.list(values())//
				.dropWhile(timezone -> !timezone.getZoneId().equals(zoneId))//
				.takeAtMost(1)//
				.firstElementOption()//
				.getOrElse(getDefault());

	}

	public static Timezone getByOffset(final int offset) {

		return List.list(values())//
				.dropWhile(timezone -> timezone.getOffset() != offset)//
				.takeAtMost(1)//
				.firstElementOption()//
				.getOrElse(getDefault());

	}

	public static Timezone getByTimeZone(final TimeZone timeZone) {
		return getByZoneId(timeZone.getID());
	}

}
