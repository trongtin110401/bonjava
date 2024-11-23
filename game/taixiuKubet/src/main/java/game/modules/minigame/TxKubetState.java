package game.modules.minigame;

import java.util.HashMap;
import java.util.Map;

public enum TxKubetState {

    INIT(-1, -1),
    GENERATE_RESULT(2, 0),
    BETTING(0, 3),
    SHOW_RESULT(3, 1),
    CONFIRM_RESULT(1, 2);

    private final int step;
    private final int nextStep;
    private static final Map<Integer, TxKubetState> STEP_TO_STATE_MAP = new HashMap<>();

    // Static block to initialize the map
    static {
        for (TxKubetState state : TxKubetState.values()) {
            STEP_TO_STATE_MAP.put(state.getStep(), state);
        }
    }

    TxKubetState(int step, int nextStep) {
        this.step = step;
        this.nextStep = nextStep;
    }

    /**
     * Lookup method to get TxKubetState by step
     */
    public static TxKubetState getByStep(int step) {
        return STEP_TO_STATE_MAP.get(step);
    }

    public int getStep() {
        return step;
    }

    public int getNextStep() {
        return nextStep;
    }
}

