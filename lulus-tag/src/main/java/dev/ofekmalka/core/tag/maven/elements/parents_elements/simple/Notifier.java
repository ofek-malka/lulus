package dev.ofekmalka.core.tag.maven.elements.parents_elements.simple;

import dev.ofekmalka.core.tag.maven.MavenXmlTags.Leaf;
import dev.ofekmalka.core.tag.maven.MavenXmlTags.Parent;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.MavenParentElement;
import dev.ofekmalka.core.tag.maven.elements.parents_elements.simple.Notifier.Builder.AddAddress;

public final record Notifier(MavenParentElement parent) {

	public static AddAddress addType(final String type) {
		return Builder.builder().addType(type);
	}

	public static class Builder {

		public static AddType builder() {
			return type -> //
			address -> //
			sendOnError -> //
			sendOnFailure -> //
			sendOnSuccess -> //
			sendOnWarning -> //
			configuration -> {

				final var createParent =

						MavenParentElement

								.name(Parent.NOTIFIER)//
								.addChild(Leaf.TYPE, type)//
								.addChild(Leaf.ADDRESS, address)//
								.addChild(Leaf.SEND_ON_ERROR, String.valueOf(sendOnError))//
								.addChild(Leaf.SEND_ON_FAILURE, String.valueOf(sendOnFailure))//
								.addChild(Leaf.SEND_ON_SUCCESS, String.valueOf(sendOnSuccess))//
								.addChild(Leaf.SEND_ON_WARNING, String.valueOf(sendOnWarning))//

								.addChild(Parent.CONFIGURATION, configuration.parent().getContent())//

				;

				return new Notifier(createParent);

			};

		}

		public interface AddType {
			Builder.AddAddress addType(String type);
		}

		public interface AddAddress {
			Builder.AddSendOnError addAddress(final String address);
		}

		public interface AddSendOnError {
			Builder.AddSendOnFailure isSendOnError(final boolean sendOnError);
		}

		public interface AddSendOnFailure {
			Builder.AddSendOnSuccess isSendOnFailure(final boolean sendOnFailure);
		}

		public interface AddSendOnSuccess {
			Builder.AddSendOnWarning isSendOnSuccess(final boolean sendOnSuccess);
		}

		public interface AddSendOnWarning {
			Builder.AddConfiguration isSendOnWarning(final boolean sendOnWarning);
		}

		public interface AddConfiguration {
			Notifier addConfiguration(final Configuration configuration);
		}

	}

}
