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
		ContinuousSpace<Object> space =
				spaceFactory.createContinuousSpace("space", context,
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						50, 50);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(),
						true, 50, 50));
		
		Parameters params = RunEnvironment.getInstance().getParameters();
/*		int zombieCount = (Integer) params.getValue("zombie.count");
		for (int i = 0; i < zombieCount; i++) {
			context.add(new Zombie(space, grid));
		} */

		LightSensor leftLightSensor = new LightSensor(Math.PI / -12);
		LightSensor rightLightSensor = new LightSensor(Math.PI / 12);		
		LightSensor[] lightSensors = 
				new LightSensor[] { leftLightSensor, rightLightSensor };
		
		Robot robot = new Robot(5, 0, Math.PI / 4, Math.PI / 6, lightSensors);
		
		LightSource lightSource = new LightSource(10, 0, Math.PI / 6, 10);
		
		ExperimentSimulator experimentSimulator = 
				new ExperimentSimulator(robot, lightSource);
		context.add(experimentSimulator);
		
		InputNeuron inputNeuron1 = 
				new InputNeuron(neuralNetwork, leftLightSensor);
		context.add(inputNeuron1);
		InputNeuron inputNeuron2 = 
				new InputNeuron(neuralNetwork, rightLightSensor);
		context.add(inputNeuron2);
		
		MotorNeuron motorNeuron = new MotorNeuron(neuralNetwork);
		context.add(motorNeuron);
		neuralNetwork.addEdge(inputNeuron1, motorNeuron);
		neuralNetwork.addEdge(inputNeuron2, motorNeuron);
		
/*		int humanCount = (Integer) params.getValue("human.count");
		for (int i = 0; i < humanCount; i++) {
			int energy = RandomHelper.nextIntFromTo(4, 10);
			context.add(new Human(space, grid, energy));
		}

		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		} */
		
		return context;
	}

}
