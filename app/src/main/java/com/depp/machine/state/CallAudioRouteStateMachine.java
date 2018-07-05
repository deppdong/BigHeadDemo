package com.depp.machine.state;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

public class CallAudioRouteStateMachine extends StateMachine {
    private final ActiveHeadsetRoute mActiveHeadsetRoute = new ActiveHeadsetRoute();
    private final ActiveBluetoothRoute mActiveBluetoothRoute = new ActiveBluetoothRoute();
    private final ActiveSpeakerRoute mActiveSpeakerRoute = new ActiveSpeakerRoute();


    public CallAudioRouteStateMachine(
            Context context) {
        super("CallAudioRouteStateMachine");
        addState(mActiveHeadsetRoute);
        addState(mActiveBluetoothRoute);
        addState(mActiveSpeakerRoute);

        setInitialState(mActiveHeadsetRoute);
        start();
    }

    abstract class AudioState extends State {
        @Override
        public void enter() {
            super.enter();

        }

        @Override
        public void exit() {
            super.exit();
        }

        @Override
        public boolean processMessage(Message msg) {

            return NOT_HANDLED;
        }

        // Behavior will depend on whether the state is an active one or a quiescent one.
        abstract public String getName();
    }

    abstract class EarpieceRoute extends AudioState {
        @Override
        public boolean processMessage(Message msg) {
            if (super.processMessage(msg) == HANDLED) {
                return HANDLED;
            }
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    class ActiveHeadsetRoute extends HeadsetRoute {
        @Override
        public String getName() {
            return "ActiveHeadsetRoute";
        }

        @Override
        public void enter() {
            super.enter();
            dlog("enter "+ getName());
        }

        @Override
        public boolean processMessage(Message msg) {
            dlog("processMessage "+ getName());
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    abstract class HeadsetRoute extends AudioState {

        @Override
        public boolean processMessage(Message msg) {
            if (super.processMessage(msg) == HANDLED) {
                return HANDLED;
            }
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    abstract class BluetoothRoute extends AudioState {

        @Override
        public boolean processMessage(Message msg) {
            if (super.processMessage(msg) == HANDLED) {
                return HANDLED;
            }
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    class ActiveBluetoothRoute extends BluetoothRoute {
        @Override
        public String getName() {
            return "ActiveBluetoothRoute";
        }

        @Override
        public void enter() {
            super.enter();
            dlog("enter "+ getName());
        }

        @Override
        public boolean processMessage(Message msg) {
            dlog("processMessage "+ getName());
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    abstract class SpeakerRoute extends AudioState {

        @Override
        public boolean processMessage(Message msg) {
            if (super.processMessage(msg) == HANDLED) {
                return HANDLED;
            }
            switch (msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }

    class ActiveSpeakerRoute extends SpeakerRoute {
        @Override
        public String getName() {
            return "ActiveSpeakerRoute";
        }

        @Override
        public void enter() {
            super.enter();
            dlog("enter "+ getName());
        }

        @Override
        public boolean processMessage(Message msg) {
            dlog("processMessage "+ getName());
            switch(msg.what) {
                default:
                    return NOT_HANDLED;
            }
        }
    }


    private static void dlog(String msg) {
        Log.d("StateMachineDepp", msg);
    }
}
