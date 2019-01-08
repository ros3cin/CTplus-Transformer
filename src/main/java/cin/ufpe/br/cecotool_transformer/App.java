package cin.ufpe.br.cecotool_transformer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class App 
{
	public static final Logger logger = LogManager.getLogger(App.class);
	
    public static void main( String[] args ) throws IOException
    {
    	String rootTargetDirectory = "C:\\Users\\RENATO\\Documents\\Mestrado\\Hasan Apps\\commons-math\\src\\main\\java";
    	String recommendationsFile = "C:\\Users\\RENATO\\scala-ide-workspace\\CECOTool\\nexus 7 commons math recommendations.csv";
    	App app = new App();
    	app.execute(rootTargetDirectory, recommendationsFile);
    }
    
    public void execute(String rootTargetDirectory, String recommendationsFile) throws IOException {
    	logger.info("Transformer started");
    	Map<String, ModifiedFile> modifiedFiles = new HashMap<String, ModifiedFile>();
    	logger.info("Staging recommendations...");
    	stage(recommendationsFile, rootTargetDirectory, new RecommendationMap(), modifiedFiles);
    	logger.info("Applying recommendations...");
    	apply(modifiedFiles);
    	logger.info("Recommendations applied!");
    }
    
    private void apply(Map<String, ModifiedFile> modifiedFiles) throws IOException {
    	for(ModifiedFile modifiedFile : modifiedFiles.values()) {
    		modifiedFile.apply();
    	}
    }
    
    private void stage(String recommendationsFile, String rootTargetDirectory, RecommendationMap recommendationMap, Map<String, ModifiedFile> modifiedFiles) throws FileNotFoundException, IOException {
    	List<Recommendation> recommendations = getRecommendations(recommendationsFile);
    	for(Recommendation recommendation : recommendations) {
    		stage(recommendation, rootTargetDirectory, recommendationMap, modifiedFiles);
    	}
    }
    
    private void stage(
    		Recommendation recommendation,
    		String rootTargetDirectory,
    		RecommendationMap recommendationMap,
    		Map<String, ModifiedFile> modifiedFiles
    		) throws IOException {
    	String classPath = recommendation.getClassPath().replace('.', '/');
    	int innerClassMarkerIndex = classPath.indexOf('$');
		if (innerClassMarkerIndex != -1) {
			classPath = classPath.substring(0,innerClassMarkerIndex);
		}
    	String absolutePath = String.format("%s/%s.java", rootTargetDirectory, classPath);
    	if (modifiedFiles.containsKey(absolutePath)) {
    		CompilationUnit compiledFile = modifiedFiles.get(absolutePath).getCompUnit();
    		compiledFile.accept(new DeclarationAndAssignmentsVisitor(recommendationMap), recommendation);
    	} else {
	    	FileInputStream in = new FileInputStream(absolutePath);
	    	CompilationUnit compiledFile = JavaParser.parse(in);
	    	compiledFile.accept(new DeclarationAndAssignmentsVisitor(recommendationMap), recommendation);
	    	in.close();
	    	modifiedFiles.put(absolutePath, new ModifiedFile(absolutePath, compiledFile));
    	}
    }
    
    private List<Recommendation> getRecommendations(String recommendationsFile) throws FileNotFoundException, IOException {
    	List<Recommendation> recommendations = new ArrayList<Recommendation>();
    	final CSVParser reader = new CSVParser(new FileReader(recommendationsFile), CSVFormat.DEFAULT.withHeader());
    	for (final CSVRecord line : reader) {
    		String variableName = line.get(RecommendationFileHeader.FIELD_NAME.getDescription());
    		String classPath = line.get(RecommendationFileHeader.CONTAINING_CLASS.getDescription());
    		String sourceCodeLines = line.get(RecommendationFileHeader.SOURCE_CODE_LINE.getDescription());
    		String originalCollection = line.get(RecommendationFileHeader.ORIGINAL_COLLECTION.getDescription());
    		String orderedRecommendations = line.get(RecommendationFileHeader.ORDERED_RECOMMENDATIONS.getDescription());
    		String chosenRecommendation = line.get(RecommendationFileHeader.CHOSEN_RECOMMENDATION.getDescription());
    		int chosenRecommendationInt = 1;
    		if (!StringUtils.isEmpty(chosenRecommendation)) {
    			chosenRecommendationInt = Integer.parseInt(chosenRecommendation);
    		}
    		recommendations.add(new Recommendation(sourceCodeLines, classPath, originalCollection, orderedRecommendations, variableName, chosenRecommendationInt));
    	}
    	reader.close();
    	return recommendations;
    }
}
class DeclarationAndAssignmentsVisitor extends VoidVisitorAdapter<Recommendation> {
	private RecommendationMap recommendationMap;
	private Map<String, VariableDeclarator> classVariableDeclarations;
	private Map<String, VariableDeclarator> localVariableDeclarations;
	
	public DeclarationAndAssignmentsVisitor(RecommendationMap recommendationMap) {
		this.recommendationMap = recommendationMap;
		this.classVariableDeclarations = new HashMap<String,VariableDeclarator>();
		this.localVariableDeclarations = new HashMap<String,VariableDeclarator>();
	}

	@Override
	public void visit(FieldDeclaration n, Recommendation recommendation) {
		for(VariableDeclarator declarator : n.getVariables()) {
			int line = declarator.getBegin().get().line;
			ClassOrInterfaceDeclaration clazz = getClass(n);
			if (recommendation.getSourceCodeLines().contains(line)) {
				Expression right = declarator.getInitializer().get();
				change(recommendation, right, declarator);
			}
			String key = String.format("%s-%s",clazz.getName().toString(), declarator.getName().toString());
			this.classVariableDeclarations.put(key, declarator);
		}
	}
	
	@Override
    public void visit(VariableDeclarator n, Recommendation recommendation) {
		int line = n.getBegin().get().line;
		if (recommendation.getSourceCodeLines().contains(line)) {
			Expression right = n.getInitializer().get();
			change(recommendation, right, n);
		}
		CallableDeclaration<?> method = getContainingCallable(n);
		ClassOrInterfaceDeclaration clazz = getClass(n);
		if (method != null) {
			String key = String.format("%s-%s-%s", clazz.getName().toString(), method.getDeclarationAsString(), n.getName().toString());
			this.localVariableDeclarations.put(key, n);
		}
    }
	
	@Override
    public void visit(AssignExpr n, Recommendation recommendation) {
		int line = n.getBegin().get().line;
		if (recommendation.getSourceCodeLines().contains(line)) {
			VariableDeclarator fieldDeclarator = null;
			Expression left = n.getTarget();
			ClassOrInterfaceDeclaration clazz = getClass(n);
			
			if (left.isNameExpr()) {
				String name = left.asNameExpr().toString();
				CallableDeclaration<?> method = getContainingCallable(n);
				if (method != null) {
					String key = String.format("%s-%s-%s", clazz.getName().toString(), method.getDeclarationAsString(), name);
					if (this.localVariableDeclarations.containsKey(key)) {						
						fieldDeclarator = this.localVariableDeclarations.get(key);
					}
				}
				if (fieldDeclarator == null) {
					String key = String.format("%s-%s", clazz.getName().toString(),name);
					if (this.classVariableDeclarations.containsKey(key)) {
						fieldDeclarator = this.classVariableDeclarations.get(key);
					}
				}
			}
			
			if (left.isFieldAccessExpr()) {
				FieldAccessExpr fieldAccess = left.asFieldAccessExpr();
				String key = String.format("%s-%s", clazz.getName().toString(),fieldAccess.getName().toString());
				fieldDeclarator = this.classVariableDeclarations.get(key);
			}
			
			Expression right = n.getValue();
			change(recommendation, right, fieldDeclarator);
		}
    }
	
	private CallableDeclaration<?> getContainingCallable(Node node) {
		if (node != null) {
			if (node instanceof CallableDeclaration) {
				return (CallableDeclaration<?>)node;
			} else {
				Node parent = node.getParentNode().isPresent() ? node.getParentNode().get() : null;
				return getContainingCallable(parent);
			}
		} else {
			return null;
		}
	}

	private ClassOrInterfaceDeclaration getClass(Node node) {
		if (node != null) {
			if (node instanceof ClassOrInterfaceDeclaration) {
				return (ClassOrInterfaceDeclaration)node;
			} else {
				return getClass(node.getParentNode().get());
			}
		} else {
			return null;
		}
	}
	
	private void change(Recommendation recommendation, Expression right, VariableDeclarator declarator) {
		if (right.isObjectCreationExpr()) {
			ObjectCreationExpr creationExpr = right.asObjectCreationExpr();
			String chosenRecommendation = recommendation.getOrderedRecommendations().get(recommendation.getChosenRecommendation()-1);
			this.recommendationMap.transformExpression(creationExpr, chosenRecommendation, declarator);
		}
	}
}