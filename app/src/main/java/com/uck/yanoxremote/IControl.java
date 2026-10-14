package com.uck.yanoxremote;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/** Minimal client for the UEI QuickSet service used on LeEco EUI phones. */
public class IControl {
    public static final String ACTION = "com.uei.control.IControl";
    public static final String QUICKSET_UEI_PACKAGE_NAME = "com.uei.quicksetsdk.letv";
    public static final String QUICKSET_UEI_SERVICE_CLASS = "com.uei.control.Service";
    private final IBinder service;

    public IControl(IBinder service) { this.service = service; }

    public int getLastResultcode() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ACTION);
            service.transact(13, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } finally { reply.recycle(); data.recycle(); }
    }

    public int transmit(int carrierFrequency, int[] pattern) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ACTION);
            data.writeInt(carrierFrequency);
            data.writeIntArray(pattern);
            service.transact(18, data, reply, 0);
            reply.readException();
            return reply.readInt();
        } finally { reply.recycle(); data.recycle(); }
    }
}
