package ga.data;

public interface Initializer<Pop extends Population<C>, C  extends Chromosome> {
    public Pop breed(int popSize);
}
