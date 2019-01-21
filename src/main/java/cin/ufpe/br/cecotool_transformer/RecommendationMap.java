package cin.ufpe.br.cecotool_transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class RecommendationMap {
	/**
	 * The recommended collections from the recommendations file do not have their full path,
	 * they rather have a brief name for more legibility. So variable maps the abbreviation to
	 * its full path
	 */
	private Map<String,String> recommendationsMap;
	/**
	 * Collections that receive a wrapping for thread-safeness.
	 */
	private Map<String,String> wrappedCollections;
	private Set<String> jcfLists;
	private Set<String> jcfMaps;
	private Set<String> jcfSets;
	
	public RecommendationMap() {
		this.recommendationsMap = new HashMap<String,String>();
		this.wrappedCollections = new HashMap<String,String>();
		this.jcfLists = new HashSet<String>();
		this.jcfMaps = new HashSet<String>();
		this.jcfSets = new HashSet<String>();
		
		this.jcfLists.add("ArrayList");
		this.jcfLists.add("java.util.ArrayList");
		this.jcfLists.add("LinkedList");
		this.jcfLists.add("java.util.LinkedList");
		this.jcfLists.add("Vector");
		this.jcfLists.add("java.util.Vector");
		this.jcfLists.add("CopyOnWriteArrayList");
		this.jcfLists.add("java.util.concurrent.CopyOnWriteArrayList");
		
		this.jcfMaps.add("HashMap");
		this.jcfMaps.add("java.util.HashMap");
		this.jcfMaps.add("LinkedHashMap");
		this.jcfMaps.add("java.util.LinkedHashMap");
		this.jcfMaps.add("TreeMap");
		this.jcfMaps.add("java.util.TreeMap");
		this.jcfMaps.add("Hashtable");
		this.jcfMaps.add("java.util.Hashtable");
		this.jcfMaps.add("ConcurrentHashMap");
		this.jcfMaps.add("java.util.concurrent.ConcurrentHashMap");
		
		this.jcfSets.add("HashSet");
		this.jcfSets.add("java.util.HashSet");
		this.jcfSets.add("LinkedHashSet");
		this.jcfSets.add("java.util.LinkedHashSet");
		this.jcfSets.add("TreeSet");
		this.jcfSets.add("java.util.TreeSet");
		
		//synchronized
		this.recommendationsMap.put("vector", "java.util.Vector");
		this.recommendationsMap.put("CopyOnWriteArrayList", "java.util.concurrent.CopyOnWriteArrayList");
		this.recommendationsMap.put("concurrentHashMap", "java.util.concurrent.ConcurrentHashMap");
		this.recommendationsMap.put("hashtable", "java.util.Hashtable");
		this.recommendationsMap.put("concurrentHashMap(EclipseCollections)", "org.eclipse.collections.impl.map.mutable.ConcurrentHashMap");
		
		this.wrappedCollections.put("synchronizedLinkedList", "java.util.Collections.synchronizedList(#linkedList#)");
		this.wrappedCollections.put("synchronizedArrayList", "java.util.Collections.synchronizedList(#arrayList#)");
		this.wrappedCollections.put("synchronizedFastList(EclipseCollections)", "java.util.Collections.synchronizedList(#fastList(EclipseCollections)#)");
		
		this.wrappedCollections.put("synchronizedHashMap", "java.util.Collections.synchronizedMap(#hashMap#)");
		this.wrappedCollections.put("synchronizedTreeMap", "java.util.Collections.synchronizedMap(#treeMap#)");
		this.wrappedCollections.put("synchronizedUnifiedMap(EclipseCollections)", "java.util.Collections.synchronizedMap(#unifiedMap#)");
		this.wrappedCollections.put("synchronizedLinkedHashMap", "java.util.Collections.synchronizedMap(#linkedHashMap#)");
		
		this.wrappedCollections.put("synchronizedHashSet", "java.util.Collections.synchronizedSet(#hashSet#)");
		this.wrappedCollections.put("synchronizedLinkedHashSet", "java.util.Collections.synchronizedSet(#linkedHashSet#)");
		this.wrappedCollections.put("synchronizedTreeSet", "java.util.Collections.synchronizedSet(#treeSet#)");
		this.wrappedCollections.put("synchronizedTreeSortedSet(Eclipse Collections)", "java.util.Collections.synchronizedSet(#treeSortedSet(Eclipse Collections)#)");
		this.wrappedCollections.put("synchronizedUnifiedSet(Eclipse Collections)", "java.util.Collections.synchronizedSet(#unifiedSet(Eclipse Collections)#)");
		
		//non synchronized
		this.recommendationsMap.put("arrayList", "java.util.ArrayList");
		this.recommendationsMap.put("linkedList", "java.util.LinkedList");
		this.recommendationsMap.put("fastList(EclipseCollections)", "org.eclipse.collections.impl.list.mutable.FastList");
		this.recommendationsMap.put("treeList(ApacheCommonsCollections)", "org.apache.commons.collections4.list.TreeList");
		this.recommendationsMap.put("nodeCachingLinkedList(ApacheCommonsCollections)", "org.apache.commons.collections4.list.NodeCachingLinkedList");
		
		this.recommendationsMap.put("hashMap", "java.util.HashMap");
		this.recommendationsMap.put("linkedHashMap", "java.util.LinkedHashMap");
		this.recommendationsMap.put("treeMap", "java.util.TreeMap");
		this.recommendationsMap.put("unifiedMap(EclipseCollections)", "org.eclipse.collections.impl.map.mutable.UnifiedMap");
		this.recommendationsMap.put("hashedMap(ApacheCommonsCollections)", "org.apache.commons.collections4.map.HashedMap");
		
		this.recommendationsMap.put("hashSet", "java.util.HashSet");
		this.recommendationsMap.put("linkedHashSet", "java.util.LinkedHashSet");
		this.recommendationsMap.put("treeSet", "java.util.TreeSet");
		this.recommendationsMap.put("unifiedSet(Eclipse Collections)", "org.eclipse.collections.impl.set.mutable.UnifiedSet");
		this.recommendationsMap.put("treeSortedSet(Eclipse Collections)", "org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet");
	}
	
	public void transformExpression(ObjectCreationExpr expr, String recommendation, VariableDeclarator declarator) {
		NodeList<Type> typeArguments = expr.getType().getTypeArguments().isPresent() ? expr.getType().getTypeArguments().get() : null;
		boolean isWrappedCollection = this.wrappedCollections.containsKey(recommendation);
		String collection = isWrappedCollection ? this.wrappedCollections.get(recommendation) : this.recommendationsMap.get(recommendation);
		if (declarator != null) {
			checkVariableType(declarator);
		}
		if (isWrappedCollection) {
			int indexOfFirstHashtag = collection.indexOf('#');
			int indexOfLastHashtag = collection.lastIndexOf('#');
			String innerCollection = collection.substring(indexOfFirstHashtag+1,indexOfLastHashtag);
			String innerCollectionFullPath = this.recommendationsMap.get(innerCollection);
			expr.setType(new ClassOrInterfaceType(null, new SimpleName(innerCollectionFullPath), typeArguments));
			collection = collection.replace(String.format("#%s#", innerCollection), expr.toString());
			Expression newExpr = JavaParser.parseExpression(collection);
			expr.replace(newExpr);
		} else {
			expr.setType(new ClassOrInterfaceType(null, new SimpleName(collection), typeArguments));
		}
	}
	
	/**
	 * If the variable receiving the recommendation is not from an interface type (List, Set or Map),
	 * change it.
	 * @param declarator
	 */
	private void checkVariableType(VariableDeclarator declarator) {
		ClassOrInterfaceType type = declarator.getType().asClassOrInterfaceType();
		String typeName = type.getName().toString();
		if(!StringUtils.equalsAny(
				typeName,
				"java.util.List",
				"java.util.Map",
				"java.util.Set",
				"List",
				"Map",
				"Set")) {
			NodeList<Type> typeArguments = type.getTypeArguments().isPresent() ? type.getTypeArguments().get() : null;
			if (jcfLists.contains(typeName))
				declarator.setType(new ClassOrInterfaceType(null, new SimpleName("java.util.List"), typeArguments));
			else if (jcfMaps.contains(typeName))
				declarator.setType(new ClassOrInterfaceType(null, new SimpleName("java.util.Map"), typeArguments));
			else if (jcfSets.contains(typeName))
				declarator.setType(new ClassOrInterfaceType(null, new SimpleName("java.util.Set"), typeArguments));				
		}
	}

}
