package de.logiball.monorepo.service.serviceone;

import de.logiball.monorepo.lib.libtwo.LibOne;
import de.logiball.monorepo.lib.libtwo.LibTwo;

/**
 * Created by tim on 25.06.17.
 */
public class ServiceOne {
    public static void main (String[] args) {
        System.out.println(String.format("I'm using libraries %s and %s.", LibOne.info(), LibTwo.info()));
    }
}
