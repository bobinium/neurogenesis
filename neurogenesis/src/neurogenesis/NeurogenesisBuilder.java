/**
 * 
 */
package neurogenesis;

import java.util.Map;

import neurogenesis.brain.CellFactory;
import neurogenesis.brain.CellProductType;
import neurogenesis.brain.ExtracellularMatrix;
import neurogenesis.brain.GenomeFactory;
import neurogenesis.brain.InputNeuron;
import neurogenesis.brain.Neuron;
import neurogenesis.brain.OutputNeuron;
import neurogenesis.brain.UndifferentiatedCell;
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

	// CONSTANTS ***************************************************************
	
	/**
	 * 
	 */
	//public static final int BRAIN_GRID_QUADRANT_SIZE = 5;
	
	
	/**
	 * 
	 */
	//public static final int BRAIN_GRID_SIZE = 2 * BRAIN_GRID_QUADRANT_SIZE + 1;

	
	/**
	 * 
	 */
	//public static final int BRAIN_GRID_ORIGIN = BRAIN_GRID_QUADRANT_SIZE;
	

	/**
	 * 
	 */
	public static final int MOTHER_CELL_GRID_POS_X = 0;
	
	/**
	 * 
	 */
	public static final int MOTHER_CELL_GRID_POS_Y = 0;
	
	/**
	 * 
	 */
	public static final int MOTHER_CELL_GRID_POS_Z = 0;
	
	/**
	 * 
	 */
	public static final int OUTPUT_NEURON_GRID_POS_X = 0;
	
	/**
	 * 
	 */
	//public static final int OUTPUT_NEURON_GRID_POS_Y = -BRAIN_GRID_QUADRANT_SIZE;
	
	/**
	 * 
	 */
	public static final int OUTPUT_NEURON_GRID_POS_Z = 0;
	
	/**
	 * 
	 */
	public static final int RIGHT_INPUT_NEURON_GRID_POS_X = 0;
	
	/**
	 * 
	 */
	public static final int RIGHT_INPUT_NEURON_GRID_POS_Y = 0;

	/**
	 * 
	 */
	public static final int RIGHT_INPUT_NEURON_GRID_POS_Z = 0;
	
	/**
	 * 
	 */
	public static final int LEFT_INPUT_NEURON_GRID_POS_X = 0;
	
	/**
	 * 
	 */
	public static final int LEFT_INPUT_NEURON_GRID_POS_Y = 0;

	/**
	 * 
	 */
	public static final int LEFT_INPUT_NEURON_GRID_POS_Z = 0;

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

		// Get the paramters of the model.
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		final int paramGenomeSize = (Integer) params.getValue("genome.size");
		
		final int paramBrainGridQuadrantSize = 
				(Integer) params.getValue("quadrant.size");
		final int paramBrainGridSize = 2 * paramBrainGridQuadrantSize + 1;
		final int paramBrainGridOrigin = paramBrainGridQuadrantSize;
		final int paramOutputNeuronGridPosY = -paramBrainGridQuadrantSize;
		
		context.setId("neurogenesis");
		
		NetworkBuilder<Object> netBuilder = 
				new NetworkBuilder<Object>("neural network", context, true);
		Network<Object> neuralNetwork = netBuilder.buildNetwork();
		
		netBuilder = 
				new NetworkBuilder<Object>("neurites network", context, true);
		Network<Object> neuritesNetwork = netBuilder.buildNetwork();
		
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
						new double[] { paramBrainGridSize, 
							paramBrainGridSize, paramBrainGridSize }, 
						new double[] { paramBrainGridOrigin, 
							paramBrainGridOrigin, paramBrainGridOrigin });
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> brainGrid = gridFactory.createGrid("brain grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new SimpleGridAdder<Object>(), true, 
						new int[] { paramBrainGridSize, 
							paramBrainGridSize, paramBrainGridSize }, 
						new int[] { paramBrainGridOrigin, 
							paramBrainGridOrigin, paramBrainGridOrigin }));
		
		CellFactory.setContinuousSpace(brainSpace);
		CellFactory.setGrid(brainGrid);
		CellFactory.setNeuralNetwork(neuralNetwork);
		CellFactory.setNeuritesNetwork(neuritesNetwork);
		
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
		
		InputNeuron leftInputNeuron = 
				new InputNeuron(brainSpace, brainGrid, null, neuralNetwork, 
						neuritesNetwork, leftLightSensor);
		//context.add(leftInputNeuron);
		
		InputNeuron rightInputNeuron = 
				new InputNeuron(brainSpace, brainGrid, null, neuralNetwork, 
						neuritesNetwork, rightLightSensor);
		//context.add(rightInputNeuron);
		
		OutputNeuron motorNeuron = new OutputNeuron(brainSpace, brainGrid, 
				null, neuralNetwork, neuritesNetwork);
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
				
		brainSpace.moveTo(motorNeuron, OUTPUT_NEURON_GRID_POS_X + 0.5, 
				paramOutputNeuronGridPosY + 0.5, OUTPUT_NEURON_GRID_POS_Z + 0.5);
		brainGrid.moveTo(motorNeuron, OUTPUT_NEURON_GRID_POS_X, 
				paramOutputNeuronGridPosY, OUTPUT_NEURON_GRID_POS_Z);
		
		final int offsetInputNeuronPos = paramBrainGridSize / 3;
		
//		brainSpace.moveTo(leftInputNeuron, 
//				paramBrainGridQuadrantSize - offsetInputNeuronPos + 0.5, 0.5, 
//				paramBrainGridQuadrantSize + 0.5);
//		brainGrid.moveTo(leftInputNeuron, 
//				paramBrainGridQuadrantSize - offsetInputNeuronPos, 0,
//				paramBrainGridQuadrantSize);
//		
//		brainSpace.moveTo(rightInputNeuron, 
//				-paramBrainGridQuadrantSize + offsetInputNeuronPos + 0.5, 0.5, 
//				paramBrainGridQuadrantSize + 0.5);
//		brainGrid.moveTo(rightInputNeuron, 
//				-paramBrainGridQuadrantSize + offsetInputNeuronPos, 0,
//				paramBrainGridQuadrantSize);
		
		/* Setup the gene regulatory network */
		
		Neuron.count = 0;
		
//		Neuron neuron1 = CellFactory.getNewNeuron(paramGenomeSize);
//		context.add(neuron1);
//		brainSpace.moveTo(neuron1, paramBrainGridQuadrantSize - offsetInputNeuronPos + 0.5, 0.5, 0.5);
//		brainGrid.moveTo(neuron1, paramBrainGridQuadrantSize - offsetInputNeuronPos, 0, 0);
		
//		Neuron neuron2 = CellFactory.getNewNeuron(paramGenomeSize);
//		context.add(neuron2);
//		brainSpace.moveTo(neuron2, -paramBrainGridQuadrantSize + offsetInputNeuronPos + 0.5, 0.5, 0.5);
//		brainGrid.moveTo(neuron2, -paramBrainGridQuadrantSize + offsetInputNeuronPos, 0, 0);
		
//		final int s = 2;
//		for (int x = -s; x <= s; x++) {
//			
//			for (int y = -s; y <= s; y++) {
//				
//				for (int z = -s; z <= s; z++) {
//					
//					UndifferentiatedCell motherCell = CellFactory
//							.getNewUndifferentiatedCell(paramGenomeSize); 
//					motherCell.setGenerationId(x + "," + y + "," + z);
//					context.add(motherCell);
//					brainSpace.moveTo(motherCell, x + 0.5, y + 0.5, z + 0.5);
//					brainGrid.moveTo(motherCell, x, y, z);
//
//				}
//				
//			}
//			
//		}
		
//		GenomeFactory genomeFactory = new GenomeFactory();
//		Neuron neuron = new Neuron(brainSpace, brainGrid, 
//				genomeFactory.getNewGenome(), neuritesNetwork, neuritesNetwork);
//		context.add(neuron);
//		brainSpace.moveTo(neuron, 0, 0, 0);
		
//		UndifferentiatedCell motherCell1 = new UndifferentiatedCell(brainSpace, 
//				brainGrid, genomeFactory.getNewGenome());
//		context.add(motherCell1);
//		brainSpace.moveTo(motherCell1, MOTHER_CELL_GRID_POS_X + 0.5,
//				MOTHER_CELL_GRID_POS_Y + 0.5, MOTHER_CELL_GRID_POS_Y + 0.5);
				
//		for (Object obj : context) {
//			NdPoint pt = brainSpace.getLocation(obj);
//			brainGrid.moveTo(obj, (int) pt.getX(), (int) pt.getY(), (int) pt.getZ());
//		}
		
		for (int x = -paramBrainGridQuadrantSize; x <= paramBrainGridQuadrantSize; x++) {
			
			for (int y = -paramBrainGridQuadrantSize; y <= paramBrainGridQuadrantSize; y++) {
				
				for (int z = -paramBrainGridQuadrantSize; z <= paramBrainGridQuadrantSize; z++) {
					
					ExtracellularMatrix extracellularMatrix = 
							new ExtracellularMatrix(brainSpace, brainGrid);
					
					Map<CellProductType, Double> concentrations = 
							extracellularMatrix.getConcentrations();
					
					concentrations.put(CellProductType.FOOD, 0.2);
					//concentrations.put(CellProduct.MUTAGEN, 0.5);
					
					context.add(extracellularMatrix);
					brainSpace.moveTo(extracellularMatrix,
							x + 0.5, y + 0.5, z + 0.5);
					brainGrid.moveTo(extracellularMatrix, x, y, z);
					
				} // End of for(z)
				
			} // End of for(y)
			
		} // End of for(x)
		
		return context;
	}

} // End of NeurogenesisBuilder class
