package dev.ofekmalka.core.tag.maven.elements.enums;

//Warning! Like a baby bird in a nest, the status should never be touched by human hands! The reason for this is that Maven will set the status of the project when it is transported out to the repository. It is described here just for understanding, but should never be configured in your pom.xml.
//
//Status valid values are as follows:
//
//none: No special status. This is the default for a POM.
//converted: The manager of the repository converted this POM from an earlier version to Maven 2.
//partner: This artifact has been synchronized with a partner repository.
//deployed: By far the most common status, meaning that this artifact was deployed from a Maven 2 or 3 instance. This is what you get when you manually deploy using the command-line deploy phase.
//verified: This project has been verified, and should be considered finalized.
public enum Status {
	NONE("none"), //
	CONVERTED("converted"), //
	RELEASED("released"), //
	DEPLOYED("deployed"), //
	VERIFIED("verified");//

	private final String status;

	Status(final String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
