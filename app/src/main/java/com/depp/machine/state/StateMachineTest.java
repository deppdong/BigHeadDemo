package com.depp.machine.state;

import android.content.Context;

public class StateMachineTest {

    public static void doTest(Context context){
        CallAudioRouteStateMachine sm = new CallAudioRouteStateMachine(context);
        sm.sendMessage(1);
    }
}
