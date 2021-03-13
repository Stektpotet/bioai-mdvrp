package ga.selection;
import ga.data.Population;

public interface ParentSelector<C> {
    public C[] select(Population<C> population);
}
