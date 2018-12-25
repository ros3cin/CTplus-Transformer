package cin.ufpe.br.cecotool_transformer;

public enum RecommendationFileHeader {
	FIELD_NAME("Field name"),
	IS_LOCAL("Is local?"),
	SOURCE_CODE_LINE("Source code line"),
	CONTAINING_CLASS("Containing class"),
	ORIGINAL_COLLECTION("Original collection"),
	ORDERED_RECOMMENDATIONS("Ordered recommendations"),
	CHOSEN_RECOMMENDATION("Choosen recommendation (used by the CECOTool transfomer, change it accordingly)");
	
	private String description;
	
	private RecommendationFileHeader(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
