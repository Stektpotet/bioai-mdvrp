package ga.data;

public interface Initializer<C> {
    public Population<C> breed(int popSize);
}
