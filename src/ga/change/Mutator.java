package ga.change;

public interface Mutator<C> {
    public C[] mutateAll(C[] chromosome);
    C mutate(C chromosome);
}
