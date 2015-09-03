/**
 * 
 */
package neurogenesis;

import java.util.Map;

import org.apache.log4j.Logger;

import neurogenesis.brain.CellFactory;
import neurogenesis.brain.CellProductType;
import neurogenesis.brain.ExtracellularMatrix;
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

	
	// INSTANCE VARIABLES ******************************************************
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(NeurogenesisBuilder.class);	

	
	//
	private int genomeSize;
	
	
	//
	private int brainGridQuadrantSize;
	
	
	//
	private int brainGridSize;
	
	
	//
	private int brainGridOrigin;
	
	
	//
	private int outputNeuronGridPosY;
	
	
	//
	private int initialPopulationExtent;
	
	
	//
	private double initialMatrixFoodConcentration;
	
	
	//
	private Grid<Object> brainGrid;
	
	
	//
	private ContinuousSpace<Object> brainSpace;
	
	
	//
	private ContinuousSpace<Object> arenaSpace;
	
	
	//
	private LightSensor leftLightSensor;
	
	
	//
	private LightSensor rightLightSensor;
	
	
	//
	private ArenaSimulator arenaSimulator;
	
	
	//
	private Network<Object> neuralNetwork;
	
	
	//
	private Network<Object> neuritesNetwork;
	
	
	//
	private InputNeuron leftInputNeuron;
	
	
	//
	private InputNeuron rightInputNeuron;
	
	
	//
	private OutputNeuron motorNeuron;
	
	
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

		// Get the parameters of the model.
		initialiseParameters();
		
		context.setId("neurogenesis");
		
		NetworkBuilder<Object> netBuilder = 
				new NetworkBuilder<Object>("neural network", context, true);
		this.neuralNetwork = netBuilder.buildNetwork();
		
		netBuilder = 
				new NetworkBuilder<Object>("neurites network", context, true);
		this.neuritesNetwork = netBuilder.buildNetwork();
		
		ContinuousSpaceFactory spaceFactory =
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		this.arenaSpace =
				spaceFactory.createContinuousSpace("arena space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.InfiniteBorders<Object>(),
						new double[]{50, 50}, new double[]{25, 25});
		
		this.brainSpace =
				spaceFactory.createContinuousSpace("brain space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.StrictBorders(),
						new double[] { this.brainGridSize, 
							this.brainGridSize, this.brainGridSize }, 
						new double[] { this.brainGridOrigin, 
							this.brainGridOrigin, this.brainGridOrigin });
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		this.brainGrid = gridFactory.createGrid("brain grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new SimpleGridAdder<Object>(), true, 
						new int[] { this.brainGridSize, 
							this.brainGridSize, this.brainGridSize }, 
						new int[] { this.brainGridOrigin, 
							this.brainGridOrigin, this.brainGridOrigin }));
		
		CellFactory.setContinuousSpace(this.brainSpace);
		CellFactory.setGrid(this.brainGrid);
		CellFactory.setNeuralNetwork(this.neuralNetwork);
		CellFactory.setNeuritesNetwork(this.neuritesNetwork);

		// Initialisation has to be in this order because of dependencies.
		setupOutputNeuron(context);
		setupArena(context);
		setupInputNeurons(context);
		
		setupInitialPopulation(context);
		setupInitialEnvironment(context);
		
		RunEnvironment.getInstance().endAt(10000);
		
		return context;
		
	} // End of build()

	
	/**
	 * 
	 */
	private void initialiseParameters() {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.genomeSize = params.getInteger("genome.size");
		
		this.brainGridQuadrantSize = params.getInteger("quadrant.size");
		this.brainGridSize = 2 * this.brainGridQuadrantSize + 1;
		this.brainGridOrigin = this.brainGridQuadrantSize;
		this.outputNeuronGridPosY = -this.brainGridQuadrantSize;

		this.initialPopulationExtent = params.getInteger("population.extent");
		this.initialMatrixFoodConcentration = 
				params.getDouble("matrix.food.concentration");
		
		Neuron.MAX_DENDRITE_ROOTS = params.getInteger("dendrites.roots.max");
		Neuron.MAX_DENDRITE_LEAVES = params.getInteger("dendrites.leaves.max");
		Neuron.LEARNING_RATE = params.getDouble("neuron.learning.rate");
		
	} // End of initiliseParameters()

	
	/**
	 * 
	 * @param context
	 */
	private void setupArena(final Context<Object> context) {
		
		this.leftLightSensor = new LightSensor(this.arenaSpace, Math.PI / 12);
		this.rightLightSensor =	new LightSensor(this.arenaSpace, Math.PI / -12);
		context.add(this.leftLightSensor);
		context.add(this.rightLightSensor);

		LightSensor[] lightSensors = new LightSensor[] { 
				this.leftLightSensor, this.rightLightSensor };
		
		Robot robot = new Robot(3, 0, Math.PI / 12, 0, lightSensors);
		context.add(robot);
		this.arenaSpace.moveTo(robot, 0, 0);

		this.arenaSpace.moveTo(this.leftLightSensor, 
				robot.getRadius() 
				* Math.cos(robot.getAngularPosition(this.leftLightSensor)), 
				robot.getRadius() 
				* Math.sin(robot.getAngularPosition(this.leftLightSensor)));
		this.arenaSpace.moveTo(this.rightLightSensor, 
				robot.getRadius() 
				* Math.cos(robot.getAngularPosition(this.rightLightSensor)), 
				robot.getRadius() 
				* Math.sin(robot.getAngularPosition(this.rightLightSensor)));
				
		LightSource lightSource = 
				new LightSource(this.arenaSpace, 10, Math.PI, Math.PI / 36, 10);
		context.add(lightSource);
		this.arenaSpace.moveTo(lightSource, 
				lightSource.getRadiusOfTrajectory() 
				* Math.cos(lightSource.getAngularPosition()), 
				lightSource.getRadiusOfTrajectory() 
				* Math.sin(lightSource.getAngularPosition()));
		
		this.arenaSimulator = 
				new ArenaSimulator(robot, lightSource, this.motorNeuron);
		context.add(arenaSimulator);
		
	} // End of setupArena()
	
	
	/**
	 * 
	 */
	private void setupInputNeurons(final Context<Object> context) {
		
		this.leftInputNeuron = new InputNeuron(this.brainSpace, this.brainGrid, 
				null, this.neuralNetwork, this.neuritesNetwork, 
				this.leftLightSensor);
		context.add(this.leftInputNeuron);
		
		this.rightInputNeuron = new InputNeuron(this.brainSpace, this.brainGrid, 
				null, this.neuralNetwork, this.neuritesNetwork, 
				this.rightLightSensor);
		context.add(this.rightInputNeuron);
		
		final int offsetInputNeuronPos = this.brainGridSize / 3;

		this.brainSpace.moveTo(this.leftInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos + 0.5, 0.5, 
				this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.leftInputNeuron, 
				this.brainGridQuadrantSize - offsetInputNeuronPos, 0,
				this.brainGridQuadrantSize);
		
		this.brainSpace.moveTo(this.rightInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos + 0.5, 0.5, 
				this.brainGridQuadrantSize + 0.5);
		this.brainGrid.moveTo(this.rightInputNeuron, 
				-this.brainGridQuadrantSize + offsetInputNeuronPos, 0,
				this.brainGridQuadrantSize);

	} // End of setupInputNeurons()
	
	
	/**
	 * 
	 * @param context
	 */
	private void setupOutputNeuron(final Context<Object> context) {
		
		this.motorNeuron = new OutputNeuron(this.brainSpace, 
				this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork);
		context.add(this.motorNeuron);
		
		//neuralNetwork.addEdge(leftInputNeuron, motorNeuron, 1);
		//neuralNetwork.addEdge(rightInputNeuron, motorNeuron, -1);
		
		this.brainSpace.moveTo(motorNeuron, OUTPUT_NEURON_GRID_POS_X + 0.5, 
				this.outputNeuronGridPosY + 0.5, 
				OUTPUT_NEURON_GRID_POS_Z + 0.5);
		this.brainGrid.moveTo(motorNeuron, OUTPUT_NEURON_GRID_POS_X, 
				this.outputNeuronGridPosY, OUTPUT_NEURON_GRID_POS_Z);

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
							.getNewUndifferentiatedCell(this.genomeSize); 
					motherCell.setGenerationId(x + "," + y + "," + z);
					context.add(motherCell);
					this.brainSpace.moveTo(motherCell, 
							x + 0.5, y + 0.5, z + 0.5);
					this.brainGrid.moveTo(motherCell, x, y, z);

				} // End for(z)
				
			} // End for(y)
			
		} // End for(x)

	} // End of setupInitialPopulation()
	
	
} // End of NeurogenesisBuilder class
