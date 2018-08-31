package io.meme.toolbox.wrench.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author meme
 * @since 1.0
 */
public class Agent {

    public static Instrumentation instrumentation;

    public static void agentmain (String agentArgs, Instrumentation inst) {
        System.out.println("agent run completely.");
        instrumentation = inst;
    }

}
