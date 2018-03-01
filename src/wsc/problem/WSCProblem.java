package wsc.problem;

import java.util.Arrays;

import ec.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.*;
import wsc.InitialWSCPool;
import wsc.ecj.nsga2.SequenceVectorIndividual;
import wsc.graph.ServiceGraph;

public class WSCProblem extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;

	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation,
			final int threadnum) {
		if (ind.evaluated)
			return;

		if (!(ind instanceof SequenceVectorIndividual))
			state.output.fatal("Whoa!  It's not a SequenceVectorIndividual!!!", null);

		SequenceVectorIndividual ind2 = (SequenceVectorIndividual) ind;
		WSCInitializer init = (WSCInitializer) state.initializer;

		// use ind2 to generate graph
		InitialWSCPool.getServiceCandidates().clear();
		InitialWSCPool.setServiceCandidates(Arrays.asList(ind2.genome));

		ServiceGraph ind2_graph = init.graGenerator.generateGraphBySerQueue();
		ind2.setStrRepresentation(ind2_graph.toString());
		// evaluate updated updated_graph
		init.eval.aggregationAttribute(ind2, ind2_graph);

		((MultiObjectiveFitness) ind2.fitness).setObjectives(state, init.eval.calculateFitness(ind2));
		ind2.evaluated = true;
	}
}