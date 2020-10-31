package iris.json;

import iris.json.plain.Female;
import iris.json.plain.Human;
import iris.json.plain.Male;
import iris.json.serialization.PolymorphCaseString;
import iris.json.serialization.PolymorphData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author [Ivan Ivanov](https://vk.com/irisism)
 * @created 15.10.2020
 */
public final class PureJavaUser {

	public int id;
	public List<String> type;

	@PolymorphData(
			sourceField = "type",
			strings = {@PolymorphCaseString(
					instance = Male.class,
					label = "MaleFirst"
			), @PolymorphCaseString(
					instance = Female.class,
					label = "FemaleFirst"
			)}
	)
	public Human person1;
	@PolymorphData(
			sourceField = "type",
			strings = {@PolymorphCaseString(
					instance = Male.class,
					label = "FemaleFirst"
			), @PolymorphCaseString(
					instance = Female.class,
					label = "MaleFirst"
			)}
	)
	public Human person2;

	public PureJavaUser(int id, String type, Human person1, Human person2) {
		this.id = id;
		this.type = new ArrayList(1);
		this.type.add(type);
		this.person1 = person1;
		this.person2 = person2;
	}
}