package wsc.problem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.graph.ServiceEdge;

public class WSCEvaluation {

	public void aggregationAttribute(SequenceVectorIndividual individual,
			DirectedGraph<String, ServiceEdge> directedGraph) {

		double a = 1.0;
		double r = 1.0;
		double t = 0.0;
		double c = 0.0;
		double mt = 1.0;
		double dst = 0.0; // Exact Match dst = 1 ; 0 < = dst < = 1

		// set a, r, c aggregation
		Set<String> verticeSet = directedGraph.vertexSet();

		// Map<String, double[]> SerQoSMap = serviceQoSMap;

		for (String v : verticeSet) {
			if (!v.equals("startNode") && !v.equals("endNode")) {
				double qos[] = WSCInitializer.serviceQoSMap.get(v);
				a *= qos[WSCInitializer.AVAILABILITY];
				r *= qos[WSCInitializer.RELIABILITY];
				c += qos[WSCInitializer.COST];

			}
		}

		// set time aggregation
		t = getLongestPathVertexList(directedGraph, WSCInitializer.serviceQoSMap);

		// set mt,dst aggregation

		for (ServiceEdge serviceEdge : directedGraph.edgeSet()) {
			mt *= serviceEdge.getAvgmt();
			dst += serviceEdge.getAvgsdt();
		}

		individual.setMatchingType(mt);
		individual.setSemanticDistance(dst / directedGraph.edgeSet().size());
		individual.setAvailability(a);
		individual.setReliability(r);
		individual.setTime(t);
		individual.setCost(c);
	}

	public double[] calculateFitness(SequenceVectorIndividual individual) {

		double mt = individual.getMatchingType();
		double dst = individual.getSemanticDistance();
		double a = individual.getAvailability();
		double r = individual.getReliability();
		double t = individual.getTime();
		double c = individual.getCost();

		mt = normaliseMatchType(mt);
		dst = normaliseDistanceValue(dst);
		a = normaliseAvailability(a);
		r = normaliseReliability(r);
		t = normaliseTime(t);
		c = normaliseCost(c);

		double[] objectives = new double[2];
//		objectives[0] = (WSCInitializer.w3 * a) + (WSCInitializer.w4 * r);
//		objectives[1] = (WSCInitializer.w5 * t) + (WSCInitializer.w6 * c);

		objectives[0] = (WSCInitializer.w1 * mt) + (WSCInitializer.w2 * dst);
		objectives[1] = (WSCInitializer.w3 * a) + (WSCInitializer.w4 * r) + (WSCInitializer.w5 * t)
				+ (WSCInitializer.w6 * c);

		return objectives;
	}

	private double normaliseMatchType(double matchType) {
		if (WSCInitializer.MAXINUM_MATCHTYPE - WSCInitializer.MINIMUM_MATCHTYPE == 0.0)
			return 1.0;
		else
			return (WSCInitializer.MAXINUM_MATCHTYPE - matchType)
					/ (WSCInitializer.MAXINUM_MATCHTYPE - WSCInitializer.MINIMUM_MATCHTYPE);
	}

	private double normaliseDistanceValue(double distanceValue) {
		if (WSCInitializer.MAXINUM_SEMANTICDISTANCE - WSCInitializer.MININUM_SEMANTICDISTANCE == 0.0)
			return 1.0;
		else
			return (WSCInitializer.MAXINUM_SEMANTICDISTANCE - distanceValue)
					/ (WSCInitializer.MAXINUM_SEMANTICDISTANCE - WSCInitializer.MININUM_SEMANTICDISTANCE);
	}

	public double normaliseAvailability(double availability) {
		if (WSCInitializer.MAXIMUM_AVAILABILITY - WSCInitializer.MINIMUM_AVAILABILITY == 0.0)
			return 1.0;
		else
			return (WSCInitializer.MAXIMUM_AVAILABILITY - availability)
					/ (WSCInitializer.MAXIMUM_AVAILABILITY - WSCInitializer.MINIMUM_AVAILABILITY);
	}

	public double normaliseReliability(double reliability) {
		if (WSCInitializer.MAXIMUM_RELIABILITY - WSCInitializer.MINIMUM_RELIABILITY == 0.0)
			return 1.0;
		else
			return (WSCInitializer.MAXIMUM_RELIABILITY - reliability)
					/ (WSCInitializer.MAXIMUM_RELIABILITY - WSCInitializer.MINIMUM_RELIABILITY);
	}

	public double normaliseTime(double time) {
		if (WSCInitializer.MAXIMUM_TIME - WSCInitializer.MINIMUM_TIME == 0.0)
			return 1.0;
		else
			return (time - WSCInitializer.MINIMUM_TIME) / (WSCInitializer.MAXIMUM_TIME - WSCInitializer.MINIMUM_TIME);
	}

	public double normaliseCost(double cost) {
		if (WSCInitializer.MAXIMUM_COST - WSCInitializer.MINIMUM_COST == 0.0)
			return 1.0;
		else
			return (cost - WSCInitializer.MINIMUM_COST) / (WSCInitializer.MAXIMUM_COST - WSCInitializer.MINIMUM_COST);
	}

	public static double getLongestPathVertexList(DirectedGraph<String, ServiceEdge> g,
			Map<String, double[]> serQoSMap) {
		// A algorithm to find all paths
		AllDirectedPaths<String, ServiceEdge> allPath = new AllDirectedPaths<String, ServiceEdge>(g);
		List<GraphPath<String, ServiceEdge>> pathList = allPath.getAllPaths("startNode", "endNode", true, null);
		double maxTime = 0;
		double sumTime;

		for (int i = 0; i < pathList.size(); i++) {

			sumTime = 0;

			// for (String v : Graphs.getPathVertexList(pathList.get(i))) {
			for (String v : (pathList.get(i).getVertexList())) {
				if (!v.equals("startNode") && !v.equals("endNode")) {
					double qos[] = serQoSMap.get(v);
					sumTime += qos[WSCInitializer.TIME];
				}
			}
			if (sumTime > maxTime) {
				maxTime = sumTime;
			}

		}
		// return pathList.get(IndexPathLength).getEdgeList();
		return maxTime;
	}
}
