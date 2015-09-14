package org.thoughtsfactory.neurogenesis;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.brain.CellFactory;
import org.thoughtsfactory.neurogenesis.brain.CellProductType;
import org.thoughtsfactory.neurogenesis.brain.DistributedExtracellularMatrix;
import org.thoughtsfactory.neurogenesis.brain.ExtracellularMatrix;
import org.thoughtsfactory.neurogenesis.brain.FoodInputNeuron;
import org.thoughtsfactory.neurogenesis.brain.InputNeuron;
import org.thoughtsfactory.neurogenesis.brain.MotionInputNeuron;
import org.thoughtsfactory.neurogenesis.brain.Neuron;
import org.thoughtsfactory.neurogenesis.brain.OutputNeuron;
import org.thoughtsfactory.neurogenesis.brain.TestInputNeuron;
import org.thoughtsfactory.neurogenesis.brain.TestOutputNeuron;
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
 * This object has the task of initialising the whole simulation.
 * 
 * @author Robert Langlois
 */
public class NeurogenesisBuilder implements ContextBuilder<Object> {

    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger = 
            Logger.getLogger(NeurogenesisBuilder.class);    

    
    // The brain three dimensional Cartesian grid.
    private Grid<Object> brainGrid;
    
    
    // The continuous space from which all visual brain objects are displayed.
    private ContinuousSpace<Object> brainSpace;
    
    
    // The continuous space from which all visual arena objects are displayed.
    private ContinuousSpace<Object> arenaSpace;
    
    
    // The virtual robot.
    private Robot robot;
    
    
    // The arena supervisor.
    private ArenaSupervisor arenaSupervisor;
    
    
    // The network of all neurons.
    private Network<Object> neuralNetwork;
    
    
    // The network of all neurites.
    private Network<Object> neuritesNetwork;
    
    
    // The left light input neuron.
    private InputNeuron leftLightInputNeuron;
    
    
    // The right light input neuron.
    private InputNeuron rightLightInputNeuron;
    
    
    // The left motion input neuron.
    private MotionInputNeuron leftMotionInputNeuron;
    
    
    // The right motion input neuron.
    private MotionInputNeuron rightMotionInputNeuron;
    
    
    // The left motor neuron.
    private OutputNeuron leftMotorNeuron;
    

    // The right motor neuron.
    private OutputNeuron rightMotorNeuron;
    

    // Indicate if we are initialising the control test.
    private boolean controlTestSetup;
    
    
    // The brain grid quadrant size.
    private int brainGridQuadrantSize;
    
    
    // The brain grid size.
    private int brainGridSize;
    
    
    // The brain grid origin.
    private int brainGridOrigin;
    
    
    // The initial population extent into the brain grid.
    private int initialPopulationExtent;
    
    
    // The initial external food concentration.
    private double initialMatrixFoodConcentration;
    

    // METHODS =================================================================
    
    
    /**
     * Main builder method called by Repast.
     */
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

        setupArena(context);
        
        if (this.controlTestSetup) {
            
            setupControlTest(context);
            
        } else {
            
            // Initialisation has to be in this order because of dependencies.
            setupOutputNeuron(context);
            setupLightInputNeurons(context);
            setupMotionInputNeurons(context);
            //setupTestNeuronPair(context);

            setupInitialPopulation(context);
            
        } // End if()
        
        setupInitialEnvironment(context);
        
        RunEnvironment.getInstance().endAt(20000);
        
        return context;
        
    } // End of build()

    
    /**
     * Retrieves all parameters set by the user.
     */
    private void initialiseParameters() {
        
        Parameters params = RunEnvironment.getInstance().getParameters();
        
        Configuration config = Configuration.getInstance();
        
        this.controlTestSetup = params.getBoolean("simulation.test");
        
        config.setGenomeSize(params.getInteger("genome.size"));
        
        this.brainGridQuadrantSize = params.getInteger("quadrant.size");
        this.brainGridSize = 2 * this.brainGridQuadrantSize + 1;
        this.brainGridOrigin = this.brainGridQuadrantSize;

        config.setBrainGridQuadrantSize(this.brainGridQuadrantSize);
        
        this.initialPopulationExtent = this.brainGridQuadrantSize 
                * params.getInteger("population.extent.percent") / 100;
        //this.initialPopulationExtent = params.getInteger("population.extent");
        this.initialMatrixFoodConcentration = 
                params.getDouble("matrix.food.concentration");
        
        Neuron.MAX_DENDRITE_ROOTS = params.getInteger("dendrites.roots.max");
        Neuron.MAX_DENDRITE_LEAVES = params.getInteger("dendrites.leaves.max");
        Neuron.LEARNING_RATE = params.getDouble("neuron.learning.rate");
        
        config.setCellAdhesionEnabled(
                params.getBoolean("cell.adhesion.enabled"));
        
    } // End of initialiseParameters()

    
    /**
     * Setup the arena.
     * 
     * @param context The Repast simulation context.
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
        
        this.arenaSupervisor =  new ArenaSupervisor(this.robot, lightSource);
        context.add(this.arenaSupervisor);
        
    } // End of setupArena()
    
    
    /**
     * Wires the light sensors.
     * 
     * @param context The Repast simulation context.
     */
    private void setupLightInputNeurons(final Context<Object> context) {
        
        this.leftLightInputNeuron = new FoodInputNeuron("Left",
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getLeftLightSensor());
        context.add(this.leftLightInputNeuron);
        
        this.rightLightInputNeuron = new FoodInputNeuron("Right",
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getRightLightSensor());
        context.add(this.rightLightInputNeuron);
        
        final int offsetInputNeuronPos = this.brainGridSize / 3;

        this.leftLightInputNeuron.moveTo( 
                this.brainGridQuadrantSize - offsetInputNeuronPos, 0,
                this.brainGridQuadrantSize);
        
        this.rightLightInputNeuron.moveTo( 
                -this.brainGridQuadrantSize + offsetInputNeuronPos, 0,
                this.brainGridQuadrantSize);

    } // End of setupLightInputNeurons()
    
    
    /**
     * Wires the motion sensors.
     * 
     * @param context The Repast simulation context.
     */
    private void setupMotionInputNeurons(final Context<Object> context) {
        
        final int offsetInputNeuronPos = this.brainGridSize / 3;

        this.leftMotionInputNeuron = new MotionInputNeuron("Left",
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getLeftMotionSensor());
        context.add(this.leftMotionInputNeuron);
        
        this.leftMotionInputNeuron.moveTo(
                this.brainGridQuadrantSize - offsetInputNeuronPos, 0, 
                -this.brainGridQuadrantSize);

        this.rightMotionInputNeuron = new MotionInputNeuron("Right", 
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getRightMotionSensor());
        context.add(this.rightMotionInputNeuron);
        
        this.rightMotionInputNeuron.moveTo( 
                -this.brainGridQuadrantSize + offsetInputNeuronPos, 0, 
                -this.brainGridQuadrantSize);

    } // End of setupMotionInputNeurons()

    
    /**
     * Wires the actuators.
     *  
     * @param context The Repast simulation context.
     */
    private void setupOutputNeuron(final Context<Object> context) {
        
        final int offsetOutputNeuronPos = this.brainGridSize / 3;

        this.leftMotorNeuron = new OutputNeuron("Left", this.brainSpace, 
                this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork, 
                this.robot.getLeftMotor());
        context.add(this.leftMotorNeuron);
        
        this.leftMotorNeuron.moveTo( 
                this.brainGridQuadrantSize - offsetOutputNeuronPos, 
                -this.brainGridQuadrantSize, 0);

        this.rightMotorNeuron = new OutputNeuron("Right", this.brainSpace, 
                this.brainGrid,    null, this.neuralNetwork, this.neuritesNetwork, 
                this.robot.getRightMotor());
        context.add(this.rightMotorNeuron);
        
        this.rightMotorNeuron.moveTo(
                -this.brainGridQuadrantSize + offsetOutputNeuronPos, 
                -this.brainGridQuadrantSize, 0);

    } // End of setupOutputNeurons()

    
    /**
     * Setup the initial chemical environment.
     * 
     * @param context The Repast simulation context.
     */
    private void setupInitialEnvironment(final Context<Object> context) {
        
        logger.info("Initialising extracellular matrix.");

        Map<CellProductType, Double> initialConcentrations = 
                new HashMap<CellProductType, Double>();
        
        initialConcentrations.put(CellProductType.FOOD, 
                this.initialMatrixFoodConcentration);

        ExtracellularMatrix matrix = new DistributedExtracellularMatrix(
                context, this.brainSpace, this.brainGrid, 
                this.brainGridQuadrantSize,    initialConcentrations);

        SimulationContextHolder simulationContext = 
                SimulationContextHolder.getInstance();        
        simulationContext.setExtracellularMatrix(matrix);
        
    } // End of setupInitialEnvironment()
    
    
    /**
     * Setup the initial brain cell population.
     *  
     * @param context The Repast simulation context.
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
                    
                    if (!this.brainGrid.getObjectsAt(x, y, z)
                            .iterator().hasNext()) {
                    
                        UndifferentiatedCell motherCell = 
                                CellFactory.getNewUndifferentiatedCell(
                                        "U(" + x + "," + y + "," + z + ")"); 
                        context.add(motherCell);
                    
                        motherCell.moveTo(x, y, z);
                        
                    } // End if()

                } // End for(z)
                
            } // End for(y)
            
        } // End for(x)

    } // End of setupInitialPopulation()
    
    
    /**
     * For test purposes.
     *  
     * @param context The Repast simulation context.
     */
    @SuppressWarnings("unused")
    private void setupTestNeuron(final Context<Object> context) {
        
        Neuron neuron = CellFactory.getNewNeuron("");
        
        context.add(neuron);
        this.brainSpace.moveTo(neuron, 0.5, 0.5, 0.5);
        this.brainGrid.moveTo(neuron, 0, 0, 0);

    } // End of setupTestNeuron()
    
    
    /**
     * For test purposes.
     * 
     * @param context The Repast simulation context.
     */
    @SuppressWarnings("unused")
    private void setupTestNeuronPair(final Context<Object> context) {
        
        final int offsetNeuronPos = this.brainGridSize / 3;

        Neuron neuron1 = CellFactory.getNewNeuron("");
        
        context.add(neuron1);
        this.brainSpace.moveTo(neuron1, 
                this.brainGridQuadrantSize - offsetNeuronPos + 0.5, 0.5, 0.5);
        this.brainGrid.moveTo(neuron1, 
                this.brainGridQuadrantSize - offsetNeuronPos, 0, 0);

        Neuron neuron2 = CellFactory.getNewNeuron("");
        
        context.add(neuron2);
        neuron2.moveTo(-this.brainGridQuadrantSize + offsetNeuronPos, 0, 0);

    } // End of setupTestNeuron()
    
    
    /**
     * Setup the control test used to demonstrate the ability of a very simple
     * neural network to drive the robot in following the light source. 
     *
     * @param context The Repast simulation context.
     */
    private void setupControlTest(final Context<Object> context) {

        this.leftLightInputNeuron = new TestInputNeuron("Left",
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getLeftLightSensor());
        context.add(this.leftLightInputNeuron);
        
        this.rightLightInputNeuron = new TestInputNeuron("Right", 
                this.brainSpace, this.brainGrid, null, this.neuralNetwork, 
                this.neuritesNetwork, this.robot.getRightLightSensor());
        context.add(this.rightLightInputNeuron);
        
        final int offsetInputNeuronPos = this.brainGridSize / 3;

        this.leftLightInputNeuron.moveTo( 
                this.brainGridQuadrantSize - offsetInputNeuronPos, 0,
                this.brainGridQuadrantSize);
        
        this.rightLightInputNeuron.moveTo( 
                -this.brainGridQuadrantSize + offsetInputNeuronPos, 0,
                this.brainGridQuadrantSize);

        // Sufficient to wire only the left (positive) motor of the robot. 
        this.leftMotorNeuron = new TestOutputNeuron("Left", this.brainSpace, 
                this.brainGrid, null, this.neuralNetwork, this.neuritesNetwork, 
                this.robot.getLeftMotor());
        context.add(this.leftMotorNeuron);
        
        this.leftMotorNeuron.moveTo(0, -this.brainGridQuadrantSize, 0);
        
        this.neuralNetwork.addEdge(this.leftLightInputNeuron, 
                this.leftMotorNeuron, 1);
        
        this.neuralNetwork.addEdge(this.rightLightInputNeuron, 
                this.leftMotorNeuron, -1);
        
    } // End of setupControlTest()

    
} // End of NeurogenesisBuilder class
