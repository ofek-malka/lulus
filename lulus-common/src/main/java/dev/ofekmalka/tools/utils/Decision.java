package dev.ofekmalka.tools.utils;

public enum Decision {
	YES("Yes", 1, true) {
		@Override
		public Decision opposite() {
			return NO;
		}
	},
	NO("No", 0, false) {
		@Override
		public Decision opposite() {
			return YES;
		}
	};

	private final String option; // String representation
	private final int binaryBit; // Integer representation
	private final boolean boolValue; // Boolean representation

	Decision(final String option, final int binaryBit, final boolean boolValue) {
		this.option = option;
		this.binaryBit = binaryBit;
		this.boolValue = boolValue;
	}

	public boolean isYes() {
		return this == YES;
	}

	public boolean isNo() {
		return this == NO;
	}

	@Override
	public String toString() {
		return option;
	}

	public int toBinaryBit() {
		return binaryBit;
	}

	public boolean toBoolean() {
		return boolValue;
	}

	public abstract Decision opposite();

	public static Decision fromBoolean(final boolean decision) {
		return decision ? YES : NO;
	}

}
