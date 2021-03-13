package ga.selection;

import ga.data.Population;

public interface SurvivorSelector<Pop extends Population<C>, C> {
    public Population<Pop> select(Pop generation, C[] parents, C[] offspring);
}
