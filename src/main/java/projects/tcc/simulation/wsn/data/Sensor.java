package projects.tcc.simulation.wsn.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import projects.tcc.simulation.algorithms.graph.GraphEdge;
import projects.tcc.simulation.io.SimulationOutput;
import sinalgo.nodes.Position;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(of = "sensorId")
public class Sensor {

    private static double DISTANCES_ARRAY[] = {
            5.142,
            5.769,
            6.473,
            7.263,
            8.150,
            9.144,
            10.260,
            11.512,
            12.916,
            14.492,
            16.261,
            18.245,
            20.471,
            22.969,
            25.771,
            28.916,
            32.444,
            36.403,
            40.845,
            45.829,
            51.420,
            57.695,
            64.735,
            72.633,
            81.496,
            91.440};

    private static double[] CURRENT_ARRAY = {
            8.6,
            8.8,
            9.0,
            9.0,
            9.1,
            9.3,
            9.3,
            9.5,
            9.7,
            9.9,
            10.1,
            10.4,
            10.6,
            10.8,
            11.1,
            13.8,
            14.5,
            14.5,
            15.1,
            15.8,
            16.8,
            17.2,
            18.5,
            19.2,
            21.3,
            25.4,
    };

    //Vetor de Corrente x Distância
    public static double getCurrentPerDistance(double distancia) {
        int i = 0;
        while (DISTANCES_ARRAY[i] <= distancia) {
            i++;
            if (i == DISTANCES_ARRAY.length) {
                SimulationOutput.println("\n\nERROR: Distância ao Pai não informada corretamente");
                SimulationOutput.println("Valor da Distância: " + distancia);
            }
        }

        return CURRENT_ARRAY[i];
    }

    private final int sensorId;
    private final Position position;
    private double batteryEnergy;
    private double batteryCapacity;
    private List<Sensor> children;
    private Sensor parent;
    private double sensRadius;
    private double commRadius;
    private boolean active;
    private boolean useActivationPower;
    private boolean connected;
    private boolean failed;

    private double activationPower;
    private double receivePower;
    private double maintenancePower;
    private double commRatio; //Taxa de comunicação durante a transmissão em uma u.t.

    private List<Sensor> neighborhood;
    private List<Integer> coveredPoints;
    private List<Integer> exclusivelyCoveredPoints;
    private double costToSink;

    private List<GraphEdge> adjacencies;
    private double minDistance;
    private Sensor previous;

    public Sensor(int sensorId, double x, double y, double commRadius, double commRatio) {
        this.sensorId = sensorId;
        this.position = new Position(x, y, 0);
        this.setCommRadius(commRadius);

        this.setActive(true);

        this.setChildren(new ArrayList<>());
        this.setNeighborhood(new ArrayList<>());

        this.setAdjacencies(new ArrayList<>());
        this.setMinDistance(Double.POSITIVE_INFINITY);

        this.setCommRatio(commRatio);
    }

    public Sensor(int sensorId, double x, double y, double sensRadius, double commRadius,
                  double batteryEnergy, double activationPower, double receivePower,
                  double maintenancePower, double commRatio) {
        this(sensorId, x, y, commRadius, commRatio);

        this.setActivationPower(activationPower);
        this.setReceivePower(receivePower);
        this.setMaintenancePower(maintenancePower);

        this.setBatteryEnergy(batteryEnergy);
        this.setBatteryCapacity(batteryEnergy);
        this.setSensRadius(sensRadius);

        this.setParent(null);

        this.setActive(false);
        this.setFailed(false);
        this.setConnected(false);

        this.setCoveredPoints(new ArrayList<>());
        this.setExclusivelyCoveredPoints(new ArrayList<>());
    }

    public void resetConnections() {
        this.setParent(null);
        this.setPrevious(null);
        this.setConnected(false);
        this.getAdjacencies().clear();
        this.setMinDistance(Double.POSITIVE_INFINITY);
        this.getChildren().clear();
    }

    public void addChild(Sensor child) {
        this.getChildren().add(child);
    }

    public int queryDescendants() {
        int totalChildCount = this.getChildren().size();
        for (Sensor child : this.getChildren()) {
            totalChildCount += child.queryDescendants();
        }
        return totalChildCount;
    }

    public void drawEnergySpent(double energySpent) {
        this.setBatteryEnergy(Math.max(this.getBatteryEnergy() - energySpent, 0));
    }

    public double getPowerToTransmit(double distanceToParent, int totalChildCount) {
        double current = Sensor.getCurrentPerDistance(distanceToParent);
        return this.commRatio * current * (totalChildCount + 1);
    }

    public void disconnectChildren() {
        for (Sensor child : this.getChildren()) {
            child.setConnected(false);
            child.disconnectChildren();
        }
    }

    public String toString() {
        return Integer.toString(this.getSensorId());
    }
}
