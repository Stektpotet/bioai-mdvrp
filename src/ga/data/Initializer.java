package ga.data;

public interface Initializer<Pop extends Population<C>, C> {
    public Pop breed(int popSize);
}
