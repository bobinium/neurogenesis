/**
 * 
 */
package neurogenesis;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * @author bob
 *
 */
public class NeurogenesisBuilder implements ContextBuilder<Object> {

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
						new repast.simphony.space.continuous.InfiniteBorders<Object>(),
						new double[]{50, 50, 50}, new double[]{25, 25, 25});
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> brainGrid = gridFactory.createGrid("brain grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(),
						true, new int[]{50, 50, 50}, new int[]{25, 25, 25}));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
/*		int zombieCount = (Integer) params.getValue("zombie.count");
		for (int i = 0; i < zombieCount; i++) {
			context.add(new Zombie(space, grid));
		} */

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
		
		InputNeuron leftInputNeuron = 
				new InputNeuron(neuralNetwork, leftLightSensor, arenaSpace, brainGrid);
		context.add(leftInputNeuron);
		InputNeuron rightInputNeuron = 
				new InputNeuron(neuralNetwork, rightLightSensor, arenaSpace, brainGrid);
		context.add(rightInputNeuron);
		
		OutputNeuron motorNeuron = new OutputNeuron(neuralNetwork, arenaSpace, brainGrid);
		context.add(motorNeuron);
		neuralNetwork.addEdge(leftInputNeuron, motorNeuron, 1);
		neuralNetwork.addEdge(rightInputNeuron, motorNeuron, -1);
		
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
				
		brainSpace.moveTo(motorNeuron, 0, 0, 0);
		brainSpace.moveTo(leftInputNeuron, -2, -2, 0);
		brainSpace.moveTo(rightInputNeuron, 2, -2, 0);
		
		return context;
	}

}
