package dev.ofekmalka.core.error;

import dev.ofekmalka.core.assertion.result.Result;

interface ProblemDescription {
	ErrorMessageBuilder problem(Object description);

	ErrorMessageBuilder reason(Object explanation);

	ErrorMessageBuilder causedBy(Object cause);

	ErrorMessageBuilder hint(Object tip);

	ErrorMessageBuilder context(Object location);

	ErrorMessageBuilder detail(Object info);

	ErrorMessageBuilder resolution(Object solution); // Suggested fix or workaround.

	ErrorMessageBuilder severity(Object level); // Indicates the importance (e.g., "critical", "warning").

	ErrorMessageBuilder impact(Object consequence); // Describes the negative effect of the problem.

	ErrorMessageBuilder occurredAt(Object timestamp); // When the problem happened.
}

interface ExpectedActualMismatch {
	ErrorMessageBuilder expected(Object expectedValue);

	ErrorMessageBuilder actual(Object actualValue);

	ErrorMessageBuilder found(Object foundValue);

	ErrorMessageBuilder got(Object actualValue); // Synonymic

	ErrorMessageBuilder instead(Object replacement);

	ErrorMessageBuilder required(Object requirement);

	ErrorMessageBuilder use(Object suggestion);

	ErrorMessageBuilder withinTolerance(Object tolerance); // For numerical comparisons with acceptable deviation.

	ErrorMessageBuilder similarTo(Object resemblingValue); // When the actual is close but not exact.
}

interface ConditionsAndStates {
	ErrorMessageBuilder on(Object subject);

	ErrorMessageBuilder when(Object condition);

	ErrorMessageBuilder ifPresent(Object condition);

	ErrorMessageBuilder unless(Object condition);

	ErrorMessageBuilder state(Object currentState);

	ErrorMessageBuilder before(Object event); // A preceding event relevant to the current state.

	ErrorMessageBuilder after(Object event); // A subsequent event relevant to the current state.

	ErrorMessageBuilder during(Object phase); // The specific part of a process where the condition applies.

	ErrorMessageBuilder currentStateOf(Object component, Object stateValue); // More specific about which component's
																				// state.
}

interface RulesAndExpectations {
	ErrorMessageBuilder must(Object rule);

	ErrorMessageBuilder cannot(Object restriction);

	ErrorMessageBuilder should(Object guideline);

	ErrorMessageBuilder failed(Object failureMessage);

	ErrorMessageBuilder violated(Object ruleName); // Explicitly names the rule that was broken.

	ErrorMessageBuilder requires(Object prerequisite); // A necessary condition that wasn't met.

	ErrorMessageBuilder suggested(Object recommendation); // A softer expectation.

	ErrorMessageBuilder permitted(Object allowedAction); // Useful for clarifying what is allowed when something isn't.
}

interface TechnicalMetadata {
	ErrorMessageBuilder input(Object input);

	ErrorMessageBuilder identifier(Object name);

	ErrorMessageBuilder type(Object typeName);

	ErrorMessageBuilder module(Object moduleName); // The specific component or module involved.

	ErrorMessageBuilder operation(Object operationName); // The action being performed when the error occurred.

	ErrorMessageBuilder parameter(Object name, Object value); // Specific input parameters.

	ErrorMessageBuilder returnValue(Object value); // The result of a function call that led to the error.

	ErrorMessageBuilder stackTrace(Object trace); // The technical call stack for debugging.
}

interface Connectives {
	ErrorMessageBuilder but();

	ErrorMessageBuilder not();

	ErrorMessageBuilder and();

	ErrorMessageBuilder or();

	ErrorMessageBuilder also();

	ErrorMessageBuilder notOnly();

	ErrorMessageBuilder then();

	ErrorMessageBuilder although();

	ErrorMessageBuilder however();

	ErrorMessageBuilder therefore();

	// Adding more connectives in English

	ErrorMessageBuilder moreover(); // Adds more information

	ErrorMessageBuilder furthermore(); // Adds more information

	ErrorMessageBuilder besides(); // Adds more information

	ErrorMessageBuilder inAddition(); // Adds more information

	ErrorMessageBuilder indeed(); // Emphasizes a point

	ErrorMessageBuilder forExample(); // Introduces an example

	ErrorMessageBuilder forInstance(); // Introduces a specific example

	ErrorMessageBuilder consequently(); // Indicates a result or consequence

	ErrorMessageBuilder thus(); // Indicates a result or consequence

	ErrorMessageBuilder hence(); // Indicates a result or consequence

	ErrorMessageBuilder asAResult(); // Indicates a result or consequence

	ErrorMessageBuilder otherwise(); // Indicates an alternative

	ErrorMessageBuilder else_(); // Indicates an alternative

	ErrorMessageBuilder while_(); // Indicates a contrast or simultaneous action

	ErrorMessageBuilder whereas(); // Indicates a contrast

	ErrorMessageBuilder yet(); // Indicates a contrast (similar to but)

	ErrorMessageBuilder still(); // Indicates a contrast despite something

	ErrorMessageBuilder because(); // Introduces a reason

	ErrorMessageBuilder since(); // Introduces a reason (can also indicate time)

	ErrorMessageBuilder as(); // Introduces a reason or manner

	ErrorMessageBuilder if_(); // Introduces a condition

	ErrorMessageBuilder unless(); // Introduces a negative condition

	ErrorMessageBuilder finally_(); // Indicates the last point or conclusion

	ErrorMessageBuilder inConclusion(); // Indicates the closing remarks

	ErrorMessageBuilder toSummarize(); // Introduces a summary

	ErrorMessageBuilder equally(); // Indicates similarity or balance

	ErrorMessageBuilder similarly(); // Indicates similarity

	ErrorMessageBuilder rather(); // Indicates a preference or correction

	ErrorMessageBuilder instead(); // Indicates a substitution

	ErrorMessageBuilder concerning(); // Introduces a topic

	ErrorMessageBuilder regarding(); // Introduces a topic

	ErrorMessageBuilder about(); // Introduces a topic

	ErrorMessageBuilder givenThat(); // Introduces a premise

	ErrorMessageBuilder assumingThat(); // Introduces a supposition

	ErrorMessageBuilder accordingly(); // Indicates in a fitting manner

	ErrorMessageBuilder henceForth(); // Indicates from this time on

	ErrorMessageBuilder notwithstanding();// Indicates despite something

	ErrorMessageBuilder nevertheless();// Indicates despite something

	ErrorMessageBuilder subsequently();// Indicates after something

	ErrorMessageBuilder previously(); // Indicates before something

	ErrorMessageBuilder currently(); // Indicates at the present time

	ErrorMessageBuilder meanwhile(); // Indicates during the same time

	ErrorMessageBuilder briefly(); // Introduces a concise statement

	ErrorMessageBuilder inShort(); // Introduces a concise summary
}

interface Punctuation {

	ErrorMessageBuilder newline(); // \n

	ErrorMessageBuilder comma(); // ,

	ErrorMessageBuilder dot(); // .

	ErrorMessageBuilder dotAndNewLine();

	ErrorMessageBuilder colon(); // :
	// ErrorMessageBuilder colonAndNewLine();

	ErrorMessageBuilder semicolon(); // ;

	// ErrorMessageBuilder semicolonAndNewLine();

	// Adding more punctuation marks

	ErrorMessageBuilder questionMark(); // ?

	ErrorMessageBuilder exclamationMark(); // !

	ErrorMessageBuilder hyphen(); // -

	ErrorMessageBuilder dash(); // – or — (en dash or em dash)

	ErrorMessageBuilder openParenthesis(); // (

	ErrorMessageBuilder closeParenthesis(); // )

	ErrorMessageBuilder openBracket(); // [

	ErrorMessageBuilder closeBracket(); // ]

	ErrorMessageBuilder openBrace(); // {

	ErrorMessageBuilder closeBrace(); // }

	ErrorMessageBuilder apostrophe(); // '

	ErrorMessageBuilder quotationMark(); // "

	ErrorMessageBuilder ellipsis(); // ...

	ErrorMessageBuilder slash(); // /

	ErrorMessageBuilder backslash(); // \

	ErrorMessageBuilder asterisk(); // *

	ErrorMessageBuilder ampersand(); // &

	ErrorMessageBuilder equalsSign(); // =

	ErrorMessageBuilder plusSign(); // +

	ErrorMessageBuilder lessThanSign(); // <

	ErrorMessageBuilder greaterThanSign(); // >

	ErrorMessageBuilder caret(); // ^

	ErrorMessageBuilder percentSign(); // %

	ErrorMessageBuilder atSign(); // @

	ErrorMessageBuilder dollarSign(); // $

	ErrorMessageBuilder poundSign(); // #

	ErrorMessageBuilder tilde(); // ~

	ErrorMessageBuilder underscore(); // _
}

interface Preposition {
	ErrorMessageBuilder of(Object nounPhrase);

	ErrorMessageBuilder in(Object locationOrState);

	ErrorMessageBuilder at(Object specificLocationOrTime);

	ErrorMessageBuilder on(Object surfaceOrTopicOrTime);

	ErrorMessageBuilder for_(Object purposeOrRecipient);

	ErrorMessageBuilder with(Object accompanimentOrMeans);

	ErrorMessageBuilder by(Object agentOrMeansOrTime);

	ErrorMessageBuilder from(Object origin);

	ErrorMessageBuilder to(Object destinationOrRecipient);

	ErrorMessageBuilder as(Object roleOrManner);

	ErrorMessageBuilder about(Object topic);

	ErrorMessageBuilder above(Object position);

	ErrorMessageBuilder below(Object position);

	ErrorMessageBuilder under(Object position);

	ErrorMessageBuilder over(Object position);

	ErrorMessageBuilder through(Object movementOrMeans);

	ErrorMessageBuilder after(Object timeOrSequence);

	ErrorMessageBuilder before(Object timeOrSequence);

	ErrorMessageBuilder during(Object timePeriod);

	ErrorMessageBuilder since(Object startingPointInTime);

	ErrorMessageBuilder until(Object endPointInTime);

	ErrorMessageBuilder against(Object oppositionOrContrast);

	ErrorMessageBuilder between(Object separation);

	ErrorMessageBuilder among(Object group);

	ErrorMessageBuilder beyond(Object limit);

	ErrorMessageBuilder inside(Object enclosure);

	ErrorMessageBuilder outside(Object enclosure);

	ErrorMessageBuilder near(Object proximity);

	ErrorMessageBuilder off(Object separationOrRemoval);

	ErrorMessageBuilder toward(Object direction);

	ErrorMessageBuilder upon(Object onNounPhrase); // More descriptive name

	ErrorMessageBuilder across(Object extentOrPath);

	ErrorMessageBuilder along(Object pathOrLength);

	ErrorMessageBuilder around(Object areaOrObject); // More general

	ErrorMessageBuilder outOf(Object source);

	ErrorMessageBuilder onto(Object surface);

	ErrorMessageBuilder into(Object interior);

	ErrorMessageBuilder despite(Object circumstance);

	ErrorMessageBuilder becauseOf(Object reason);

	ErrorMessageBuilder dueTo(Object cause);

	ErrorMessageBuilder insteadOf(Object substitute);

	ErrorMessageBuilder accordingTo(Object sourceOrMethod);

	ErrorMessageBuilder withRespectTo(Object subject);

	ErrorMessageBuilder inOrderTo(Object purposeClause);

	ErrorMessageBuilder byMeansOf(Object methodOrTool);
}

interface Auxiliaries {
	ErrorMessageBuilder be();

	ErrorMessageBuilder being();

	ErrorMessageBuilder been();

	ErrorMessageBuilder is();

	ErrorMessageBuilder was();

	ErrorMessageBuilder are();

	ErrorMessageBuilder were();

	ErrorMessageBuilder has();

	ErrorMessageBuilder have();

	ErrorMessageBuilder had();

	ErrorMessageBuilder do_();

	ErrorMessageBuilder does();

	ErrorMessageBuilder did();

	ErrorMessageBuilder done();

	ErrorMessageBuilder can();

	ErrorMessageBuilder could();

	ErrorMessageBuilder will();

	ErrorMessageBuilder would();

	ErrorMessageBuilder may();

	ErrorMessageBuilder might();

	ErrorMessageBuilder must();

	ErrorMessageBuilder should();

}

interface Pronoun {
	ErrorMessageBuilder it();

	ErrorMessageBuilder he();

	ErrorMessageBuilder she();

	ErrorMessageBuilder they();

	ErrorMessageBuilder him();

	ErrorMessageBuilder her();

	ErrorMessageBuilder them();

	ErrorMessageBuilder his();

	ErrorMessageBuilder hers();

	ErrorMessageBuilder theirs();

	ErrorMessageBuilder its();

	ErrorMessageBuilder we();

	ErrorMessageBuilder us();

	ErrorMessageBuilder our();

	ErrorMessageBuilder ours();

	ErrorMessageBuilder you();

	ErrorMessageBuilder your();

	ErrorMessageBuilder yours();
}

interface VerbPhraseBuilder {
	ErrorMessageBuilder been(Object phrase); // For the past participle

	ErrorMessageBuilder being(Object phrase); // For the present participle

	ErrorMessageBuilder phrase(Object phrase); // For the present participle

}

interface StructuralUnits extends ProblemDescription, //
		ExpectedActualMismatch, //
		ConditionsAndStates, //
		RulesAndExpectations, //
		TechnicalMetadata, //
		Connectives, //
		Punctuation, //
		Preposition, //
		Auxiliaries, //
		VerbPhraseBuilder, //
		Pronoun {
	// Placeholder for structural rules if needed
}

public class ErrorMessageBuilder implements StructuralUnits {
	private final String builder;

	public ErrorMessageBuilder(final String builder) {
		this.builder = builder;
	}

	public static ErrorMessageBuilder create() {
		return new ErrorMessageBuilder("");
	}

	private ErrorMessageBuilder appendSemantic(final Object semanticContent) {
		return new ErrorMessageBuilder(builder + semanticContent.toString() + " ");
	}

	private ErrorMessageBuilder appendStructural(final Object structuralElement) {

		return new ErrorMessageBuilder(builder + structuralElement.toString() + " ");

	}

	private ErrorMessageBuilder appendStructuralForNewLine(final Object structuralElement) {

		return new ErrorMessageBuilder(builder + structuralElement.toString());

	}

	/**
	 * @return the builder
	 */
	public <T> Result<T> asFailure() {
		return Result.failure(builder);
	}

	@Override
	public ErrorMessageBuilder comma() {
		return appendStructural(",");
	}

	@Override
	public ErrorMessageBuilder dot() {
		return appendStructural(".");
	}

	@Override
	public ErrorMessageBuilder dotAndNewLine() {
		return appendStructuralForNewLine(".\n");
	}

	@Override
	public ErrorMessageBuilder colon() {
		return appendStructural(":");
	}

	@Override
	public ErrorMessageBuilder semicolon() {
		return appendStructural(";");
	}

	@Override
	public ErrorMessageBuilder newline() {
		return appendStructuralForNewLine("\n");
	}

	@Override
	public ErrorMessageBuilder but() {
		return appendStructural("but");
	}

	@Override
	public ErrorMessageBuilder not() {
		return appendStructural("not");
	}

	@Override
	public ErrorMessageBuilder and() {
		return appendStructural("and");
	}

	@Override
	public ErrorMessageBuilder or() {
		return appendStructural("or");
	}

	@Override
	public ErrorMessageBuilder also() {
		return appendStructural("also");
	}

	@Override
	public ErrorMessageBuilder notOnly() {
		return appendStructural("not only");
	}

	@Override
	public ErrorMessageBuilder then() {
		return appendStructural("then");
	}

	@Override
	public ErrorMessageBuilder although() {
		return appendStructural("although");
	}

	@Override
	public ErrorMessageBuilder however() {
		return appendStructural("however");
	}

	@Override
	public ErrorMessageBuilder therefore() {
		return appendStructural("therefore");
	}

	@Override
	public ErrorMessageBuilder moreover() {
		return appendStructural("moreover");
	}

	@Override
	public ErrorMessageBuilder furthermore() {
		return appendStructural("furthermore");
	}

	@Override
	public ErrorMessageBuilder besides() {
		return appendStructural("besides");
	}

	@Override
	public ErrorMessageBuilder inAddition() {
		return appendStructural("in addition");
	}

	@Override
	public ErrorMessageBuilder indeed() {
		return appendStructural("indeed");
	}

	@Override
	public ErrorMessageBuilder consequently() {
		return appendStructural("consequently");
	}

	@Override
	public ErrorMessageBuilder thus() {
		return appendStructural("thus");
	}

	@Override
	public ErrorMessageBuilder hence() {
		return appendStructural("hence");
	}

	@Override
	public ErrorMessageBuilder asAResult() {
		return appendStructural("as a result");
	}

	@Override
	public ErrorMessageBuilder otherwise() {
		return appendStructural("otherwise");
	}

	@Override
	public ErrorMessageBuilder else_() {
		return appendStructural("else");
	}

	@Override
	public ErrorMessageBuilder while_() {
		return appendStructural("while");
	}

	@Override
	public ErrorMessageBuilder whereas() {
		return appendStructural("whereas");
	}

	@Override
	public ErrorMessageBuilder yet() {
		return appendStructural("yet");
	}

	@Override
	public ErrorMessageBuilder still() {
		return appendStructural("still");
	}

	@Override
	public ErrorMessageBuilder because() {
		return appendStructural("because");
	}

	@Override
	public ErrorMessageBuilder since() {
		return appendStructural("since");
	}

	@Override
	public ErrorMessageBuilder as() {
		return appendStructural("as");
	}

	@Override
	public ErrorMessageBuilder if_() {
		return appendStructural("if");
	}

	@Override
	public ErrorMessageBuilder unless() {
		return appendStructural("unless");
	}

	@Override
	public ErrorMessageBuilder finally_() {
		return appendStructural("finally");
	}

	@Override
	public ErrorMessageBuilder inConclusion() {
		return appendStructural("in conclusion");
	}

	@Override
	public ErrorMessageBuilder toSummarize() {
		return appendStructural("to summarize");
	}

	@Override
	public ErrorMessageBuilder equally() {
		return appendStructural("equally");
	}

	@Override
	public ErrorMessageBuilder similarly() {
		return appendStructural("similarly");
	}

	@Override
	public ErrorMessageBuilder rather() {
		return appendStructural("rather");
	}

	@Override
	public ErrorMessageBuilder instead() {
		return appendStructural("instead");
	}

	@Override
	public ErrorMessageBuilder concerning() {
		return appendStructural("concerning");
	}

	@Override
	public ErrorMessageBuilder regarding() {
		return appendStructural("regarding");
	}

	@Override
	public ErrorMessageBuilder about() {
		return appendStructural("about");
	}

	@Override
	public ErrorMessageBuilder givenThat() {
		return appendStructural("given that");
	}

	@Override
	public ErrorMessageBuilder assumingThat() {
		return appendStructural("assuming that");
	}

	@Override
	public ErrorMessageBuilder accordingly() {
		return appendStructural("accordingly");
	}

	@Override
	public ErrorMessageBuilder henceForth() {
		return appendStructural("henceforth");
	}

	@Override
	public ErrorMessageBuilder notwithstanding() {
		return appendStructural("notwithstanding");
	}

	@Override
	public ErrorMessageBuilder nevertheless() {
		return appendStructural("nevertheless");
	}

	@Override
	public ErrorMessageBuilder subsequently() {
		return appendStructural("subsequently");
	}

	@Override
	public ErrorMessageBuilder previously() {
		return appendStructural("previously");
	}

	@Override
	public ErrorMessageBuilder currently() {
		return appendStructural("currently");
	}

	@Override
	public ErrorMessageBuilder meanwhile() {
		return appendStructural("meanwhile");
	}

	@Override
	public ErrorMessageBuilder briefly() {
		return appendStructural("briefly");
	}

	@Override
	public ErrorMessageBuilder inShort() {
		return appendStructural("in short");
	}

	@Override
	public ErrorMessageBuilder problem(final Object description) {
		return appendSemantic(description);
	}

	@Override
	public ErrorMessageBuilder reason(final Object explanation) {
		return appendSemantic(explanation);
	}

	@Override
	public ErrorMessageBuilder causedBy(final Object cause) {
		return appendSemantic(cause);
	}

	@Override
	public ErrorMessageBuilder hint(final Object tip) {
		return appendSemantic(tip);
	}

	@Override
	public ErrorMessageBuilder context(final Object location) {
		return appendSemantic(location);
	}

	@Override
	public ErrorMessageBuilder detail(final Object info) {
		return appendSemantic(info);
	}

	@Override
	public ErrorMessageBuilder resolution(final Object solution) {
		return appendSemantic(solution);
	}

	@Override
	public ErrorMessageBuilder severity(final Object level) {
		return appendSemantic(level);
	}

	@Override
	public ErrorMessageBuilder impact(final Object consequence) {
		return appendSemantic(consequence);
	}

	@Override
	public ErrorMessageBuilder occurredAt(final Object timestamp) {
		return appendSemantic(timestamp);
	}

	@Override
	public ErrorMessageBuilder expected(final Object expectedValue) {
		return appendSemantic(expectedValue);
	}

	@Override
	public ErrorMessageBuilder actual(final Object actualValue) {
		return appendSemantic(actualValue);
	}

	@Override
	public ErrorMessageBuilder found(final Object foundValue) {
		return appendSemantic(foundValue);
	}

	@Override
	public ErrorMessageBuilder got(final Object actualValue) {
		return appendSemantic(actualValue);
	}

	@Override
	public ErrorMessageBuilder instead(final Object replacement) {
		return appendSemantic(replacement);
	}

	@Override
	public ErrorMessageBuilder required(final Object requirement) {
		return appendSemantic(requirement);
	}

	@Override
	public ErrorMessageBuilder use(final Object suggestion) {
		return appendSemantic(suggestion);
	}

	@Override
	public ErrorMessageBuilder withinTolerance(final Object tolerance) {
		return appendSemantic(tolerance);
	}

	@Override
	public ErrorMessageBuilder similarTo(final Object resemblingValue) {
		return appendSemantic(resemblingValue);
	}

	@Override
	public ErrorMessageBuilder on(final Object subject) {
		return appendSemantic(subject);
	}

	@Override
	public ErrorMessageBuilder when(final Object condition) {
		return appendSemantic(condition);
	}

	@Override
	public ErrorMessageBuilder ifPresent(final Object condition) {
		return appendSemantic(condition);
	}

	@Override
	public ErrorMessageBuilder unless(final Object condition) {
		return appendSemantic(condition);
	}

	@Override
	public ErrorMessageBuilder state(final Object currentState) {
		return appendSemantic(currentState);
	}

	@Override
	public ErrorMessageBuilder before(final Object event) {
		return appendSemantic(event);
	}

	@Override
	public ErrorMessageBuilder after(final Object event) {
		return appendSemantic(event);
	}

	@Override
	public ErrorMessageBuilder during(final Object phase) {
		return appendSemantic(phase);
	}

	@Override
	public ErrorMessageBuilder currentStateOf(final Object component, final Object stateValue) {
		return appendSemantic(component + " is " + stateValue);
	}

	@Override
	public ErrorMessageBuilder must(final Object rule) {
		return appendSemantic(rule);
	}

	@Override
	public ErrorMessageBuilder cannot(final Object restriction) {
		return appendSemantic(restriction);
	}

	@Override
	public ErrorMessageBuilder should(final Object guideline) {
		return appendSemantic(guideline);
	}

	@Override
	public ErrorMessageBuilder failed(final Object failureMessage) {
		return appendSemantic(failureMessage);
	}

	@Override
	public ErrorMessageBuilder violated(final Object ruleName) {
		return appendSemantic("violated rule: " + ruleName);
	}

	@Override
	public ErrorMessageBuilder requires(final Object prerequisite) {
		return appendSemantic("requires: " + prerequisite);
	}

	@Override
	public ErrorMessageBuilder suggested(final Object recommendation) {
		return appendSemantic("suggestion: " + recommendation);
	}

	@Override
	public ErrorMessageBuilder permitted(final Object allowedAction) {
		return appendSemantic("permitted: " + allowedAction);
	}

	@Override
	public ErrorMessageBuilder input(final Object input) {
		return appendSemantic("input: " + input);
	}

	@Override
	public ErrorMessageBuilder identifier(final Object name) {
		return appendSemantic("identifier: " + name);
	}

	@Override
	public ErrorMessageBuilder type(final Object typeName) {
		return appendSemantic("type: " + typeName);
	}

	@Override
	public ErrorMessageBuilder module(final Object moduleName) {
		return appendSemantic("module: " + moduleName);
	}

	@Override
	public ErrorMessageBuilder operation(final Object operationName) {
		return appendSemantic("operation: " + operationName);
	}

	@Override
	public ErrorMessageBuilder parameter(final Object name, final Object value) {
		return appendSemantic("parameter " + name + ": " + value);
	}

	@Override
	public ErrorMessageBuilder returnValue(final Object value) {
		return appendSemantic("returned: " + value);
	}

	@Override
	public ErrorMessageBuilder stackTrace(final Object trace) {
		return appendSemantic("\nStack Trace:\n" + trace);
	}

	@Override
	public ErrorMessageBuilder of(final Object nounPhrase) {
		return appendStructural("of " + nounPhrase);
	}

	@Override
	public ErrorMessageBuilder for_(final Object purposeOrRecipient) {
		return appendStructural("for " + purposeOrRecipient);
	}

	@Override
	public ErrorMessageBuilder forExample() {
		return appendStructural("for example");
	}

	@Override
	public ErrorMessageBuilder forInstance() {
		return appendStructural("for instance");
	}

	@Override
	public ErrorMessageBuilder questionMark() {
		return appendStructural("?");
	}

	@Override
	public ErrorMessageBuilder exclamationMark() {
		return appendStructural("!");
	}

	@Override
	public ErrorMessageBuilder hyphen() {
		return appendStructural("-");
	}

	@Override
	public ErrorMessageBuilder dash() {
		return appendStructural("–");
	}

	@Override
	public ErrorMessageBuilder openParenthesis() {
		return appendStructural("(");
	}

	@Override
	public ErrorMessageBuilder closeParenthesis() {
		return appendStructural(")");
	}

	@Override
	public ErrorMessageBuilder openBracket() {
		return appendStructural("[");
	}

	@Override
	public ErrorMessageBuilder closeBracket() {
		return appendStructural("]");
	}

	@Override
	public ErrorMessageBuilder openBrace() {
		return appendStructural("{");
	}

	@Override
	public ErrorMessageBuilder closeBrace() {
		return appendStructural("}");
	}

	@Override
	public ErrorMessageBuilder apostrophe() {
		return appendStructural("'");
	}

	@Override
	public ErrorMessageBuilder quotationMark() {
		return appendStructural("\"");
	}

	@Override
	public ErrorMessageBuilder ellipsis() {
		return appendStructural("…");
	}

	@Override
	public ErrorMessageBuilder slash() {
		return appendStructural("/");
	}

	@Override
	public ErrorMessageBuilder backslash() {
		return appendStructural("\\");
	}

	@Override
	public ErrorMessageBuilder asterisk() {
		return appendStructural("*");
	}

	@Override
	public ErrorMessageBuilder ampersand() {
		return appendStructural("&");
	}

	@Override
	public ErrorMessageBuilder equalsSign() {
		return appendStructural("=");
	}

	@Override
	public ErrorMessageBuilder plusSign() {
		return appendStructural("+");
	}

	@Override
	public ErrorMessageBuilder lessThanSign() {
		return appendStructural("<");
	}

	@Override
	public ErrorMessageBuilder greaterThanSign() {
		return appendStructural(">");
	}

	@Override
	public ErrorMessageBuilder caret() {
		return appendStructural("^");
	}

	@Override
	public ErrorMessageBuilder percentSign() {
		return appendStructural("%");
	}

	@Override
	public ErrorMessageBuilder atSign() {
		return appendStructural("@");
	}

	@Override
	public ErrorMessageBuilder dollarSign() {
		return appendStructural("$");
	}

	@Override
	public ErrorMessageBuilder poundSign() {
		return appendStructural("#");
	}

	@Override
	public ErrorMessageBuilder tilde() {
		return appendStructural("~");
	}

	@Override
	public ErrorMessageBuilder underscore() {
		return appendStructural("_");
	}

	@Override
	public ErrorMessageBuilder in(final Object locationOrState) {
		return appendStructural("in " + locationOrState);
	}

	@Override
	public ErrorMessageBuilder at(final Object specificLocationOrTime) {
		return appendStructural("at " + specificLocationOrTime);
	}

	@Override
	public ErrorMessageBuilder with(final Object accompanimentOrMeans) {
		return appendStructural("with " + accompanimentOrMeans);
	}

	@Override
	public ErrorMessageBuilder by(final Object agentOrMeansOrTime) {
		return appendStructural("by " + agentOrMeansOrTime);
	}

	@Override
	public ErrorMessageBuilder from(final Object origin) {
		return appendStructural("from " + origin);
	}

	@Override
	public ErrorMessageBuilder to(final Object destinationOrRecipient) {
		return appendStructural("to " + destinationOrRecipient);
	}

	@Override
	public ErrorMessageBuilder as(final Object roleOrManner) {
		return appendStructural("as " + roleOrManner);
	}

	@Override
	public ErrorMessageBuilder about(final Object topic) {
		return appendStructural("about " + topic);
	}

	@Override
	public ErrorMessageBuilder above(final Object position) {
		return appendStructural("above " + position);
	}

	@Override
	public ErrorMessageBuilder below(final Object position) {
		return appendStructural("below " + position);
	}

	@Override
	public ErrorMessageBuilder under(final Object position) {
		return appendStructural("under " + position);
	}

	@Override
	public ErrorMessageBuilder over(final Object position) {
		return appendStructural("over " + position);
	}

	@Override
	public ErrorMessageBuilder through(final Object movementOrMeans) {
		return appendStructural("through " + movementOrMeans);
	}

	@Override
	public ErrorMessageBuilder since(final Object startingPointInTime) {
		return appendStructural("since " + startingPointInTime);
	}

	@Override
	public ErrorMessageBuilder until(final Object endPointInTime) {
		return appendStructural("until " + endPointInTime);
	}

	@Override
	public ErrorMessageBuilder against(final Object oppositionOrContrast) {
		return appendStructural("against " + oppositionOrContrast);
	}

	@Override
	public ErrorMessageBuilder between(final Object separation) {
		return appendStructural("between " + separation);
	}

	@Override
	public ErrorMessageBuilder among(final Object group) {
		return appendStructural("among " + group);
	}

	@Override
	public ErrorMessageBuilder beyond(final Object limit) {
		return appendStructural("beyond " + limit);
	}

	@Override
	public ErrorMessageBuilder inside(final Object enclosure) {
		return appendStructural("inside " + enclosure);
	}

	@Override
	public ErrorMessageBuilder outside(final Object enclosure) {
		return appendStructural("outside " + enclosure);
	}

	@Override
	public ErrorMessageBuilder near(final Object proximity) {
		return appendStructural("near " + proximity);
	}

	@Override
	public ErrorMessageBuilder off(final Object separationOrRemoval) {
		return appendStructural("off " + separationOrRemoval);
	}

	@Override
	public ErrorMessageBuilder toward(final Object direction) {
		return appendStructural("toward " + direction);
	}

	@Override
	public ErrorMessageBuilder upon(final Object onNounPhrase) {
		return appendStructural("upon " + onNounPhrase);
	}

	@Override
	public ErrorMessageBuilder across(final Object extentOrPath) {
		return appendStructural("across " + extentOrPath);
	}

	@Override
	public ErrorMessageBuilder along(final Object pathOrLength) {
		return appendStructural("along " + pathOrLength);
	}

	@Override
	public ErrorMessageBuilder around(final Object areaOrObject) {
		return appendStructural("around " + areaOrObject);
	}

	@Override
	public ErrorMessageBuilder outOf(final Object source) {
		return appendStructural("out of " + source);
	}

	@Override
	public ErrorMessageBuilder onto(final Object surface) {
		return appendStructural("onto " + surface);
	}

	@Override
	public ErrorMessageBuilder into(final Object interior) {
		return appendStructural("into " + interior);
	}

	@Override
	public ErrorMessageBuilder despite(final Object circumstance) {
		return appendStructural("despite " + circumstance);
	}

	@Override
	public ErrorMessageBuilder becauseOf(final Object reason) {
		return appendStructural("because of " + reason);
	}

	@Override
	public ErrorMessageBuilder dueTo(final Object cause) {
		return appendStructural("due to " + cause);
	}

	@Override
	public ErrorMessageBuilder insteadOf(final Object substitute) {
		return appendStructural("instead of " + substitute);
	}

	@Override
	public ErrorMessageBuilder accordingTo(final Object sourceOrMethod) {
		return appendStructural("according to " + sourceOrMethod);
	}

	@Override
	public ErrorMessageBuilder withRespectTo(final Object subject) {
		return appendStructural("with respect to " + subject);
	}

	@Override
	public ErrorMessageBuilder inOrderTo(final Object purposeClause) {
		return appendStructural("in order to " + purposeClause);
	}

	@Override
	public ErrorMessageBuilder byMeansOf(final Object methodOrTool) {
		return appendStructural("by means of " + methodOrTool);
	}

	@Override
	public ErrorMessageBuilder be() {
		return appendStructural("be");

	}

	@Override
	public ErrorMessageBuilder being() {
		return appendStructural("being");

	}

	@Override
	public ErrorMessageBuilder been() {

		return appendStructural("been");
	}

	@Override
	public ErrorMessageBuilder is() {
		return appendStructural("is");

	}

	@Override
	public ErrorMessageBuilder was() {
		return appendStructural("was");

	}

	@Override
	public ErrorMessageBuilder are() {
		return appendStructural("are");

	}

	@Override
	public ErrorMessageBuilder were() {
		return appendStructural("were");

	}

	@Override
	public ErrorMessageBuilder has() {
		return appendStructural("has");

	}

	@Override
	public ErrorMessageBuilder have() {
		return appendStructural("have");

	}

	@Override
	public ErrorMessageBuilder had() {
		return appendStructural("had");

	}

	@Override
	public ErrorMessageBuilder do_() {

		return appendStructural("do");

	}

	@Override
	public ErrorMessageBuilder does() {
		return appendStructural("does");

	}

	@Override
	public ErrorMessageBuilder did() {
		return appendStructural("did");

	}

	@Override
	public ErrorMessageBuilder done() {
		return appendStructural("done");

	}

	@Override
	public ErrorMessageBuilder can() {
		return appendStructural("can");

	}

	@Override
	public ErrorMessageBuilder could() {
		return appendStructural("could");

	}

	@Override
	public ErrorMessageBuilder will() {
		return appendStructural("will");

	}

	@Override
	public ErrorMessageBuilder would() {
		return appendStructural("would");

	}

	@Override
	public ErrorMessageBuilder may() {
		return appendStructural("may");

	}

	@Override
	public ErrorMessageBuilder might() {
		return appendStructural("might");

	}

	@Override
	public ErrorMessageBuilder must() {
		return appendStructural("must");

	}

	@Override
	public ErrorMessageBuilder should() {
		return appendStructural("should");

	}

	@Override
	public ErrorMessageBuilder been(final Object phrase) {
		return appendStructural("been " + phrase);

	}

	@Override
	public ErrorMessageBuilder being(final Object phrase) {
		return appendStructural("being " + phrase);

	}

	@Override
	public ErrorMessageBuilder phrase(final Object phrase) {
		return appendSemantic(phrase);

	}

	@Override
	public ErrorMessageBuilder it() {
		return appendSemantic("it");
	}

	@Override
	public ErrorMessageBuilder he() {
		return appendSemantic("he");
	}

	@Override
	public ErrorMessageBuilder she() {
		return appendSemantic("she");
	}

	@Override
	public ErrorMessageBuilder they() {
		return appendSemantic("they");
	}

	@Override
	public ErrorMessageBuilder him() {
		return appendSemantic("him");
	}

	@Override
	public ErrorMessageBuilder her() {
		return appendSemantic("her");
	}

	@Override
	public ErrorMessageBuilder them() {
		return appendSemantic("them");
	}

	@Override
	public ErrorMessageBuilder his() {
		return appendSemantic("his");
	}

	@Override
	public ErrorMessageBuilder hers() {
		return appendSemantic("hers");
	}

	@Override
	public ErrorMessageBuilder theirs() {
		return appendSemantic("theirs");
	}

	@Override
	public ErrorMessageBuilder its() {
		return appendSemantic("its");
	}

	@Override
	public ErrorMessageBuilder we() {
		return appendSemantic("we");
	}

	@Override
	public ErrorMessageBuilder us() {
		return appendSemantic("us");
	}

	@Override
	public ErrorMessageBuilder our() {
		return appendSemantic("our");
	}

	@Override
	public ErrorMessageBuilder ours() {
		return appendSemantic("ours");
	}

	@Override
	public ErrorMessageBuilder you() {
		return appendSemantic("you");
	}

	@Override
	public ErrorMessageBuilder your() {
		return appendSemantic("your");
	}

	@Override
	public ErrorMessageBuilder yours() {
		return appendSemantic("yours");
	}

	public static void main(final String[] args) {

		ErrorMessageBuilder.create()//
				.problem("Failed to process user order")//
				.colon()//
				.newline()//
				.reason("Order amount exceeds the allowed limit")//
				.dotAndNewLine()//

				.phrase("it").should().not().be().above("22")

				.asFailure()//
				.printlnToConsole()//
				.run()//
		;

	}

}
