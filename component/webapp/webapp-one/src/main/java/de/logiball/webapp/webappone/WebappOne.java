package de.logiball.webapp.webappone;

import de.logiball.monorepo.lib.libtwo.LibThree;

/**
 * Created by tim on 25.06.17.
 */
public class WebappOne {
    public static void main (String[] args) {
        System.out.println(String.format("I'm using library %s.", LibThree.info()));
    }
}
