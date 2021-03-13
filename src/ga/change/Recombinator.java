package ga.change;

public interface Recombinator<C> {
    public C[] recombine(C[] parents);
    C[] crossover(C mama, C papa);
}
