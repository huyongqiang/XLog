package com.zwy.xlog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Used to determine how messages should be printed or saved.
 */
public interface FormatStrategy {

  void log(int priority, @Nullable String tag, @NonNull String message);
}
