package cin.ufpe.br.cecotool_transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class Recommendation {
	private List<Integer> sourceCodeLines;
	private String classPath;
	private String originalCollection;
	private List<String> orderedRecommendations;
	private int chosenRecommendation;
	private String variableName;
	
	public Recommendation(String sourceCodeLines, String classPath, String originalCollection, String orderedRecommendations, String variableName, int chosenRecommendation) {
		this.classPath = classPath;
		this.originalCollection = originalCollection;
		this.variableName = variableName;
		this.chosenRecommendation = chosenRecommendation;
		setSourceCodeLinesFromString(sourceCodeLines);
		setOrderedRecommendationsFromString(orderedRecommendations);
	}
	
	public void setSourceCodeLinesFromString(String sourceCodeLines) {
		if (!StringUtils.isEmpty(sourceCodeLines)) {
			this.sourceCodeLines = new ArrayList<Integer>();
			StringTokenizer strTok = new StringTokenizer(sourceCodeLines,",");
			while(strTok.hasMoreTokens()) {
				this.sourceCodeLines.add(Integer.parseInt(strTok.nextToken()));
			}
		}
	}
	
	public void setOrderedRecommendationsFromString(String orderedRecommendations) {
		if (!StringUtils.isEmpty(orderedRecommendations)) {
			this.orderedRecommendations = new ArrayList<String>();
			StringTokenizer strTok = new StringTokenizer(orderedRecommendations,"<");
			while(strTok.hasMoreTokens()) {
				this.orderedRecommendations.add(strTok.nextToken());
			}
		}
	}
	
	public List<Integer> getSourceCodeLines() {
		return sourceCodeLines;
	}
	public void setSourceCodeLines(List<Integer> sourceCodeLines) {
		this.sourceCodeLines = sourceCodeLines;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public String getOriginalCollection() {
		return originalCollection;
	}
	public void setOriginalCollection(String originalCollection) {
		this.originalCollection = originalCollection;
	}
	public List<String> getOrderedRecommendations() {
		return orderedRecommendations;
	}
	public void setOrderedRecommendations(List<String> orderedRecommendations) {
		this.orderedRecommendations = orderedRecommendations;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getChosenRecommendation() {
		return chosenRecommendation;
	}

	public void setChosenRecommendation(int chosenRecommendation) {
		this.chosenRecommendation = chosenRecommendation;
	}
}
