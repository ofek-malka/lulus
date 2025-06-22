package dev.ofekmalka.core.tag.maven.elements.parents_elements.repository;

import dev.ofekmalka.core.assertion.If;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.tools.utils.Decision;

public record RepositoryPolicy(MavenParentElement parent) {

	public static final AddChecksumPolicy disabled() {
		final var createParent = MavenParentElement.name(Parent.REPOSITORY_POLICY)//
				.addChild(Leaf.ENABLED, Decision.NO.toString())//

		;
		return new AddChecksumPolicy(createParent);

	}

	public static final AddChecksumPolicy enabled() {
		final var createParent = MavenParentElement.name(Parent.REPOSITORY_POLICY)//
				.addChild(Leaf.ENABLED, Decision.YES.toString())//

		;
		return new AddChecksumPolicy(createParent);

	}

	public static final class AddChecksumPolicy {
		private final MavenParentElement parent;

		private AddChecksumPolicy(final MavenParentElement parent) {
			this.parent = parent;
		}

		enum ChecksumPolicyOptions {
			FAIL, WARN, IGNORE;

			String getValue() {

				return toString().toLowerCase();
			}

		}

		public AddUpdatePolicy addChecksumPolicyToFail() {
			return helperChecksumPolicyTo(ChecksumPolicyOptions.FAIL);
		}

		public AddUpdatePolicy addChecksumPolicyToWarn() {
			return helperChecksumPolicyTo(ChecksumPolicyOptions.WARN);
		}

		public AddUpdatePolicy addChecksumPolicyToIgnore() {
			return helperChecksumPolicyTo(ChecksumPolicyOptions.IGNORE);
		}

		private AddUpdatePolicy helperChecksumPolicyTo(final ChecksumPolicyOptions option) {
			final var createParent = parent.addChild(Leaf.CHECKSUM_POLICY, option.getValue());
			return new AddUpdatePolicy(createParent);

		}

		public static final class AddUpdatePolicy {
			private final MavenParentElement parent;

			private AddUpdatePolicy(final MavenParentElement parent) {
				this.parent = parent;
			}

			private RepositoryPolicy addChild(final String option) {

				final var createParent = parent.addChild(Leaf.UPDATE_POLICY, option);
				return new RepositoryPolicy(createParent);

			}

			// Set the frequency for downloading updates - can be always, daily (default),
			// interval:XXX (in minutes) or never (only if it doesn't exist locally).

			enum UpdatePolicyOptions {
				ALWAYS("always"), DAILY("daily")/* (default) */, INTERVAL("interval"), NEVER("never");

				String option;

				UpdatePolicyOptions(final String option) {
					this.option = option;
				}

				String getValue() {

					return option;
				}
			}

			public RepositoryPolicy addUpdatePolicyToDaily() {
				return addChild(UpdatePolicyOptions.DAILY.getValue());

			}

			public RepositoryPolicy addUpdatePolicyToAlways() {
				return addChild(UpdatePolicyOptions.ALWAYS.getValue());

			}

			public RepositoryPolicy addUpdatePolicyToNever() {
				return addChild(UpdatePolicyOptions.NEVER.getValue());

			}

			public RepositoryPolicy addUpdatePolicyToValidIntervalOrDefaultZero(final int minutes) {
				final var calculateTime = If.isItTrue(minutes<0).will()
						.returnValue(() -> 0)//
						.orGet(() -> minutes);
				return addChild(UpdatePolicyOptions.INTERVAL.getValue() + ":" + calculateTime);

			}

		}

	}

}
