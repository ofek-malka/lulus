package dev.ofekmalka.core.tag.maven.elements.enums;

public enum InceptionYear {
	YEAR_2020(2020), //
	YEAR_2021(2021), //
	YEAR_2022(2022), //
	YEAR_2023(2023), //
	YEAR_2024(2024); //

	private final int year;

	InceptionYear(final int year) {
		this.year = year;
	}

	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return String.valueOf(year);
	}

//    public static InceptionYear fromYear(int year) {
//        for (InceptionYear inceptionYear : values()) {
//            if (inceptionYear.getYear() == year) {
//                return inceptionYear;
//            }
//        }
//        throw new IllegalArgumentException("Invalid year");
//    }
}
