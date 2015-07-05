/**
 * 
 */
package neurogenesis;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;

/**
 * @author bob
 *
 */
public class NeurogenesisBuilder implements ContextBuilder<Object> {

	/**
	 * 
	 */
	public static final int BRAIN_GRID_QUADRANT_SIZE = 3;
	
	
	/**
	 * 
	 */
	public static final int BRAIN_GRID_SIZE = 2 * BRAIN_GRID_QUADRANT_SIZE + 1;

	
	/**
	 * 
	 */
	public static final int BRAIN_GRID_ORIGIN = BRAIN_GRID_QUADRANT_SIZE;
	

//	@SuppressWarnings("rawtypes")
//	public Context build(Context<Object> context) {
//
//		context.setId("neurogenesis");
//		
//		NetworkBuilder<Object> netBuilder = 
//				new NetworkBuilder<Object>("neural network", context, true);
//		Network<Object> neuralNetwork = netBuilder.buildNetwork();
//		
//		ContinuousSpaceFactory spaceFactory =
//				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
//		ContinuousSpace<Object> arenaSpace =
//				spaceFactory.createContinuousSpace("arena space", context,
//						new RandomCartesianAdder<Object>(),
//						new repast.simphony.space.continuous.InfiniteBorders<Object>(),
//						new double[]{50, 50}, new double[]{25, 25});
//		
//		ContinuousSpace<Object> brainSpace =
//				spaceFactory.createContinuousSpace("brain space", context,
//						new RandomCartesianAdder<Object>(),
//						new repast.simphony.space.continuous.InfiniteBorders<Object>(),
//						new double[]{50, 50, 50}, new double[]{25, 25, 25});
//		
//		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
//		Grid<Object> brainGrid = gridFactory.createGrid("brain grid", context,
//				new GridBuilderParameters<Object>(new WrapAroundBorders(),
//						new SimpleGridAdder<Object>(),
//						true, new int[]{50, 50, 50}, new int[]{25, 25, 25}));
//		
//		Parameters params = RunEnvironment.getInstance().getParameters();
///*		int zombieCount = (Integer) params.getValue("zombie.count");
//		for (int i = 0; i < zombieCount; i++) {
//			context.add(new Zombie(space, grid));
//		} */
//
//		LightSensor leftLightSensor = new LightSensor(arenaSpace, Math.PI / 12);
//		LightSensor rightLightSensor = new LightSensor(arenaSpace, Math.PI / -12);
//		context.add(leftLightSensor);
//		context.add(rightLightSensor);
//
//		LightSensor[] lightSensors = 
//				new LightSensor[] { leftLightSensor, rightLightSensor };
//		
//		Robot robot = new Robot(3, 0, Math.PI / 12, 0, lightSensors);
//		context.add(robot);
//		arenaSpace.moveTo(robot, 0, 0);
//		
//		LightSource lightSource = new LightSource(arenaSpace, 10, Math.PI, Math.PI / 36, 10);
//		context.add(lightSource);
//		arenaSpace.moveTo(lightSource, 
//				lightSource.getRadiusOfTrajectory() 
//				* Math.cos(lightSource.getAngularPosition()), 
//				lightSource.getRadiusOfTrajectory() 
//				* Math.sin(lightSource.getAngularPosition()));
//		
//		InputNeuron leftInputNeuron = 
//				new InputNeuron(neuralNetwork, leftLightSensor, arenaSpace, brainGrid);
//		context.add(leftInputNeuron);
//		InputNeuron rightInputNeuron = 
//				new InputNeuron(neuralNetwork, rightLightSensor, arenaSpace, brainGrid);
//		context.add(rightInputNeuron);
//		
//		OutputNeuron motorNeuron = new OutputNeuron(neuralNetwork, arenaSpace, brainGrid);
//		context.add(motorNeuron);
//		neuralNetwork.addEdge(leftInputNeuron, motorNeuron, 1);
//		neuralNetwork.addEdge(rightInputNeuron, motorNeuron, -1);
//		
//		ArenaSimulator arenaSimulator = 
//				new ArenaSimulator(robot, lightSource, motorNeuron);
//		context.add(arenaSimulator);
//		
//		arenaSpace.moveTo(leftLightSensor, 
//				robot.getRadius() 
//				* Math.cos(robot.getAngularPosition(leftLightSensor)), 
//				robot.getRadius() 
//				* Math.sin(robot.getAngularPosition(leftLightSensor)));
//		arenaSpace.moveTo(rightLightSensor, 
//				robot.getRadius() 
//				* Math.cos(robot.getAngularPosition(rightLightSensor)), 
//				robot.getRadius() 
//				* Math.sin(robot.getAngularPosition(rightLightSensor)));
//				
//		brainSpace.moveTo(motorNeuron, 0, 0, 0);
//		brainSpace.moveTo(leftInputNeuron, -2, -2, 0);
//		brainSpace.moveTo(rightInputNeuron, 2, -2, 0);
//		
//		return context;
//	}

	@SuppressWarnings("rawtypes")
	public Context build(Context<Object> context) {

		context.setId("neurogenesis");
		
		NetworkBuilder<Object> netBuilder = 
				new NetworkBuilder<Object>("neural network", context, true);
		Network<Object> neuralNetwork = netBuilder.buildNetwork();
		
		ContinuousSpaceFactory spaceFactory =
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> arenaSpace =
				spaceFactory.createContinuousSpace("arena space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.InfiniteBorders<Object>(),
						new double[]{50, 50}, new double[]{25, 25});
		
		ContinuousSpace<Object> brainSpace =
				spaceFactory.createContinuousSpace("brain space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.StrictBorders(),
						new double[] { BRAIN_GRID_SIZE, 
							BRAIN_GRID_SIZE, BRAIN_GRID_SIZE }, 
						new double[] { BRAIN_GRID_ORIGIN, 
							BRAIN_GRID_ORIGIN, BRAIN_GRID_ORIGIN });
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> brainGrid = gridFactory.createGrid("brain grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new SimpleGridAdder<Object>(), true, 
						new int[] { BRAIN_GRID_SIZE, 
							BRAIN_GRID_SIZE, BRAIN_GRID_SIZE }, 
						new int[] { BRAIN_GRID_ORIGIN, 
							BRAIN_GRID_ORIGIN, BRAIN_GRID_ORIGIN }));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
/*		int zombieCount = (Integer) params.getValue("zombie.count");
		for (int i = 0; i < zombieCount; i++) {
			context.add(new Zombie(space, grid));
		} */

		/* Setup the arena */
		
		LightSensor leftLightSensor = new LightSensor(arenaSpace, Math.PI / 12);
		LightSensor rightLightSensor = new LightSensor(arenaSpace, Math.PI / -12);
		context.add(leftLightSensor);
		context.add(rightLightSensor);

		LightSensor[] lightSensors = 
				new LightSensor[] { leftLightSensor, rightLightSensor };
		
		Robot robot = new Robot(3, 0, Math.PI / 12, 0, lightSensors);
		context.add(robot);
		arenaSpace.moveTo(robot, 0, 0);
		
		LightSource lightSource = new LightSource(arenaSpace, 10, Math.PI, Math.PI / 36, 10);
		context.add(lightSource);
		arenaSpace.moveTo(lightSource, 
				lightSource.getRadiusOfTrajectory() 
				* Math.cos(lightSource.getAngularPosition()), 
				lightSource.getRadiusOfTrajectory() 
				* Math.sin(lightSource.getAngularPosition()));
		
		/* Setup the initial input and output cells */
		
		GeneticElement foodElement = new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_FOOD, 0, 0, 1);
		
		InputNeuron leftInputNeuron = 
				new InputNeuron(brainSpace, brainGrid, neuralNetwork, leftLightSensor, foodElement);
		context.add(leftInputNeuron);
		InputNeuron rightInputNeuron = 
				new InputNeuron(brainSpace, brainGrid, neuralNetwork, rightLightSensor, foodElement);
		context.add(rightInputNeuron);
		
		OutputNeuron motorNeuron = new OutputNeuron(brainSpace, brainGrid, neuralNetwork);
		context.add(motorNeuron);
		//neuralNetwork.addEdge(leftInputNeuron, motorNeuron, 1);
		//neuralNetwork.addEdge(rightInputNeuron, motorNeuron, -1);
		
		ArenaSimulator arenaSimulator = 
				new ArenaSimulator(robot, lightSource, motorNeuron);
		context.add(arenaSimulator);
		
		arenaSpace.moveTo(leftLightSensor, 
				robot.getRadius() 
				* Math.cos(robot.getAngularPosition(leftLightSensor)), 
				robot.getRadius() 
				* Math.sin(robot.getAngularPosition(leftLightSensor)));
		arenaSpace.moveTo(rightLightSensor, 
				robot.getRadius() 
				* Math.cos(robot.getAngularPosition(rightLightSensor)), 
				robot.getRadius() 
				* Math.sin(robot.getAngularPosition(rightLightSensor)));
				
		brainSpace.moveTo(motorNeuron, 0, -BRAIN_GRID_QUADRANT_SIZE, 0);
		final int offsetInputNeuronPos = BRAIN_GRID_SIZE / 3;
		brainSpace.moveTo(leftInputNeuron, 
				BRAIN_GRID_QUADRANT_SIZE - offsetInputNeuronPos, 0, 
				BRAIN_GRID_QUADRANT_SIZE);
		brainSpace.moveTo(rightInputNeuron, 
				-BRAIN_GRID_QUADRANT_SIZE + offsetInputNeuronPos, 0, 
				BRAIN_GRID_QUADRANT_SIZE);
		
		/* Setup the gene regulatory network */
		
		// Cellular growth regulatory unit.
		
		GeneticElement cisElementFood = new GeneticElement(GeneticElement.ELEMENT_TYPE_CIS, 10, 10, 1);
		GeneticElement transElementGrowth = new GeneticElement(GeneticElement.ELEMENT_TYPE_TRANS, 10, 10, 1);
		
		RegulatoryUnit growthUnit = new RegulatoryUnit(new GeneticElement[] { cisElementFood, transElementGrowth});
		
		GeneticElement cellDivisionRegulator = 
				new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 10, 10, 1);
		
		GeneticElement cellDeathRegulator = 
				new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 5, 5, 1);
		
		GeneticElement SAMregulator = new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 5, 5, 1);
		
		RegulatoryNetwork alphaNetwork = new RegulatoryNetwork(
				new RegulatoryUnit[] { growthUnit }, 
				new GeneticElement[] { cellDivisionRegulator });
		
		UndifferentiatedCell motherCell = new UndifferentiatedCell(brainSpace, brainGrid, alphaNetwork, 
				cellDivisionRegulator, null); //cellDeathRegulator);
		context.add(motherCell);
		brainSpace.moveTo(motherCell,  0, 0, 0);
		
		for (Object obj : context) {
			NdPoint pt = brainSpace.getLocation(obj);
			brainGrid.moveTo(obj, (int) pt.getX(), (int) pt.getY(), (int) pt.getZ());
		}
		
		for (int x = -BRAIN_GRID_QUADRANT_SIZE; x <= BRAIN_GRID_QUADRANT_SIZE; x++) {
			for (int y = -BRAIN_GRID_QUADRANT_SIZE; y <= BRAIN_GRID_QUADRANT_SIZE; y++) {
				for (int z = -BRAIN_GRID_QUADRANT_SIZE; z <= BRAIN_GRID_QUADRANT_SIZE; z++) {
					ExtracellularMatrix morphogenesList = new ExtracellularMatrix(brainSpace, brainGrid);
					context.add(morphogenesList);
					brainSpace.moveTo(morphogenesList, x, y, z);
					brainGrid.moveTo(morphogenesList, x, y, z);
//					if (x == -BRAIN_GRID_QUADRANT_SIZE + 1 && y == -BRAIN_GRID_QUADRANT_SIZE + 1 && z == -BRAIN_GRID_QUADRANT_SIZE + 1) {
//						morphogenesList.getConcentrations().put(cellDeathRegulator, 1.0);
//					}
				}
			}
		}
		return context;
	}

}
