package ga.change;

public interface Mutator<C> {
    C mutate(C chromosome);
}
