package game.xocdia.kubet;

import java.util.HashMap;
import java.util.Map;

public enum XocDiaKubetState {

    INIT(-1, -1),
    GENERATE_RESULT(0, 2),
    BETTING(2, 3),
    SHOW_RESULT(3, 4),
    CONFIRM_RESULT(4, 0);

    private final int step;
    private final int nextStep;
    private static final Map<Integer, XocDiaKubetState> STEP_TO_STATE_MAP = new HashMap<>();

    // Static block to initialize the map
    static {
        for (XocDiaKubetState state : XocDiaKubetState.values()) {
            STEP_TO_STATE_MAP.put(state.getStep(), state);
        }
    }

    XocDiaKubetState(int step, int nextStep) {
        this.step = step;
        this.nextStep = nextStep;
    }

    /**
     * Lookup method to get TxKubetState by step
     */
    public static XocDiaKubetState getByStep(int step) {
        return STEP_TO_STATE_MAP.get(step);
    }

    public int getStep() {
        return step;
    }

    public int getNextStep() {
        return nextStep;
    }
}

