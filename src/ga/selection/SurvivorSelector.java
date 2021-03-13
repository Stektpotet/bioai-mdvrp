package ga.selection;

import ga.data.Population;

public interface SurvivorSelector<C> {
    public Population<C> select(Population<C> generation, C[] offspring);
}
