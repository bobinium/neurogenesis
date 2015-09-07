/**
 * 
 */
package org.thoughtsfactory.neurogenesis;

import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.brain.CellFactory;
import org.thoughtsfactory.neurogenesis.brain.CellProductType;
import org.thoughtsfactory.neurogenesis.brain.ExtracellularMatrix;
import org.thoughtsfactory.neurogenesis.brain.FoodInputNeuron;
import org.thoughtsfactory.neurogenesis.brain.MotionInputNeuron;
import org.thoughtsfactory.neurogenesis.brain.Neuron;
import org.thoughtsfactory.neurogenesis.brain.OutputNeuron;
import org.thoughtsfactory.neurogenesis.brain.UndifferentiatedCell;

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
	


	
	// INSTANCE VARIABLES ******************************************************
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(NeurogenesisBuilder.class);	

	
	//
	private Grid<Object> brainGrid;
	
	
	//
	private ContinuousSpace<Object> brainSpace;
	
	
	//
	private ContinuousSpace<Object> arenaSpace;
	
	
	//
	private Robot robot;
	
	
	//
	private ArenaSimulator arenaSimulator;
	
	
	//
	private Network<Object> neuralNetwork;
	
	
	//
	private Network<Object> neuritesNetwork;
	
	
	//
	private FoodInputNeuron leftLightInputNeuron;
	
	
	//
	private FoodInputNeuron rightLightInputNeuron;
	
	
	//
	private MotionInputNeuron leftMotionInputNeuron;
	
	
	//
	private MotionInputNeuron rightMotionInputNeuron;
	
	
	//
	private OutputNeuron leftMotorNeuron;
	

	//
	private OutputNeuron rightMotorNeuron;
	

	//
	private boolean testSetup;
	
	
	//
	private int brainGridQuadrantSize;
	
	
	//
	private int brainGridSize;
	
	
	//
	private int brainGridOrigin;
	
	
	//
	private int initialPopulationExtent;
	
	
	//
	private double initialMatrixFoodConcentration;
	
	

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
		
		// Get the parameters of the model.
		initialiseParameters();
		
		SimulationContextHolder simulationContext = 
				SimulationContextHolder.getInstance();
		
		ContinuousSpaceFactory spaceFactory =
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		
		this.arenaSpace =
				spaceFactory.createContinuousSpace("arena space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous
								.InfiniteBorders<Object>(),
						new double[]{50, 50}, new double[]{25, 25});
		
		this.brainSpace =
				spaceFactory.createContinuousSpace("brain space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.StrictBorders(),
						new double[] { this.brainGridSize, this.brainGridSize, 
							this.brainGridSize }, 
						new double[] { this.brainGridOrigin, 
							this.brainGridOrigin, this.brainGridOrigin });
		
		simulationContext.setBrainSpace(this.brainSpace);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		this.brainGrid = gridFactory.createGrid("brain grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new SimpleGridAdder<Object>(), true, 
						new int[] { this.brainGridSize, this.brainGridSize, 
							this.brainGridSize }, 
						new int[] { this.brainGridOrigin, this.brainGridOrigin, 
							this.brainGridOrigin }));
		
		simulationContext.setBrainGrid(this.brainGrid);

		NetworkBuilder<Object> netBuilder = 
				new NetworkBuilder<Object>("neural network", context, true);
		this.neuralNetwork = netBuilder.buildNetwork();
		simulationContext.setNeuralNetwork(this.neuralNetwork);
		
		netBuilder = new NetworkBuilder<Object>("neurites network", 
				context, true);
		this.neuritesNetwork = netBuilder.buildNetwork();
		simulationContext.setNeuritesNetwork(this.neuritesNetwork);

		// Initialisation has to be in this order because of dependencies.
		setupArena(context);
		setupOutputNeuron(context);
		setupLightInputNeurons(context);
		
		if (this.testSetup) {
			setupTestModel(context);
		} else {
			setupMotionInputNeurons(context);
			//setupTestNeuronPair(context);
			setupInitialPopulation(context);
		}
		
		setupInitialEnvironment(context);
		
		RunEnvironment.getInstance().endAt(10000);
		
		return context;
		
	} // End of build()

	
	/**
	 * 
	 */
	private void initialiseParameters() {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		Configuration config = Configuration.getInstance();
		
		this.testSetup = params.getBoolean("simulation.test");
		
		config.setGenomeSize(params.getInteger("genome.size"));
		
		this.brainGridQuadrantSize = params.getInteger("quadrant.size");
		this.brainGridSize = 2 * this.brainGridQuadrantSize + 1;
		this.brainGridOrigin = this.brainGridQuadrantSize;

		this.initialPopulationExtent = params.getInteger("population.extent");
		this.initialMatrixFoodConcentration = 
				params.getDouble("matrix.food.concentration");
		
		Neuron.MAX_DENDRITE_ROOTS = params.getInteger("dendrites.roots.max");
		Neuron.MAX_DENDRITE_LEAVES = params.getInteger("dendrites.leaves.max");
		Neuron.LEARNING_RATE = params.getDouble("neuron.learning.rate");
		
		config.setCellAdhesionEnabled(
				params.getBoolean("cell.adhesion.enabled"));
		
	} // End of initialiseParameters()

	
	/**
	 * 
	 * @param context
	 */
	private void setupArena(final Context<Object> context) {
		
		this.robot = new Robot(3, this.arenaSpace, Math.PI / 12);
		this.robot.setUp(context);
		
		LightSource lightSource = 
				new LightSource(this.arenaSpace, 10, Math.PI, Math.PI / 36, 10);
		context.add(lightSource);
		this.arenaSpace.moveTo(lightSource, 
				lightSource.getRadiusOfTrajectory() 
				* Math.cos(lightSource.getAngularPosition()), 
				lightSource.getRadiusOfTrajectory() 
				* Math.sin(lightSource.getAngularPosition()));
		
		this.arenaSimulator =  new ArenaSimulator(this.robot, lightSource);
		context.add(this.arenaSimulator);
		
	} // End of setupArena()
	
	
	/**
	 * 
	 */
	private void setupLightInputNeurons(final Context<Object> context) {
		
		this.leftLightInputNeuron = new FoodInputNeuron(this.brainSpace, 
				this.brainGrid,	null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getLeftLightSensor());
		context.add(this.leftLightInputNeuron);
		
		this.rightLightInputNeuron = new FoodInputNeuron(this.brainSpace, 
				this.brainGrid,	null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getRightLightSensor());
		context.add(this.rightLightInputNeuron);
		
		final int offsetInputNeuronPos = this.brainGridSize / 3;

		this.brainSpace.moveTo(this.leftLightInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos + 0.5, 0.5, 
				this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.leftLightInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos, 0,
				this.brainGridQuadrantSize);
		
		this.brainSpace.moveTo(this.rightLightInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos + 0.5, 0.5, 
				this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.rightLightInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos, 0,
				this.brainGridQuadrantSize);

	} // End of setupLightInputNeurons()
	
	
	/**
	 * 
	 */
	private void setupMotionInputNeurons(final Context<Object> context) {
		
		final int offsetInputNeuronPos = this.brainGridSize / 3;

		this.leftMotionInputNeuron = new MotionInputNeuron(this.brainSpace, 
				this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getLeftMotionSensor());
		context.add(this.leftMotionInputNeuron);
		
		this.brainSpace.moveTo(this.leftMotionInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos + 0.5, 0.5, 
				-this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.leftMotionInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos, 0, 
				-this.brainGridQuadrantSize);
		
		this.rightMotionInputNeuron = new MotionInputNeuron(this.brainSpace, 
				this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getRightMotionSensor());
		context.add(this.rightMotionInputNeuron);
		
		this.brainSpace.moveTo(this.rightMotionInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos + 0.5, 0.5, 
				-this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.rightMotionInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos, 0, 
				-this.brainGridQuadrantSize);

	} // End of setupMotionInputNeurons()

	
	/**
	 * 
	 * @param context
	 */
	private void setupOutputNeuron(final Context<Object> context) {
		
		final int offsetOutputNeuronPos = this.brainGridSize / 3;

		this.leftMotorNeuron = new OutputNeuron(this.brainSpace, 
				this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getLeftMotor());
		context.add(this.leftMotorNeuron);
		
		this.brainSpace.moveTo(leftMotorNeuron, 
				this.brainGridQuadrantSize - offsetOutputNeuronPos + 0.5, 
				-this.brainGridQuadrantSize + 0.5, 0.5);
		this.brainGrid.moveTo(leftMotorNeuron, 
				this.brainGridQuadrantSize - offsetOutputNeuronPos, 
				-this.brainGridQuadrantSize, 0);

		this.rightMotorNeuron = new OutputNeuron(this.brainSpace, 
				this.brainGrid,	null, this.neuralNetwork, this.neuritesNetwork, 
				this.robot.getRightMotor());
		context.add(this.rightMotorNeuron);
		
		this.brainSpace.moveTo(rightMotorNeuron, 
				-this.brainGridQuadrantSize + offsetOutputNeuronPos + 0.5, 
				-this.brainGridQuadrantSize + 0.5, 0.5);
		this.brainGrid.moveTo(rightMotorNeuron,
				-this.brainGridQuadrantSize + offsetOutputNeuronPos, 
				-this.brainGridQuadrantSize, 0);

	} // End of setupOutputNeurons()

	
	/**
	 * 
	 * @param context
	 */
	private void setupInitialEnvironment(final Context<Object> context) {
		
		logger.info("Initialising extracellular matrix.");
		
		for (int x = -this.brainGridQuadrantSize; 
				x <= this.brainGridQuadrantSize; x++) {
			
			for (int y = -this.brainGridQuadrantSize; 
					y <= this.brainGridQuadrantSize; y++) {
				
				for (int z = -this.brainGridQuadrantSize; 
						z <= this.brainGridQuadrantSize; z++) {
					
					ExtracellularMatrix extracellularMatrix = 
							new ExtracellularMatrix(this.brainSpace, 
									this.brainGrid);
					
					Map<CellProductType, Double> concentrations = 
							extracellularMatrix.getConcentrations();
					
					concentrations.put(CellProductType.FOOD, 
							this.initialMatrixFoodConcentration);
					//concentrations.put(CellProduct.MUTAGEN, 0.5);
					
					context.add(extracellularMatrix);
					this.brainSpace.moveTo(extracellularMatrix,
							x + 0.5, y + 0.5, z + 0.5);
					this.brainGrid.moveTo(extracellularMatrix, x, y, z);
					
				} // End of for(z)
				
			} // End of for(y)
			
		} // End of for(x)
		
	} // End of setupInitialEnvironment()
	
	
	/**
	 * 
	 * @param context
	 */
	private void setupInitialPopulation(final Context<Object> context) {
		
		logger.info("Creating initial cell population: extent = " 
				+ this.initialPopulationExtent);
		
		for (int x = -this.initialPopulationExtent; 
				x <= this.initialPopulationExtent; x++) {
			
			for (int y = -this.initialPopulationExtent; 
					y <= this.initialPopulationExtent; y++) {
				
				for (int z = -this.initialPopulationExtent; 
						z <= this.initialPopulationExtent; z++) {
					
					UndifferentiatedCell motherCell = CellFactory
							.getNewUndifferentiatedCell(); 
					motherCell.setGenerationId(x + "," + y + "," + z);
					context.add(motherCell);
					this.brainSpace.moveTo(motherCell, 
							x + 0.5, y + 0.5, z + 0.5);
					this.brainGrid.moveTo(motherCell, x, y, z);

				} // End for(z)
				
			} // End for(y)
			
		} // End for(x)

	} // End of setupInitialPopulation()
	
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unused")
	private void setupTestNeuron(final Context<Object> context) {
		
		Neuron neuron = CellFactory.getNewNeuron();
		
		context.add(neuron);
		this.brainSpace.moveTo(neuron, 0.5, 0.5, 0.5);
		this.brainGrid.moveTo(neuron, 0, 0, 0);

	} // End of setupTestNeuron()
	
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unused")
	private void setupTestNeuronPair(final Context<Object> context) {
		
		final int offsetNeuronPos = this.brainGridSize / 3;

		Neuron neuron1 = CellFactory.getNewNeuron();
		
		context.add(neuron1);
		this.brainSpace.moveTo(neuron1, 
				this.brainGridQuadrantSize - offsetNeuronPos + 0.5, 0.5, 0.5);
		this.brainGrid.moveTo(neuron1, 
				this.brainGridQuadrantSize - offsetNeuronPos, 0, 0);

		Neuron neuron2 = CellFactory.getNewNeuron();
		
		context.add(neuron2);
		this.brainSpace.moveTo(neuron2, 
				-this.brainGridQuadrantSize + offsetNeuronPos + 0.5, 0.5, 0.5);
		this.brainGrid.moveTo(neuron2, 
				-this.brainGridQuadrantSize + offsetNeuronPos, 0, 0);


	} // End of setupTestNeuron()
	
	
	/**
	 * 
	 * @param context
	 */
	private void setupTestModel(final Context<Object> context) {
		
		this.neuralNetwork.addEdge(this.leftLightInputNeuron, 
				this.leftMotorNeuron, 0.5);
		this.neuralNetwork.addEdge(this.leftLightInputNeuron, 
				this.rightMotorNeuron, -0.5);
		
		this.neuralNetwork.addEdge(this.rightLightInputNeuron, 
				this.rightMotorNeuron, 0.5);
		this.neuralNetwork.addEdge(this.rightLightInputNeuron, 
				this.leftMotorNeuron, -0.5);
		
	} // End of setupTestModel)_
	
} // End of NeurogenesisBuilder class
