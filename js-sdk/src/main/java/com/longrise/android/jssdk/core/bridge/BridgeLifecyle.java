package com.longrise.android.jssdk.core.bridge;

import android.app.Activity;

import com.longrise.android.jssdk.lifecycle.LifecycleManager;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020-04-24.
 *
 * @author godliness
 */

abstract class BridgeLifecyle<T extends Activity> implements LifecycleManager.OnLifecycleListener {

    private final LifecycleManager mManager;
    private WeakReference<T> mHost;

    protected abstract void onDestroy();

    protected final T getHost() {
        return mHost.get();
    }

    protected final boolean isFinished() {
        final T host = getHost();
        return host == null || host.isFinishing();
    }

    @Override
    public final void onActivityFinished(Activity host) {
        if (host == getHost()) {
            mManager.removeLifecyclelistener(this);
            onDestroy();
        }
    }

    final void attachHost(T host) {
        this.mHost = new WeakReference<>(host);
        this.mManager.registerLifecycle(host);
    }

    BridgeLifecyle() {
        this.mManager = LifecycleManager.getManager();
        this.mManager.addLifecyceListener(this);
    }
}
